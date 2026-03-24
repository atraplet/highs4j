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

    @Test
    void intParameterReturnsExpectedValues() {
        var p = new IntParameter("threads", 4);

        assertEquals("threads", p.name());
        assertEquals(4, p.value());
    }

    @Test
    void intParameterIsParameter() {
        assertInstanceOf(Parameter.class, new IntParameter("threads", 0));
    }

    @Test
    void intParameterThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> new IntParameter(null, 1));
    }

    @Test
    void intParameterThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new IntParameter("", 1));
    }

}
