package com.axana.app;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.OUL_R21;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;


public class ExtractOULR21ToJson {

    public static void main(String[] args) {
        String oulR21Message = "MSH|^~\\&|LAB|LIS|HOSPITAL|HIS|202309101200||OUL^R21|123456|P|2.4\r"
                + "PID|1||123456^^^HOSPITAL^MR||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^12345^USA||(555)555-5555\r"
                + "ORC|RE|ORD123456^LIS|ACC123456^LIS|^||CM||||202309091200\r"
                + "OBR|1|ORD123456^LIS|ACC123456^LIS|1234^COMPLETE BLOOD COUNT^L|||202309091200|202309091230|||N|||||||||F|||||||||||^^^LAB^LIS\r"
                + "OBX|1|NM|12345-6^Hemoglobin^LN||13.5|g/dL|13.0-17.0|N|||F|||202309091200|LAB^Lab Technician^L\r"
                + "OBX|2|NM|12345-7^Hematocrit^LN||40.0|%|36.0-50.0|N|||F|||202309091200|LAB^Lab Technician^L\r"
                + "OBX|3|NM|12345-8^White Blood Cell Count^LN||6.5|10^3/uL|4.5-11.0|N|||F|||202309091200|LAB^Lab Technician^L\r"
                + "OBX|4|NM|12345-9^Platelet Count^LN||250|10^3/uL|150-400|N|||F|||202309091200|LAB^Lab Technician^L\r";

        Parser parser = new PipeParser();

        try {
            // Parse the message string into an OUL_R21 object
            OUL_R21 oulR21 = (OUL_R21) parser.parse(oulR21Message);

            // Access MSH Segment
            MSH mshSegment = oulR21.getMSH() ;// .getMSH();
            System.out.println("Message Type: " + mshSegment.getMessageType().getTriggerEvent()); // .getMessageCode().getValue());

            // Access PID Segment
            PID pidSegment = oulR21.getPATIENT().getPID();
            System.out.println("Patient ID: " + pidSegment.getPatientID().getCx1_ID().getValue()); // .getIDNumber().getValue());
            System.out.println("Patient Name: " + pidSegment.getPatientName(0).getFamilyName().getSurname().getValue());

            // Access OBR and OBX Segments
            /*for (OBR obrSegment : oulR21.getORDER_OBSERVATION().getOBRAll()) {
                System.out.println("Test Ordered: " + obrSegment.getUniversalServiceIdentifier().getText().getValue());
            }

            for (OBX obxSegment : oulR21.getORDER_OBSERVATION().getOBSERVATIONAll()) {
                System.out.println("Observation Identifier: " + obxSegment.getObservationIdentifier().getIdentifier().getValue());
                System.out.println("Observation Value: " + obxSegment.getObservationValue(0).getData().toString());
            }*/

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
}
