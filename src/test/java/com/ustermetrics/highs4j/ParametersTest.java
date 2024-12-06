package com.ustermetrics.ecos4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParametersTest {

    @Test
    void buildParametersWithCustomOptionsReturnsParametersWithCustomOptions() {
        val parameters = Parameters.builder()
                .feasTol(1.)
                .absTol(1.)
                .relTol(1.)
                .feasTolInacc(1.)
                .absTolInacc(1.)
                .relTolInacc(1.)
                .maxIt(1)
                .verbose(true)
                .build();

        val tol = 1e-8;
        assertEquals(1., parameters.feasTol(), tol);
        assertEquals(1., parameters.absTol(), tol);
        assertEquals(1., parameters.relTol(), tol);
        assertEquals(1., parameters.feasTolInacc(), tol);
        assertEquals(1., parameters.absTolInacc(), tol);
        assertEquals(1., parameters.relTolInacc(), tol);
        assertNull(parameters.nItRef());
        assertEquals(1, parameters.maxIt());
        assertTrue(parameters.verbose());
    }

    @Test
    void buildParametersWithInvalidFeasTolThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .feasTol(-1.)
                .build());

        assertEquals("feasTol must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidAbsTolThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .absTol(-1.)
                .build());

        assertEquals("absTol must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidRelTolThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .relTol(-1.)
                .build());

        assertEquals("relTol must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidFeasTolInaccThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .feasTolInacc(-1.)
                .build());

        assertEquals("feasTolInacc must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidAbsTolInaccThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .absTolInacc(-1.)
                .build());

        assertEquals("absTolInacc must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidRelTolInaccThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .relTolInacc(-1.)
                .build());

        assertEquals("relTolInacc must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidNItRefThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .nItRef(-1)
                .build());

        assertEquals("nItRef must be null or positive", exception.getMessage());
    }

    @Test
    void buildParametersWithInvalidMaxItThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () -> Parameters.builder()
                .maxIt(-1)
                .build());

        assertEquals("maxIt must be null or positive", exception.getMessage());
    }

}
