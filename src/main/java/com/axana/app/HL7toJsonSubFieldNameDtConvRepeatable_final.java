package com.axana.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v23.datatype.XPN;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;

public class HL7toJsonSubFieldNameDtConvRepeatable_final {

    private static final Map<String, String> HL7_FIELD_NAMES = createFieldNameMap();

    // Function to convert camelCase to snake_case
    private static String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
/*
    // Function to retrieve field names from a segment and order them
    private static Map<String, Object> getFieldNames(Structure segment) {
        Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

        try {
            for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
                String key = toSnakeCase(segment.getClass().getSimpleName()) + "-" + i;
                Type[] fields = ((Segment) segment).getField(i);
                for (Type field : fields) {
                    if (field instanceof Composite) {
                        fieldNames.put(key, getSubFields((Composite) field));
                    } else {
                        String value = field.encode();
                        if (key.contains("ts")) {
                            value = convertTimestamp(value);
                        }
                        fieldNames.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldNames;
    }
 */
/*
    // Function to retrieve subfields from a composite data type
    private static Map<String, Object> getSubFields(Composite composite) {
        Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

        try {
            Type[] components = composite.getComponents();
            for (int i = 0; i < components.length; i++) {
                String key =  toSnakeCase(composite.getClass().getSimpleName()) + "." + (i + 1);
                Type component = components[i];
                String dtype = component.getName();
                if (component instanceof Composite) {
                    subFieldNames.put(key, getSubFields((Composite) component));
                 //   subFieldNames.put("dataType", component.getName() );

                } else {
                    String value = component.encode();
                   
                    if (key.contains("ts")) {
                        value = convertTimestamp(value);
                    }
                    subFieldNames.put(key, value);
                  //  subFieldNames.put("dataType", dtype );

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subFieldNames;
    }
 */
/*
 // Function to retrieve field names from a segment and order them
private static Map<String, Object> getFieldNames(Structure segment) {
    Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
            String baseKey = toSnakeCase(segment.getClass().getSimpleName()) + "-" + i;
            Type[] fields = ((Segment) segment).getField(i);
            for (Type field : fields) {
                if (field instanceof Composite) {
                    Map<String, Object> subFields = getSubFields((Composite) field, baseKey);
                    fieldNames.putAll(subFields);
                } else {
                    String value = field.encode();
                    if (baseKey.contains("ts")) {
                        value = convertTimestamp(value);
                    }
                    fieldNames.put(baseKey, value);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return fieldNames;
}

// Function to retrieve subfields from a composite data type
private static Map<String, Object> getSubFields(Composite composite, String baseKey) {
    Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        Type[] components = composite.getComponents();
        for (int i = 0; i < components.length; i++) {
            String key = baseKey + "." + (i + 1); // Modified key
            Type component = components[i];
            String dtype = component.getName();
            if (component instanceof Composite) {
                subFieldNames.putAll(getSubFields((Composite) component, key)); // Recursive call for nested composites
            } else {
                String value = component.encode();
                if (key.contains("ts")) {
                    value = convertTimestamp(value);
                }
                subFieldNames.put(key, value);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return subFieldNames;
}

*/


/* 
// Function to retrieve field names from a segment and order them
private static Map<String, Object> getFieldNames(Structure segment) {
    Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
            String key = toSnakeCase(segment.getClass().getSimpleName()) + "-" + i;
            Type[] fields = ((Segment) segment).getField(i);
            for (Type field : fields) {
                if (field instanceof Composite) {
                    Map<String, Object> subFields = getSubFields((Composite) field);
                    for (Map.Entry<String, Object> subField : subFields.entrySet()) {
                        String subKey = key + "." + subField.getKey();
                        fieldNames.put(subKey, subField.getValue());
                        System.out.println("Extracted subfield key: " + subKey + ", value: " + subField.getValue() + ",Type : " + subField.getKey());

                    }
                } else {
                    String value = field.encode();
                    if (key.contains("ts")) {
                        value = convertTimestamp(value);
                    }
                    fieldNames.put(key, value);
                }

            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return fieldNames;
}

// Function to retrieve subfields from a composite data type
private static Map<String, Object> getSubFields(Composite composite) {
    Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        Type[] components = composite.getComponents();
        for (int i = 0; i < components.length; i++) {
            String key = String.valueOf(i + 1); // Just the index for the subfield key
            Type component = components[i];
            String value = component.encode();
            if (key.contains("ts")) {
                value = convertTimestamp(value);
            }
            subFieldNames.put(key, value);

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return subFieldNames;
}
*/
    // Function to convert HL7 timestamp to desired format
    private static String convertTimestamp(String hl7Timestamp) {
        SimpleDateFormat hl7Format = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat hl7DtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat desiredDtFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(hl7Timestamp.length()!=0)
            {
            if(hl7Timestamp.length()>8  )
            {
            Date date = hl7Format.parse(hl7Timestamp);
            return desiredFormat.format(date);
            }
            else
            {
                Date date = hl7DtFormat.parse(hl7Timestamp);
                return desiredDtFormat.format(date);
            }
        }
            
        } catch (ParseException e) {
            e.printStackTrace();
            return hl7Timestamp; // Return original if parsing fails
        }
        return hl7Timestamp;
    }




