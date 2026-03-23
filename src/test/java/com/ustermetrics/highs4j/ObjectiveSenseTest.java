package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ObjectiveSenseTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 1})
    void valueOfDoesNotThrow(int sense) {
        assertDoesNotThrow(() -> ObjectiveSense.valueOf(sense));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> ObjectiveSense.valueOf(100));

        assertEquals("Unknown objective sense 100", exception.getMessage());
    }

}
