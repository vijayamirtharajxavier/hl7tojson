package com.axana.app;
import java.lang.reflect.Method;

public class HL7Reflection {

    public static void main(String[] args) {
        String segmentName = "OBX";
        String fieldIdentifier = "1"; // Example for FamilyName

        // Assume PID is the class that corresponds to the segment
      //  String packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
      //  String className = packageName + "." + segmentName;
      //  Class<?> segmentClass = PID.class;
       Class<?> segmentClass = getSegmentClass(segmentName);

        String methodName = findMethodNameForSubfield(segmentClass, segmentName,fieldIdentifier);

        if (methodName != null) {
            System.out.println("Method Name for " + fieldIdentifier + ": " + methodName);
        } else {
            System.out.println("No method found for field: " + fieldIdentifier);
        }
    }

    // Method to dynamically get the segment class using reflection
    private static Class<?> getSegmentClass(String segmentName) {
        try {
            // Construct the fully qualified class name
            String packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
            String className = packageName + "." + segmentName;
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String findMethodNameForSubfield(Class<?> segmentClass,String segName, String fieldIdentifier) {
        try {
            Method[] methods = segmentClass.getDeclaredMethods();

            // Split the identifier to find field and subfield numbers
            String[] parts = fieldIdentifier.split("\\.");
            String fieldNumber = parts[0];
            String subfieldNumber = parts.length > 1 ? parts[1] : null;

            for (Method method : methods) {
                // Check if the method corresponds to the field
                if (method.getName().toLowerCase().contains(segName.toLowerCase() + fieldNumber)) {
                    // If it's a composite, search for the subfield method
                    if (subfieldNumber != null) {
                        // Check if the return type is another class representing the composite
                        Class<?> returnType = method.getReturnType();
                        Method[] compositeMethods = returnType.getDeclaredMethods();

                        for (Method compositeMethod : compositeMethods) {
                            if (compositeMethod.getName().toLowerCase().contains(subfieldNumber)) {
                                return compositeMethod.getName();
                            }
                        }
                    } else {
                        // Return the method name if there's no subfield
                        return method.getName();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}