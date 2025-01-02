package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_OBJSENSE_MAXIMIZE;
import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.K_HIGHS_OBJSENSE_MINIMIZE;

public enum Sense {

    MINIMIZE(K_HIGHS_OBJSENSE_MINIMIZE()),
    MAXIMIZE(K_HIGHS_OBJSENSE_MAXIMIZE());

    private final long sense;

    Sense(long sense) {
        this.sense = sense;
    }

    long sense() {
        return sense;
    }

    static Sense valueOf(long sense) {
        for (val c : values()) {
            if (c.sense() == sense) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown sense " + sense);
    }

}
