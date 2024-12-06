package com.ustermetrics.highs4j.bindings;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.ustermetrics.highs4j.bindings.highs_c_api_h.Highs_version;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BindingsTest {

    @Test
    void versionReturnsNonEmptyString() {
        val version = Highs_version().getString(0);

        assertFalse(version.isEmpty());
    }

}
