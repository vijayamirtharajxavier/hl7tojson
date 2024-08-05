package com.axana.app;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hlmainfieldsubfiledFinal {

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Function to dynamically retrieve subfield names using reflection
    private static Map<Integer, String> getSubfieldNames(Class<?> compositeClass) {
        Map<Integer, String> subfieldNames = new HashMap<>();
        try {
            Method[] methods = compositeClass.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName();
                    String subfieldName = toSnakeCase(methodName.substring(3));
//System.out.println("method nm: " + methodName);
                    // Extract numeric part from method name
                    String numberPart = methodName.replaceAll("[^0-9]", "");
                    
                    // Ensure the number part is not empty and within a reasonable range
                    if (!numberPart.isEmpty()) {
                        try {
                            int fieldIndex = Integer.parseInt(numberPart);
                            // Check if the field index is within a reasonable range
                            if (fieldIndex > 0 && fieldIndex < 1000) {
                                subfieldNames.put(fieldIndex, subfieldName);
                            }
                        } catch (NumberFormatException e) {
                            // Ignore numbers that cannot be parsed into an integer
                            System.err.println("Ignoring invalid field index: " + numberPart);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subfieldNames;
    }

    // Function to retrieve field names from a segment and order them
    private static Map<String, Object> getFieldNames(Segment segment) {
        Map<String, Object> fieldNames = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher1 = pattern.matcher(key1);
                Matcher matcher2 = pattern.matcher(key2);
                if (matcher1.find() && matcher2.find()) {
                    Integer number1 = Integer.valueOf(matcher1.group());
                    Integer number2 = Integer.valueOf(matcher2.group());
                    return number1.compareTo(number2);
                }
                return key1.compareTo(key2);
            }
        });

        try {
            for (int i = 1; i <= segment.numFields(); i++) {
                String key = toSnakeCase(segment.getClass().getSimpleName()) + "." + i;
                
                Type[] fields = segment.getField(i);
                for (int j = 0; j < fields.length; j++) {
                    Type field = fields[j];
                    if (field instanceof Composite) {
                        //System.out.println(field);
                        fieldNames.put(key, getSubFields((Composite) field));
                        //System.out.println(getSubFields((Composite) field));
                    } else {
                        fieldNames.put(key, field.encode());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    // Function to retrieve subfields from a composite data type dynamically
    private static Map<String, Object> getSubFields(Composite composite) {
        Map<String, Object> subFieldNames = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher1 = pattern.matcher(key1);
                Matcher matcher2 = pattern.matcher(key2);
//                System.out.println("key 1: " + key1.substring(3));
    //            System.out.println("key 2: " + key2.substring(3));
  //              System.out.println(key1.substring(key1.lastIndexOf("_") + -1));
//                System.out.println(key2.substring(key2.lastIndexOf("_") + -1));

                String separator ="_";
                int sepPos = key1.indexOf(separator);
                if (sepPos == -1) {
                   System.out.println("");
                }
//                System.out.println("Substring after separator = "+key1.substring(sepPos + separator.length()));
  //              String key_1=key1.substring(sepPos + separator.length());
    //            String key_2=key2.substring(sepPos + separator.length());
    //            Matcher matcher_1 = pattern.matcher(key_1);
     //           Matcher matcher_2 = pattern.matcher(key_2);
                if (matcher1.find() && matcher2.find()) {
                    Integer number1 = Integer.valueOf(matcher1.group());
                    Integer number2 = Integer.valueOf(matcher2.group());
                    return number1.compareTo(number2);
                }
                return key1.compareTo(key2);
            }
        });

        try {
            Type[] components = composite.getComponents();
            Map<Integer, String> subfieldMappings = getSubfieldNames(composite.getClass());
            for (int i = 0; i < components.length; i++) {
                String baseKey = toSnakeCase(composite.getClass().getSimpleName());
                String key = baseKey + "." + (i + 1);

                if (subfieldMappings.containsKey(i + 1)) {
                    key = subfieldMappings.get(i + 1);
                    String separator ="_";
                    int sepPos = key.indexOf(separator);
                    if (sepPos == -1) {
                       System.out.println("");
                    }
                        key=key.substring(sepPos + separator.length());

                }

                Type component = components[i];
                if (component instanceof Composite) {
                    subFieldNames.put(key, getSubFields((Composite) component));
                } else {
                    subFieldNames.put(key, component.encode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subFieldNames;
    }

    // Recursive function to handle both segments and groups
    private static void processStructure(Group structure, JSONObject jsonOutput) throws HL7Exception {
        for (String name : structure.getNames()) {
            Structure component = structure.get(name);
            if (component instanceof Segment) {
                Segment segment = (Segment) component;
                Map<String, Object> fieldNames = getFieldNames(segment);
                String segmentKey = toSnakeCase(segment.getClass().getSimpleName());
                JSONObject segmentJson = new JSONObject(fieldNames);
          //      System.out.println("sg_key: " + segmentKey);
                jsonOutput.put(segmentKey, segmentJson);
            } else if (component instanceof Group) {
                Group group = (Group) component;
                processStructure(group, jsonOutput);
            }
        }
    }

    // Function to convert HL7 message to nested JSON
    public static JSONObject hl7ToJson(String hl7Message) throws HL7Exception {
        Parser parser = new GenericParser();
        Message message = parser.parse(hl7Message);
System.out.println("msg : " + message);
        JSONObject jsonOutput = new JSONObject();
        processStructure(message, jsonOutput);
        return jsonOutput;
    }

    public static void main(String[] args) {
        //        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202103011230||ADT^A01|12345|P|2.3\r" +
         //                           "PID|1||123456^^^Hospital^MR||Doe^John^A||19600101|M|||123 Main St^^Anytown^CA^12345||555-1234|||||||\r" +
         //                           "NK1|1|Doe^Jane^A|SPO||||555-5678\r" +
          //                          "DG1|1||I10|Hypertension||F";
        
                String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202107251230||ADT^A01|12345|P|2.3\r" +
        "EVN|A01|202107251230|||\r" +
        "PID|1||123456^^^Hospital^MR||Doe^John^A||19600101|M|||123 Main St^^Anytown^CA^12345||555-1234|||||||\r" +
        "NK1|1|Doe^Jane^A|SPO||||555-5678\r" +
        "PV1|1|I|ICU^01^01^Hospital||||1234^Smith^John^A|||SUR||||ADM||1234567^Doe^Jane^A||||||||||||||||||||||||||||202107251230\r" +
        "PV2|||ICU|||||||||||||||||||||||||||||202107251230\r" +
        "OBX|1|NM|12345-6^Heart Rate^LN||70|bpm|60-100|N|||F\r" +
        "OBX|2|NM|12346-7^Blood Pressure^LN|||120/80|mmHg|90-140||N|||F\r" +
        "AL1|1||^Penicillin||Rash\r" +
        "DG1|1||I10|Hypertension||20210725|A\r" +
        "DG1|2||E11|Type 2 Diabetes||20210725|A\r" +
        "PR1|1||1234^Appendectomy||20210725|John Smith\r" +
        "GT1|1||Doe^John^A|123 Main St^^Anytown^CA^12345|M||555-1234\r" +
        "IN1|1|12345|Aetna||123 Main St^^Anytown^CA^12345|John Doe||||\r" +
        "IN2|1|Doe^Jane^A|SPO|555-5678";
        
        
                try {
                    JSONObject jsonOutput = hl7ToJson(hl7Message);
                    System.out.println(jsonOutput.toString(4));
                } catch (HL7Exception e) {
                    e.printStackTrace();
                }
            }
        }
        