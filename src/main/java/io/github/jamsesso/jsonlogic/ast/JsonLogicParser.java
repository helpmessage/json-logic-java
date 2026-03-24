package io.github.jamsesso.jsonlogic.ast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public final class JsonLogicParser {

    private static final ObjectMapper PARSER = new ObjectMapper();

    private JsonLogicParser() {
        // Utility class has no public constructor.
    }

    public static JsonLogicNode parse(String json) throws JsonLogicParseException {
        try {
            return parse(PARSER.readTree(json));
        } catch (JsonProcessingException e) {
            throw new JsonLogicParseException(e, "$");
        }
    }

    private static JsonLogicNode parse(JsonNode root) throws JsonLogicParseException {
        return parse(root, "$");
    }

    private static JsonLogicNode parse(JsonNode root, String jsonPath) throws JsonLogicParseException {
        // Handle null
        if (root.isNull()) {
            return JsonLogicNull.NULL;
        }

        // Handle primitives
        if (root.isValueNode()) {

            if (root.isTextual()) {
                return new JsonLogicString(root.asText());
            }

            if (root.isNumber()) {
                return new JsonLogicNumber(root.asDouble());
            }

            if (root.isBoolean() && root.asBoolean()) {
                return JsonLogicBoolean.TRUE;
            } else {
                return JsonLogicBoolean.FALSE;
            }
        }

        // Handle arrays
        if (root.isArray()) {
            ArrayNode array = (ArrayNode) root;
            List<JsonLogicNode> elements = new ArrayList<>(array.size());

            int index = 0;
            for (JsonNode element : array) {
                elements.add(parse(element, String.format("%s[%d]", jsonPath, index++)));
            }

            return new JsonLogicArray(elements);
        }

        // Handle objects & variables
        ObjectNode object = (ObjectNode) root;

        if (object.properties().size() != 1) {
            throw new JsonLogicParseException("objects must have exactly 1 key defined, found " + object.properties().size(), jsonPath);
        }

        String key = object.propertyStream().findAny().map(Entry::getKey).get();
        JsonLogicNode argumentNode = parse(object.get(key), String.format("%s.%s", jsonPath, key));
        JsonLogicArray arguments;

        // Always coerce single-argument operations into a JsonLogicArray with a single element.
        if (argumentNode instanceof JsonLogicArray) {
            arguments = (JsonLogicArray) argumentNode;
        } else {
            arguments = new JsonLogicArray(Collections.singletonList(argumentNode));
        }

        // Special case for variable handling
        if ("var".equals(key)) {
            JsonLogicNode defaultValue = arguments.size() > 1 ? arguments.get(1) : JsonLogicNull.NULL;
            return new JsonLogicVariable(arguments.size() < 1 ? JsonLogicNull.NULL : arguments.get(0), defaultValue);
        }

        // Handle regular operations
        return new JsonLogicOperation(key, arguments);
    }
}
