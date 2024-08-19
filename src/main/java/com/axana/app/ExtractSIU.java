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
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v23.message.SIU_S12;
import ca.uhn.hl7v2.model.v23.message.SIU_S14;
import ca.uhn.hl7v2.model.v23.message.SIU_S17;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;



public class ExtractSIU {


    
    // Function to retrieve field and subfield names from a segment
  /*  private static Map<String, Object> getFieldNames(Segment segment, Class<?> segmentClass) 
    {
        Map<String, Object> fieldNames = new LinkedHashMap<>();

        try {
            int numFields = segment.numFields();
            System.out.println("Segment " + segment.getName() + " has " + numFields + " fields.");

            for (int i = 1; i <= numFields; i++) {
                Type[] fields = segment.getField(i);
                String fieldIdentifier = Integer.toString(i).trim();
                System.out.println("main fieldId no: " + fieldIdentifier);

                System.out.println("fi:" + fieldIdentifier + " of " + segment.getName());
                HL7Reflection hr = new HL7Reflection();
                // Retrieve the method name for the field
                String methodName = HL7Reflection.findMethodNameForSubfield(segmentClass, segment.getName(), fieldIdentifier);
                
                // .findMethodNameForSubfield(segmentClass, segment.getName(), fieldIdentifier);

                if (methodName != null) {
                    System.out.println("Method Name for " + fieldIdentifier + ": " + methodName);
                } else {
                    System.out.println("No method found for field: " + fieldIdentifier);
                }

                for (int j = 0; j < fields.length; j++) {
                    if (fields[j] instanceof Composite) {
                        Composite composite = (Composite) fields[j];
                        Map<String, Object> compositeFields = new LinkedHashMap<>();
                        Type[] components = composite.getComponents();
                        
                        for (int k = 0; k < components.length; k++) {
                            Type component = components[k];
                            String componentIdentifier = Integer.toString(i) + "." + Integer.toString(k + 1);
                            System.out.println("comp no: " + componentIdentifier);
                            String submethodName = findMethodNameForSubfield(segmentClass, segment.getName(), componentIdentifier);
                            if (component instanceof Primitive) {
                                Primitive primitive = (Primitive) component;
                                String primitiveName = primitive.getName();
                                String value = primitive.encode();

                                if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
                                    value = convertTimestamp(value);
                                }
                              //  compositeFields.put(submethodName.split("_")[1] , value);
                                compositeFields.put(componentIdentifier + " (" + primitiveName + ")" + "["+ submethodName.split("_")[1] + "]", value);
                            }
                        }
                        fieldNames.put(methodName.split("_")[1], compositeFields);
                    } else if (fields[j] instanceof Primitive) {
                        Primitive primitive = (Primitive) fields[j];
                        String primitiveName = primitive.getName();
                        String value = primitive.encode();

                        if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
                            value = convertTimestamp(value);
                        }

                        fieldNames.put(methodName.split("_")[1], value);
                    }
                }
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }

        return fieldNames;
    }

*/


private static Map<String, Object> getFieldNames(Segment segment, Class<?> segmentClass) {
    Map<String, Object> fieldNames = new LinkedHashMap<>();

    try {
        int numFields = segment.numFields();
        System.out.println("Segment " + segment.getName() + " has " + numFields + " fields.");

        for (int i = 1; i <= numFields; i++) {
            Type[] fields = segment.getField(i);
            String fieldIdentifier = Integer.toString(i).trim();
            System.out.println("main fieldId no: " + fieldIdentifier);

            String methodName = findMethodNameForSubfield(segmentClass, segment.getName(), fieldIdentifier);

            if (methodName != null) {
                System.out.println("Method Name for " + fieldIdentifier + ": " + methodName);
            } else {
                System.out.println("No method found for field: " + fieldIdentifier);
            }

            for (int j = 0; j < fields.length; j++) {
                if (fields[j] instanceof Composite) {
                    Composite composite = (Composite) fields[j];
                    Map<String, Object> compositeFields = new LinkedHashMap<>();
                    Type[] components = composite.getComponents();

                    for (int k = 0; k < components.length; k++) {
                        Type component = components[k];
                        String componentIdentifier = Integer.toString(i) + "." + Integer.toString(k + 1);
                        System.out.println("comp no: " + componentIdentifier);
                        String submethodName = findMethodNameForSubfield(segmentClass, segment.getName(), componentIdentifier);

                        if (component instanceof Primitive) {
                            Primitive primitive = (Primitive) component;
                            String primitiveName = primitive.getName();
                            String value = primitive.encode();

                            if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
                                value = convertTimestamp(value);
                            }

                            compositeFields.put(componentIdentifier + " (" + primitiveName + ")" + 
                                                (submethodName != null ? "[" + submethodName.split("_")[1] + "]" : ""), value);
                        }
                    }

                    if (!compositeFields.isEmpty()) {
                        fieldNames.put(methodName != null ? methodName.split("_")[1] : "Field_" + i, compositeFields);
                    }
                } else if (fields[j] instanceof Primitive) {
                    Primitive primitive = (Primitive) fields[j];
                    String primitiveName = primitive.getName();
                    String value = primitive.encode();

                    if ((primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT"))) {
                        value = convertTimestamp(value);
                    }

                    fieldNames.put(methodName != null ? methodName.split("_")[1] : "Field_" + i, value);
                }
            }
        }
    } catch (HL7Exception e) {
        e.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("Index out of bounds: " + e.getMessage());
    }

    return fieldNames;
}


