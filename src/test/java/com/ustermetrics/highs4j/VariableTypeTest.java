package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VariableTypeTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void valueOfDoesNotThrow(int type) {
        assertDoesNotThrow(() -> VariableType.valueOf(type));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> VariableType.valueOf(100));

        assertEquals("Unknown variable type 100", exception.getMessage());
    }

}
