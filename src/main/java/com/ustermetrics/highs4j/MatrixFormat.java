package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsMatrixFormatColwise;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsMatrixFormatRowwise;

public enum MatrixFormat {

    COLWISE(kHighsMatrixFormatColwise()),
    ROWWISE(kHighsMatrixFormatRowwise());

    private final int format;

    MatrixFormat(int format) {
        this.format = format;
    }

    int format() {
        return format;
    }

    static MatrixFormat valueOf(int format) {
        for (val c : values()) {
            if (c.format() == format) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown matrix format " + format);
    }

}
