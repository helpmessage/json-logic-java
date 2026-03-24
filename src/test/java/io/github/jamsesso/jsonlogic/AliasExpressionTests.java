package io.github.jamsesso.jsonlogic;

import io.github.jamsesso.jsonlogic.evaluator.expressions.AliasExpression;
import io.github.jamsesso.jsonlogic.evaluator.expressions.EqualityExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author seseso
 */
public class AliasExpressionTests {
    
    private static final JsonLogic jsonLogic = new JsonLogic();
    
    @Test
    public void testEqualityAlias() throws JsonLogicException {
        
        jsonLogic.addOperation(
                AliasExpression.create("equals", EqualityExpression.INSTANCE)
        );
        
        
        assertEquals(true, jsonLogic.apply("{\"equals\": [1, 1]}", null));
        assertEquals(true, jsonLogic.apply("{\"equals\": [1, 1]}", null));
        assertEquals(true, jsonLogic.apply("{\"equals\": [[], false]}", null));
        assertEquals(true, jsonLogic.apply("{\"equals\": [\" \", 0]}", null));
    }
}
