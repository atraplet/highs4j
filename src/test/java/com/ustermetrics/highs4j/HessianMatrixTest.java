package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HessianMatrixTest {

    @Test
    void createTriangularHessianMatrixReturnsHessianMatrix() {
        val matrix = new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 1, 2}, new long[]{0, 1},
                new double[]{6., 4.});

        assertEquals(2, matrix.dim());
        assertEquals(HessianFormat.TRIANGULAR, matrix.hessianFormat());
        assertArrayEquals(new long[]{0, 1, 2}, matrix.start());
        assertArrayEquals(new long[]{0, 1}, matrix.index());
        assertArrayEquals(new double[]{6., 4.}, matrix.value(), 1e-8);
    }

    @Test
    void createSquareHessianMatrixReturnsHessianMatrix() {
        val matrix = new HessianMatrix(2, HessianFormat.SQUARE, new long[]{0, 1, 2}, new long[]{0, 1},
                new double[]{6., 4.});

        assertEquals(2, matrix.dim());
        assertEquals(HessianFormat.SQUARE, matrix.hessianFormat());
        assertArrayEquals(new long[]{0, 1, 2}, matrix.start());
        assertArrayEquals(new long[]{0, 1}, matrix.index());
        assertArrayEquals(new double[]{6., 4.}, matrix.value(), 1e-8);
    }

    @Test
    void createZeroHessianMatrixReturnsZeroHessianMatrix() {
        val matrix = new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 0, 0}, new long[]{}, new double[]{});

        assertEquals(2, matrix.dim());
        assertArrayEquals(new long[]{0, 0, 0}, matrix.start());
        assertEquals(0, matrix.index().length);
        assertEquals(0, matrix.value().length);
    }

    @Test
    void createHessianMatrixWithZeroDimensionThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(0, HessianFormat.TRIANGULAR, new long[]{}, new long[]{}, new double[]{})
        );

        assertEquals("dimension must be positive", exception.getMessage());
    }

    @Test
    void createHessianMatrixWithZeroLengthColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(1, HessianFormat.TRIANGULAR, new long[]{}, new long[]{}, new double[]{})
        );

        assertEquals("length of the column index must be positive", exception.getMessage());
    }

    @Test
    void createHessianMatrixWithInvalidDataThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 1, 2}, new long[]{0, 1},
                        new double[]{6., 4., 5.})
        );

        assertEquals("length of data must be equal to the length of the row index", exception.getMessage());
    }

    @Test
    void createHessianMatrixWithInvalidColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 1, 2, 3}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("length of the column index must be equal to the dimension plus one",
                exception.getMessage());
    }

    @Test
    void createHessianMatrixWithInvalidNumberOfNonZeroEntriesThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 1, 2}, new long[]{0, 1, 2, 3, 4},
                        new double[]{0., 1., 2., 3., 4.})
        );

        assertEquals("number of non-zero entries must be less equal than the dimension squared",
                exception.getMessage());
    }

    @Test
    void createHessianMatrixWithInvalidRowIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 1, 2}, new long[]{-1, 1},
                        new double[]{6., 4.})
        );

        assertEquals("entries of the row index must be greater equal zero and less than the dimension",
                exception.getMessage());
    }

    @Test
    void createHessianMatrixWithInvalidColumnIndexEntriesThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{-1, 1, 2}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("the first entry of the column index must be equal to zero and the last entry must be equal to " +
                "the number of non-zero entries", exception.getMessage());
    }

    @Test
    void createHessianMatrixWithTooLargeEntryInColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(2, HessianFormat.TRIANGULAR, new long[]{0, 3, 2}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("entries of the column index must be greater equal zero, less equal than the number of non-zero " +
                "entries, and must be ordered", exception.getMessage());
    }

    @Test
    void createHessianMatrixWithUnorderedRowIndexWithinColumnThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new HessianMatrix(4, HessianFormat.TRIANGULAR, new long[]{0, 2, 5, 7, 8},
                        new long[]{0, 3, 1, 3, 3, 0, 3, 3},
                        new double[]{1., 4., 3., 5., 6., 2., 7., 8.})
        );

        assertEquals("entries of the row index within each column must be strictly ordered", exception.getMessage());
    }

    @Test
    void createLargeHessianMatrixDoesNotThrowException() {
        val m = 46341;
        val start = new long[m + 1];
        Arrays.fill(start, 1, m + 1, 1);
        assertDoesNotThrow(() ->
                new HessianMatrix(m, HessianFormat.TRIANGULAR, start, new long[]{0}, new double[]{1.})
        );
    }

    @Test
    void createHessianMatrixWithNullHessianFormatThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new HessianMatrix(2, null, new long[]{0, 0, 0}, new long[]{}, new double[]{})
        );
    }

}
