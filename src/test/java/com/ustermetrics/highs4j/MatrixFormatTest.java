package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MatrixFormatTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void valueOfDoesNotThrow(int format) {
        assertDoesNotThrow(() -> MatrixFormat.valueOf(format));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> MatrixFormat.valueOf(100));

        assertEquals("Unknown matrix format 100", exception.getMessage());
    }

}
