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

    @Test
    void doubleParameterReturnsExpectedValues() {
        var p = new DoubleParameter("time_limit", 60.0);

        assertEquals("time_limit", p.name());
        assertEquals(60.0, p.value());
    }

    @Test
    void doubleParameterIsParameter() {
        assertInstanceOf(Parameter.class, new DoubleParameter("time_limit", 0.0));
    }

    @Test
    void doubleParameterThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> new DoubleParameter(null, 1.0));
    }

    @Test
    void doubleParameterThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new DoubleParameter("", 1.0));
    }

}
