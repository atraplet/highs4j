package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

/**
 * The return status from a <a href="https://highs.dev">HiGHS</a> solver operation.
 *
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public enum Status {

    ERROR(kHighsStatusError()),
    OK(kHighsStatusOk()),
    WARNING(kHighsStatusWarning());

    private final int status;

    Status(int status) {
        this.status = status;
    }

    int status() {
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
