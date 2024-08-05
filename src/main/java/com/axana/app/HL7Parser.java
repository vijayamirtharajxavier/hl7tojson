package com.axana.app;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;

public class HL7Parser {

    public static void main(String[] args) {
        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202103071200||ADT^A01|123456|P|2.5\r"
                          + "PID|1|12345|67890||Doe^John^A||19900101|M|||123 Main St^^Anytown^PA^12345||(123)456-7890|||S||123456789|987-65-4321\r"
                          + "NK1|1|Doe^Jane^A|SPO||(123)456-7891||EC\r"
                          + "NK1|2|Smith^John^B|FRI||(123)456-7892||EC\r";

        try {
            Parser parser = new PipeParser();
            Message message = parser.parse(hl7Message);

            // Use Terser to navigate through the message
            Terser terser = new Terser(message);

            // Define a map to hold segment field mappings
            Map<String, Map<Integer, String>> segmentFieldMappings = new HashMap<>();

            // Loop through segments
            String[] segmentNames = {"MSH", "PID", "NK1"};
            for (String segmentName : segmentNames) {
                Map<Integer, String> fieldNames = new HashMap<>();
                int fieldNum = 1;
                while (true) {
                    try {
                        String fieldValue = terser.get(segmentName + "-" + fieldNum);
                        if (fieldValue == null) break;
                        fieldNames.put(fieldNum, fieldValue);
                        fieldNum++;
                    } catch (HL7Exception e) {
                        break;
                    }
                }
                segmentFieldMappings.put(segmentName, fieldNames);
            }

            // Print the field mappings
            for (Map.Entry<String, Map<Integer, String>> entry : segmentFieldMappings.entrySet()) {
                String segmentName = entry.getKey();
                Map<Integer, String> fieldNames = entry.getValue();
                System.out.println("Segment: " + segmentName);
                for (Map.Entry<Integer, String> fieldEntry : fieldNames.entrySet()) {
                    System.out.println("  Field " + fieldEntry.getKey() + ": " + fieldEntry.getValue());
                }
            }

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
