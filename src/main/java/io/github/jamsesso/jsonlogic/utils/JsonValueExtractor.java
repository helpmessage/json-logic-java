package io.github.jamsesso.jsonlogic.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonValueExtractor {

    private JsonValueExtractor() {
    }

    public static Object extract(JsonNode element) {
        if (element.isObject()) {
            Map<String, Object> map = new HashMap<>();
            ObjectNode object = (ObjectNode) element;
            
            object.forEachEntry((k, v) -> map.put(k, extract(v)));

            return map;
        } else if (element.isArray()) {
            List<Object> values = new ArrayList<>();

            for (JsonNode item : (ArrayNode)element) {
                values.add(extract(item));
            }

            return values;
        } else if (element.isNull()) {
            return null;
        } else if (element.isValueNode()) {
            
            if (element.isBoolean()) {
                return element.asBoolean();
            } else if (element.isNumber()) {
                return element.asDouble();
            } else {
                return element.asText();
            }
        }

        return element.toString();
    }
}
