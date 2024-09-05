import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7ReflectionRDE {

    public static void main(String[] args) throws HL7Exception {
        String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20230821083000||RDE^O11|123456|P|2.3\rPID|1||123456^^^Hosp^MR||Doe^John^A||19600101|M|||123 Main St^^Hometown^NY^12345^USA|(555)555-1234||M|C|123456789|987-65-4320";

        PipeParser parser = new PipeParser();
        Message message = parser.parse(hl7Message);

        // Example: Retrieving the PID segment and extracting the field names
        Segment pidSegment = (Segment) message.get("PID");
        Map<String, List<String>> pidFields = extractFields(pidSegment);

        System.out.println("PID Fields:");
        System.out.println(pidFields);
    }

    // Function to extract fields using reflection
    public static Map<String, List<String>> extractFields(Segment segment) {
        Map<String, List<String>> fieldsMap = new HashMap<>();

        try {
            for (int i = 1; i <= segment.numFields(); i++) {
                Type[] field = segment.getField(i);
                List<String> fieldValues = new ArrayList<>();
                if (field instanceof Composite) {
                    Composite composite = (Composite) field;
                    for (int j = 0; j < composite.numComponents(); j++) {
                        Type component = composite.getComponent(j);
                        fieldValues.add(getFieldValue(component));
                    }
                } else if (field instanceof Type) {
                    fieldValues.add(getFieldValue(field));
                }
                fieldsMap.put(segment.getName() + "-" + i, fieldValues);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving field name: " + e.getMessage());
        }

        return fieldsMap;
    }

    // Function to retrieve the value of a field using reflection
    public static String getFieldValue(Type type) {
        try {
            Method getValueMethod = type.getClass().getMethod("getValue");
            Object value = getValueMethod.invoke(type);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "Error retrieving field value: " + e.getMessage();
        }
    }
}
