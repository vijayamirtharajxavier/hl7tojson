package com.axana.app;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.NK1;

public class HL7FieldNameGeneratorv25 {

    // Function to retrieve field names from a segment class and order them
    private static Map<Integer, String> getFieldNames(Class<?> segmentClass) {
        Map<Integer, String> fieldNames = new HashMap<>();
        try {
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();
            List<String> fieldNamesList = new ArrayList<>();
            int fieldIndex = 1;

            for (Method method : methods) {
                // Check if method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Get the field name from the method name
                    String fieldName = method.getName().substring(3);
                    fieldNamesList.add(fieldName);
                }
            }

            // Populate field names in order
            for (int i = 0; i < fieldNamesList.size(); i++) {
                fieldNames.put(i + 1, fieldNamesList.get(i));
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

            // Print the field names for each segment in order
            for (Map.Entry<String, Map<Integer, String>> entry : segmentFieldMappings.entrySet()) {
                String segmentName = entry.getKey();
                Map<Integer, String> fieldNames = entry.getValue();
                System.out.print("'" + segmentName + "': [");
                for (Map.Entry<Integer, String> fieldEntry : fieldNames.entrySet()) {
                    System.out.print(fieldEntry.getValue());
                    if (fieldEntry.getKey() != fieldNames.size()) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
