package com.axana.app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import ca.uhn.hl7v2.model.v23.segment.DG1;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NK1;
import ca.uhn.hl7v2.model.v23.segment.PID;

public class HL7FieldNameGeneratorJson {

    // Function to retrieve field names from a segment class and order them
    private static Map<String, String> getFieldNames(Class<?> segmentClass) {
        Map<String, String> fieldNames = new HashMap<>();
        try {
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                // Check if method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Extract the field number from the method name
                    String methodName = method.getName();
                    String fieldNumber = methodName.replaceAll("[^0-9]", ""); // Extract digits
                    //System.out.println("methName : " + methodName);
                    if (!fieldNumber.isEmpty()) {
                        String fieldName =methodName; //methodName.substring(3); // Remove 'get' prefix
                        
                        String segmentPrefix = segmentClass.getSimpleName();
                        String key = segmentPrefix + "." + fieldNumber;
                        fieldNames.put(key, fieldName.split("_")[1]);
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
            // Define the segment classes to analyze
            Class<?>[] segmentClasses = { MSH.class, PID.class, NK1.class , DG1.class };
            JSONObject jsonOutput = new JSONObject();

            for (Class<?> segmentClass : segmentClasses) {
                // Get field names dynamically
                Map<String, String> fieldNames = getFieldNames(segmentClass);
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
