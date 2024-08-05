package com.axana.app;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;

public class hl7fieldname {

    // Dynamically map subfield names
    private static final Map<String, Map<Integer, String>> segmentMappings = new HashMap<>();

    // Function to dynamically retrieve subfield names using reflection
    private static Map<Integer, String> getSubfieldNames(Class<?> compositeClass) {
        Map<Integer, String> subfieldNames = new HashMap<>();
        try {
            Method[] methods = compositeClass.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName();
                    String subfieldName = toSnakeCase(methodName.substring(3));
                    String numberPart = methodName.replaceAll("[^0-9]", "");

                    if (!numberPart.isEmpty()) {
                        try {
                            int fieldIndex = Integer.parseInt(numberPart);
                            if (fieldIndex > 0 && fieldIndex < 1000) {
                                subfieldNames.put(fieldIndex, subfieldName);
                            }
                        } catch (NumberFormatException e) {
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

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Function to dynamically retrieve field names and handle nested structures
    private static Map<String, Object> getFieldNames(Segment segment) {
        Map<String, Object> fieldNames = new TreeMap<>();

        try {
            for (int i = 1; i <= segment.numFields(); i++) {
                String key = toSnakeCase(segment.getClass().getSimpleName()) + "." + i;
                Type[] fields = segment.getField(i);
                System.out.println("Fields : " + key);
                if (fields.length > 0 && fields[0] instanceof Composite) {
                    fieldNames.put(key, getSubFields((Composite) fields[0]));
                } else {
                    fieldNames.put(key, fields[0].encode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    // Function to retrieve subfields from a composite data type dynamically
    private static Map<String, Object> getSubFields(Composite composite) {
        Map<String, Object> subFieldNames = new TreeMap<>();
        try {
            Type[] components = composite.getComponents();
            Map<Integer, String> subfieldMappings = getSubfieldNames(composite.getClass());
            for (int i = 0; i < components.length; i++) {
                String key = (i + 1) + "";

                String subfieldName = subfieldMappings.getOrDefault(i + 1, toSnakeCase(composite.getClass().getSimpleName()) + "." + (i + 1));

                // Create key-value pair for subfields
                key = subfieldName;

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
                JSONObject segmentJson = new JSONObject();
                for (Map.Entry<String, Object> entry : fieldNames.entrySet()) {
                    String fieldKey = entry.getKey();
                    Object fieldValue = entry.getValue();

                    // Check if the field value is a nested structure
                    if (fieldValue instanceof Map) {
                        JSONObject subFieldJson = new JSONObject((Map<?, ?>) fieldValue);
                        segmentJson.put(toSnakeCase(fieldKey), subFieldJson);
                    } else {
                        segmentJson.put(toSnakeCase(fieldKey), fieldValue);
                    }
                }
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

        JSONObject jsonOutput = new JSONObject();
        processStructure(message, jsonOutput);
        return jsonOutput;
    }


    public static void main(String[] args) {
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
"GT1|1||Doe^John^A|123 Main St^^Anytown^CA^12345|M||555-1234\r";
        try {
            JSONObject jsonOutput = hl7ToJson(hl7Message);
            System.out.println(jsonOutput.toString(4));
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
