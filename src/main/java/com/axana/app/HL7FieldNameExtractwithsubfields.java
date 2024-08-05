package com.axana.app;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import ca.uhn.hl7v2.model.v23.segment.DG1;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NK1;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.datatype.CX;

public class HL7FieldNameExtractwithsubfields {

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // Function to retrieve field names from a segment class and order them
    private static Map<String, Object> getFieldNames(Class<?> segmentClass) {
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
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                // Check if method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Extract the field number from the method name
                    String methodName = method.getName();
                    String fieldNumber = methodName.replaceAll("[^0-9]", ""); // Extract digits
                    if (!fieldNumber.isEmpty()) {
                        // Invoke the method to get the field value
                        Object fieldInstance = method.invoke(segmentClass.getDeclaredConstructor().newInstance());
                        String fieldName = methodName; // Get the method name
                        String segmentPrefix = segmentClass.getSimpleName();
                        String key = toSnakeCase(segmentPrefix) + "." + fieldNumber;
                        
                        // If the field is a complex type, recursively get its subfields
                        if (fieldInstance != null && fieldInstance.getClass().getPackage().getName().contains("datatype")) {
                            Map<String, Object> subFields = getSubFields(fieldInstance.getClass());
                            fieldNames.put(key, subFields);
                        } else {
                            fieldNames.put(key, toSnakeCase(fieldName.split("_")[1]));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    // Function to retrieve subfields from a complex datatype class
    private static Map<String, Object> getSubFields(Class<?> dataTypeClass) {
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
            Method[] methods = dataTypeClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName();
                    String subFieldNumber = methodName.replaceAll("[^0-9]", "");
                    if (!subFieldNumber.isEmpty()) {
                        String subFieldName = methodName;
                        String key = toSnakeCase(dataTypeClass.getSimpleName()) + "." + subFieldNumber;
                        subFieldNames.put(key, toSnakeCase(subFieldName.split("_")[1]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subFieldNames;
    }

    public static void main(String[] args) {
        try {
            // Define the segment classes to analyze
            Class<?>[] segmentClasses = { MSH.class, PID.class, NK1.class, DG1.class };
            JSONObject jsonOutput = new JSONObject();

            for (Class<?> segmentClass : segmentClasses) {
                // Get field names dynamically
                Map<String, Object> fieldNames = getFieldNames(segmentClass);
                String segmentName = toSnakeCase(segmentClass.getSimpleName());

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
