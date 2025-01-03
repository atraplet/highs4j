package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SenseTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 1})
    void valueOfDoesNotThrow(int sense) {
        assertDoesNotThrow(() -> Sense.valueOf(sense));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Sense.valueOf(100));

        assertEquals("Unknown sense 100", exception.getMessage());
    }

}
