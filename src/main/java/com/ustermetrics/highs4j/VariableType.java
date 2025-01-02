package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.*;

public enum VariableType {

    CONTINUOUS(K_HIGHS_VAR_TYPE_CONTINUOUS()),
    INTEGER(K_HIGHS_VAR_TYPE_INTEGER()),
    SEMI_CONTINUOUS(K_HIGHS_VAR_TYPE_SEMI_CONTINUOUS()),
    SEMI_INTEGER(K_HIGHS_VAR_TYPE_SEMI_INTEGER()),
    IMPLICIT_INTEGER(K_HIGHS_VAR_TYPE_IMPLICIT_INTEGER());

    private final long type;

    VariableType(long type) {
        this.type = type;
    }

    long type() {
        return type;
    }

    static VariableType valueOf(long type) {
        for (val c : values()) {
            if (c.type() == type) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown variable type " + type);
    }

}
