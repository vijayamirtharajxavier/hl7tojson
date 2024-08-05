package com.axana.app;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class hl7tojsonfieldnamesubfield {

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Mapping for common subfield names
    private static Map<String, Map<Integer, String>> subfieldMappings = new HashMap<>();

    static {
        Map<Integer, String> xpnMapping = new HashMap<>();
        xpnMapping.put(1, "last_name");
        xpnMapping.put(2, "first_name");
        subfieldMappings.put("xpn", xpnMapping);
        
        // Add other mappings as needed
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

    // Function to retrieve subfields from a composite data type
    private static Map<String, Object> getSubFields(Composite composite) {
        Map<String, Object> subFieldNames = new TreeMap<>(new Comparator<String>() {
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
            Type[] components = composite.getComponents();
            for (int i = 0; i < components.length; i++) {
                String baseKey = toSnakeCase(composite.getClass().getSimpleName());
                String key = baseKey + "." + (i + 1);
                
                Map<Integer, String> mapping = subfieldMappings.get(baseKey);
                if (mapping != null && mapping.containsKey(i + 1)) {
                    key = mapping.get(i + 1);
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

    // Function to convert HL7 message to nested JSON
    public static JSONObject hl7ToJson(String hl7Message) throws HL7Exception {
        Parser parser = new GenericParser();
        Message message = parser.parse(hl7Message);

        JSONObject jsonOutput = new JSONObject();
        for (String segmentName : message.getNames()) {
            try {
                Segment segment = (Segment) message.get(segmentName);
                Map<String, Object> fieldNames = getFieldNames(segment);
                String segmentKey = toSnakeCase(segmentName);
                JSONObject segmentJson = new JSONObject(fieldNames);
                jsonOutput.put(segmentKey, segmentJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
