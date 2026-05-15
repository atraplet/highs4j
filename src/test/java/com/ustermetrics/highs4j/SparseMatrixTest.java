package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SparseMatrixTest {

    @Test
    void createColwiseSparseMatrixReturnsSparseMatrix() {
        val matrix = new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 1, 2}, new long[]{0, 1},
                new double[]{6., 4.});

        assertEquals(2, matrix.numRow());
        assertEquals(2, matrix.numCol());
        assertEquals(MatrixFormat.COLWISE, matrix.matrixFormat());
        assertArrayEquals(new long[]{0, 1, 2}, matrix.start());
        assertArrayEquals(new long[]{0, 1}, matrix.index());
        assertArrayEquals(new double[]{6., 4.}, matrix.value(), 1e-8);
    }

    @Test
    void createRowwiseSparseMatrixReturnsSparseMatrix() {
        val matrix = new SparseMatrix(2, 2, MatrixFormat.ROWWISE, new long[]{0, 1, 2}, new long[]{0, 1},
                new double[]{6., 4.});

        assertEquals(2, matrix.numRow());
        assertEquals(2, matrix.numCol());
        assertEquals(MatrixFormat.ROWWISE, matrix.matrixFormat());
        assertArrayEquals(new long[]{0, 1, 2}, matrix.start());
        assertArrayEquals(new long[]{0, 1}, matrix.index());
        assertArrayEquals(new double[]{6., 4.}, matrix.value(), 1e-8);
    }

    @Test
    void createZeroSparseMatrixReturnsZeroSparseMatrix() {
        val matrix = new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 0, 0}, new long[]{}, new double[]{});

        assertEquals(2, matrix.numRow());
        assertEquals(2, matrix.numCol());
        assertArrayEquals(new long[]{0, 0, 0}, matrix.start());
        assertEquals(0, matrix.index().length);
        assertEquals(0, matrix.value().length);
    }

    @Test
    void createSparseMatrixWithZeroNumberOfRowsThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(0, 0, MatrixFormat.COLWISE, new long[]{}, new long[]{}, new double[]{})
        );

        assertEquals("number of rows must be positive", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithZeroNumberOfColumnsThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(1, 0, MatrixFormat.COLWISE, new long[]{}, new long[]{}, new double[]{})
        );

        assertEquals("number of columns must be positive", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithZeroLengthColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(1, 1, MatrixFormat.COLWISE, new long[]{}, new long[]{}, new double[]{})
        );

        assertEquals("length of the start index must be positive", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithInvalidDataThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 1, 2}, new long[]{0, 1},
                        new double[]{6., 4., 5.})
        );

        assertEquals("length of values must be equal to the length of the index", exception.getMessage());
    }

    @Test
    void createColwiseSparseMatrixWithInvalidColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 1, 2, 3}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("length of the start index must be equal to the number of columns (column-wise) or rows " +
                "(row-wise) plus one", exception.getMessage());
    }

    @Test
    void createRowwiseSparseMatrixWithInvalidColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.ROWWISE, new long[]{0, 1, 2, 3, 4}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("length of the start index must be equal to the number of columns (column-wise) or rows " +
                "(row-wise) plus one", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithInvalidNumberOfNonZeroEntriesThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 1, 2}, new long[]{0, 1, 2, 3, 4},
                        new double[]{0., 1., 2., 3., 4.})
        );

        assertEquals("number of non-zero entries must be less equal than the number of rows times the number of " +
                "columns", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithInvalidRowIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 1, 2}, new long[]{-1, 1},
                        new double[]{6., 4.})
        );

        assertEquals("entries of the index must be greater equal zero and less than the number of rows " +
                "(column-wise) or columns (row-wise)", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithInvalidColumnIndexEntriesThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{-1, 1, 2}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("the first entry of the start index must be equal to zero and the last entry must be " +
                "equal to the number of non-zero entries", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithTooLargeEntryInColumnIndexThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(2, 2, MatrixFormat.COLWISE, new long[]{0, 3, 2}, new long[]{0, 1},
                        new double[]{6., 4.})
        );

        assertEquals("entries of the start index must be greater equal zero, less equal than the number of " +
                "non-zero entries, and must be increasing", exception.getMessage());
    }

    @Test
    void createSparseMatrixWithUnorderedRowIndexWithinColumnThrowsException() {
        val exception = assertThrowsExactly(IllegalArgumentException.class, () ->
                new SparseMatrix(5, 4, MatrixFormat.COLWISE, new long[]{0, 2, 5, 7, 8}, new long[]{0, 3, 1, 4, 3, 0, 4, 4},
                        new double[]{1., 4., 3., 5., 6., 2., 7., 8.})
        );

        assertEquals("entries of the index within each compressed vector must be strictly increasing", exception.getMessage());
    }

    @Test
    void createLargeSparseMatrixDoesNotThrowException() {
        val m = 46341;
        val start = new long[m + 1];
        Arrays.fill(start, 1, m + 1, 1);
        assertDoesNotThrow(() ->
                new SparseMatrix(m, m, MatrixFormat.COLWISE, start, new long[]{0}, new double[]{1.})
        );
    }

    @Test
    void createSparseMatrixWithNullMatrixFormatThrowsException() {
        assertThrows(NullPointerException.class, () ->
                new SparseMatrix(2, 2, null, new long[]{0, 0, 0}, new long[]{}, new double[]{})
        );
    }

}
