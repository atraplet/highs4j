package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

/**
 * The status of a variable (or slack variable for a constraint) in a basis.
 *
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public enum BasisStatus {

    LOWER(kHighsBasisStatusLower()),
    BASIC(kHighsBasisStatusBasic()),
    UPPER(kHighsBasisStatusUpper()),
    ZERO(kHighsBasisStatusZero()),
    NONBASIC(kHighsBasisStatusNonbasic());

    private final int status;

    BasisStatus(int status) {
        this.status = status;
    }

    private int status() {
        return status;
    }

    static BasisStatus valueOf(int status) {
        for (val c : values()) {
            if (c.status() == status) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown basis status " + status);
    }

}
