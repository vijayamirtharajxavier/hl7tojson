package com.axana.app;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class HL7ToJsonParserORU_O01 {
    
    public static void main(String[] args) {
        String hl7Message = "YOUR_HL7_MESSAGE_HERE"; // ORU_O01 HL7 message

        try {
            // Create a PipeParser with the HAPI HL7 library
            PipeParser parser = new PipeParser(new CanonicalModelClassFactory("2.3"));
            
            // Parse the HL7 message string into an ORU_O01 message object
            Message message = parser.parse(hl7Message);
            ORU_O01 oruMessage = (ORU_O01) message;
            
            // Convert ORU_O01 message to JSON
            String jsonResult = convertORUMessageToJSON(oruMessage);
            System.out.println("Converted JSON: " + jsonResult);

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertORUMessageToJSON(ORU_O01 oruMessage) throws HL7Exception {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        
        // Extract MSH Segment
        jsonObject.add("MSH", extractSegment(oruMessage.getMSH()));
        
        // Extract PID Segment (Patient Information)
        jsonObject.add("PID", extractSegment(oruMessage.getRESPONSE().getPATIENT().getPID()));
        
        // Extract OBX Segment (Observation/Result)
        if (oruMessage.getRESPONSE().getORDER_OBSERVATION().getOBSERVATIONReps() > 0) {
            jsonObject.add("OBX", extractSegment(oruMessage.getRESPONSE().getORDER_OBSERVATION().getOBSERVATION(0).getOBX()));
        }

        return gson.toJson(jsonObject);
    }

    private static JsonObject extractSegment(Object segment) {
        Map<String, String> fieldsMap = new HashMap<>();
        
        try {
            // Use reflection to extract all fields from the HL7 segment dynamically
            var fields = segment.getClass().getDeclaredMethods();

            for (var field : fields) {
                if (field.getName().startsWith("get")) {
                    var value = field.invoke(segment);
                    if (value != null) {
                        fieldsMap.put(field.getName().substring(3), value.toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        return gson.toJsonTree(fieldsMap).getAsJsonObject();
    }
}
