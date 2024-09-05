package com.axana.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractRDEToJson_allfields {
    public static void main(String[] args) {
        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20230821083000||RDE^O11|123456|P|2.3\r" +
                            "PID|1||123456^^^Hosp^MR||Doe^John^A||19600101|M|||123 Main St^^Hometown^NY^12345^USA||(555)555-1234|||M|C|123456789|987-65-4320\r" +
                            "PV1|1|I|ICU^01^01^Hospital|U|3^Doctor^John|4^Surgeon^Paul|5^Nurse^Anne|||||I|987654321|||||||11111111111\r" +
                            "ORC|RE|123456|123456||CM||||20230821083000|||3^Doctor^John^A\r" +
                            "RXO|123456^Medication Order^99MED|2||PO|7D|||PRN\r" +
                            "RXR|PO^Oral^HL70162\r" +
                            "OBX|1|NM|Glucose^Serum^L|1|7.8|mmol/L|3.9-5.6|H|||F";

        Parser parser = new PipeParser();

        try {
            Message message = parser.parse(hl7Message);
            JsonObject jsonOutput = parseMessageToJson(message);
            System.out.println(jsonOutput.toString());
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }

    private static JsonObject parseMessageToJson(Message message) throws HL7Exception {
        JsonObject jsonObject = new JsonObject();

        for (String segmentName : message.getNames()) {
            JsonArray segmentArray = new JsonArray();

            for (Structure structure : message.getAll(segmentName)) {
                if (structure instanceof Segment) {
                    Segment segment = (Segment) structure;
                    JsonObject segmentJson = new JsonObject();

                    int fieldNum = 1;
                    while (fieldNum <= segment.numFields()) {
                        try {
                            Type[] fieldRepetitions = segment.getField(fieldNum);
                            JsonArray fieldArray = new JsonArray();

                            for (Type field : fieldRepetitions) {
                                JsonObject fieldObject = new JsonObject();

                                // Extract components and subcomponents
                                String[] subComponents = field.encode().split("\\^");
                                for (int i = 0; i < subComponents.length; i++) {
                                    fieldObject.addProperty("subcomponent-" + (i + 1), subComponents[i]);
                                }

                                fieldArray.add(fieldObject);
                            }

                            if (fieldArray.size() > 0) {
                                segmentJson.add(segmentName + "-" + fieldNum, fieldArray);
                            }

                            fieldNum++;
                        } catch (HL7Exception e) {
                            System.out.println("Error processing field " + fieldNum + " in segment " + segmentName);
                            break;
                        }
                    }
                    segmentArray.add(segmentJson);
                }
            }

            jsonObject.add(segmentName, segmentArray);
        }
        return jsonObject;
    }
}
