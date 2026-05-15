package com.ustermetrics.highs4j;

import lombok.val;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsObjSenseMaximize;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsObjSenseMinimize;

/**
 * The optimization sense of a <a href="https://highs.dev">HiGHS</a> model.
 */
public enum ObjectiveSense {

    MINIMIZE(kHighsObjSenseMinimize()),
    MAXIMIZE(kHighsObjSenseMaximize());

    private final int sense;

    ObjectiveSense(int sense) {
        this.sense = sense;
    }

    int sense() {
        return sense;
    }

    static ObjectiveSense valueOf(int sense) {
        for (val c : values()) {
            if (c.sense() == sense) {
                return c;
            }
        }

        throw new IllegalArgumentException("Unknown objective sense " + sense);
    }

}
