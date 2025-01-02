package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_HESSIAN_FORMAT_SQUARE;
import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_HESSIAN_FORMAT_TRIANGULAR;

public enum HessianFormat {

    TRIANGULAR(K_HIGHS_HESSIAN_FORMAT_TRIANGULAR()),
    SQUARE(K_HIGHS_HESSIAN_FORMAT_SQUARE());

    private final long format;

    HessianFormat(long format) {
        this.format = format;
    }

    long format() {
        return format;
    }

    static HessianFormat valueOf(long format) {
        for (val c : values()) {
            if (c.format() == format) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown Hessian format " + format);
    }

}
