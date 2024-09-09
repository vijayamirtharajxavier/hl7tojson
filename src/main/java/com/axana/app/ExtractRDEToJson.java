package com.axana.app;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

public class ExtractRDEToJson {
    public static void main(String[] args) {

/*String hl7Message ="MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202409060830||ORU^R03|123456|P|2.3\r" +
"PID|1|123456^^^Hospital^MR|987654^^^Hospital^SS|Doe^John^A^III||19650515||||123 Main St^^Somewhere^NY^12345^USA||(123)456-7890|||M|||123-45-6789\r" +
"PV1|1|I|ICU^15^D||||1234^Doctor^John^D|||SUR|||||123456^RefDoc^Mary^E|||ICU|A|||1|||||||||||||||||||||||202409050000|202409060000\r" +
"ORC|RE|567890||112233|CM||||202409060800|||1234^Doctor^John^D\r" +
"OBR|1|567890|112233|12345^CBC Panel^L|||202409060800|||||||||1234^Doctor^John^D|||||||\r" +
"OBX|1|NM|1234-5^WBC^L|1|7.2|10^9/L|4.0-10.0|N|||F\r" +
"OBX|2|NM|5678-9^HGB^L|1|14.0|g/dL|13.5-17.5|N|||F";*/

       /*String hl7Message = "MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20230821083000||RDE^O11|123456|P|2.3\r" +
                            "PID|1||123456^^^Hosp^MR||Doe^John^A||19600101|M|||123 Main St^^Hometown^NY^12345^USA||(555)555-1234|||M|C|123456789|987-65-4320\r" +
                            "PV1|1|I|ICU^01^01^Hospital|U|3^Doctor^John|4^Surgeon^Paul|5^Nurse^Anne|||||I|987654321|||||||11111111111\r" +
                            "ORC|RE|123456|123456||CM||||20230821083000|||3^Doctor^John^A\r" +
                            "RXO|123456^Medication Order^99MED|2||PO|7D|||PRN\r" +
                            "RXR|PO^Oral^HL70162\r" +
                            "OBX|1|NM|Glucose^Serum^L|1|7.8|mmol/L|3.9-5.6|H|||F\r" +
                            "OBX|2|NM|Glucose^Systolic^L|1|9.8|mmol/L|10.9-12.6|H|||F"; */
String hl7Message="MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202408291200||RDE^O11|123456|P|2.3\r" +
"PID|1|78141|80901|12005|Khalil^Abdul^^BB^CC^EE^D|C|19370831|M|D|E|4612 Washington Blvd^Lot238^Harsh^FL^33772^United States^GG^FF^Pinellas|F|||English|M^Married|M^Methodist|0173|034-30-4828|23443-er-3243|R456|Test|India|1|1|U.S.|N|Ind|20200908040908|1|J|K|20240908110608|||||||\r" +
"PV1|1|I|1^231^A^1^^N^12^1|D|0987|NA|01234|0234|034|Opertion|N|N|N|AA|BB|CC|234|1|8090101|V|I|J|A|Y|20240908|0|1|4|34|20240908|1|2|3|4|20220709|R|A|B|C|VISIT|A|D|E|20240414110000|20240417110000|300.0|110.0|20.0|90.0|765|1|2345\r" +
"ORC|NW|ORD12345|RX12345|RX45678|CM|A0|202408291200|||123456^OrderingProvider^John^D||123456^Placer^Michael^D||||202408291200\r" +
"RXO|12345|67890|45678|100^mg^MCG|PO|1|202408291200|202408301200||||||^No^Sig|No^Dispense|No^Admin|No^Instructions\r" +
"RXR|IV|Forearm|1^Once^Day|202408291200|202408301200";

 /*                           String hl7Message="MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202409060830||ORU^R21|123457|P|2.3\r " +
"PID|1|123457^^^Hospital^MR|987654^^^Hospital^SS|Smith^Jane^B^III||19701215||||456 Elm St^^Somewhere^NY^12345^USA||(321)654-0987|||M|||987-65-4321\r " +
"PV1|1|O|OutpatientClinic^Room12||||5678^Doctor^Alice^M|||AMB|||||987654^RefDoc^Nancy^R|||Outpatient|A|||1|||||||||||||||||||||||202409050930|202409060930\r " +
"ORC|RE|678901||223344|CM||||202409060900|||5678^Doctor^Alice^M\r " +
"OBR|1|678901|223344|54321^Lipid Panel^L|||202409060900|||||||||5678^Doctor^Alice^M|||||||\r " +
"OBX|1|NM|2085-9^Cholesterol, Total^L|1|200|mg/dL|<200|N|||F\r " +
"OBX|2|NM|2571-8^HDL Cholesterol^L|1|50|mg/dL|>40|N|||F\r " +
"OBX|3|NM|13457-7^Triglycerides^L|1|150|mg/dL|<150|N|||F\r " +
"OBX|4|NM|2089-1^LDL Cholesterol^L|1|100|mg/dL|<100|N|||F";
*/
/*String hl7Message="MSH|^~\\&|LABADT|MCM|IFENG|IFENG|202408261030||ORU^R01|123456|P|2.3\r" +
"PID|1||123456^^^MCM^MR||DOE^JOHN^A||19700101|M|||1234 Main St^^Metropolis^IL^60615|(123)456-7890|(123)456-7891|||123456789|123-45-6789\r" +
"PV1|1|O|ICU^02^03^MCM||||1234^Jones^Barry^M^^MD|5678^Smith^John^A^^MD|||||||||V1001^|V001|||||||||||||||||||||||||||202408261030\r" +
"ORC|RE|123456^MCM||123456^MCM||||202408261030|5678^Smith^John^A^^MD\r" +
"OBR|1|123456^MCM|123456^MCM|88304^Biopsy, skin, other than cyst/tumor^L||202408261030|202408261040|||||5678^Smith^John^A^^MD||||||123456^MCM||L\r" +
"OBX|1|ST|88304-1^Biopsy^L|1|Positive for malignancy|||||F\r" +
"OBX|2|ST|88304-2^Biopsy^L|2|No abnormal mitosis detected|||||F\r" +
"OBX|3|NM|88304-3^Biopsy Size^L|3|1.2|cm|0.0-3.0|N|||F\r" +
"OBX|4|CE|88304-4^Specimen Adequacy^L|4|Adequate|||||F\r" +
"OBX|5|TX|88304-5^Pathologist's Notes^L|5|Tissue sample adequate for diagnosis|||||F\r" +
"NTE|1|L|Specimen received in good condition. No issues noted.";




String hl7Message="MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191000||SIU^S12|12345|P|2.3\r" +
"SCH|1|1234|5678||RP|CHECKUP||||202408191100|202408191200|||45|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
"PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321\r";
*


String hl7Message="MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20230708123000||ADT^A01|123456|P|2.3|\r" +
"EVN|A01|20230708123000|||123456\r" +
"PID|1||123456^^^Hospital^MR||Doe^John^A^^^Mr.||19800101|M||C|123 Main St^^Anytown^CA^12345^USA||(123)456-7890|||M|123456789|987-65-4321\r" +
"PD1|||FamilyPractice^^11111111|1234567890^Doe^Jane^^^^^MD|||||\r" +
"NK1|1|Doe^Jane^M^Mrs.|Wife|123 Main St^^Anytown^CA^12345^USA|(123)456-7890|(123)456-7891|||||||||||||||\r" +
"PV1|1|I|ICU^101^1^ICU^2^A|^||||1234567890^Smith^John^^^^^MD|1234567890^Brown^Mary^^^^^RN|Surgeon^001||||||V01|O|||||||||||||||||||V|1234567890|987654321^Smith^Jane^M^^^^^PA|199||A||||||||||||||||||||||||||20230708123000\r" +
"PV2||||\r" +
"OBX|1|NM|1234-5^Height^LN||180|cm^centimeter^UCUM|135-145|N|||F\r" +
"AL1|1|DA|^Penicillin||Hives\r" +
"DG1|1||R51.9^Headache^ICD-10||||A\r" +
"PR1|1|20230708110000|12345^Appendectomy^ICD-9|S||||||||20230708130000\r" +
"GT1|1|12345|Doe^John^A^^^Mr.||123 Main St^^Anytown^CA^12345^USA||(123)456-7890\r" +
"IN1|1|InsuranceCompany|123 Insurance St^^Anytown^CA^12345^USA|555-1234|INS1234|Doe^John^A^^^Mr.||123 Main St^^Anytown^CA^12345^USA||19800101|M|||123-45-6789||PPO|||123456|20240101|||||67890||Doe^Jane^M^Mrs.|123 Main St^^Anytown^CA^12345^USA";
*/

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
        Class<?> segmentClass = getSegmentClass(segmentName);
        JsonArray segmentArray = new JsonArray();

        for (Structure structure : message.getAll(segmentName)) {
            if (structure instanceof Segment) {
                Segment segment = (Segment) structure;
                JsonObject segmentJson = new JsonObject();
                int fieldNum = 1;

                while (fieldNum <= segment.numFields()) {
                    try {
                        Type[] fieldRepetitions = segment.getField(fieldNum);
                        
                        String methodName = findMethodNameForSubfield(segmentClass, segmentName, Integer.toString(fieldNum));
                        JsonArray fieldArray = new JsonArray();

                        if (methodName != null) {
                            System.out.println("Main Method Name for " + fieldNum + ": " + methodName);
                        } else {
                            System.out.println("No main method found for field: " + fieldNum);
                        }

                        for (Type field : fieldRepetitions) {
                            String fieldValue = field.encode().trim();
                            // Get the data type class name
                            String dataType = field.getClass().getSimpleName();
                            //if (dataType.contains("TS") || dataType.contains("TSComponentOne") || dataType.contains("DTM") || dataType.contains("DT")) {
                            //    fieldValue = convertTimestamp(fieldValue);
                           // }

                            JsonObject fieldObject = new JsonObject();
                            if(methodName!=null)
                            {
                            if (!fieldValue.isEmpty()) {
                                if (fieldRepetitions.length ==1) {
                                    // Single value
                                    System.out.println("M--Method Name for " + fieldNum + ": " + methodName + "dataType :" + dataType);
                                 //   if (dataType.contains("TS") || dataType.contains("TSComponentOne") || dataType.contains("DTM") || dataType.contains("DT")) {
                                 //       fieldValue = convertTimestamp(fieldValue);
                                  //  }
                                   dataType = field.getClass().getSimpleName();
                                   if (dataType.contains("TQ") || dataType.contains("TS") || dataType.contains("TSComponentOne") || dataType.contains("DTM") || dataType.contains("DT")) {
                                    fieldValue = convertTimestamp(fieldValue);
                                }
    
                                    segmentJson.addProperty(methodName.split("_")[1], fieldValue);

                                    System.out.println("Repeated elem : " + fieldValue);
                                // Extract components and subcomponents
                                String[] subComponents = field.encode().split("\\^");
                                for (int i = 0; i < subComponents.length; i++) {

                                  String submethodName = findMethodNameForSubfield(segmentClass, segmentName,fieldNum + "." + (i + 1));

                                    if (submethodName != null) {
                                        System.out.println("SubMethod Name for " + (i+1) + ": " + submethodName);
                                     dataType = field.getClass().getSimpleName();
                                     if (dataType.contains("TQ") ||dataType.contains("TS") || dataType.contains("TSComponentOne") || dataType.contains("DTM") || dataType.contains("DT")) {
                                        subComponents[i] = convertTimestamp(subComponents[i]);
                                     }

                                        fieldObject.addProperty(submethodName.split("_")[1], subComponents[i]);       
                                    } else {
                                      //  System.out.println("No method found for field: " + i);
                                    }
                                }
                                
                            }

                            
                            if(fieldObject.size()>0)
                            {
                                System.out.println("sub object : " + fieldObject);
                              //  fieldArray.add(fieldObject);
                                segmentJson.add(methodName.split("_")[1], fieldObject);
                            }
                         
                            } else {
                            }

                        }    
                            
                        }
                        
                      //  if (fieldArray.size() > 0) {
//                            segmentJson.add(methodName.split("_")[1], fieldArray);
                     //   }

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
                if (method.getName().toLowerCase().contains(segName.toLowerCase() + fieldNumber + "_")) {
                    // If it's a composite, search for the subfield method
                    if (subfieldNumber != null) {
                        // Check if the return type is another class representing the composite
                        Class<?> returnType = method.getReturnType();
                        Method[] compositeMethods = returnType.getDeclaredMethods();

                        for (Method compositeMethod : compositeMethods) {
                            if (compositeMethod.getName().toLowerCase().contains(subfieldNumber + "_")) {
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


    // Function to convert HL7 TS, DTM, and DT fields into a standard timestamp
    private static String convertTimestamp(String hl7Timestamp) {
        try {
            SimpleDateFormat hl7DateFormat;
            if (hl7Timestamp.length() == 8) { // YYYYMMDD
                hl7DateFormat = new SimpleDateFormat("yyyyMMdd");
            } else if (hl7Timestamp.length() == 12) { // YYYYMMDDHHMM
                hl7DateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            } else if (hl7Timestamp.length() == 14) { // YYYYMMDDHHMMSS
                hl7DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            } else {
                return hl7Timestamp; // Return the original value if it doesn't match expected formats
            }

            Date date = hl7DateFormat.parse(hl7Timestamp);
            SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            return standardDateFormat.format(date);
        } catch (ParseException e) {
            return hl7Timestamp; // Return the original value in case of parsing failure
        }
    }


}
