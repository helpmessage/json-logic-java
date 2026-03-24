package io.github.jamsesso.jsonlogic.evaluator.expressions;

import io.github.jamsesso.jsonlogic.ast.JsonLogicArray;
import io.github.jamsesso.jsonlogic.evaluator.JsonLogicEvaluationException;
import io.github.jamsesso.jsonlogic.evaluator.JsonLogicEvaluator;
import io.github.jamsesso.jsonlogic.evaluator.JsonLogicExpression;

/**
 *
 * @author seseso
 */
public class AliasExpression {
    
    public static JsonLogicExpression create(final String key, final JsonLogicExpression delegate) {
        
        return new JsonLogicExpression() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public Object evaluate(JsonLogicEvaluator evaluator, JsonLogicArray arguments, Object data, String jsonPath) throws JsonLogicEvaluationException {
                return delegate.evaluate(evaluator, arguments, data, jsonPath);
            }
        };
    }
}
