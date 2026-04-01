package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.util.List;

import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    // LP problem from BindingsTest / call_highs_from_c.c full_api():
    // Min    f  =  x_0 +  x_1 + 3
    // s.t.                x_1 <= 7
    //        5 <=  x_0 + 2x_1 <= 15
    //        6 <= 3x_0 + 2x_1
    // 0 <= x_0 <= 4; 1 <= x_1

    private static final double[] COL_COST = {1., 1.};
    private static final double[] COL_LOWER = {0., 1.};
    private static final double[] COL_UPPER = {4., 1e30};
    private static final double[] ROW_LOWER = {-1e30, 5., 6.};
    private static final double[] ROW_UPPER = {7., 15., 1e30};
    private static final SparseMatrix A = new SparseMatrix(3, 2, MatrixFormat.COLWISE,
            new long[]{0, 2, 5}, new long[]{1, 2, 0, 1, 2}, new double[]{1., 3., 1., 2., 2.});
    private static final double OFFSET = 3.;
    private static final double TOL = 1e-8;

    @Test
    void solveLinearProgramReturnsExpectedSolution() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);

            val status = model.solve();

            assertEquals(Status.OK, status);
            assertEquals(ModelStatus.OPTIMAL, model.modelStatus());
            assertEquals(5.75, model.objectiveFunctionValue(), TOL);
            assertEquals(2, model.simplexIterationCount());
            assertEquals(kHighsSolutionStatusFeasible(), model.primalSolutionStatus());
            assertEquals(kHighsSolutionStatusFeasible(), model.dualSolutionStatus());
            assertEquals(kHighsBasisValidityValid(), model.basisValidity());
            assertArrayEquals(new double[]{0.5, 2.25}, model.colValue(), TOL);
            assertArrayEquals(new double[]{0., 0.}, model.colDual(), TOL);
            assertArrayEquals(new double[]{2.25, 5., 6.}, model.rowValue(), TOL);
            assertArrayEquals(new double[]{0., 0.25, 0.25}, model.rowDual(), TOL);
            assertArrayEquals(new BasisStatus[]{BasisStatus.BASIC, BasisStatus.BASIC}, model.colBasisStatus());
            assertArrayEquals(new BasisStatus[]{BasisStatus.BASIC, BasisStatus.LOWER, BasisStatus.LOWER},
                    model.rowBasisStatus());
            assertEquals(ObjectiveSense.MINIMIZE, model.objectiveSense());
        }
    }

    // MIP problem from call_highs_from_c.c:
    // Same LP but maximized with both variables integer
    // Max    f  =  x_0 +  x_1 + 3

    @Test
    void solveMipReturnsExpectedSolution() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MAXIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A,
                    new Integrality[]{Integrality.INTEGER, Integrality.INTEGER});

            val status = model.solve();

            assertEquals(Status.OK, status);
            assertEquals(ModelStatus.OPTIMAL, model.modelStatus());

            val colValue = model.colValue();
            val rowValue = model.rowValue();
            // Compute expected objective: offset + sum(colCost[i] * colValue[i])
            var expectedObj = OFFSET;
            for (int i = 0; i < COL_COST.length; i++) {
                expectedObj += COL_COST[i] * colValue[i];
            }
            assertEquals(expectedObj, model.objectiveFunctionValue(), TOL);

            // MIP should have feasible primal, no dual, invalid basis
            assertEquals(kHighsSolutionStatusFeasible(), model.primalSolutionStatus());
            assertEquals(kHighsSolutionStatusNone(), model.dualSolutionStatus());
            assertEquals(kHighsBasisValidityInvalid(), model.basisValidity());
        }
    }

    // QP problem from call_highs_from_c.c minimal_api_qp():
    // minimize -x_2 + (1/2)(2x_1^2 - 2x_1x_3 + 0.2x_2^2 + 2x_3^2)
    // subject to x_1 + x_2 + x_3 >= 1; x >= 0

    @Test
    void solveQpReturnsExpectedSolution() {
        val colCost = new double[]{0., -1., 0.};
        val colLower = new double[]{0., 0., 0.};
        val colUpper = new double[]{1e30, 1e30, 1e30};
        val rowLower = new double[]{1.};
        val rowUpper = new double[]{1e30};
        // Constraint matrix: row-wise, single row [1, 1, 1]
        val a = new SparseMatrix(1, 3, MatrixFormat.ROWWISE,
                new long[]{0, 3}, new long[]{0, 1, 2}, new double[]{1., 1., 1.});
        // Hessian (triangular): 2x1^2 - 2x1x3 + 0.2x2^2 + 2x3^2
        val q = new HessianMatrix(3, HessianFormat.TRIANGULAR,
                new long[]{0, 2, 3, 4}, new long[]{0, 2, 1, 2}, new double[]{2., -1., 0.2, 2.});

        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, 0., colCost, colLower, colUpper, rowLower, rowUpper, a,
                    q, null);

            val status = model.solve();

            assertEquals(Status.OK, status);
            assertEquals(ModelStatus.OPTIMAL, model.modelStatus());

            val colValue = model.colValue();
            // Compute expected objective manually
            var expectedObj = 0.;
            for (int i = 0; i < colCost.length; i++) {
                expectedObj += colCost[i] * colValue[i];
            }
            // Add quadratic part
            val qStart = q.start();
            val qIndex = q.index();
            val qValue = q.value();
            for (int i = 0; i < colCost.length; i++) {
                val fromEl = (int) qStart[i];
                val toEl = (int) qStart[i + 1];
                for (int el = fromEl; el < toEl; el++) {
                    val j = (int) qIndex[el];
                    expectedObj += 0.5 * colValue[i] * colValue[j] * qValue[el];
                }
            }
            assertEquals(expectedObj, model.objectiveFunctionValue(), TOL);
        }
    }

    @Test
    void solveWithExternalArenaReturnsExpectedSolution() {
        try (val arena = Arena.ofConfined()) {
            try (val model = new Model(arena)) {
                model.setParameters(List.of(new BooleanParameter("output_flag", false)));
                model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);

                val status = model.solve();

                assertEquals(Status.OK, status);
                assertEquals(ModelStatus.OPTIMAL, model.modelStatus());
                assertEquals(5.75, model.objectiveFunctionValue(), TOL);
            }
        }
    }

    @Test
    void cleanupAndReuseReturnsExpectedSolution() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);
            model.solve();
            assertEquals(ModelStatus.OPTIMAL, model.modelStatus());

            model.cleanup();

            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);
            model.solve();
            assertEquals(ModelStatus.OPTIMAL, model.modelStatus());
            assertEquals(5.75, model.objectiveFunctionValue(), TOL);
        }
    }

    @Test
    void setParametersAfterSetupThrowsException() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);

            assertThrows(IllegalStateException.class, () -> model.setParameters(List.of()));
        }
    }

    @Test
    void solveBeforeSetupThrowsException() {
        try (val model = new Model()) {
            assertThrows(IllegalStateException.class, model::solve);
        }
    }

    @Test
    void resultBeforeSolveThrowsException() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);

            assertThrows(IllegalStateException.class, model::modelStatus);
        }
    }

    @Test
    void setupAfterSolveThrowsException() {
        try (val model = new Model()) {
            model.setParameters(List.of(new BooleanParameter("output_flag", false)));
            model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER, A);
            model.solve();

            assertThrows(IllegalStateException.class, () ->
                    model.setup(ObjectiveSense.MINIMIZE, OFFSET, COL_COST, COL_LOWER, COL_UPPER, ROW_LOWER, ROW_UPPER,
                            A));
        }
    }

    @Test
    void cleanupBeforeSolveThrowsException() {
        try (val model = new Model()) {
            assertThrows(IllegalStateException.class, model::cleanup);
        }
    }

}
