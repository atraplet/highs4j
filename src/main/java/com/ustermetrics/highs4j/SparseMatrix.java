package com.ustermetrics.highs4j;

import lombok.Builder;
import lombok.NonNull;
import lombok.val;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.toIntExact;

/**
 * A parameter object for a
 * <a href="https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_column_(CSC_or_CCS)">compressed sparse column</a>
 * or
 * <a href="https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_(CSR,_CRS_or_Yale_format)">compressed sparse row</a>
 * matrix.
 *
 * @param numRow       number of rows
 * @param numCol       number of columns
 * @param matrixFormat format (column-wise or row-wise)
 * @param start        start index of each compressed vector
 * @param index        indices of the non-zeros. Entries within each compressed vector must be strictly increasing.
 * @param value        values of the non-zeros
 */
@Builder
public record SparseMatrix(
        int numRow,
        int numCol,
        @NonNull MatrixFormat matrixFormat,
        long @NonNull [] start,
        long @NonNull [] index,
        double @NonNull [] value
) {

    public SparseMatrix {
        checkArgument(numRow > 0, "number of rows must be positive");
        checkArgument(numCol > 0, "number of columns must be positive");
        checkArgument(start.length > 0, "length of the start index must be positive");

        val colwise = matrixFormat == MatrixFormat.COLWISE;
        val numNz = value.length;
        checkArgument(numNz == index.length, "length of values must be equal to the length of the index");
        checkArgument(start.length == (colwise ? numCol + 1 : numRow + 1),
                "length of the start index must be equal to the number of columns (column-wise) or rows (row-wise) plus one");
        checkArgument(numNz <= (long) numRow * numCol,
                "number of non-zero entries must be less equal than the number of rows times the number of columns");
        checkArgument(Arrays.stream(index).allMatch(i -> 0 <= i && i < (colwise ? numRow : numCol)),
                "entries of the index must be greater equal zero and less than the number of rows (column-wise) or columns " +
                        "(row-wise)");
        checkArgument(start[0] == 0 && start[start.length - 1] == numNz,
                "the first entry of the start index must be equal to zero and the last entry must be equal to the " +
                        "number of non-zero entries");
        checkArgument(IntStream.range(0, start.length - 1)
                        .allMatch(i -> 0 <= start[i] && start[i] <= numNz && start[i] <= start[i + 1]),
                "entries of the start index must be greater equal zero, less equal than the number of non-zero " +
                        "entries, and must be increasing");
        checkArgument(IntStream.range(0, start.length - 1)
                        .allMatch(i -> IntStream.range(toIntExact(start[i]), toIntExact(start[i + 1]) - 1)
                                .allMatch(j -> index[j] < index[j + 1])),
                "entries of the index within each compressed vector must be strictly increasing");
    }

}
