package com.ustermetrics.highs4j.bindings;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.util.Arrays;

import static com.ustermetrics.highs4j.bindings.highs_c_api_h.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BindingsTest {

    @Test
    void versionReturnsNonEmptyString() {
        val version = Highs_version().getString(0);

        assertFalse(version.isEmpty());
    }

    @Test
    void solveLinearProgramReturnsExpectedSolution() {
        try (val arena = Arena.ofConfined()) {
            // Create linear program from the HiGHS examples
            // https://github.com/atraplet/HiGHS/blob/master/examples/call_highs_from_c.c
            val numCol = 2;
            val numRow = 3;
            val numNz = 5;
            val aFormat = 1;
//            val sense = 1;
            val sense = kHighsObjSenseMinimize();
            val offset = 3.;
            val colCostSeg = arena.allocateFrom(C_DOUBLE, 1., 1.);
            val colLowerSeg = arena.allocateFrom(C_DOUBLE, 0., 1.);
            val colUpperSeg = arena.allocateFrom(C_DOUBLE, 4., 1e30);
            val rowLowerSeg = arena.allocateFrom(C_DOUBLE, -1e30, 5., 6.);
            val rowUpperSeg = arena.allocateFrom(C_DOUBLE, 7., 15., 1e30);
            val aStartSeg = arena.allocateFrom(C_LONG_LONG, 0, 2);
            val aIndexSeg = arena.allocateFrom(C_LONG_LONG, 1, 2, 0, 1, 2);
            val aValueSeg = arena.allocateFrom(C_DOUBLE, 1., 3., 1., 2., 2.);
            val colValueSeg = arena.allocate(C_DOUBLE, numCol);
            val colDualSeg = arena.allocate(C_DOUBLE, numCol);
            val rowValueSeg = arena.allocate(C_DOUBLE, numRow);
            val rowDualSeg = arena.allocate(C_DOUBLE, numRow);
            val colBasisStatusSeg = arena.allocate(C_LONG_LONG, numCol);
            val rowBasisStatusSeg = arena.allocate(C_LONG_LONG, numRow);
            val modelStatusSeg = arena.allocate(C_LONG_LONG);

            val runStatus = Highs_lpCall(numCol, numRow, numNz, aFormat, sense, offset, colCostSeg, colLowerSeg,
                    colUpperSeg, rowLowerSeg, rowUpperSeg, aStartSeg, aIndexSeg, aValueSeg, colValueSeg, colDualSeg,
                    rowValueSeg, rowDualSeg, colBasisStatusSeg, rowBasisStatusSeg, modelStatusSeg);

            assertEquals(0, runStatus);
            assertEquals(7, modelStatusSeg.get(C_LONG_LONG, 0));

            val x = rowValueSeg.toArray(C_DOUBLE);
            System.out.println(Arrays.toString(x));

        }
    }

}
