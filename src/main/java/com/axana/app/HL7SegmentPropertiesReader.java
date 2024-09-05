package com.axana.app;
import java.lang.reflect.Field;

import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7SegmentPropertiesReader {
    public static void main(String[] args) throws Exception {
        // Example HL7 message string (ADT_A01 message with PID segment)
        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20230821083000||ADT^A01|123456|P|2.3\r" +
                            "PID|1||123456^^^Hosp^MR||Doe^John^A||19600101|M|||123 Main St^^Hometown^NY^12345^USA||(555)555-1234|||||987-65-4320\r" +
                            "PV1|1|I|ICU^01^01^Hospital";

        // Parse the HL7 message
        Parser parser = new PipeParser();
        ADT_A01 adtMessage = (ADT_A01) parser.parse(hl7Message);

        // Extract the PID segment
        PID pidSegment = adtMessage.getPID();

        // Now you can pass the pidSegment object to the printSegmentProperties method
        printSegmentProperties(pidSegment);
    }

    public static void printSegmentProperties(Object segment) throws IllegalAccessException {
        Class<?> segmentClass = segment.getClass();
        while (segmentClass != null) {
            Field[] fields = segmentClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(segment);
                Object type = field.getType();
                System.out.println("Field name: " + field.getName() + ", Value: " + value + ", Type : " + type);
            }
            segmentClass = segmentClass.getSuperclass();
        }
    }
}
