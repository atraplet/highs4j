package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

public enum HessianFormat {

    TRIANGULAR(kHighsHessianFormatTriangular()),
    SQUARE(kHighsHessianFormatSquare());

    private final int format;

    HessianFormat(int format) {
        this.format = format;
    }

    int format() {
        return format;
    }

    static HessianFormat valueOf(int format) {
        for (val c : values()) {
            if (c.format() == format) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown hessian format " + format);
    }

}
