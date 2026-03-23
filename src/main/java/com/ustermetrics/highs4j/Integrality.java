package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

public enum Integrality {

    CONTINUOUS(kHighsVarTypeContinuous()),
    INTEGER(kHighsVarTypeInteger()),
    SEMI_CONTINUOUS(kHighsVarTypeSemiContinuous()),
    SEMI_INTEGER(kHighsVarTypeSemiInteger()),
    IMPLICIT_INTEGER(kHighsVarTypeImplicitInteger());

    private final int integrality;

    Integrality(int integrality) {
        this.integrality = integrality;
    }

    int integrality() {
        return integrality;
    }

    static Integrality valueOf(int integrality) {
        for (val c : values()) {
            if (c.integrality() == integrality) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown integrality " + integrality);
    }

}
