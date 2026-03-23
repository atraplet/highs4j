package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

public enum ModelStatus {

    NOTSET(kHighsModelStatusNotset()),
    LOAD_ERROR(kHighsModelStatusLoadError()),
    MODEL_ERROR(kHighsModelStatusModelError()),
    PRESOLVE_ERROR(kHighsModelStatusPresolveError()),
    SOLVE_ERROR(kHighsModelStatusSolveError()),
    POSTSOLVE_ERROR(kHighsModelStatusPostsolveError()),
    MODEL_EMPTY(kHighsModelStatusModelEmpty()),
    OPTIMAL(kHighsModelStatusOptimal()),
    INFEASIBLE(kHighsModelStatusInfeasible()),
    UNBOUNDED_OR_INFEASIBLE(kHighsModelStatusUnboundedOrInfeasible()),
    UNBOUNDED(kHighsModelStatusUnbounded()),
    OBJECTIVE_BOUND(kHighsModelStatusObjectiveBound()),
    OBJECTIVE_TARGET(kHighsModelStatusObjectiveTarget()),
    TIME_LIMIT(kHighsModelStatusTimeLimit()),
    ITERATION_LIMIT(kHighsModelStatusIterationLimit()),
    UNKNOWN(kHighsModelStatusUnknown()),
    SOLUTION_LIMIT(kHighsModelStatusSolutionLimit()),
    INTERRUPT(kHighsModelStatusInterrupt());

    private final int status;

    ModelStatus(int status) {
        this.status = status;
    }

    int status() {
        return status;
    }

    static ModelStatus valueOf(int status) {
        for (val c : values()) {
            if (c.status() == status) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown model status " + status);
    }

}
