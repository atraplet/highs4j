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
 * Hessian matrix.
 *
 * @param dim           dimension of the Hessian
 * @param hessianFormat format (triangular or square)
 * @param start         start index of each compressed vector
 * @param index         indices of the non-zeros. Entries within each compressed vector must be strictly increasing.
 * @param value         values of the non-zeros
 * @see <a href="https://highs.dev">HiGHS</a>
 */
@Builder
public record HessianMatrix(
        int dim,
        @NonNull HessianFormat hessianFormat,
        long @NonNull [] start,
        long @NonNull [] index,
        double @NonNull [] value
) {

    public HessianMatrix {
        checkArgument(dim > 0, "dimension must be positive");
        checkArgument(start.length > 0, "length of the start index must be positive");

        val numNz = value.length;
        checkArgument(numNz == index.length, "length of values must be equal to the length of the index");
        checkArgument(start.length == dim + 1,
                "length of the start index must be equal to the dimension plus one");
        checkArgument(numNz <= (long) dim * dim,
                "number of non-zero entries must be less equal than the dimension squared");
        checkArgument(Arrays.stream(index).allMatch(i -> 0 <= i && i < dim),
                "entries of the index must be greater equal zero and less than the dimension");
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
