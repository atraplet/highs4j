package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsHessianFormatSquare;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsHessianFormatTriangular;

/**
 * The storage format of a Hessian {@link Matrix} in a <a href="https://highs.dev">HiGHS</a> quadratic program.
 *
 * @see <a href="https://highs.dev">HiGHS</a>
 */
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