    // Method to dynamically get the segment class using reflection
    private static Class<?> getSegmentClass(String segmentName) {
        try {
            // Construct the fully qualified class name
            String packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
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
/*        try {
            String hl7Message = "MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191000||SIU^S12|12345|P|2.3\r" +
"SCH|1|1234|5678||RP|CHECKUP||||202408191100|202408191200|||45|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
"PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321\r";

*             String hl7Message = "MSH|^~\\&|HIS|RIH|EKG|EKG|20240806221120||ADT^A01|MSG00001|P|2.3\r" +
                                "PID|1||123456^^^Hospital^MR||Doe^John||19650525|M|||123 Main St^^Metropolis^IL^12345|555-1234\r" +
                                "OBX|1|ST|1234^Test1||Positive|||\r" +
                                "OBX|2|ST|5678^Test2||Negative|||\r";
*
            Parser parser = new PipeParser();
            Message message = parser.parse(hl7Message);

            String[] segmentNames = { "MSH", "PID", "OBX" };
//            Map<String, JSONObject> jsonMap = new LinkedHashMap<>();






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

* 
            for (String segmentName : segmentNames) {
                Structure[] structures = message.getAll(segmentName);
                for (int index = 0; index < structures.length; index++) {
                    Segment segment = (Segment) structures[index];
                    
                    // Retrieve segment class dynamically
                    Class<?> segmentClass = getSegmentClass(segment.getName());
                    if (segmentClass != null) {
                        Map<String, Object> fieldNames = getFieldNames(segment, segmentClass);
                        JSONObject segmentJson = new JSONObject(fieldNames);
//                        jsonMap.put(segmentName + "[" + index + "]", segmentJson);
                     jsonMap.put(segmentName +"["+ index +"]" , segmentJson);
                    }
                    }
            }*

            JSONObject jsonOutput = new JSONObject(jsonMap);
            System.out.println(jsonOutput.toString(4)); // Pretty print with indentation
        }
        } catch (Exception e) {
            e.printStackTrace();
        }


*/


try {

String hl7Message = "MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191045||SIU^S17|12347|P|2.3\r" +
"SCH|1|1234|5678||RP|CHECKUP||||202408191400|202408191500|||0|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
"PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321";

 /*   String hl7Message = "MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191000||SIU^S12|12345|P|2.3\r" +
        "SCH|1|1234|5678||RP|CHECKUP||||202408191100|202408191200|||45|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
        "PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321\r";

String hl7Message = "MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191030||SIU^S14|12346|P|2.3\r" +
"SCH|1|1234|5678||RP|CHECKUP||||202408191300|202408191400|||60|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
"PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321";
*/


    Parser parser = new PipeParser();
    Message parsedMessage = parser.parse(hl7Message);
           // Extract the MSH segment
 // Extract the MSH segment
 MSH mshSegment = (MSH) parsedMessage.get("MSH");

 // Extract the message type from MSH-9
 String messageType = mshSegment.getMessageType().getMessageType().getValue();
 String triggerEvent = mshSegment.getMessageType().getTriggerEvent().getValue();

 System.out.println("msg_type, triggerevnt: " + messageType + ", " + triggerEvent);
    // Cast the parsed message to the specific SIU_S12 type
    
    
    Map<String, JSONArray> jsonMap = new LinkedHashMap<>();
    if(messageType.equals("SIU")  && triggerEvent.equals("S14"))
    {
    SIU_S14 message = (SIU_S14) parsedMessage;
    // Process MSH segment
    processSegment(message.getMSH(), "MSH", jsonMap);

    // Process SCH segment
    processSegment(message.getSCH(), "SCH", jsonMap);

    // Process PID segment within the Patient Group (PATIENT)
    processSegment(message.getPATIENT().getPID(), "PID", jsonMap);
    }
    else if(messageType.equals("SIU")  && triggerEvent.equals("S12"))
    {
        SIU_S12 message = (SIU_S12) parsedMessage;

    // Process MSH segment
    processSegment(message.getMSH(), "MSH", jsonMap);

    // Process SCH segment
    processSegment(message.getSCH(), "SCH", jsonMap);

    // Process PID segment within the Patient Group (PATIENT)
    processSegment(message.getPATIENT().getPID(), "PID", jsonMap);
    }
    else if(messageType.equals("SIU")  && triggerEvent.equals("S17"))
    {
        SIU_S17 message = (SIU_S17) parsedMessage;

    // Process MSH segment
    processSegment(message.getMSH(), "MSH", jsonMap);

    // Process SCH segment
    processSegment(message.getSCH(), "SCH", jsonMap);

    // Process PID segment within the Patient Group (PATIENT)
    processSegment(message.getPATIENT().getPID(), "PID", jsonMap);
    }
    


    JSONObject jsonOutput = new JSONObject(jsonMap);
    System.out.println(jsonOutput.toString(4)); // Pretty print with indentation
} catch (Exception e) {
    e.printStackTrace();
}
}

private static void processSegment(Segment segment, String segmentName, Map<String, JSONArray> jsonMap) throws HL7Exception {
JSONArray segmentArray = new JSONArray();

// Retrieve segment class dynamically
Class<?> segmentClass = getSegmentClass(segment.getName());
if (segmentClass != null) {
    Map<String, Object> fieldNames = getFieldNames(segment, segmentClass);
    JSONObject segmentJson = new JSONObject(fieldNames);
    segmentArray.put(segmentJson);
}

jsonMap.put(segmentName, segmentArray);
}

    private static Structure[] getSegmentStructures(Group group, String segmentName) throws HL7Exception {
        Structure[] structures = null;
        try {
            structures = group.getAll(segmentName);  // Attempt to get the segment
        } catch (HL7Exception e) {
            // If the segment is not found directly, it might be within a subgroup
            for (String childName : group.getNames()) {
                if (group.isGroup(childName)) {
                    Group childGroup = (Group) group.get(childName);
                    structures = getSegmentStructures(childGroup, segmentName);
                    if (structures != null && structures.length > 0) {
                        return structures;
                    }
                }
            }
        }
        return structures;
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
