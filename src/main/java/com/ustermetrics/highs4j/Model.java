package com.ustermetrics.highs4j;

import lombok.NonNull;
import lombok.val;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;
import static java.lang.foreign.MemorySegment.NULL;

/**
 * An optimization model which can be solved with the <a href="https://highs.dev">HiGHS</a> solver.
 * <p>
 * Supports linear programs (LP), mixed-integer programs (MIP), and quadratic programs (QP).
 * <p>
 * To control the lifecycle of native memory, {@link Model} implements the {@link AutoCloseable} interface and should
 * be used with the <i>try-with-resources</i> statement or the {@link #close()} method needs to be called manually.
 */
public class Model implements AutoCloseable {

    private enum Stage {NEW, SETUP, SOLVED}

    private final Arena arena;
    private final boolean closeArena;
    private Stage stage = Stage.NEW;
    private List<Parameter> parameters;
    private MemorySegment highsSeg;
    private int numCol;
    private int numRow;

    /**
     * Creates a new {@link Model} instance, where the lifecycle of native memory is controlled by a new confined arena.
     * The arena is closed when the {@link Model} instance is closed.
     */
    public Model() {
        arena = Arena.ofConfined();
        closeArena = true;
    }

    /**
     * Creates a new {@link Model} instance, where the lifecycle of native memory is controlled by the given
     * {@link Arena} instance.
     *
     * @param arena {@link Arena} instance to control the lifecycle of native memory
     */
    public Model(Arena arena) {
        this.arena = arena;
        closeArena = false;
    }

