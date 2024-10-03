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
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;




public class ExtractRDEToJson {
    
        
//ORU^R30, RDE^O11, MDM^T02,     
    public static void main(String[] args) {
        
       /* String hl7Message="MSH|^~\\&|Epic|Epic|||20160510071633||MDM^T02|12345|D|2.3\r" + 
"PID|1||050901^^^^EPI||Thomas^Mike^J^^MR.^||19500101|M||AfrAm|506 S. HAMILTON AVE^^MADISON^WI^53505^US^^^DN |DN|(608)123-9998|(608)123-5679||S||18273652|123-45-9999||||^^^WI^^\r" +
"PV1|||^^^CARE HEALTH SYSTEMS^^^^^||||||1173^MATTHEWS^JAMES^A^^^||||||||||05090101||\r" +
"TXA||CN||20160510071633|1173^MATTHEWS^JAMES^A^^^|||||||^^12345|||||PA|\r" +
"OBX|1|TX|||Care Plan\r" +
"OBX|2|ED|^My Care Plan^||^APPLICATION^PDF^Base64^JVBERi0xLjcKJbXtrvsKNCAwIG9iago8PCAvTGVuZ3RoIDUgMCBSCiAgIC9GaWx0ZXIgL0ZsYXRlRGVjb2RlCj4+CnN0cmVhbQp4nDNUMABCXU^";
*/

/*String hl7Message="MSH|^~\\&|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|202409111200||ORU^42|12345|P|2.3\r" +
"PID|||123456^^^Hospital^MR||Doe^Jane^A||19750101|F|||456 Elm St^^Gotham^NY^10002^USA||(555)987-6543|||S||987-65-4321\r" +
"PV1||I|ER^2^1^Hospital||||5678^Attending^Physician^MD||||||||||9876543210^Consulting^Doctor^MD|||||||||||||||||||||202409111230\r" +
"ORC|RE|ORD123456|54321||CM||||202409111230|5678^Ordering^Physician^MD|||||||||Gotham Hospital Pharmacy\r" +
"OBR|1|ORD123456|54321|12345^CustomTest^L||202409111230|||||||||5678^Ordering^Physician^MD|||||||||||F\r" +
"OBX|1|NM|12345-6^Custom Observation^LN||99.9|units|50-100|N|||F|||202409111230\r" +
"OBX|2|CE|54321-1^Custom Interpretation^LN||Normal|||||F|||202409111230"; */

/*String hl7Message="MSH|^~\\&|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|202409110830||MFN^M02|12345|P|2.3\r" +
"MFI|PRD^Practitioner Information^HL70075|202409110830|UPD|||NE\r" +
"MFE|MAD|202409110830||123456^ProviderID||202409110830\r" +
"PRD|PS|1234567890^Smith^John^A||MD|^^^Metropolis Hospital|123 Main St^^Metropolis^NY^10001^USA||\r" +
"ORG|1^Metropolis Hospital^L|2|ICU^Intensive Care Unit^L|Active";*/


/*String hl7Message ="MSH|^~\\&|HL7REG|UH|HL7LAB|CH|19910918060544||MFN^M02|MSGID002|P|2.4|||AL|NE\r" +
"MFI|PRA^Practitioner Master File^HL70175||UPD|||AL\r" +
"MFE|MAD|U2246|199110011230|PMF98123789182^^PLW|ST\r" +
"STF|PMF98123789182^^PLW|U2246^^^PLW~111223333^^^USSSA^SS|KILDARE^RICHARD^J^JR^DR^M.D.|P|M|19511004|A|^ICU|^MED|(206)689-1999X345CO~(206)283-3334CH(206)689-1345X789CB|214 JOHNSON ST^SUITE 200^SEATTLE^WA^98199^H~3029 24TH AVEW^^SEATTLE, WA^98198^O |19890125^UMC&University MedicalCenter&L01||PMF88123453334|74160.2326@COMPUSERV.COM|B\r" +
"PRA|PMF98123789182^^PLW|^KILDARE FAMILY PRACTICE|ST|I|OB/GYN^STATE BOARD OFOBSTETRICS ANDGYNECOLOGY^C^19790123|1234887609^UPIN~1234987^CTY^MECOSTA~223987654^TAX~1234987757^DEA~12394433879^MDD^CA|ADMIT&&ADT^MED&&L2^19941231~DISCH&&ADT^MED&&L2^19941231|";*/

/*String hl7Message = "MSH|^~\\&|HIS|Hospital|BILLING|BillingSystem|202309231200||DFT^P03|123456|P|2.3\r" +
"EVN|P03|202309231200\r" +
"PID|1|12345|67890||Doe^John^A||19700101|M|||123 Main St^^Metropolis^IL^12345||(555)123-4567|||M|123456789|987654321\r" +
"PV1|1|I|W^101^1^A||||12345^Smith^John^J|||||||||||||123456^^^|||IN|12345|\r" +
"FT1|1|202309230900|202309230930|||101^Consultation^CPT4|1|500|USD||123456^Smith^^||12345^Jones^Jane^K|||||||1^Standard^Price\r" +
"FT1|2|202309231000|202309231030|||99213^Office Visit^CPT4|1|100|USD||123456^Smith^^||12345^Jones^Jane^K|||||||1^Standard^Price\r" +
"FT1|3|202309231100|202309231130|||123456^Aspirin 100mg^NDC|1|20|USD||123456^Smith^^||12345^Jones^Jane^K|||||||1^Standard^Price\r" +
"IN1|1|INS12345|BlueCross|1234 Blue St^^^IL^12345||(555)987-6543||||John Doe||19700101||123456789|S|20210101|20221231";
*/

/*String hl7Message = "MSH|^~\\&|HIS|RIH|EKG|EKG|20230917103000||MFN^M02|123456|P|2.3\r" +
"MFI|ORG^Organizational data^HL70064|20230917103000|UPD|||AL\r" +
"MFE|MUP|20230917103000||ORG^Organizational data^HL70064|CE\r" +
"ORG|123456789|Cardiology|CARD|Active";
*/
/*String hl7Message = "MSH|^~\\&|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|202409110830||ORU^R40|12345|P|2.3\r" +
"PID|||123456^^^Hospital^MR||Doe^John^A||19600101|M|||123 Main St^^Metropolis^NY^10001^USA||(555)123-4567|||S||123-45-6789\r" +
"PV1||I|ICU^1^1^Hospital||||1234^Attending^Physician^MD||||||||||1234567890^Consulting^Doctor^MD|||||||||||||||||||||202409110830\r" +
"ORC|RE|RX123456|987654321||CM||||202409110830|1234^Ordering^Physician^MD|||||||||Hospital Pharmacy\r" +
"OBR|1|RX123456|987654321|12345^Specialized Test^L||202409110830|||||||||1234^Ordering^Physician^MD|||||||||||F\r" +
"OBX|1|NM|54321-1^ObservationType^LN||5.6|mg/dL|4.0-6.0|N|||F|||202409110830\r" +
"OBX|2|CE|54321-2^Interpretation^LN||Normal|||||F|||202409110830";*/


/*String hl7Message="MSH|^~\\&|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|202409110830||ORU^R32^ORU_R32|12345|P|2.3\r" + //
        "PID|||123456^^^Hospital^MR||Doe^John^A||19600101|M|||123 Main St^^Metropolis^NY^10001^USA||(555)123-4567|||S||123-45-6789\r" + //
        "PV1||I|ICU^1^1^Hospital||||1234^Attending^Physician^MD||||||||||1234567890^Consulting^Doctor^MD|||||||||||||||||||||202409110830\r" + //
        "ORC|RE|RX123456|987654321||CM||||202409110830|1234^Ordering^Physician^MD|||||||||Hospital Pharmacy\r" + //
        "OBR|1|RX123456|987654321|12345^Test Name^L||202409110830|||||||||1234^Ordering^Physician^MD|||||||||||F\r" + //
        "OBX|1|NM|12345-6^Hemoglobin^LN||14.0|g/dL|13.5-17.5|N|||F|||202409110830\r" + //
        "OBX|2|NM|12345-7^White Blood Cell Count^LN||7.1|x10^9/L|4.0-11.0|N|||F|||202409110830";
*/

/*String hl7Message="MSH|^~\\&|HIS|RIH|LAB|RMC|202409101200||ORU^R30|123456789|P|2.3\r" +
"PID|||123456^^^RIH^MR||Doe^John^A||19650101|M|||123 Elm St^^Springfield^IL^62701||(555)555-5555|||EN^English\r" +
"ORC|RE|123456^LAB|789012^LAB|456789^LAB|CM|A|123^Test Room^^^|987654^Test Doctor^John^A||||202409101200\r" +
"OBR|1|123456^LAB|789012^LAB|88304^Test Test^L|||202409101200||||||||1234^Test Doctor^John^A||||||||\r" +
"OBX|1|CE|88304^Test Test^L|1|Normal^N||F|||202409101200\r" +
"OBX|2|CE|88304^Test Test^L|2|Abnormal^A||F|||202409101200";
*/

/*String hl7Message="MSH|^~\\&|LAB|LIS|HOSPITAL|HIS|202309101200||ORU^R30^ORU_R30|123456|P|2.3\r" +
"PID|1||123456^^^HOSPITAL^MR||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^12345^USA||(555)555-5555|||||S||123456789|987654321\r" +
"ORC|RE|ORD123456^LIS|ACC123456^LIS|^||CM||||202309091200\r" +
"OBR|1|ORD123456^LIS|ACC123456^LIS|1234^COMPLETE BLOOD COUNT^L|||202309091200|202309091230|||N|||||||||F|||||||||||||||^^^LAB^LIS\r" +
"OBX|1|NM|12345-6^Hemoglobin^LN||13.5|g/dL|13.0-17.0|N|||F|||202309091200|LAB^Lab Technician^L\r" +
"OBX|2|NM|12345-7^Hematocrit^LN||40.0|%|36.0-50.0|N|||F|||202309091200|LAB^Lab Technician^L\r" +
"OBX|3|NM|12345-8^White Blood Cell Count^LN||6.5|10^3/uL|4.5-11.0|N|||F|||202309091200|LAB^Lab Technician^L\r" +
"OBX|4|NM|12345-9^Platelet Count^LN||250|10^3/uL|150-400|N|||F|||202309091200|LAB^Lab Technician^L";*/

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
/*String hl7Message="MSH|^~\\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|202408291200||RDE^O11|123456|P|2.3\r" +
"PID|1|78141|80901|12005|Khalil^Abdul^^BB^CC^EE^D|C|19370831|M|D|E|4612 Washington Blvd^Lot238^Harsh^FL^33772^United States^GG^FF^Pinellas|F|||English|M^Married|M^Methodist|0173|034-30-4828|23443-er-3243|R456|Test|India|1|1|U.S.|N|Ind|20200908040908|1|J|K|20240908110608|||||||\r" +
"PV1|1|I|1^231^A^1^^N^12^1|D|0987|NA|01234|0234|034|Opertion|N|N|N|AA|BB|CC|234|1|8090101|V|I|J|A|Y|20240908|0|1|4|34|20240908|1|2|3|4|20220709|R|A|B|C|VISIT|A|D|E|20240414110000|20240417110000|300.0|110.0|20.0|90.0|765|1|2345\r" +
"ORC|NW|ORD12345|RX12345|RX45678|CM|A0|202408291200|||123456^OrderingProvider^John^D||123456^Placer^Michael^D||||202408291200\r" +
"RXO|12345|67890|45678|100^mg^MCG|PO|1|202408291200|202408301200||||||^No^Sig|No^Dispense|No^Admin|No^Instructions\r" +
"RXR|IV|Forearm|1^Once^Day|202408291200|202408301200";*/

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
"NTE|1|L|Specimen received in good condition. No issues noted.";*/


/*String hl7Message="MSH|^~\\&|MESA_OP|XYZ_HOSPITAL|iFW|ABC_HOSPITAL|20110613061611||SIU^S12|24916560|P|2.3||||||\r" +
"SCH|10345^10345|2196178^2196178|||10345|OFFICE^Office visit|reason for the appointment|OFFICE|60|m|^^60^20110617084500^20110617093000|||||9^DENT^ARTHUR^||||9^DENT^COREY^|||||Scheduled\r" +
"PID|1||42||SMITH^PAUL||19781012|M|||1 Broadway Ave^^Fort Wayne^IN^46804||(260)555-1234|||S||999999999|||||||||||||||||||||\r" +
"PV1|1|O|||||1^Smith^Miranda^A^MD^^^^|2^Withers^Peter^D^MD^^^^||||||||||||||||||||||||||||||||||||||||||99158||\r" +
"RGS|1|A\r" +
"AIG|1|A|1^White, Charles|D^^\r" +
"AIL|1|A|OFFICE^^^OFFICE|^Main Office||20110614084500|||45|m^Minutes||Scheduled\r" +
"AIP|1|A|1^White^Charles^A^MD^^^^|D^White, Douglas||20110614084500|||45|m^Minutes||Scheduled";
*/
String hl7Message="MSH|^~\\&|HOSPITAL|HIS|CLINIC|PACS|202408191000||SIU^S12|12345|P|2.3\r" +
"SCH|1|1234|5678||RP|CHECKUP||||202408191100|202408191200|||45|min|||||||||12345^Doe^John^A|||555-1234|111-2222\r" +
"PID|1||123456^^^HOSPITAL||Doe^John^A||19800101|M|||123 Main St^^Metropolis^NY^10001||555-1234|||M|S|||987-65-4321\r";

/*


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

            Message parsedMessage = parser.parse(hl7Message);



            //ca.uhn.hl7v2.model.v251.message.ORU_R01 oruR01 = new ca.uhn.hl7v2.model.v251.message.ORU_R01();
ca.uhn.hl7v2.model.v23.segment.MSH mshSegment = (MSH) parsedMessage.get("MSH");

    //        MSH mshSegment = (MSH) parsedMessage.get("MSH");
            String messageType = mshSegment.getMessageType().getMessageType().getValue();
            String triggerEvent = mshSegment.getMessageType().getTriggerEvent().getValue();
            if(messageType.equals("SIU")) {
                JsonObject jsonOutput = parseMessageToJson(message,messageType,triggerEvent);

            }
            else {
            JsonObject jsonOutput = parseMessageToJson(message,messageType,triggerEvent);
            
            System.out.println(jsonOutput.toString());
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }



private static JsonObject parseMessageToJson(Message message, String msg_type, String trigger_event) throws HL7Exception {
    JsonObject jsonObject = new JsonObject();
    String msg_version = message.getVersion();
    
//    System.out.println("All SegNames : " + message.getNames().toString());


    for (String segmentName : message.getNames()) {
        Class<?> segmentClass = getSegmentClass(segmentName,msg_version,msg_type,trigger_event);
        JsonArray segmentArray = new JsonArray();
        System.out.println("segClass Name : " + segmentClass +", segName : "+ segmentName);
        
        if(segmentClass!=null)
        {
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
    }
        jsonObject.add(segmentName, segmentArray);
    }

    return jsonObject;
}









    // Method to dynamically get the segment class using reflection
    private static Class<?> getSegmentClass(String segmentName, String msgversion, String msg_type, String event_trigger) {
        try {
            String className=null;
            String packageName;
            // Construct the fully qualified class name
            if(msg_type.equals("ORU") && event_trigger.equals("R30") || msg_type.equals("ORU") && event_trigger.equals("R40") || msg_type.equals("ORU") && event_trigger.equals("R32")) {
                packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
            }
            else if(msg_type.equals("MFN") && event_trigger.equals("M02") || msg_type.equals("DFT") && event_trigger.equals("P03") ) {
                packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
            }
            else if(msg_type.equals("SIU") ) {
                packageName = "ca.uhn.hl7v2.model.v23.segment"; // Replace with your actual package
            }
            else 
            {
             packageName = "ca.uhn.hl7v2.model.v"+ msgversion.replace(".", "") +".segment"; // Replace with your actual package
             System.out.println("Else - SegmentName : " + segmentName);
            }
            System.out.println("SegmentName : " + segmentName);
            System.out.println("pkg version: " + packageName);
         

            System.out.println("Class Name - Pacakage Selected as : " + className);
             className = packageName + "." + segmentName;

            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Exception SegmentClass Error : " + e);
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
