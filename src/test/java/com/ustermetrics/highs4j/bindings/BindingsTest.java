package com.ustermetrics.highs4j.bindings;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;

import static com.ustermetrics.highs4j.bindings.highs4j_c_api_h.*;
import static org.junit.jupiter.api.Assertions.*;

class BindingsTest {

    @Test
    void versionReturnsNonEmptyString() {
        val version = Highs_version().getString(0);

        assertFalse(version.isEmpty());
    }

    @Test
    void solveLinearProgramReturnsExpectedSolution() {
        // Set up the linear program from the HiGHS examples
        // https://github.com/atraplet/HiGHS/blob/master/examples/call_highs_from_c.c
        // first part of full_api()
        val numCol = 2;
        val numRow = 3;
        val numNz = 5;
        val offset = 3.;
        val sense = K_HIGHS_OBJSENSE_MINIMIZE();
        val aFormat = K_HIGHS_MATRIX_FORMAT_COLWISE();

        try (val arena = Arena.ofConfined()) {
            val colCostSeg = arena.allocateFrom(C_DOUBLE, 1., 1.);
            val colLowerSeg = arena.allocateFrom(C_DOUBLE, 0., 1.);
            val colUpperSeg = arena.allocateFrom(C_DOUBLE, 4., 1e30);
            val rowLowerSeg = arena.allocateFrom(C_DOUBLE, -1e30, 5., 6.);
            val rowUpperSeg = arena.allocateFrom(C_DOUBLE, 7., 15., 1e30);
            val aStartSeg = arena.allocateFrom(C_LONG_LONG, 0, 2);
            val aIndexSeg = arena.allocateFrom(C_LONG_LONG, 1, 2, 0, 1, 2);
            val aValueSeg = arena.allocateFrom(C_DOUBLE, 1., 3., 1., 2., 2.);

            val highsSeg = Highs_create();

            assertEquals(K_HIGHS_STATUS_OK(), Highs_setBoolOptionValue(highsSeg, arena.allocateFrom("output_flag"), 0));

            assertEquals(K_HIGHS_STATUS_OK(), Highs_passLp(highsSeg, numCol, numRow, numNz, aFormat, sense, offset,
                    colCostSeg, colLowerSeg, colUpperSeg, rowLowerSeg, rowUpperSeg, aStartSeg, aIndexSeg, aValueSeg));

            assertEquals(K_HIGHS_STATUS_OK(), Highs_run(highsSeg));

            val modelStatus = Highs_getModelStatus(highsSeg);
            assertEquals(K_HIGHS_MODEL_STATUS_OPTIMAL(), modelStatus);

            val objectiveFunctionValueSeg = arena.allocate(C_DOUBLE);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getDoubleInfoValue(highsSeg,
                    arena.allocateFrom("objective_function_value"), objectiveFunctionValueSeg));
            val objectiveFunctionValue = objectiveFunctionValueSeg.get(C_DOUBLE, 0);
            val tol = 1e-8;
            assertEquals(5.75, objectiveFunctionValue, tol);

            val simplexIterationCountSeg = arena.allocate(C_LONG_LONG);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getIntInfoValue(highsSeg,
                    arena.allocateFrom("simplex_iteration_count"), simplexIterationCountSeg));
            val simplexIterationCount = simplexIterationCountSeg.get(C_LONG_LONG, 0);
            assertEquals(2, simplexIterationCount);

            val primalSolutionStatusSeg = arena.allocate(C_LONG_LONG);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getIntInfoValue(highsSeg,
                    arena.allocateFrom("primal_solution_status"), primalSolutionStatusSeg));
            val primalSolutionStatus = primalSolutionStatusSeg.get(C_LONG_LONG, 0);
            assertEquals(K_HIGHS_SOLUTION_STATUS_FEASIBLE(), primalSolutionStatus);

            val dualSolutionStatusSeg = arena.allocate(C_LONG_LONG);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getIntInfoValue(highsSeg,
                    arena.allocateFrom("dual_solution_status"), dualSolutionStatusSeg));
            val dualSolutionStatus = dualSolutionStatusSeg.get(C_LONG_LONG, 0);
            assertEquals(K_HIGHS_SOLUTION_STATUS_FEASIBLE(), dualSolutionStatus);

            val basisValiditySeg = arena.allocate(C_LONG_LONG);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getIntInfoValue(highsSeg, arena.allocateFrom("basis_validity"),
                    basisValiditySeg));
            val basisValidity = basisValiditySeg.get(C_LONG_LONG, 0);
            assertEquals(K_HIGHS_BASIS_VALIDITY_VALID(), basisValidity);

            val colValueSeg = arena.allocate(C_DOUBLE, numCol);
            val colDualSeg = arena.allocate(C_DOUBLE, numCol);
            val rowValueSeg = arena.allocate(C_DOUBLE, numRow);
            val rowDualSeg = arena.allocate(C_DOUBLE, numRow);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getSolution(highsSeg, colValueSeg, colDualSeg, rowValueSeg,
                    rowDualSeg));
            val colValue = colValueSeg.toArray(C_DOUBLE);
            val colDual = colDualSeg.toArray(C_DOUBLE);
            val rowValue = rowValueSeg.toArray(C_DOUBLE);
            val rowDual = rowDualSeg.toArray(C_DOUBLE);
            assertArrayEquals(new double[]{0.5, 2.25}, colValue, tol);
            assertArrayEquals(new double[]{0., 0.}, colDual, tol);
            assertArrayEquals(new double[]{2.25, 5., 6.}, rowValue, tol);
            assertArrayEquals(new double[]{0., 0.25, 0.25}, rowDual, tol);

            val colBasisStatusSeg = arena.allocate(C_LONG_LONG, numCol);
            val rowBasisStatusSeg = arena.allocate(C_LONG_LONG, numRow);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getBasis(highsSeg, colBasisStatusSeg, rowBasisStatusSeg));
            val colBasisStatus = colBasisStatusSeg.toArray(C_LONG_LONG);
            val rowBasisStatus = rowBasisStatusSeg.toArray(C_LONG_LONG);
            assertArrayEquals(new long[]{1, 1}, colBasisStatus);
            assertArrayEquals(new long[]{1, 0, 0}, rowBasisStatus);

            val objectiveSenseSeg = arena.allocate(C_LONG_LONG);
            assertEquals(K_HIGHS_STATUS_OK(), Highs_getObjectiveSense(highsSeg, objectiveSenseSeg));
            val objectiveSense = objectiveSenseSeg.get(C_LONG_LONG, 0);
            assertEquals(K_HIGHS_OBJSENSE_MINIMIZE(), objectiveSense);

            Highs_destroy(highsSeg);
        }
    }

}
