package com.axana.app;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v23.message.SIU_S12;
import ca.uhn.hl7v2.model.v23.message.SIU_S14;
import ca.uhn.hl7v2.model.v23.message.SIU_S17;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractORU_O01ToJson {

    // Function to retrieve field and subfield names from a segment

    private static Map<String, Object> getFieldNames(Segment segment, Class<?> segmentClass) {
        Map<String, Object> fieldNames = new LinkedHashMap<>();
        Type[] fields;
        try {
            int numFields = segment.numFields();
            for (int i = 1; i <= numFields; i++) {
                 fields = segment.getField(i);
                if (fields.length == 0) {
                    continue;
                }
                String fieldIdentifier = Integer.toString(i).trim();
                String methodName = getMethodFieldNames(segmentClass, segment.getName(), i);
                System.out.println("Processing field for segment " + segment.getName() + " : " + i + " , FieldName : " + methodName + ", Value : " + fields[0].encode());
                

            //    if(methodName.equals("getObx5_ObservationValue"))
            //    {
                    System.out.println("ccccc - obx5 " + segment.getName() + " : " + i + " , FieldName : " + methodName + ", Value : " + fields[0].encode());
                    String key = methodName != null ? methodName.split("_")[1] : "Field_" + i;
                    fieldNames.put(key,fields[0].encode());
             //   }
                
                //String methodName = findMethodNameForSubfield(segmentClass, segment.getName(), Integer.toString(i).trim());

                for (Type field : fields) {
                    if (field instanceof Composite) {
                        Composite composite = (Composite) field;
                        Map<String, Object> compositeFields = new LinkedHashMap<>();
                        Type[] components = composite.getComponents();
                                             
                        for (int k = 0; k < components.length; k++) {
                            Type component = components[k];
                            String componentIdentifier = Integer.toString(i) + "." + Integer.toString(k+1 );
                            //String submethodName = findMethodNameForSubfield(segmentClass, segment.getName(), componentIdentifier);
                            String submethodName = getMethodFieldNames(segmentClass, segment.getName(), k+1);

                            if (component instanceof Primitive) {
                                Primitive primitive = (Primitive) component;
                                String primitiveName = primitive.getName();
                                String value = primitive.encode();

                                System.out.println("primitive: " + primitive + ", primitiveName : " + primitiveName + ", value : " + value);
                                // Special handling for OBX-5.1
                                if (segment.getName().equals("OBX")) {
                      //             System.out.println("compo_id : " + componentIdentifier + ", value: " + value);
                                    
                                }
                                if (primitiveName.contains("TS") || primitiveName.contains("TSComponentOne") || primitiveName.contains("DTM") || primitiveName.contains("DT")) {
                                    value = convertTimestamp(value);
                                }
                                System.out.println("aaaa :" + componentIdentifier);
                             //   System.out.println("methodname : " + methodName +",componentIdentifier: " + componentIdentifier + ", value : " + value);
                                compositeFields.put(componentIdentifier + " (" + primitiveName + ")" +
                                        (submethodName != null ? "[" + submethodName.split("_")[1] + "]" : ""), value);
                            }
                            else
                            {
                                System.out.println("bbb :" + componentIdentifier);
                            }
                        }
    



                        if (!compositeFields.isEmpty()) {
                            System.out.println("IF -Processing field for segment " + segment.getName() + " : " + i + " , FieldName : " + methodName + ", Value : " + fields[0].encode());    
                            
                             key = methodName != null ? methodName.split("_")[1] : "Field_" + i;
                            
                            
                                fieldNames.put(key, compositeFields);
                            
    
                        }
                        else
                        {
                            System.out.println("ELS-Processing field for segment " + segment.getName() + " : " + i + " , FieldName : " + methodName + ", Value : " + fields[0]);
                              key = methodName != null ? methodName.split("_")[1] : "Field_" + i;
                             System.out.println("obx-val :" + key);    
                                fieldNames.put(key, fields[0].encode());
                            
    
                            
                        }
                    } else if (field instanceof Primitive) {
                        Primitive primitive = (Primitive) field;
                        String primitiveName = primitive.getName();
                        String value = primitive.encode();


                        if (primitiveName.contains("TS") || primitiveName.contains("DTM") || primitiveName.contains("DT") || primitiveName.contains("CM_EIP")) {
                            value = convertTimestamp(value);
                        }
    
                          key = methodName != null ? methodName.split("_")[1] : "Field_" + i;
                         System.out.println("oooo :" + key);
                         
                            fieldNames.put(key, value);
                        

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
            return null;
        }
    }

    private static String findMethodNameForSubfield(Class<?> segmentClass, String segmentName, Integer fieldIdentifier) {
        try {
            Method[] methods = segmentClass.getDeclaredMethods();
            
            //String[] parts = fieldIdentifier.split("\\.");
            String[] parts = Integer.toString(fieldIdentifier).split("\\.");
            String fieldNumber = parts[0];
            String subfieldNumber = parts.length > 1 ? parts[1] : null;
       //     System.out.println("Field Number " + fieldNumber + ", SubField Number  " + subfieldNumber );
            for (Method method : methods) {
                if (method.getName().toLowerCase().contains(segmentName.toLowerCase() + fieldNumber + "_")) {
                    if (subfieldNumber != null) {
                        Class<?> returnType = method.getReturnType();
                        Method[] compositeMethods = returnType.getDeclaredMethods();

                        for (Method compositeMethod : compositeMethods) {
                            if (compositeMethod.getName().toLowerCase().contains(subfieldNumber +"_")) {
                      //          System.out.println("Subfield Name " + compositeMethod.getName() );
                                return compositeMethod.getName();
                            }
                        }
                    } else {
                    //    System.out.println("Field Name " + method.getName());

                        return method.getName();
                    }

                 //   System.out.println("FieldName_Method : " + method.getName());
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }



    private static String getMethodFieldNames(Class<?> segmentClass, String segmentName, Integer fieldIdentifier) {
        try {
            Method[] methods = segmentClass.getDeclaredMethods();
            
            //String[] parts = fieldIdentifier.split("\\.");
            String[] parts = Integer.toString(fieldIdentifier).split("\\.");
            String fieldNumber = parts[0];
            String subfieldNumber = parts.length > 1 ? parts[1] : null;
       //     System.out.println("Field Number " + fieldNumber + ", SubField Number  " + subfieldNumber );
            for (Method method : methods) {
                if (method.getName().toLowerCase().contains(segmentName.toLowerCase() + fieldNumber + "_")) {
                    if (subfieldNumber != null) {
                        Class<?> returnType = method.getReturnType();
                        Method[] compositeMethods = returnType.getDeclaredMethods();

                        for (Method compositeMethod : compositeMethods) {
                            if (compositeMethod.getName().toLowerCase().contains(subfieldNumber +"_")) {
                      //          System.out.println("Subfield Name " + compositeMethod.getName() );
                      String compmethod = compositeMethod.getName() != null ? compositeMethod.getName().split("_")[1] : "Field_";
                                return compositeMethod.getName();
                               // return compmethod;
                            }
                        }
                    } else {
                    //    System.out.println("Field Name " + method.getName());
                    String mainmethod = method.getName() != null ? method.getName().split("_")[1] : "Field_";
                        return method.getName();
                    //return mainmethod;

                    }

                 //   System.out.println("FieldName_Method : " + method.getName());
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }




    public static void main(String[] args) {
        try {
/*           String hl7Message = "MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191045||SIU^S17|12347|P|2.3\r" +
                    "SCH|1|551234|5678||RP|CHECKUP||||202408191400|202408191500|||0|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
                    "PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321";
*/

String hl7Message="MSH|^~\\&|LABADT|MCM|IFENG|IFENG|202408261030||ORU^O01|123456|P|2.3\r" +
"PID|1||123456^^^MCM^MR||DOE^JOHN^A||19700101|M|||1234 Main St^^Metropolis^IL^60615|(123)456-7890|(123)456-7891|||123456789|123-45-6789\r" +
"PV1|1|O|ICU^02^03^MCM||||1234^Jones^Barry^M^^MD|5678^Smith^John^A^^MD|||||||||V1001^|V001|||||||||||||||||||||||||||202408261030\r" +
"ORC|RE|123456^MCM||123456^MCM||||202408261030|5678^Smith^John^A^^MD\r" +
"OBR|1|123456^MCM|123456^MCM|88304^Biopsy, skin, other than cyst/tumor^L||202408261030|202408261040|||||5678^Smith^John^A^^MD||||||123456^MCM||L\r" +
"OBX|1|ST|88304-1^Biopsy^L|1|Positive for malignancy|||||F\r" +
"OBX|2|ST|88304-2^Biopsy^L|2|No abnormal mitosis detected|||||F\r" +
"OBX|3|NM|88304-3^Biopsy Size^L|3|1.2|cm|0.0-3.0|N|||F\r" +
"OBX|4|CE|88304-4^Specimen Adequacy^L|4|Adequate|||||F\r" +
"OBX|5|TX|88304-5^Pathologist's Notes^L|5|Tissue sample adequate for diagnosis|||||F\r" +
"NTE|1|L|Specimen received in good condition. No issues noted.";



/*String hl7Message ="MSH|^~\\&|PHARM|AAA|HL7SENDER|HL7RECEIVER|20230821083000||RDE^O11|123456|P|2.3\r" +
"PID|1||123456^^^Hosp^MR||Doe^John^A||19600101|M|||123 Main St^^Hometown^NY^12345^USA||(555)555-1234|||M|C|123456789|987-65-4320\r" +
"PV1|1|I|ICU^01^01^Hospital|U|3^Doctor^John|4^Surgeon^Paul|5^Nurse^Anne|||||I|987654321|||||||11111111111\r" +
"ORC|RE|123456|123456||CM||||20230821083000|||3^Doctor^John^A\r" +
"RXO|123456^Medication Order^99MED|2||PO|7D|||PRN\r" +
"RXR|PO^Oral^HL70162\r" +
"OBX|1|NM|Glucose^Serum^L|1|7.8|mmol/L|3.9-5.6|H|||F";
*/

String className;
            Parser parser = new PipeParser();
            Message parsedMessage = parser.parse(hl7Message);

            MSH mshSegment = (MSH) parsedMessage.get("MSH");

            String messageType = mshSegment.getMessageType().getMessageType().getValue();
            String triggerEvent = mshSegment.getMessageType().getTriggerEvent().getValue();
//System.out.println(messageType + "_" + triggerEvent);
         
                className = "ca.uhn.hl7v2.model.v23.message." + messageType + "_" + triggerEvent;
            
            Class<?> clazz = Class.forName(className);
            Message messageInstance = (Message) clazz.cast(parsedMessage);

      //      System.out.println("Successfully cast to: " + messageInstance.getClass().getSimpleName());
            JsonObject jsonOutput = new JsonObject();
       //     Map<String, JsonObject> jsonMap = new LinkedHashMap<>();
        
            if (messageType.equals("SIU") && triggerEvent.equals("S14")) {
                SIU_S14 message = (SIU_S14) parsedMessage;
                processSegment(message.getMSH(), "MSH", jsonOutput);
                processSegment(message.getSCH(), "SCH", jsonOutput);
                processSegment(message.getPATIENT().getPID(), "PID", jsonOutput);
            } else if (messageType.equals("SIU") && triggerEvent.equals("S12")) {
                SIU_S12 message = (SIU_S12) parsedMessage;
                processSegment(message.getMSH(), "MSH", jsonOutput);
                processSegment(message.getSCH(), "SCH", jsonOutput);
                processSegment(message.getPATIENT().getPID(), "PID", jsonOutput);
            } else if (messageType.equals("SIU") && triggerEvent.equals("S17")) {
                SIU_S17 message = (SIU_S17) parsedMessage;
                processSegment(message.getMSH(), "MSH", jsonOutput);
                processSegment(message.getSCH(), "SCH", jsonOutput);
                processSegment(message.getPATIENT().getPID(), "PID", jsonOutput);
            }
            else if (messageType.equals("ORU") && triggerEvent.equals("R01")) {
                ORU_R01 message = (ORU_R01) parsedMessage;
             //   JsonObject jsonOutput = new JsonObject();
                
                processSegment(message.getMSH(), "MSH", jsonOutput);
                processSegment(message.getPATIENT_RESULT().getPATIENT().getPID(), "PID", jsonOutput);
                processSegment(message.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1(), "PV1", jsonOutput);
                processSegment(message.getPATIENT_RESULT().getORDER_OBSERVATION().getORC(), "ORC", jsonOutput);
                processSegment(message.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR(), "OBR", jsonOutput);

                JsonArray obxArray = new JsonArray();
                for (int i = 0; i < message.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATIONReps(); i++) {
                    JsonObject obxSegmentJson = new JsonObject();
                    processSegment(message.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(i).getOBX(), "OBX", obxSegmentJson);
                    obxArray.add(obxSegmentJson);
                }
                jsonOutput.add("Observations", obxArray);
                processSegment(message.getPATIENT_RESULT().getORDER_OBSERVATION().getNTE(), "NTE", jsonOutput);
                
         //       System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonOutput));
            }    

//            for (Map.Entry<String, JsonArray> entry : jsonMap.entrySet()) {
  //              jsonOutput.add(entry.getKey(), entry.getValue());
    //        }
//            System.out.println(jsonOutput.toString());
        // Create a Gson instance with pretty printing enabled
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonOutput));

        } catch (HL7Exception | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void processSegment(Segment segment, String segmentName, JsonObject jsonObject) throws HL7Exception {
        JsonObject segmentJson = new JsonObject();
    
        // Retrieve segment class dynamically
        Class<?> segmentClass = getSegmentClass(segment.getName());
        if (segmentClass != null) {
            Map<String, Object> fieldNames = getFieldNames(segment, segmentClass);
      //      System.out.println("Main-fieldName: " + fieldNames);

            for (Map.Entry<String, Object> entry : fieldNames.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    JsonObject nestedObject = new JsonObject();
                    Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                    for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                        nestedObject.addProperty(nestedEntry.getKey(), (String) nestedEntry.getValue());
                    }
                    segmentJson.add(entry.getKey(), nestedObject);
                } else {
                    segmentJson.addProperty(entry.getKey(), (String) entry.getValue());
                }
            }
        }
    
        jsonObject.add(segmentName, segmentJson);
    }
    
    // Function to convert HL7 TS, DTM, and DT fields into a standard timestamp
    private static String convertTimestamp(String hl7Timestamp) {
        try {
            SimpleDateFormat hl7DateFormat;
            if (hl7Timestamp.length() == 8) { // YYYYMMDD
                hl7DateFormat = new SimpleDateFormat("yyyyMMdd");
            } else if (hl7Timestamp.length() == 12) { // YYYYMMDDHHMM
                hl7DateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            } else if (hl7Timestamp.length() == 14) { // YYYYMMDDHHMMSS
                hl7DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            } else {
                return hl7Timestamp; // Return the original value if it doesn't match expected formats
            }

            Date date = hl7DateFormat.parse(hl7Timestamp);
            SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            return standardDateFormat.format(date);
        } catch (ParseException e) {
            return hl7Timestamp; // Return the original value in case of parsing failure
        }
    }
}
