package com.axana.app;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.segment.IN3;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.model.Type;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class fieldnameExtraction {

    // Method to get the field names dynamically using reflection
    private static Map<String, String> getFieldNames(Class<?> segmentClass) {
        Map<String, String> fieldNames = new HashMap<>();
        try {
            // Get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String fieldNumber = method.getName().replaceAll("[^0-9]", "");
                    if (!fieldNumber.isEmpty()) {
                        // Get field name
                        String fieldName = method.getName().substring(3); // Remove 'get' prefix
                        fieldNames.put(fieldNumber, fieldName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }

    // Method to get the component names dynamically
    private static Map<String, String> getComponentNames(Type type) {
        Map<String, String> componentNames = new HashMap<>();
        try {
            // Check if the type is a composite (i.e., has components)
            if (type instanceof ca.uhn.hl7v2.model.Composite) {
                ca.uhn.hl7v2.model.Composite composite = (ca.uhn.hl7v2.model.Composite) type;
                for (int i = 1; i <= composite.getComponent(0); i++) { 
                    //   .numComponents(); i++) {
                    String componentName = composite.getComponent(i).getName();
                    componentNames.put(String.valueOf(i), componentName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return componentNames;
    }

    public static void main(String[] args) {
        try {
            // Example HL7 message
            String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202107251230||ADT^A01|12345|P|2.3|||IN3|1|InsurancePlan|InsCoName|InsCoAddress|ContactPerson|555-5555";
            
            // Parse the HL7 message
            PipeParser parser = new PipeParser();
            Message message = parser.parse(hl7Message);

            // Access the IN3 segment
            IN3 in3 = (IN3) message.get("IN3");

            // Get field names dynamically
            Map<String, String> fieldNames = getFieldNames(IN3.class);

            // Example of accessing IN3.1
            String fieldId = "IN3.1";
            String fieldNumber = fieldId.split("\\.")[1];

            // Get the value of the field
            String fieldValue = in3.getField(Integer.parseInt(fieldNumber)).encode();
            System.out.println("Field Value: " + fieldValue);

            // Get the component names
            Map<String, String> componentNames = getComponentNames(in3.getField(Integer.parseInt(fieldNumber)));
            System.out.println("Component Names: " + componentNames);

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
