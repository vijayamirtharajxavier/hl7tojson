package com.axana.app;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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

public class ExtractSegmentwithElements {

    // Function to retrieve field and subfield names from a segment
    private static Map<String, String> getFieldNames(Segment segment) {
        Map<String, String> fieldNames = new LinkedHashMap<>();

        try {
            int numFields = segment.numFields();
            System.out.println("Segment " + segment.getName() + " has " + numFields + " fields.");

            for (int i = 1; i <= numFields; i++) {
                Type[] fields = segment.getField(i);
                    String fieldName = segment.getName() + "-" + i;

                for (int j = 0; j < fields.length; j++) {
                    if (fields[j] instanceof Composite) {
                        Composite composite = (Composite) fields[j];
                        Type[] components = composite.getComponents();

                        for (int k = 0; k < components.length; k++) {
                            Type component = components[k];
                            if (component instanceof Primitive) {
                                String value = component.encode();
                                String componentName = component.getName();
                                String key = fieldName + "." + (k + 1);
                           //     System.out.println("value: " + value + ", compname : " + componentName);
                          // fieldNames.put(key, componentName);
                          if ((componentName.contains("TS") || componentName.contains("DTM") || componentName.contains("DT"))) {
                            value = convertTimestamp(value);
                        }

                                fieldNames.put(key, value);
                            }
                        }
                    } else if (fields[j] instanceof Primitive) {
                        Primitive primitive = (Primitive) fields[j];
                        String primitiveName = primitive.getName();
                        String value = primitive.encode();
 //                       fieldNames.put(fieldName, primitiveName);
 if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
    value = convertTimestamp(value);
}
fieldNames.put(fieldName, value);
 //System.out.println("primName:: " + primitiveName);
                    }
                }
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }

        return fieldNames;
    }

    public static void main(String[] args) {
        try {
            // Sample HL7 message with repeated OBX segments
            String hl7Message = "MSH|^~\\&|HIS|RIH|EKG|EKG|20240806221120||ADT^A01|MSG00001|P|2.3\r" +
                                "PID|1||123456^^^Hospital^MR||Doe^John||19650525|M|||123 Main St^^Metropolis^IL^12345|555-1234\r" +
                                "OBX|1|ST|1234^Test1||Positive|||\r" +
                                "OBX|2|ST|5678^Test2||Negative|||\r";

            // Parse the message using HAPI
            Parser parser = new PipeParser();
            Message message = parser.parse(hl7Message);

            // Define the segment names to analyze
            String[] segmentNames = { "MSH", "PID", "OBX" };

            // Use LinkedHashMap to preserve insertion order of segments
            Map<String, JSONObject> jsonMap = new LinkedHashMap<>();

            for (String segmentName : segmentNames) {
                // Retrieve all instances of the segment
                Structure[] structures = message.getAll(segmentName);
                for (int index = 0; index < structures.length; index++) {
                    Segment segment = (Segment) structures[index];
                    Map<String, String> fieldNames = getFieldNames(segment);

                    // Convert to JSON object
                    JSONObject segmentJson = new JSONObject(fieldNames);
                    jsonMap.put(segmentName + "[" + index + "]", segmentJson);
                }
            }

            // Create JSON object maintaining the order
            JSONObject jsonOutput = new JSONObject(jsonMap);

            // Print JSON output
            System.out.println(jsonOutput.toString(4)); // Pretty print with indentation

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Function to retrieve field names from a segment class and order them
    private static Map<String, String> getFieldNames(Class<?> segmentClass) {
        Map<String, String> fieldNames = new TreeMap<>(); // Use TreeMap for sorting
        try {
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                // Check if method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Extract the field number from the method name
                    String methodName = method.getName(); // .getName();
                    System.out.println("mName : " + methodName);
                    String underscoreSeparated = methodName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
        
                    // Handle the case where the string starts with an uppercase letter
                    if (Character.isUpperCase(methodName.charAt(0))) {
                        underscoreSeparated = "_" + underscoreSeparated;
                    }

               //     System.out.println(underscoreSeparated);
//                     methodName = methodName.replaceAll(" ", "_");
                    String fieldNumber = methodName.replaceAll("[^0-9]", ""); // Extract digits
                    if (!fieldNumber.isEmpty()) {
                        String fieldName = methodName; // Get the method name
                        String segmentPrefix = segmentClass.getSimpleName();
                        String key = segmentPrefix + "." + fieldNumber;
                        fieldNames.put(key, fieldName.split("_")[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }





    // Function to convert HL7 timestamp to desired format
    private static String convertTimestamp(String hl7Timestamp) {
        SimpleDateFormat hl7Format = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat hl7DtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat desiredDtFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(hl7Timestamp.length()!=0)
            {
            if(hl7Timestamp.length()>8  )
            {
            Date date = hl7Format.parse(hl7Timestamp);
            return desiredFormat.format(date);
            }
            else
            {
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
