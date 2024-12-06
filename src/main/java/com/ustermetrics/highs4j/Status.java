package com.ustermetrics.ecos4j;

import lombok.val;

import static com.ustermetrics.ecos4j.bindings.ecos_h.*;

/**
 * The <a href="https://github.com/embotech/ecos">ECOS</a> solver status from optimizing a {@link Model}.
 */
public enum Status {

    OPTIMAL(ECOS_OPTIMAL()),
    PINF(ECOS_PINF()),
    DINF(ECOS_DINF()),
    INACC_OFFSET(ECOS_INACC_OFFSET()),
    MAXIT(ECOS_MAXIT()),
    NUMERICS(ECOS_NUMERICS()),
    OUTCONE(ECOS_OUTCONE()),
    SIGINT(ECOS_SIGINT()),
    FATAL(ECOS_FATAL());

    private final int status;

    Status(int status) {
        this.status = status;
    }

    private int status() {
        return status;
    }

    static Status valueOf(int status) {
        for (val c : values()) {
            if (c.status() == status) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown status " + status);
    }

}
