package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BasisStatusTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void valueOfDoesNotThrow(int status) {
        assertDoesNotThrow(() -> BasisStatus.valueOf(status));
    }

    @Test
    void valueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> BasisStatus.valueOf(100));

        assertEquals("Unknown basis status 100", exception.getMessage());
    }

}