    /**
     * Sets the <a href="https://highs.dev">HiGHS</a> solver parameters.
     * <p>
     * If not called, then solver defaults are applied.
     *
     * @param parameters list of parameters for the solver settings
     */
    public void setParameters(@NonNull List<Parameter> parameters) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        this.parameters = parameters;
    }

    /**
     * Set up this {@link Model} as a linear program (LP).
     *
     * @param sense    objective sense (minimize or maximize)
     * @param offset   objective offset
     * @param colCost  column cost coefficients
     * @param colLower column lower bounds
     * @param colUpper column upper bounds
     * @param rowLower row lower bounds
     * @param rowUpper row upper bounds
     * @param a        constraint matrix
     */
    public void setup(@NonNull ObjectiveSense sense, double offset,
                      double @NonNull [] colCost, double @NonNull [] colLower, double @NonNull [] colUpper,
                      double @NonNull [] rowLower, double @NonNull [] rowUpper, @NonNull SparseMatrix a) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        numCol = colCost.length;
        numRow = rowLower.length;
        val numNz = a.value().length;

        createAndApplyParameters();

        val colCostSeg = arena.allocateFrom(C_DOUBLE, colCost);
        val colLowerSeg = arena.allocateFrom(C_DOUBLE, colLower);
        val colUpperSeg = arena.allocateFrom(C_DOUBLE, colUpper);
        val rowLowerSeg = arena.allocateFrom(C_DOUBLE, rowLower);
        val rowUpperSeg = arena.allocateFrom(C_DOUBLE, rowUpper);
        val aStartSeg = arena.allocateFrom(C_LONG_LONG, a.start());
        val aIndexSeg = arena.allocateFrom(C_LONG_LONG, a.index());
        val aValueSeg = arena.allocateFrom(C_DOUBLE, a.value());

        checkStatus(Highs_passLp(highsSeg, numCol, numRow, numNz, a.matrixFormat().format(), sense.sense(), offset,
                colCostSeg, colLowerSeg, colUpperSeg, rowLowerSeg, rowUpperSeg, aStartSeg, aIndexSeg, aValueSeg));

        stage = Stage.SETUP;
    }

    /**
     * Set up this {@link Model} as a mixed-integer program (MIP).
     *
     * @param sense       objective sense (minimize or maximize)
     * @param offset      objective offset
     * @param colCost     column cost coefficients
     * @param colLower    column lower bounds
     * @param colUpper    column upper bounds
     * @param rowLower    row lower bounds
     * @param rowUpper    row upper bounds
     * @param a           constraint matrix
     * @param integrality variable integrality constraints
     */
    public void setup(@NonNull ObjectiveSense sense, double offset,
                      double @NonNull [] colCost, double @NonNull [] colLower, double @NonNull [] colUpper,
                      double @NonNull [] rowLower, double @NonNull [] rowUpper, @NonNull SparseMatrix a,
                      @NonNull Integrality[] integrality) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        numCol = colCost.length;
        numRow = rowLower.length;
        val numNz = a.value().length;

        createAndApplyParameters();

        val colCostSeg = arena.allocateFrom(C_DOUBLE, colCost);
        val colLowerSeg = arena.allocateFrom(C_DOUBLE, colLower);
        val colUpperSeg = arena.allocateFrom(C_DOUBLE, colUpper);
        val rowLowerSeg = arena.allocateFrom(C_DOUBLE, rowLower);
        val rowUpperSeg = arena.allocateFrom(C_DOUBLE, rowUpper);
        val aStartSeg = arena.allocateFrom(C_LONG_LONG, a.start());
        val aIndexSeg = arena.allocateFrom(C_LONG_LONG, a.index());
        val aValueSeg = arena.allocateFrom(C_DOUBLE, a.value());
        val integralitySeg = arena.allocateFrom(C_LONG_LONG,
                Arrays.stream(integrality).mapToLong(Integrality::integrality).toArray());

        checkStatus(Highs_passMip(highsSeg, numCol, numRow, numNz, a.matrixFormat().format(), sense.sense(), offset,
                colCostSeg, colLowerSeg, colUpperSeg, rowLowerSeg, rowUpperSeg, aStartSeg, aIndexSeg, aValueSeg,
                integralitySeg));

        stage = Stage.SETUP;
    }

    /**
     * Set up this {@link Model} as a quadratic program (QP), optionally with integer constraints.
     *
     * @param sense       objective sense (minimize or maximize)
     * @param offset      objective offset
     * @param colCost     column cost coefficients
     * @param colLower    column lower bounds
     * @param colUpper    column upper bounds
     * @param rowLower    row lower bounds
     * @param rowUpper    row upper bounds
     * @param a           constraint matrix
     * @param q           Hessian matrix
     * @param integrality variable integrality constraints (may be {@code null})
     */
    public void setup(@NonNull ObjectiveSense sense, double offset,
                      double @NonNull [] colCost, double @NonNull [] colLower, double @NonNull [] colUpper,
                      double @NonNull [] rowLower, double @NonNull [] rowUpper, @NonNull SparseMatrix a,
                      @NonNull HessianMatrix q, Integrality[] integrality) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        numCol = colCost.length;
        numRow = rowLower.length;
        val numNz = a.value().length;
        val qNumNz = q.value().length;

        createAndApplyParameters();

        val colCostSeg = arena.allocateFrom(C_DOUBLE, colCost);
        val colLowerSeg = arena.allocateFrom(C_DOUBLE, colLower);
        val colUpperSeg = arena.allocateFrom(C_DOUBLE, colUpper);
        val rowLowerSeg = arena.allocateFrom(C_DOUBLE, rowLower);
        val rowUpperSeg = arena.allocateFrom(C_DOUBLE, rowUpper);
        val aStartSeg = arena.allocateFrom(C_LONG_LONG, a.start());
        val aIndexSeg = arena.allocateFrom(C_LONG_LONG, a.index());
        val aValueSeg = arena.allocateFrom(C_DOUBLE, a.value());
        val qStartSeg = arena.allocateFrom(C_LONG_LONG, q.start());
        val qIndexSeg = arena.allocateFrom(C_LONG_LONG, q.index());
        val qValueSeg = arena.allocateFrom(C_DOUBLE, q.value());
        val integralitySeg = integrality != null
                ? arena.allocateFrom(C_LONG_LONG,
                Arrays.stream(integrality).mapToLong(Integrality::integrality).toArray())
                : NULL;

        checkStatus(Highs_passModel(highsSeg, numCol, numRow, numNz, qNumNz, a.matrixFormat().format(),
                q.hessianFormat().format(), sense.sense(), offset,
                colCostSeg, colLowerSeg, colUpperSeg, rowLowerSeg, rowUpperSeg, aStartSeg, aIndexSeg, aValueSeg,
                qStartSeg, qIndexSeg, qValueSeg, integralitySeg));

        stage = Stage.SETUP;
    }

    /**
     * Solves this {@link Model} with the <a href="https://highs.dev">HiGHS</a> solver.
     *
     * @return solver run status
     */
    public Status solve() {
        checkState(stage == Stage.SETUP, "model must be in stage setup");

        val runStatus = Highs_run(highsSeg);
        stage = Stage.SOLVED;

        return Status.valueOf((int) runStatus);
    }

    /**
     * @return model status of this solved {@link Model}
     */
    public ModelStatus modelStatus() {
        checkStageIsSolved();
        return ModelStatus.valueOf((int) Highs_getModelStatus(highsSeg));
    }

    /**
     * @return objective function value of this solved {@link Model}
     */
    public double objectiveFunctionValue() {
        checkStageIsSolved();
        return getDoubleInfo("objective_function_value");
    }

    /**
     * @return simplex iteration count of this solved {@link Model}
     */
    public long simplexIterationCount() {
        checkStageIsSolved();
        return getIntInfo("simplex_iteration_count");
    }

    /**
     * @return primal solution status of this solved {@link Model}
     */
    public long primalSolutionStatus() {
        checkStageIsSolved();
        return getIntInfo("primal_solution_status");
    }

    /**
     * @return dual solution status of this solved {@link Model}
     */
    public long dualSolutionStatus() {
        checkStageIsSolved();
        return getIntInfo("dual_solution_status");
    }

    /**
     * @return basis validity of this solved {@link Model}
     */
    public long basisValidity() {
        checkStageIsSolved();
        return getIntInfo("basis_validity");
    }

    /**
     * @return column primal values of this solved {@link Model}
     */
    public double @NonNull [] colValue() {
        checkStageIsSolved();
        return getSolution()[0];
    }

    /**
     * @return column dual values of this solved {@link Model}
     */
    public double @NonNull [] colDual() {
        checkStageIsSolved();
        return getSolution()[1];
    }

    /**
     * @return row primal values of this solved {@link Model}
     */
    public double @NonNull [] rowValue() {
        checkStageIsSolved();
        return getSolution()[2];
    }

    /**
     * @return row dual values of this solved {@link Model}
     */
    public double @NonNull [] rowDual() {
        checkStageIsSolved();
        return getSolution()[3];
    }

    /**
     * @return column basis status of this solved {@link Model}
     */
    public BasisStatus @NonNull [] colBasisStatus() {
        checkStageIsSolved();
        return getBasis()[0];
    }

    /**
     * @return row basis status of this solved {@link Model}
     */
    public BasisStatus @NonNull [] rowBasisStatus() {
        checkStageIsSolved();
        return getBasis()[1];
    }

    /**
     * @return objective sense of this solved {@link Model}
     */
    public ObjectiveSense objectiveSense() {
        checkStageIsSolved();
        val senseSeg = arena.allocate(C_LONG_LONG);
        checkStatus(Highs_getObjectiveSense(highsSeg, senseSeg));
        return ObjectiveSense.valueOf((int) senseSeg.get(C_LONG_LONG, 0));
    }

    /**
     * Cleanup: destroy the HiGHS instance and reset to stage new.
     */
    public void cleanup() {
        checkState(stage != Stage.NEW, "model must not be in stage new");
        Highs_destroy(highsSeg);
        highsSeg = null;
        stage = Stage.NEW;
    }

    /**
     * Closes this {@link Model} and releases all associated native resources. If the {@link Model} was created with
     * its own confined arena, the arena is also closed.
     */
    @Override
    public void close() {
        if (stage != Stage.NEW) {
            Highs_destroy(highsSeg);
        }
        if (closeArena) {
            arena.close();
        }
    }

    private void createAndApplyParameters() {
        highsSeg = Highs_create();

        if (parameters != null) {
            for (val parameter : parameters) {
                switch (parameter) {
                    case BooleanParameter p ->
                            checkStatus(Highs_setBoolOptionValue(highsSeg, arena.allocateFrom(p.name()),
                                    p.value() ? 1 : 0));
                    case IntParameter p ->
                            checkStatus(Highs_setIntOptionValue(highsSeg, arena.allocateFrom(p.name()), p.value()));
                    case DoubleParameter p ->
                            checkStatus(Highs_setDoubleOptionValue(highsSeg, arena.allocateFrom(p.name()), p.value()));
                    case StringParameter p ->
                            checkStatus(Highs_setStringOptionValue(highsSeg, arena.allocateFrom(p.name()),
                                    arena.allocateFrom(p.value())));
                }
            }
        }
    }

    private double getDoubleInfo(String info) {
        val valueSeg = arena.allocate(C_DOUBLE);
        checkStatus(Highs_getDoubleInfoValue(highsSeg, arena.allocateFrom(info), valueSeg));
        return valueSeg.get(C_DOUBLE, 0);
    }

    private long getIntInfo(String info) {
        val valueSeg = arena.allocate(C_LONG_LONG);
        checkStatus(Highs_getIntInfoValue(highsSeg, arena.allocateFrom(info), valueSeg));
        return valueSeg.get(C_LONG_LONG, 0);
    }

    private double[][] getSolution() {
        val colValueSeg = arena.allocate(C_DOUBLE, numCol);
        val colDualSeg = arena.allocate(C_DOUBLE, numCol);
        val rowValueSeg = arena.allocate(C_DOUBLE, numRow);
        val rowDualSeg = arena.allocate(C_DOUBLE, numRow);
        checkStatus(Highs_getSolution(highsSeg, colValueSeg, colDualSeg, rowValueSeg, rowDualSeg));
        return new double[][]{
                colValueSeg.toArray(C_DOUBLE),
                colDualSeg.toArray(C_DOUBLE),
                rowValueSeg.toArray(C_DOUBLE),
                rowDualSeg.toArray(C_DOUBLE)
        };
    }

    private BasisStatus[][] getBasis() {
        val colBasisStatusSeg = arena.allocate(C_LONG_LONG, numCol);
        val rowBasisStatusSeg = arena.allocate(C_LONG_LONG, numRow);
        checkStatus(Highs_getBasis(highsSeg, colBasisStatusSeg, rowBasisStatusSeg));
        val colBasis = Arrays.stream(colBasisStatusSeg.toArray(C_LONG_LONG))
                .mapToObj(s -> BasisStatus.valueOf((int) s))
                .toArray(BasisStatus[]::new);
        val rowBasis = Arrays.stream(rowBasisStatusSeg.toArray(C_LONG_LONG))
                .mapToObj(s -> BasisStatus.valueOf((int) s))
                .toArray(BasisStatus[]::new);
        return new BasisStatus[][]{colBasis, rowBasis};
    }

    private void checkStageIsSolved() {
        checkState(stage == Stage.SOLVED, "model must be in stage solved");
    }

    private static void checkStatus(long status) {
        if (status != kHighsStatusOk() && status != kHighsStatusWarning()) {
            throw new IllegalStateException("HiGHS returned error status " + status);
        }
    }

}
