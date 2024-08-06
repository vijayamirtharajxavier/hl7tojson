package com.axana.app;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HL7JsonKeyRenamer {

    private static final Map<String, String> HL7_FIELD_NAMES = createFieldNameMap();

    public static void main(String[] args) throws Exception {
        String jsonString = "{"
            + " \"msh\": {"
            + "     \"msh-1\": \"|\","
            + "     \"msh-2\": \"^~\\\\&\","
            + "     \"msh-3\": { \"hd-1\": \"2.16.840.1.113883.4.1\" },"
            + "     \"msh-4\": { \"hd-1\": \"HL7\" },"
            + "     \"msh-5\": { \"hd-1\": \"HIS\" },"
            + "     \"msh-6\": { \"hd-1\": \"ADT\" },"
            + "     \"msh-7\": { \"ts-1\": \"202408051230\" },"
            + "     \"msh-9\": { \"msg-1\": \"ADT\", \"msg-2\": \"A01\" },"
            + "     \"msh-10\": \"12345\","
            + "     \"msh-11\": { \"pt-1\": \"P\" },"
            + "     \"msh-12\": {"
            + "         \"vid-1\": \"1.0\","
            + "         \"vid-2\": { \"ce-1\": \"HL70001\", \"ce-2\": \"1\" }"
            + "     }"
            + " },"
            + " \"nk1\": {"
            + "     \"nk1-2\": { \"cx-1\": \"123456789\", \"cx-2\": \"987654321\" }"
            + " },"
            + " \"pid\": {"
            + "     \"pid-1\": \"12345\","
            + "     \"pid-2\": { \"cx-1\": \"1234567890\", \"cx-2\": \"0987654321\" },"
            + "     \"pid-5\": { \"xpn-1\": \"Doe\", \"xpn-2\": \"John\" },"
            + "     \"pid-7\": \"19700101\","
            + "     \"pid-9\": \"M\","
            + "     \"pid-10\": \"1234567890\""
            + " },"
            + " \"pv1\": {"
            + "     \"pv1-1\": \"I\","
            + "     \"pv1-2\": \"A\","
            + "     \"pv1-3\": \"123\","
            + "     \"pv1-4\": \"XYZ\","
            + "     \"pv1-5\": \"ACME Hospital\","
            + "     \"pv1-6\": \"ER\""
            + " },"
            + " \"evn\": {"
            + "     \"evn-1\": \"A01\","
            + "     \"evn-2\": \"202408051200\","
            + "     \"evn-3\": \"Admin\""
            + " }"
            + "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);

        renameKeys((ObjectNode) rootNode);

        String updatedJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(updatedJsonString);
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

    private static Map<String, String> createFieldNameMap() {
        Map<String, String> map = new HashMap<>();
        map.put("msh-10", "MessageControlID");
        map.put("pid-5.xpn-2", "FamilyName");
        // Add other mappings as needed
        return map;
    }
}
