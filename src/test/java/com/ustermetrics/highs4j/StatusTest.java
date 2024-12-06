package com.ustermetrics.ecos4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.ustermetrics.ecos4j.bindings.ecos_h.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class StatusTest {

    @Test
    void statusValueOfEcosOptimalReturnsOptimal() {
        assertEquals(Status.OPTIMAL, Status.valueOf(ECOS_OPTIMAL()));
    }

    @Test
    void statusValueOfEcosPinfReturnsPinf() {
        assertEquals(Status.PINF, Status.valueOf(ECOS_PINF()));
    }

    @Test
    void statusValueOfEcosDinfReturnsDinf() {
        assertEquals(Status.DINF, Status.valueOf(ECOS_DINF()));
    }

    @Test
    void statusValueOfEcosInaccOffsetReturnsInaccOffset() {
        assertEquals(Status.INACC_OFFSET, Status.valueOf(ECOS_INACC_OFFSET()));
    }

    @Test
    void statusValueOfEcosMaxitReturnsMaxit() {
        assertEquals(Status.MAXIT, Status.valueOf(ECOS_MAXIT()));
    }

    @Test
    void statusValueOfEcosNumericsReturnsNumerics() {
        assertEquals(Status.NUMERICS, Status.valueOf(ECOS_NUMERICS()));
    }

    @Test
    void statusValueOfEcosOutconeReturnsOutcone() {
        assertEquals(Status.OUTCONE, Status.valueOf(ECOS_OUTCONE()));
    }

    @Test
    void statusValueOfEcosSigintReturnsSigint() {
        assertEquals(Status.SIGINT, Status.valueOf(ECOS_SIGINT()));
    }

    @Test
    void statusValueOfEcosFatalReturnsFatal() {
        assertEquals(Status.FATAL, Status.valueOf(ECOS_FATAL()));
    }

    @Test
    void statusValueOf100ThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Status.valueOf(100));

        assertEquals("Unknown status 100", exception.getMessage());
    }

}
