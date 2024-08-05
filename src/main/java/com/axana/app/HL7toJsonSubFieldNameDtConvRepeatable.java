package com.axana.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;

public class HL7toJsonSubFieldNameDtConvRepeatable {

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Function to retrieve field names from a segment and order them
    private static Map<String, Object> getFieldNames(Structure segment) {
        Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable::extractNumberFromKey));

        try {
            for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
                String key = toSnakeCase(segment.getClass().getSimpleName()) + "." + i;
                Type[] fields = ((Segment) segment).getField(i);
                for (Type field : fields) {
                    if (field instanceof Composite) {
                        fieldNames.put(key, getSubFields((Composite) field));
                    } else {
                        String value = field.encode();
                        if (key.contains("ts")) {
                            value = convertTimestamp(value);
                        }
                        fieldNames.put(key, value);
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
        Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable::extractNumberFromKey));

        try {
            Type[] components = composite.getComponents();
            for (int i = 0; i < components.length; i++) {
                String key = toSnakeCase(composite.getClass().getSimpleName()) + "." + (i + 1);
                Type component = components[i];
                if (component instanceof Composite) {
                    subFieldNames.put(key, getSubFields((Composite) component));
                } else {
                    String value = component.encode();
                    if (key.contains("ts")) {
                        value = convertTimestamp(value);
                    }
                    subFieldNames.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subFieldNames;
    }

    // Function to convert HL7 timestamp to desired format
    private static String convertTimestamp(String hl7Timestamp) {
        SimpleDateFormat hl7Format = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat hl7DtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat desiredDtFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(hl7Timestamp.length()>8)
            {
            Date date = hl7Format.parse(hl7Timestamp);
            return desiredFormat.format(date);
            }
            else
            {
                Date date = hl7DtFormat.parse(hl7Timestamp);
                return desiredDtFormat.format(date);
            }
            
        } catch (ParseException e) {
            e.printStackTrace();
            return hl7Timestamp; // Return original if parsing fails
        }
    }

    // Helper function to extract the numeric part of a key
    private static int extractNumberFromKey(String key) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    // Function to convert HL7 message to nested JSON
    public static JSONObject hl7ToJson(String hl7Message) throws HL7Exception {
        Parser parser = new GenericParser();
        Message message = parser.parse(hl7Message);

        JSONObject jsonOutput = new JSONObject();
        Map<String, JSONArray> segmentGroups = new TreeMap<>();

        for (String segmentName : message.getNames()) {
            try {
                Structure[] segments = message.getAll(segmentName);
                String segmentKey = toSnakeCase(segmentName);

                for (Structure segment : segments) {
                    Map<String, Object> fieldNames = getFieldNames(segment);
                    JSONObject segmentJson = new JSONObject(fieldNames);

                    if (!segmentGroups.containsKey(segmentKey)) {
                        segmentGroups.put(segmentKey, new JSONArray());
                    }
                    segmentGroups.get(segmentKey).put(segmentJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, JSONArray> entry : segmentGroups.entrySet()) {
            if (entry.getValue().length() == 1) {
                jsonOutput.put(entry.getKey(), entry.getValue().get(0));
            } else {
                jsonOutput.put(entry.getKey(), entry.getValue());
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
