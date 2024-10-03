package com.axana.app;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.message.MDM_T02;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractMDMToJson {

    public static void main(String[] args) {
        // Example MDM_T02 HL7 message
        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202409060830||MDM^T02|123459|P|2.3\r" +
                            "EVN|T02|202409060830\r" +
                            "PID|1|123459^^^Hospital^MR|987654^^^Hospital^SS|Doe^John^A|||19650515|M|||123 Main St^^Somewhere^NY^12345^USA||(123)456-7890|||M|||123-45-6789\r" +
                            "PV1|1|I|ICU^15^D||||1234^Doctor^John^D|||SUR|||||123456^RefDoc^Mary^E|||ICU|A|||1|||||||||||||||||||||||202409050000|202409060000\r" +
                            "TXA|1|MD|1985-05-05|||||||A\r" +
                            "OBX|1|TX|12345^Discharge Summary^L||The patient is stable.||||||F";

        try {
            // Parse the HL7 message
            Parser parser = new PipeParser();
            MDM_T02 mdmMessage = (MDM_T02) parser.parse(hl7Message);

            // Convert to JSON
            Gson gson = new Gson();
            Map<String, Object> jsonMap = new HashMap<>();

            // Extract and map segments (e.g., MSH, PID, PV1, TXA, OBX)
            jsonMap.put("MSH", mdmMessage.getMSH());
            jsonMap.put("EVN", mdmMessage.getEVN());
            jsonMap.put("PID", mdmMessage.getPID());
            jsonMap.put("PV1", mdmMessage.getPV1());
            jsonMap.put("TXA", mdmMessage.getTXA());
            jsonMap.put("OBX", mdmMessage.getOBX()); // .getOBX());

            // Convert to JSON
            String jsonOutput = gson.toJson(jsonMap);
            System.out.println(jsonOutput);

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
