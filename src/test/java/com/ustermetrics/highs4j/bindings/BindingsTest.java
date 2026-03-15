package com.ustermetrics.highs4j.bindings;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;
import static org.junit.jupiter.api.Assertions.*;

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
            // minimal_api(), first part
            val numCol = 2;
            val numRow = 3;
            val numNz = 5;
            val offset = 3.;
            val sense = kHighsObjSenseMinimize();
            val aFormat = kHighsMatrixFormatColwise();

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

            assertEquals(kHighsStatusOk(), runStatus);
            assertEquals(kHighsModelStatusOptimal(), modelStatusSeg.get(C_LONG_LONG, 0));

            val tol = 1e-8;
            val colValue = colValueSeg.reinterpret(C_DOUBLE.byteSize() * numCol, arena, null)
                    .toArray(C_DOUBLE);
            assertArrayEquals(new double[]{0.5, 2.25}, colValue, tol);
            val colDual = colDualSeg.reinterpret(C_DOUBLE.byteSize() * numCol, arena, null)
                    .toArray(C_DOUBLE);
            assertArrayEquals(new double[]{0., 0.}, colDual, tol);
            val colBasisStatus = colBasisStatusSeg.reinterpret(C_LONG_LONG.byteSize() * numCol, arena, null)
                    .toArray(C_LONG_LONG);
            assertArrayEquals(new long[]{1, 1}, colBasisStatus);
            val rowValue = rowValueSeg.reinterpret(C_DOUBLE.byteSize() * numRow, arena, null)
                    .toArray(C_DOUBLE);
            assertArrayEquals(new double[]{2.25, 5., 6.}, rowValue, tol);
            val rowDual = rowDualSeg.reinterpret(C_DOUBLE.byteSize() * numRow, arena, null)
                    .toArray(C_DOUBLE);
            assertArrayEquals(new double[]{0., 0.25, 0.25}, rowDual, tol);
            val rowBasisStatus = rowBasisStatusSeg.reinterpret(C_LONG_LONG.byteSize() * numRow, arena, null)
                    .toArray(C_LONG_LONG);
            assertArrayEquals(new long[]{1, 0, 0}, rowBasisStatus);
            val colCost = colCostSeg.reinterpret(C_DOUBLE.byteSize() * numCol, arena, null)
                    .toArray(C_DOUBLE);
            assertEquals(5.75, offset + colValue[0] * colCost[0] + colValue[1] * colCost[1], tol);
        }
    }

}
