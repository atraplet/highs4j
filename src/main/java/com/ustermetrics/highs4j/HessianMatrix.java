package com.ustermetrics.highs4j;

import lombok.Builder;
import lombok.NonNull;
import lombok.val;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.toIntExact;

/**
 * A Hessian matrix in compressed column storage format for a <a href="https://highs.dev">HiGHS</a> quadratic program.
 * <p>
 * The Hessian is always square with dimension {@code dim}. Supports triangular and square storage via the
 * {@link HessianFormat} parameter.
 *
 * @param dim            dimension of the square Hessian matrix
 * @param hessianFormat  storage format (triangular or square)
 * @param start          start index of each column, length is dimension plus one
 * @param index          row indices of non-zero entries. Entries within each column must appear in order of
 *                       increasing index.
 * @param value          non-zero values
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
        checkArgument(start.length > 0, "length of the column index must be positive");

        val numNz = value.length;
        checkArgument(numNz == index.length, "length of data must be equal to the length of the row index");
        checkArgument(start.length == dim + 1,
                "length of the column index must be equal to the dimension plus one");
        checkArgument(numNz <= (long) dim * dim,
                "number of non-zero entries must be less equal than the dimension squared");
        checkArgument(Arrays.stream(index).allMatch(i -> 0 <= i && i < dim),
                "entries of the row index must be greater equal zero and less than the dimension");
        checkArgument(start[0] == 0 && start[start.length - 1] == numNz,
                "the first entry of the column index must be equal to zero and the last entry must be equal to the " +
                        "number of non-zero entries");
        checkArgument(IntStream.range(0, start.length - 1)
                        .allMatch(i -> 0 <= start[i] && start[i] <= numNz && start[i] <= start[i + 1]),
                "entries of the column index must be greater equal zero, less equal than the number of non-zero " +
                        "entries, and must be ordered");
        checkArgument(IntStream.range(0, start.length - 1)
                        .allMatch(i -> IntStream.range(toIntExact(start[i]), toIntExact(start[i + 1]) - 1)
                                .allMatch(j -> index[j] < index[j + 1])),
                "entries of the row index within each column must be strictly ordered");
    }

}
