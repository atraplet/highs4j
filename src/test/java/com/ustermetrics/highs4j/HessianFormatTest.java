package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HessianFormatTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void valueOfThrowsNoException(int format) {
        assertDoesNotThrow(() -> HessianFormat.valueOf(format));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> HessianFormat.valueOf(100));

        assertEquals("Unknown Hessian format 100", exception.getMessage());
    }

}
