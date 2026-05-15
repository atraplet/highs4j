package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

/**
 * The type of a variable.
 *
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public enum VariableType {

    CONTINUOUS(kHighsVarTypeContinuous()),
    INTEGER(kHighsVarTypeInteger()),
    SEMI_CONTINUOUS(kHighsVarTypeSemiContinuous()),
    SEMI_INTEGER(kHighsVarTypeSemiInteger()),
    IMPLICIT_INTEGER(kHighsVarTypeImplicitInteger());

    private final int type;

    VariableType(int type) {
        this.type = type;
    }

    int type() {
        return type;
    }

    static VariableType valueOf(int type) {
        for (val c : values()) {
            if (c.type() == type) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown variable type " + type);
    }

}
