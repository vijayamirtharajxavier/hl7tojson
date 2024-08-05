package com.axana.app;
import java.lang.reflect.Method;
import java.util.Comparator;
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

public class HL7ToJsonWithDynamicFieldNames {

    // Convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Dynamically retrieve field descriptions using reflection
    private static Map<Integer, String> getFieldDescriptions(Class<?> segmentClass) {
        Map<Integer, String> fieldDescriptions = new HashMap<>();
        try {
            Method[] methods = segmentClass.getMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName();
                    String description = toSnakeCase(methodName.substring(3));
                    String numberPart = methodName.replaceAll("[^0-9]", "");
                    if (!numberPart.isEmpty()) {
                        try {
                            int fieldIndex = Integer.parseInt(numberPart);
                            if (fieldIndex > 0 && fieldIndex < 1000) {
                                fieldDescriptions.put(fieldIndex, description);
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
        return fieldDescriptions;
    }

    private static Map<String, Object> getFieldNames(Segment segment) {
        Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
        Class<?> segmentClass = segment.getClass();
        Map<Integer, String> descriptions = getFieldDescriptions(segmentClass);

        try {
            for (int i = 1; i <= segment.numFields(); i++) {
                String key = descriptions.getOrDefault(i, "field_" + i);
                Type[] fields = segment.getField(i);
                for (Type field : fields) {
                    if (field instanceof Composite) {
                        fieldNames.put(key, getSubFields((Composite) field));
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

    private static Map<String, Object> getSubFields(Composite composite) {
        Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
        Map<Integer, String> subfieldMappings = getFieldDescriptions(composite.getClass());

        try {
            Type[] components = composite.getComponents();
            for (int i = 0; i < components.length; i++) {
                String key = subfieldMappings.getOrDefault(i + 1, "subfield_" + (i + 1));
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

    private static void processStructure(Group structure, JSONObject jsonOutput) throws HL7Exception {
        for (String name : structure.getNames()) {
            Structure component = structure.get(name);
            if (component instanceof Segment) {
                Segment segment = (Segment) component;
                Map<String, Object> fieldNames = getFieldNames(segment);
                String segmentKey = toSnakeCase(segment.getClass().getSimpleName());
                JSONObject segmentJson = new JSONObject(fieldNames);
                jsonOutput.put(segmentKey, segmentJson);
            } else if (component instanceof Group) {
                Group group = (Group) component;
                processStructure(group, jsonOutput);
            }
        }
    }

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
