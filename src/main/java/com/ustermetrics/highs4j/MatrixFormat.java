package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_MATRIX_FORMAT_COLWISE;
import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_MATRIX_FORMAT_ROWWISE;

public enum MatrixFormat {

    COLWISE(K_HIGHS_MATRIX_FORMAT_COLWISE()),
    ROWWISE(K_HIGHS_MATRIX_FORMAT_ROWWISE());

    private final long format;

    MatrixFormat(long format) {
        this.format = format;
    }

    long format() {
        return format;
    }

    static MatrixFormat valueOf(long format) {
        for (val c : values()) {
            if (c.format() == format) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown matrix format " + format);
    }

}
