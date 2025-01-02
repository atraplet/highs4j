package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18})
    void valueOfThrowsNoException(int status) {
        assertDoesNotThrow(() -> Status.valueOf(status));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 100})
    void valueOfThrowsException(int status) {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Status.valueOf(status));

        assertEquals("Unknown status " + status, exception.getMessage());
    }

}
