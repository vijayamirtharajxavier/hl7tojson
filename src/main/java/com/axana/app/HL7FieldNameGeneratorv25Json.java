package com.axana.app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import ca.uhn.hl7v2.model.v25.segment.DG1;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;



public class HL7FieldNameGeneratorv25Json {

    private static Map<String, String> getFieldNames(Class<?> segmentClass, boolean isRepeatable) {
        Map<String, String> fieldNames = new TreeMap<>();
        try {
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName();
                    String fieldNumber = methodName.replaceAll("[^0-9]", ""); // Extract digits
                    String subfieldNumber = "";
                    String fieldName = methodName.substring(3); // Remove 'get' prefix
                    fieldName = fieldName.replace("_", " "); // Replace underscores with spaces

                    if (fieldNumber.contains(".")) {
                        String[] parts = fieldNumber.split("\\.");
                        fieldNumber = parts[0];
                        subfieldNumber = parts[1];
                    }

                    if (!fieldNumber.isEmpty()) {
                        String segmentPrefix = segmentClass.getSimpleName();
                        String key;

                        if (isRepeatable) {
                            key = segmentPrefix  + fieldNumber + "." + subfieldNumber;
                        } else {
                            key = segmentPrefix  + fieldNumber + "." + (subfieldNumber.isEmpty() ? "" : subfieldNumber);
                        }

                        // Format field name if it contains "Reps" or similar
                        fieldName = fieldName.replaceAll("Reps$", "");

                        fieldNames.put(key, fieldName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    public static void main(String[] args) {
        try {
            // Add segment classes and their repeatable status
            Map<Class<?>, Boolean> segments = new HashMap<>();
            segments.put(MSH.class, false); // Non-repeatable
            segments.put(PID.class, false); // Non-repeatable
            segments.put(DG1.class, true); // Repeatable
            segments.put(OBX.class, true); // Repeatable
            segments.put(NK1.class, false); // Non-repeatable

            JSONObject jsonOutput = new JSONObject();

            for (Map.Entry<Class<?>, Boolean> entry : segments.entrySet()) {
                Class<?> segmentClass = entry.getKey();
                boolean isRepeatable = entry.getValue();

                Map<String, String> fieldNames = getFieldNames(segmentClass, isRepeatable);
                String segmentName = segmentClass.getSimpleName();

                // Convert to JSON object
                JSONObject segmentJson = new JSONObject(fieldNames);
                jsonOutput.put(segmentName, segmentJson);
            }

            // Print JSON output
            System.out.println(jsonOutput.toString(4)); // Pretty print with indentation

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
