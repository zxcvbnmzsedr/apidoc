package com.ztianzeng.apidoc.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ztianzeng.apidoc.swagger.util.Json;
import com.ztianzeng.apidoc.swagger.util.Yaml;


import java.io.IOException;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class SerializationMatchers {

    public static void assertEqualsToYaml(Object objectToSerialize, String yamlStr) {
        apply(objectToSerialize, yamlStr, Yaml.mapper());
    }

    public static void assertEqualsToJson(Object objectToSerialize, String jsonStr) {
        apply(objectToSerialize, jsonStr, Json.mapper());
    }

    private static void apply(Object objectToSerialize, String str, ObjectMapper mapper) {
        final ObjectNode lhs = mapper.convertValue(objectToSerialize, ObjectNode.class);
        ObjectNode rhs = null;
        try {
            rhs = mapper.readValue(str, ObjectNode.class);
        } catch (IOException ignored) {

        }
        if (!lhs.equals(new ObjectNodeComparator(), rhs)) {
            assertEquals(Yaml.pretty(lhs), Yaml.pretty(rhs));
        }
    }

    static final class ObjectNodeComparator implements Comparator<JsonNode> {
        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            if ((o1 instanceof NumericNode) && (o2 instanceof NumericNode)) {
                double d1 = o1.asDouble();
                double d2 = o2.asDouble();
                return Double.compare(d1, d2);
            }
            int comp = o1.asText().compareTo(o2.asText());
            if (comp == 0) {
                return Integer.compare(o1.hashCode(), o2.hashCode());
            }
            return comp;
        }
    }
}