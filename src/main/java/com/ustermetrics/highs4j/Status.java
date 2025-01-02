package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.*;

public enum Status {

    NOTSET(K_HIGHS_MODEL_STATUS_NOTSET()),
    MODEL_ERROR(K_HIGHS_MODEL_STATUS_MODEL_ERROR()),
    SOLVE_ERROR(K_HIGHS_MODEL_STATUS_SOLVE_ERROR()),
    MODEL_EMPTY(K_HIGHS_MODEL_STATUS_MODEL_EMPTY()),
    OPTIMAL(K_HIGHS_MODEL_STATUS_OPTIMAL()),
    INFEASIBLE(K_HIGHS_MODEL_STATUS_INFEASIBLE()),
    UNBOUNDED_OR_INFEASIBLE(K_HIGHS_MODEL_STATUS_UNBOUNDED_OR_INFEASIBLE()),
    UNBOUNDED(K_HIGHS_MODEL_STATUS_UNBOUNDED()),
    OBJECTIVE_BOUND(K_HIGHS_MODEL_STATUS_OBJECTIVE_BOUND()),
    OBJECTIVE_TARGET(K_HIGHS_MODEL_STATUS_OBJECTIVE_TARGET()),
    TIME_LIMIT(K_HIGHS_MODEL_STATUS_TIME_LIMIT()),
    ITERATION_LIMIT(K_HIGHS_MODEL_STATUS_ITERATION_LIMIT()),
    SOLUTION_LIMIT(K_HIGHS_MODEL_STATUS_SOLUTION_LIMIT()),
    INTERRUPT(K_HIGHS_MODEL_STATUS_INTERRUPT()),
    MEMORY_LIMIT(K_HIGHS_MODEL_STATUS_MEMORY_LIMIT()),
    UNKNOWN(K_HIGHS_MODEL_STATUS_UNKNOWN());

    private final long status;

    Status(long status) {
        this.status = status;
    }

    private long status() {
        return status;
    }

    static Status valueOf(long status) {
        for (val c : values()) {
            if (c.status() == status) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown status " + status);
    }

}
