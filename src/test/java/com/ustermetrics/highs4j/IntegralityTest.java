package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IntegralityTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void valueOfDoesNotThrow(int integrality) {
        assertDoesNotThrow(() -> Integrality.valueOf(integrality));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Integrality.valueOf(100));

        assertEquals("Unknown integrality 100", exception.getMessage());
    }

}
