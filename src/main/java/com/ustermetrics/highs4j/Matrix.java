package com.ustermetrics.highs4j;

import lombok.Builder;
import lombok.NonNull;
import lombok.val;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.toIntExact;

@Builder
public record Matrix(
        int numRow,
        int numCol,
        boolean colwise,
        long @NonNull [] start,
        long @NonNull [] index,
        double @NonNull [] value
) {

    public Matrix {
        checkArgument(numRow > 0, "number of rows must be positive");
        checkArgument(numCol > 0, "number of columns must be positive");
        checkArgument(start.length > 0, "length of the column index must be positive");

        val numNz = value.length;
        checkArgument(numNz == index.length, "length of data must be equal to the length of the row index");
        checkArgument(start.length == (colwise ? numCol + 1 : numRow + 1),
                "length of the column index must be equal to the number of columns or rows plus one");
        checkArgument(numNz <= (long) numRow * numCol,
                "number of non-zero entries must be less equal than the number of rows times the number of columns");
        checkArgument(Arrays.stream(index).allMatch(i -> 0 <= i && i < (colwise ? numRow : numCol)),
                "entries of the row index must be greater equal zero and less than the number of rows or columns");
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
