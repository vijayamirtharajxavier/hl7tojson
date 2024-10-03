package com.axana.app;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.SIU_S12;
import ca.uhn.hl7v2.model.v23.segment.SCH;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7ParserSIU {
    public static void main(String[] args) {
        String hl7Message="MSH|^~\\&|MESA_OP|XYZ_HOSPITAL|iFW|ABC_HOSPITAL|20110613061611||SIU^S12|24916560|P|2.3||||||\r" + //
        "SCH|10345^10345|2196178^2196178|||10345|OFFICE^Office visit|reason for the appointment|OFFICE|60|m|^^60^20110617084500^20110617093000|||||9^DENT^ARTHUR^||||9^DENT^COREY^|||||Scheduled\r" + //
        "PID|1||42||SMITH^PAUL||19781012|M|||1 Broadway Ave^^Fort Wayne^IN^46804||(260)555-1234|||S||999999999|||||||||||||||||||||\r" + //
        "PV1|1|O|||||1^Smith^Miranda^A^MD^^^^|2^Withers^Peter^D^MD^^^^||||||||||||||||||||||||||||||||||||||||||99158||\r" + //
        "RGS|1|A\r" + //
        "AIG|1|A|1^White, Charles|D^^\r" + //
        "AIL|1|A|OFFICE^^^OFFICE|^Main Office||20110614084500|||45|m^Minutes||Scheduled\r" + //
        "AIP|1|A|1^White^Charles^A^MD^^^^|D^White, Douglas||20110614084500|||45|m^Minutes||Scheduled";

        
        PipeParser parser = new PipeParser();
        
        try {
            // Parse the message
            Message message = parser.parse(hl7Message);
            
            // Cast to SIU_S12 or specific SIU message type
            if (message instanceof SIU_S12) {
                SIU_S12 siuMessage = (SIU_S12) message;
              //  extractSIUMessageDetails(siuMessage);
                  // Extract SCH segment data
                extractSCHSegment(siuMessage.getSCH());
                
            } else {
                System.out.println("Unsupported message type");
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }
    



 // Method to extract SCH segment fields
 private static void extractSCHSegment(SCH sch) {
     // Placer Appointment ID
     String placerAppointmentID = sch.getPlacerAppointmentID().getEntityIdentifier().getValue();
     System.out.println("Placer Appointment ID: " + placerAppointmentID);
     // Filler Appointment ID
     String fillerAppointmentID = sch.getFillerAppointmentID().getEntityIdentifier().getValue();
     System.out.println("Filler Appointment ID: " + fillerAppointmentID);
     // Appointment Reason
     String appointmentReason = sch.getAppointmentReason().getText().getValue();
     System.out.println("Appointment Reason: " + appointmentReason);
     // Appointment Type
     String appointmentType = sch.getAppointmentType().getIdentifier().getValue();
     System.out.println("Appointment Type: " + appointmentType);
     // Appointment Duration
     String appointmentDuration = sch.getAppointmentDuration().getValue();
     System.out.println("Appointment Duration: " + appointmentDuration);
     // Appointment Start Date/Time
     String appointmentStartDateTime = sch.getAppointmentTimingQuantity(0).getStartDateTime().getTimeOfAnEvent().getValue();
     System.out.println("Appointment Start Date/Time: " + appointmentStartDateTime);
     // Appointment End Date/Time
     String appointmentEndDateTime = sch.getAppointmentTimingQuantity(0).getEndDateTime().getTimeOfAnEvent().getValue();
     System.out.println("Appointment End Date/Time: " + appointmentEndDateTime);
}


}