/*
// Function to retrieve field names and their data types from a segment and order them
private static Map<String, Object> getFieldNames(Structure segment) {
    Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
            String key = toSnakeCase(segment.getClass().getSimpleName()) + "-" + i;
            Type[] fields = ((Segment) segment).getField(i);
            for (Type field : fields) {
                if (field instanceof Composite) {
                    Map<String, Object> subFields = getSubFields((Composite) field);
                    for (Map.Entry<String, Object> subField : subFields.entrySet()) {
                        String subKey = key + "." + subField.getKey();
                        fieldNames.put(subKey, subField.getValue());
                    }
                } else {
                    String value = field.encode();
                    String dataType = field.getName();
                    if (key.contains("ts")) {
                        value = convertTimestamp(value);
                    }
                    fieldNames.put(key, value);
                    System.out.println("Field: " + key + ", Data Type: " + dataType + ", Value: " + value);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return fieldNames;
}

// Function to retrieve subfields and their data types from a composite data type
private static Map<String, Object> getSubFields(Composite composite) {
    Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        Type[] components = composite.getComponents();
        for (int i = 0; i < components.length; i++) {
            String key = String.valueOf(i + 1); // Just the index for the subfield key
            Type component = components[i];
            String value = component.encode();
            String dataType = component.getName();
            if (key.contains("ts")) {
                value = convertTimestamp(value);
            }
            subFieldNames.put(key, value);
            System.out.println("Subfield: " + key + ", Data Type: " + dataType + ", Value: " + value);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return subFieldNames;
}


*/



