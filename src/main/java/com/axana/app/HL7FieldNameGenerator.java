package com.axana.app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.HL7Exception;

public class HL7FieldNameGenerator {

    // Function to retrieve field names from a segment class
    private static Map<Integer, String> getFieldNames(Class<?> segmentClass) {
        Map<Integer, String> fieldNames = new HashMap<>();
        try {
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();
            int fieldIndex = 1;

            for (Method method : methods) {
                // Check if method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Get the field name from the method name
                    String fieldName = method.getName().substring(3);
                    fieldNames.put(fieldIndex, fieldName);
                    fieldIndex++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    public static void main(String[] args) {
        try {
            // Define the segment classes to analyze
            Class<?>[] segmentClasses = { MSH.class, PID.class, NK1.class };
            Map<String, Map<Integer, String>> segmentFieldMappings = new HashMap<>();

            for (Class<?> segmentClass : segmentClasses) {
                // Get field names dynamically
                Map<Integer, String> fieldNames = getFieldNames(segmentClass);
                String segmentName = segmentClass.getSimpleName();
                segmentFieldMappings.put(segmentName, fieldNames);
            }

            // Print the field names for each segment
            for (Map.Entry<String, Map<Integer, String>> entry : segmentFieldMappings.entrySet()) {
                String segmentName = entry.getKey();
                Map<Integer, String> fieldNames = entry.getValue();
                System.out.println("'" + segmentName + "': " + fieldNames.values());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
