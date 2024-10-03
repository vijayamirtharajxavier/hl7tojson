package com.axana.app;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v23.segment.AL1;
import ca.uhn.hl7v2.model.v23.segment.DG1;
import ca.uhn.hl7v2.model.v23.segment.EVN;
import ca.uhn.hl7v2.model.v23.segment.FT1;
import ca.uhn.hl7v2.model.v23.segment.GT1;
import ca.uhn.hl7v2.model.v23.segment.MFE;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.NK1;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import ca.uhn.hl7v2.model.v23.segment.PD1;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v23.segment.PR1;
import ca.uhn.hl7v2.model.v23.segment.PRA;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.model.v23.segment.PV2;
import ca.uhn.hl7v2.model.v23.segment.ROL;
import ca.uhn.hl7v2.model.v23.segment.RXA;
import ca.uhn.hl7v2.model.v23.segment.RXR;
import ca.uhn.hl7v2.model.v23.segment.STF;
import ca.uhn.hl7v2.model.v23.segment.TXA;
import ca.uhn.hl7v2.model.v25.segment.AIG;
import ca.uhn.hl7v2.model.v25.segment.AIL;
import ca.uhn.hl7v2.model.v25.segment.AIP;
import ca.uhn.hl7v2.model.v25.segment.AIS;
import ca.uhn.hl7v2.model.v25.segment.DRG;
import ca.uhn.hl7v2.model.v25.segment.MFA;
import ca.uhn.hl7v2.model.v25.segment.PDA;

public class ExtractSegmentMainFieldSubField {

    // Function to retrieve field and subfield names from a segment class
    private static Map<String, String> getFieldAndSubFieldNames(Class<?> segmentClass) {
        Map<String, String> fieldNames = new TreeMap<>(); // Use TreeMap for sorting
        try {
            // Reflectively get all methods in the segment class
            Method[] methods = segmentClass.getDeclaredMethods();

            for (Method method : methods) {
                // Check if the method is a getter for a specific field
                if (method.getName().startsWith("get")) {
                    // Extract the field number from the method name
                    String methodName = method.getName();
                    String underscoreSeparated = methodName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();

                    // Handle the case where the string starts with an uppercase letter
                    if (Character.isUpperCase(methodName.charAt(0))) {
                        underscoreSeparated = "_" + underscoreSeparated;
                    }

                    // Extract digits as the field number
                    String fieldNumber = methodName.replaceAll("[^0-9]", "");
                    if (!fieldNumber.isEmpty()) {
                        String fieldName = methodName;
                        String segmentPrefix = segmentClass.getSimpleName();
                        String key = segmentPrefix + "." + fieldNumber;

                        // Add the main field to the map
                        fieldNames.put(key, fieldName.split("_")[1]);

                        // Check if the field is a composite and get subfields
                        Class<?> returnType = method.getReturnType();
                        if (Composite.class.isAssignableFrom(returnType)) {
                            Type[] components = ((Composite) returnType.getDeclaredConstructor().newInstance()).getComponents();
                            for (int i = 0; i < components.length; i++) {
                                if (components[i] instanceof Primitive) {
                                    String subfieldKey = key + "." + (i + 1);
                                    String subfieldName = ((Primitive) components[i]).getName();
                                    fieldNames.put(subfieldKey, subfieldName);
                                }
                            }
                        }
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
            Class<?>[] segmentClasses = { MSH.class, EVN.class, PID.class, PD1.class, PV1.class, PV2.class, OBR.class, OBX.class, AL1.class, DG1.class, GT1.class, NK1.class, PR1.class, ROL.class, ORC.class, RXA.class, RXR.class, FT1.class, PRA.class, PDA.class, NTE.class, MFE.class, MFA.class, DRG.class, AIG.class, AIP.class, AIL.class, AIS.class, STF.class, TXA.class };
            JSONObject jsonOutput = new JSONObject();

            for (Class<?> segmentClass : segmentClasses) {
                // Get field and subfield names dynamically
                Map<String, String> fieldNames = getFieldAndSubFieldNames(segmentClass);
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