// Function to retrieve field names, data types, and descriptions from a segment and order them
private static Map<String, Object> getFieldNames(Structure segment) {
    Map<String, Object> fieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        for (int i = 1; i <= ((Segment) segment).numFields(); i++) {
            String key = toSnakeCase(segment.getClass().getSimpleName()) + "-" + i;
            Type[] fields = ((Segment) segment).getField(i);
            String fieldName = getFieldDescription((Segment) segment, i); // Get field description
            for (Type field : fields) {
                if (field instanceof Composite) {
                    Map<String, Object> subFields = getSubFields((Composite) field);
                    for (Map.Entry<String, Object> subField : subFields.entrySet()) {
                        String subKey = key + "." + subField.getKey();
                        fieldNames.put(subKey, subField.getValue());

                        String value = field.encode();
                        String dataType = field.getName();
                        if ((dataType.contains("TS") || dataType.contains("DTM") || dataType.contains("DT"))) {
                            value = convertTimestamp(value);
                        }
    
                    }
                    String value = field.encode();
                    String dataType = field.getName();
                    if ((dataType.contains("TS") || dataType.contains("DTM") || dataType.contains("DT"))) {
                        value = convertTimestamp(value);
                    }

                } else {
                    String value = field.encode();
                    String dataType = field.getName();
                    if ((dataType.contains("TS") || dataType.contains("DTM") || dataType.contains("DT")) ) {
                        value = convertTimestamp(value);
                    }
                    fieldNames.put(key, value);
                    System.out.println("Field: " + key + ", Data Type: " + dataType + ", Description: " + fieldName + ", Value: " + value);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return fieldNames;
}

// Function to retrieve subfields and their data types from a composite data type
private static Map<String, Object> getSubFields(Composite composite) {
    Map<String, Object> subFieldNames = new TreeMap<>(Comparator.comparingInt(HL7toJsonSubFieldNameDtConvRepeatable_final::extractNumberFromKey));

    try {
        Type[] components = composite.getComponents();
        for (int i = 0; i < components.length; i++) {
            String key = String.valueOf(i + 1); // Just the index for the subfield key
            Type component = components[i];
            String value = component.encode();
            String dataType = component.getName();
            String description = getComponentDescription(composite, i + 1); // Get component description
            if ((dataType.contains("TS") || dataType.contains("DTM")  || dataType.contains("DT"))) {
                value = convertTimestamp(value);
            }
            subFieldNames.put(key, value);
            System.out.println("Subfield: " + key + ", Data Type: " + dataType + ", Description: " + description + ", Value: " + value);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return subFieldNames;
}

// Function to get the field description from the segment
private static String getFieldDescription(Segment segment, int fieldIndex) {
    if (segment instanceof PID) {
        switch (fieldIndex) {
            case 5:
                return "Patient Name";
            case 8:
                return "Administrative Sex";
            // Add more cases for other fields as needed
            default:
                return "Unknown Field";
        }
    }
    return "Unknown Segment";
}

// Function to get the component description from the composite data type
private static String getComponentDescription(Composite composite, int componentIndex) {
    if (composite instanceof XPN) {
        switch (componentIndex) {
            case 1:
                return "Family Name";
            case 2:
                return "Given Name";
            // Add more cases for other components as needed
            default:
                return "Unknown Component";
        }
    }
    return "Unknown Composite";
}





    // Helper function to extract the numeric part of a key
    private static int extractNumberFromKey(String key) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    // Function to convert HL7 message to nested JSON
    public static JSONObject hl7ToJson(String hl7Message) throws HL7Exception {
        Parser parser = new GenericParser();
        Message message = parser.parse(hl7Message);

        JSONObject jsonOutput = new JSONObject();
        Map<String, JSONArray> segmentGroups = new TreeMap<>();

        for (String segmentName : message.getNames()) {
            try {
                Structure[] segments = message.getAll(segmentName);
                String segmentKey = toSnakeCase(segmentName);

                for (Structure segment : segments) {
                    Map<String, Object> fieldNames = getFieldNames(segment);
                    JSONObject segmentJson = new JSONObject(fieldNames);

                    if (!segmentGroups.containsKey(segmentKey)) {
                        segmentGroups.put(segmentKey, new JSONArray());
                    }
                    segmentGroups.get(segmentKey).put(segmentJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, JSONArray> entry : segmentGroups.entrySet()) {
            if (entry.getValue().length() == 1) {
                jsonOutput.put(entry.getKey(), entry.getValue().get(0));
            } else {
                jsonOutput.put(entry.getKey(), entry.getValue());
            }
        }

        return jsonOutput;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String hl7Message = "MSH|^~\\&|pcc_ara|novopharm_lab-1504963468-02|QuantumRx|NOVOPH|20230414102348||ADT^A01|42776643283|P|2.5|\r" +
        "EVN|A01|20230414110000|||DHershberger|20230414110000|novopharm_fwpi-1504963468-02\r" +
        "PID|1|781992|2235656|1230044|Jammy^Smithra^AA^BB^CC^EE^D|C|19370831|M|D|E|8300 Seminole Blvd^Lot238^Seminole^FL^33772^United States^GG^FF^Pinellas|F|||G|English|M^Married|M^Methodist|0173|034-30-4828|23443-er-3243|R456|Test|India||1|U.S.|N||20200908040908|1|J||20230908110608|1|N|O|P|Q|P|\r" +
        "NK1||ROE^MARIE^^^^|SPO|A|(216)123-4567||EC|19880908|20001207|E|X|V|U|T|S|20231109110745|Q|P|O|N|1|L|K|J|I|H|G|F|E|D||B|A\r" +
        "PV1|1|I|A^231^A^novopharm_fwpi-1504963468-02^^N^12^1|D|0987|NA|01234|0234|034|Opertion|N|N|N|AA|BB|CC|234|1|10029|V|I|J|A|Y|20230908|0|1|4|34|20230414|1|2|3|4|20220709|R|A|B|C|VISIT|A|D|E|20230414110000|20230417110000|300.0|110.0|20.0|90.0|765|1|2345\r"+
        "DG1|1||A01.0^Cholera due to Vibrio cholerae 01, biovar cholerae^I10||20230701|A\r" +
        "DG1|2||J18.9^Pneumonia, unspecified organism^I10||20230702|A\r" +
        "OBX|1|ST|ABC||Positive|20230101|N\r" +
        "OBX|2|ST|DEF||Negative|20230101|N\r" +
        "OBX|3|ST|ABC||RRPositive|20230101|N";

        try {
            JSONObject jsonOutput = hl7ToJson(hl7Message);
                    ObjectMapper mapper = new ObjectMapper();
           String jsonString = jsonOutput.toString();
        JsonNode rootNode = mapper.readTree(jsonString);

        renameKeys((ObjectNode) rootNode);

        String updatedJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(updatedJsonString);

           // System.out.println(jsonOutput.toString(4));
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }


    private static void renameKeys_old(ObjectNode node) {
        List<String> keysToRemove = new ArrayList<>();
        Map<String, JsonNode> newEntries = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String currentKey = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isObject()) {
                renameKeys((ObjectNode) value);
            }

            // Check for direct key mapping
            String newKey = HL7_FIELD_NAMES.get(currentKey);
            if (newKey != null) {
                newEntries.put(newKey, value);
                keysToRemove.add(currentKey);
            } else {
                // Check for nested key mappings
                for (String keyPath : HL7_FIELD_NAMES.keySet()) {
                    if (keyPath.contains(currentKey)) {
                        String[] pathParts = keyPath.split("\\.");
                        if (pathParts.length > 1 && pathParts[0].equals(currentKey)) {
                            String nestedKey = pathParts[1];
                            if (value.has(nestedKey)) {
                                String finalNewKey = HL7_FIELD_NAMES.get(keyPath);
                                JsonNode nestedValue = value.get(nestedKey);
                                ((ObjectNode) value).set(finalNewKey, nestedValue);
                                ((ObjectNode) value).remove(nestedKey);
                            }
                        }
                    }
                }
            }
        }

        for (String key : keysToRemove) {
            node.remove(key);
        }

        for (Map.Entry<String, JsonNode> entry : newEntries.entrySet()) {
            node.set(entry.getKey(), entry.getValue());
        }
    }

    private static void renameKeys(ObjectNode node) {
        List<String> keysToRemove = new ArrayList<>();
        Map<String, JsonNode> newEntries = new HashMap<>();
    
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
    
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String currentKey = entry.getKey();
            JsonNode value = entry.getValue();
    
            if (value.isObject()) {
                renameKeys((ObjectNode) value);
            }
    
            // Check for direct key mapping
            String newKey = HL7_FIELD_NAMES.get(currentKey);
            if (newKey != null) {
                newEntries.put(newKey, value);
                keysToRemove.add(currentKey);
            } else {
                // Check for nested key mappings
                for (String keyPath : HL7_FIELD_NAMES.keySet()) {
                    String[] pathParts = keyPath.split("\\.");
                    if (pathParts[0].equals(currentKey)) {
                        ObjectNode currentObjectNode = (ObjectNode) value;
                        renameNestedKeys(currentObjectNode, pathParts, 1, keyPath, HL7_FIELD_NAMES.get(keyPath));
                    }
                }
            }
        }
    
        for (String key : keysToRemove) {
            node.remove(key);
        }
    
        for (Map.Entry<String, JsonNode> entry : newEntries.entrySet()) {
            node.set(entry.getKey(), entry.getValue());
        }
    }
    
/*


    private static void renameKeysoool(ObjectNode node) {
        List<String> keysToRemove = new ArrayList<>();
        Map<String, JsonNode> newEntries = new HashMap<>();
    
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
    
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String currentKey = entry.getKey();
            JsonNode value = entry.getValue();
    
            if (value.isObject()) {
                renameKeys((ObjectNode) value);
            }
    
            // Check for direct key mapping
            String newKey = HL7_FIELD_NAMES.get(currentKey);
            if (newKey != null) {
                newEntries.put(newKey, value);
                keysToRemove.add(currentKey);
            } else {
                // Check for nested key mappings
                for (String keyPath : HL7_FIELD_NAMES.keySet()) {
                    String[] pathParts = keyPath.split("\\.");
                    if (pathParts[0].equals(currentKey)) {
                        ObjectNode currentObjectNode = (ObjectNode) value;
                        renameNestedKeys(currentObjectNode, pathParts, 1, keyPath, HL7_FIELD_NAMES.get(keyPath));
                    }
                }
            }
        }
    
        for (String key : keysToRemove) {
            node.remove(key);
        }
    
        for (Map.Entry<String, JsonNode> entry : newEntries.entrySet()) {
            node.set(entry.getKey(), entry.getValue());
        }
    }
    */
    private static void renameNestedKeys(ObjectNode node, String[] pathParts, int index, String keyPath, String newKey) {
        if (index >= pathParts.length) {
            return;
        }
        String currentPart = pathParts[index];
    
        if (node.has(currentPart)) {
            if (index == pathParts.length - 1) {
                JsonNode value = node.get(currentPart);
                node.set(newKey, value);
                node.remove(currentPart);
            } else {
                JsonNode nextNode = node.get(currentPart);
                if (nextNode.isObject()) {
                    renameNestedKeys((ObjectNode) nextNode, pathParts, index + 1, keyPath, newKey);
                }
            }
        }
    }
    


    private static Map<String, String> createFieldNameMap() {
        Map<String, String> map = new HashMap<>();
        map.put("msh-10", "message_control_id");
        map.put("pid-5..2", "family_name");  //$.pid.pid-5[xpn.2]
        map.put("pid-5.1", "last_name");
        map.put("pid-5.ce.2", "family_name");
        map.put("pid-8", "administrative_sex");
        map.put("pid-11.1", "street_address_2");
        map.put("pid-11.3", "street_address_3");
        map.put("pid-11.2", "street_address_4");
        map.put("pid-11.3", "city");
        map.put("pid-11.4", "state");
        map.put("pid-11.5", "postal_code");
       
        // Add other mappings as needed
        return map;
    }

}
