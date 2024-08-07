package com.axana.app;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractSegementwithNameFinal {

    // Function to retrieve field and subfield names from a segment
    private static Map<String, Object> getFieldNames(Segment segment, Class<?> segmentClass) {
        Map<String, Object> fieldNames = new LinkedHashMap<>();

        try {
            int numFields = segment.numFields();
            System.out.println("Segment " + segment.getName() + " has " + numFields + " fields.");

            for (int i = 1; i <= numFields; i++) {
                Type[] fields = segment.getField(i);
                String fieldIdentifier = Integer.toString(i);

                // Retrieve the method name for the field
                String methodName = findMethodNameForSubfield(segmentClass, segment.getName(), fieldIdentifier);

                for (int j = 0; j < fields.length; j++) {
                    if (fields[j] instanceof Composite) {
                        Composite composite = (Composite) fields[j];
                        Type[] components = composite.getComponents();
                        Map<String, String> compositeMap = new LinkedHashMap<>();

                        for (int k = 0; k < components.length; k++) {
                            Type component = components[k];
                            if (component instanceof Primitive) {
                                String value = component.encode();
                                String componentName = component.getName();
                                fieldIdentifier = Integer.toString(i) + "." + Integer.toString(k + 1);
                                methodName = findMethodNameForSubfield(segmentClass, segment.getName(), fieldIdentifier);
                                String key = fieldIdentifier;

                                if ((componentName.contains("TS") || componentName.contains("DTM") || componentName.contains("DT"))) {
                                    value = convertTimestamp(value);
                                }

                                compositeMap.put(key + " (" + componentName + ")", value);
                            }
                        }
                        fieldNames.put(methodName, compositeMap);
                    } else if (fields[j] instanceof Primitive) {
                        Primitive primitive = (Primitive) fields[j];
                        String primitiveName = primitive.getName();
                        String value = primitive.encode();

                        if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
                            value = convertTimestamp(value);
                        }
                        fieldNames.put(methodName + " (" + primitiveName + ")", value);
                    }
                }
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }

        return fieldNames;
    }

    // Method to dynamically get the segment class using reflection
    private static Class<?> getSegmentClass(String segmentName) {
        try {
            // Construct the fully qualified class name
            String packageName = "ca.uhn.hl7v2.model.v251.segment"; // Replace with your actual package
            String className = packageName + "." + segmentName;
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String findMethodNameForSubfield(Class<?> segmentClass, String segmentName, String fieldIdentifier) {
        try {
            Method[] methods = segmentClass.getDeclaredMethods();
            String[] parts = fieldIdentifier.split("\\.");
            String fieldNumber = parts[0];
            String subfieldNumber = parts.length > 1 ? parts[1] : null;

            for (Method method : methods) {
                if (method.getName().toLowerCase().contains(segmentName.toLowerCase() + fieldNumber)) {
                    if (subfieldNumber != null) {
                        Class<?> returnType = method.getReturnType();
                        Method[] compositeMethods = returnType.getDeclaredMethods();

                        for (Method compositeMethod : compositeMethods) {
                            if (compositeMethod.getName().toLowerCase().contains(subfieldNumber)) {
                                return compositeMethod.getName();
                            }
                        }
                    } else {
                        return method.getName();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            String hl7Message = "MSH|^~\\&|HIS|RIH|EKG|EKG|20240806221120||ADT^A01|MSG00001|P|2.3\r" +
                    "PID|1||123456^^^Hospital^MR||Doe^John||19650525|M|||123 Main St^^Metropolis^IL^12345|555-1234\r" +
                    "OBX|1|ST|1234^Test1||Positive|||\r" +
                    "OBX|2|ST|5678^Test2||Negative|||\r";

            Parser parser = new PipeParser();
            Message message = parser.parse(hl7Message);

            String[] segmentNames = {"MSH", "PID", "OBX"};
            Map<String, JSONArray> jsonMap = new LinkedHashMap<>();

            for (String segmentName : segmentNames) {
                Structure[] structures = message.getAll(segmentName);
                JSONArray segmentArray = new JSONArray();

                for (int index = 0; index < structures.length; index++) {
                    Segment segment = (Segment) structures[index];

                    // Retrieve segment class dynamically
                    Class<?> segmentClass = getSegmentClass(segment.getName());
                    if (segmentClass != null) {
                        Map<String, Object> fieldNames = getFieldNames(segment, segmentClass);
                        JSONObject segmentJson = new JSONObject(fieldNames);
                        segmentArray.put(segmentJson);
                    }
                }

                jsonMap.put(segmentName, segmentArray);
            }

            JSONObject jsonOutput = new JSONObject(jsonMap);
            System.out.println(jsonOutput.toString(4)); // Pretty print with indentation

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertTimestamp(String hl7Timestamp) {
        SimpleDateFormat hl7Format = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat hl7DtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat desiredDtFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (hl7Timestamp.length() != 0) {
                if (hl7Timestamp.length() > 8) {
                    Date date = hl7Format.parse(hl7Timestamp);
                    return desiredFormat.format(date);
                } else {
                    Date date = hl7DtFormat.parse(hl7Timestamp);
                    return desiredDtFormat.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return hl7Timestamp; // Return original if parsing fails
        }
        return hl7Timestamp;
    }
}
