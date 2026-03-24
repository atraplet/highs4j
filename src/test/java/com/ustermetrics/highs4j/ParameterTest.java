package com.ustermetrics.highs4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterTest {

    @Test
    void booleanParameterReturnsExpectedValues() {
        var p = new BooleanParameter("output_flag", true);

        assertEquals("output_flag", p.name());
        assertTrue(p.value());
    }

    @Test
    void booleanParameterIsParameter() {
        assertInstanceOf(Parameter.class, new BooleanParameter("output_flag", false));
    }

    @Test
    void booleanParameterThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> new BooleanParameter(null, true));
    }

    @Test
    void booleanParameterThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new BooleanParameter(" ", true));
    }

}
