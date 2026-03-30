# Model Class Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a `Model` class that wraps HiGHS native bindings, enabling the same LP workflow tested in `BindingsTest` through a clean, high-level Java API.

**Architecture:** `Model` follows the clarabel4j `Model` pattern: `AutoCloseable` with `Arena`-based native memory management, a three-stage state machine (NEW → SETUP → SOLVED), and eager solution caching after solve. Under the hood it delegates to the same `Highs_c_api_h` bindings used in `BindingsTest`. Parameters are passed as `List<Parameter>` using the existing sealed interface hierarchy.

**Tech Stack:** Java 25, Foreign Function & Memory API, Lombok (`@NonNull`, `val`), Guava (`checkState`), JUnit Jupiter 6.0.3

---

## File Structure

| Action | Path | Responsibility |
|--------|------|----------------|
| Create | `src/main/java/com/ustermetrics/highs4j/Model.java` | High-level solver wrapper with lifecycle, parameter application, LP passing, solving, and solution accessors |
| Create | `src/test/java/com/ustermetrics/highs4j/ModelTest.java` | Tests reproducing `BindingsTest` LP workflow plus state machine validation |

---

### Task 1: Write the failing LP test

**Files:**
- Create: `src/test/java/com/ustermetrics/highs4j/ModelTest.java`

- [ ] **Step 1: Write ModelTest with solveLinearProgramReturnsExpectedSolution**

This test reproduces `BindingsTest.solveLinearProgramReturnsExpectedSolution` using the `Model` class.

```java
package com.ustermetrics.highs4j;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ustermetrics.highs4j.BasisStatus.BASIC;
import static com.ustermetrics.highs4j.BasisStatus.LOWER;
import static com.ustermetrics.highs4j.ModelStatus.OPTIMAL;
import static com.ustermetrics.highs4j.ObjectiveSense.MINIMIZE;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsBasisValidityValid;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.kHighsSolutionStatusFeasible;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private static final double TOLERANCE = 1e-8;

    @Test
    void solveLinearProgramReturnsExpectedSolution() {
        // Linear program from the HiGHS examples
        // https://github.com/atraplet/HiGHS/blob/master/examples/call_highs_from_c.c
        val parameters = List.<Parameter>of(new BooleanParameter("output_flag", false));

        try (val model = new Model()) {
            model.setParameters(parameters);
            model.passLp(MINIMIZE, 3.,
                    new double[]{1., 1.},
                    new double[]{0., 1.},
                    new double[]{4., 1e30},
                    new double[]{-1e30, 5., 6.},
                    new double[]{7., 15., 1e30},
                    MatrixFormat.COLWISE,
                    new long[]{0, 2},
                    new long[]{1, 2, 0, 1, 2},
                    new double[]{1., 3., 1., 2., 2.});

            val modelStatus = model.solve();

            assertEquals(OPTIMAL, modelStatus);
            assertEquals(5.75, model.getObjectiveValue(), TOLERANCE);
            assertEquals(2, model.getIntInfoValue("simplex_iteration_count"));
            assertEquals(kHighsSolutionStatusFeasible(), model.getIntInfoValue("primal_solution_status"));
            assertEquals(kHighsSolutionStatusFeasible(), model.getIntInfoValue("dual_solution_status"));
            assertEquals(kHighsBasisValidityValid(), model.getIntInfoValue("basis_validity"));
            assertArrayEquals(new double[]{0.5, 2.25}, model.colValue(), TOLERANCE);
            assertArrayEquals(new double[]{0., 0.}, model.colDual(), TOLERANCE);
            assertArrayEquals(new double[]{2.25, 5., 6.}, model.rowValue(), TOLERANCE);
            assertArrayEquals(new double[]{0., 0.25, 0.25}, model.rowDual(), TOLERANCE);
            assertArrayEquals(new BasisStatus[]{BASIC, BASIC}, model.colBasisStatus());
            assertArrayEquals(new BasisStatus[]{BASIC, LOWER, LOWER}, model.rowBasisStatus());
            assertEquals(MINIMIZE, model.getObjectiveSense());
        }
    }

}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -Dtest=ModelTest -pl . 2>&1 | tail -20`
Expected: Compilation failure — `Model` class does not exist.

---

### Task 2: Write Model class implementation

**Files:**
- Create: `src/main/java/com/ustermetrics/highs4j/Model.java`

- [ ] **Step 3: Write Model.java**

```java
package com.ustermetrics.highs4j;

import lombok.NonNull;
import lombok.val;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.ustermetrics.highs4j.bindings.Highs_c_api_h.*;

/**
 * An optimization model which can be solved with the <a href="https://highs.dev">HiGHS</a> solver.
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
    private double[] colValue;
    private double[] colDual;
    private double[] rowValue;
    private double[] rowDual;
    private BasisStatus[] colBasisStatus;
    private BasisStatus[] rowBasisStatus;

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
     * Pass a linear program (LP) to this {@link Model}.
     *
     * @param sense    objective sense (minimize or maximize)
     * @param offset   objective offset
     * @param colCost  column costs (objective coefficients)
     * @param colLower column lower bounds
     * @param colUpper column upper bounds
     * @param rowLower row lower bounds
     * @param rowUpper row upper bounds
     * @param aFormat  constraint matrix format
     * @param aStart   constraint matrix start indices
     * @param aIndex   constraint matrix row/column indices
     * @param aValue   constraint matrix values
     */
    public void passLp(@NonNull ObjectiveSense sense, double offset,
                       double @NonNull [] colCost, double @NonNull [] colLower, double @NonNull [] colUpper,
                       double @NonNull [] rowLower, double @NonNull [] rowUpper,
                       @NonNull MatrixFormat aFormat, long @NonNull [] aStart, long @NonNull [] aIndex,
                       double @NonNull [] aValue) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        numCol = colCost.length;
        numRow = rowLower.length;
        val numNz = aValue.length;

        highsSeg = Highs_create();
        applyParameters();

        checkStatus(Highs_passLp(highsSeg, numCol, numRow, numNz, aFormat.format(), sense.sense(), offset,
                arena.allocateFrom(C_DOUBLE, colCost),
                arena.allocateFrom(C_DOUBLE, colLower),
                arena.allocateFrom(C_DOUBLE, colUpper),
                arena.allocateFrom(C_DOUBLE, rowLower),
                arena.allocateFrom(C_DOUBLE, rowUpper),
                arena.allocateFrom(C_LONG_LONG, aStart),
                arena.allocateFrom(C_LONG_LONG, aIndex),
                arena.allocateFrom(C_DOUBLE, aValue)));

        stage = Stage.SETUP;
    }

    /**
     * Solves this {@link Model} with the <a href="https://highs.dev">HiGHS</a> solver.
     *
     * @return model status after optimization
     */
    public ModelStatus solve() {
        checkState(stage != Stage.NEW, "model must not be in stage new");

        checkStatus(Highs_run(highsSeg));

        val colValueSeg = arena.allocate(C_DOUBLE, numCol);
        val colDualSeg = arena.allocate(C_DOUBLE, numCol);
        val rowValueSeg = arena.allocate(C_DOUBLE, numRow);
        val rowDualSeg = arena.allocate(C_DOUBLE, numRow);
        checkStatus(Highs_getSolution(highsSeg, colValueSeg, colDualSeg, rowValueSeg, rowDualSeg));
        colValue = colValueSeg.toArray(C_DOUBLE);
        colDual = colDualSeg.toArray(C_DOUBLE);
        rowValue = rowValueSeg.toArray(C_DOUBLE);
        rowDual = rowDualSeg.toArray(C_DOUBLE);

        val colBasisStatusSeg = arena.allocate(C_LONG_LONG, numCol);
        val rowBasisStatusSeg = arena.allocate(C_LONG_LONG, numRow);
        checkStatus(Highs_getBasis(highsSeg, colBasisStatusSeg, rowBasisStatusSeg));
        val colBasisRaw = colBasisStatusSeg.toArray(C_LONG_LONG);
        val rowBasisRaw = rowBasisStatusSeg.toArray(C_LONG_LONG);
        colBasisStatus = new BasisStatus[numCol];
        for (int i = 0; i < numCol; i++) {
            colBasisStatus[i] = BasisStatus.valueOf((int) colBasisRaw[i]);
        }
        rowBasisStatus = new BasisStatus[numRow];
        for (int i = 0; i < numRow; i++) {
            rowBasisStatus[i] = BasisStatus.valueOf((int) rowBasisRaw[i]);
        }

        val modelStatus = ModelStatus.valueOf((int) Highs_getModelStatus(highsSeg));
        stage = Stage.SOLVED;

        return modelStatus;
    }

    /**
     * @return column (primal) values of this solved {@link Model}
     */
    public double @NonNull [] colValue() {
        checkStageIsSolved();
        return colValue;
    }

    /**
     * @return column dual values of this solved {@link Model}
     */
    public double @NonNull [] colDual() {
        checkStageIsSolved();
        return colDual;
    }

    /**
     * @return row (constraint) values of this solved {@link Model}
     */
    public double @NonNull [] rowValue() {
        checkStageIsSolved();
        return rowValue;
    }

    /**
     * @return row dual values of this solved {@link Model}
     */
    public double @NonNull [] rowDual() {
        checkStageIsSolved();
        return rowDual;
    }

    /**
     * @return column basis status of this solved {@link Model}
     */
    public BasisStatus @NonNull [] colBasisStatus() {
        checkStageIsSolved();
        return colBasisStatus;
    }

    /**
     * @return row basis status of this solved {@link Model}
     */
    public BasisStatus @NonNull [] rowBasisStatus() {
        checkStageIsSolved();
        return rowBasisStatus;
    }

    /**
     * @return objective function value of this solved {@link Model}
     */
    public double getObjectiveValue() {
        checkStageIsSolved();
        return Highs_getObjectiveValue(highsSeg);
    }

    /**
     * @return objective sense of this solved {@link Model}
     */
    public ObjectiveSense getObjectiveSense() {
        checkStageIsSolved();
        val senseSeg = arena.allocate(C_LONG_LONG);
        checkStatus(Highs_getObjectiveSense(highsSeg, senseSeg));
        return ObjectiveSense.valueOf((int) senseSeg.get(C_LONG_LONG, 0));
    }

    /**
     * Gets an integer info value from this solved {@link Model}.
     *
     * @param info the info name
     * @return the integer info value
     */
    public int getIntInfoValue(@NonNull String info) {
        checkStageIsSolved();
        val valueSeg = arena.allocate(C_LONG_LONG);
        checkStatus(Highs_getIntInfoValue(highsSeg, arena.allocateFrom(info), valueSeg));
        return (int) valueSeg.get(C_LONG_LONG, 0);
    }

    /**
     * Gets a double info value from this solved {@link Model}.
     *
     * @param info the info name
     * @return the double info value
     */
    public double getDoubleInfoValue(@NonNull String info) {
        checkStageIsSolved();
        val valueSeg = arena.allocate(C_DOUBLE);
        checkStatus(Highs_getDoubleInfoValue(highsSeg, arena.allocateFrom(info), valueSeg));
        return valueSeg.get(C_DOUBLE, 0);
    }

    /**
     * Gets a 64-bit integer info value from this solved {@link Model}.
     *
     * @param info the info name
     * @return the 64-bit integer info value
     */
    public long getInt64InfoValue(@NonNull String info) {
        checkStageIsSolved();
        val valueSeg = arena.allocate(C_LONG_LONG);
        checkStatus(Highs_getInt64InfoValue(highsSeg, arena.allocateFrom(info), valueSeg));
        return valueSeg.get(C_LONG_LONG, 0);
    }

    /**
     * Cleanup: free this {@link Model} native memory.
     */
    public void cleanup() {
        checkState(stage != Stage.NEW, "model must not be in stage new");
        Highs_destroy(highsSeg);
        stage = Stage.NEW;
    }

    @Override
    public void close() {
        if (stage != Stage.NEW) {
            Highs_destroy(highsSeg);
        }
        if (closeArena) {
            arena.close();
        }
    }

    private void applyParameters() {
        if (parameters != null) {
            for (val param : parameters) {
                switch (param) {
                    case BooleanParameter bp -> checkStatus(Highs_setBoolOptionValue(
                            highsSeg, arena.allocateFrom(bp.name()), bp.value() ? 1 : 0));
                    case IntParameter ip -> checkStatus(Highs_setIntOptionValue(
                            highsSeg, arena.allocateFrom(ip.name()), ip.value()));
                    case DoubleParameter dp -> checkStatus(Highs_setDoubleOptionValue(
                            highsSeg, arena.allocateFrom(dp.name()), dp.value()));
                    case StringParameter sp -> checkStatus(Highs_setStringOptionValue(
                            highsSeg, arena.allocateFrom(sp.name()), arena.allocateFrom(sp.value())));
                }
            }
        }
    }

    private void checkStatus(long status) {
        val s = Status.valueOf((int) status);
        if (s == Status.ERROR) {
            throw new IllegalStateException("HiGHS returned error status");
        }
    }

    private void checkStageIsSolved() {
        checkState(stage == Stage.SOLVED, "model must be in stage solved");
    }

}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -Dtest=ModelTest#solveLinearProgramReturnsExpectedSolution -pl . 2>&1 | tail -20`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/ustermetrics/highs4j/Model.java src/test/java/com/ustermetrics/highs4j/ModelTest.java
git commit -m "feat: add Model class with LP support and test"
```

---

### Task 3: Add arena test and cleanup/reuse test

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ModelTest.java`

- [ ] **Step 6: Add solveLinearProgramWithArenaReturnsExpectedSolution and solveProblemTwiceWithCleanupInBetweenReturnsExpectedSolution**

Append these tests to `ModelTest`:

```java
@Test
void solveLinearProgramWithArenaReturnsExpectedSolution() {
    val parameters = List.<Parameter>of(new BooleanParameter("output_flag", false));

    try (val arena = Arena.ofConfined();
         val model = new Model(arena)) {
        model.setParameters(parameters);
        model.passLp(MINIMIZE, 3.,
                new double[]{1., 1.},
                new double[]{0., 1.},
                new double[]{4., 1e30},
                new double[]{-1e30, 5., 6.},
                new double[]{7., 15., 1e30},
                MatrixFormat.COLWISE,
                new long[]{0, 2},
                new long[]{1, 2, 0, 1, 2},
                new double[]{1., 3., 1., 2., 2.});

        val modelStatus = model.solve();

        assertEquals(OPTIMAL, modelStatus);
        assertArrayEquals(new double[]{0.5, 2.25}, model.colValue(), TOLERANCE);
    }
}

@Test
void solveProblemTwiceWithCleanupInBetweenReturnsExpectedSolution() {
    val parameters = List.<Parameter>of(new BooleanParameter("output_flag", false));

    try (val model = new Model()) {
        model.setParameters(parameters);
        model.passLp(MINIMIZE, 3.,
                new double[]{1., 1.},
                new double[]{0., 1.},
                new double[]{4., 1e30},
                new double[]{-1e30, 5., 6.},
                new double[]{7., 15., 1e30},
                MatrixFormat.COLWISE,
                new long[]{0, 2},
                new long[]{1, 2, 0, 1, 2},
                new double[]{1., 3., 1., 2., 2.});
        var modelStatus = model.solve();

        assertEquals(OPTIMAL, modelStatus);
        assertArrayEquals(new double[]{0.5, 2.25}, model.colValue(), TOLERANCE);

        model.cleanup();

        model.passLp(MINIMIZE, 0.,
                new double[]{1., 1.},
                new double[]{0., 1.},
                new double[]{4., 1e30},
                new double[]{-1e30, 5., 6.},
                new double[]{7., 15., 1e30},
                MatrixFormat.COLWISE,
                new long[]{0, 2},
                new long[]{1, 2, 0, 1, 2},
                new double[]{1., 3., 1., 2., 2.});
        modelStatus = model.solve();

        assertEquals(OPTIMAL, modelStatus);
        assertArrayEquals(new double[]{0.5, 2.25}, model.colValue(), TOLERANCE);
    }
}
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `mvn test -Dtest=ModelTest -pl . 2>&1 | tail -20`
Expected: All 3 tests PASS

- [ ] **Step 8: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ModelTest.java
git commit -m "test: add arena and cleanup/reuse tests for Model"
```

---

### Task 4: Add state machine validation tests

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ModelTest.java`

- [ ] **Step 9: Add state machine tests**

Append these tests to `ModelTest`:

```java
@Test
void passLpAfterSolveThrowsException() {
    val parameters = List.<Parameter>of(new BooleanParameter("output_flag", false));

    val exception = assertThrows(IllegalStateException.class, () -> {
        try (val model = new Model()) {
            model.setParameters(parameters);
            model.passLp(MINIMIZE, 3.,
                    new double[]{1., 1.},
                    new double[]{0., 1.},
                    new double[]{4., 1e30},
                    new double[]{-1e30, 5., 6.},
                    new double[]{7., 15., 1e30},
                    MatrixFormat.COLWISE,
                    new long[]{0, 2},
                    new long[]{1, 2, 0, 1, 2},
                    new double[]{1., 3., 1., 2., 2.});
            model.solve();
            model.passLp(MINIMIZE, 0.,
                    new double[]{1., 1.},
                    new double[]{0., 1.},
                    new double[]{4., 1e30},
                    new double[]{-1e30, 5., 6.},
                    new double[]{7., 15., 1e30},
                    MatrixFormat.COLWISE,
                    new long[]{0, 2},
                    new long[]{1, 2, 0, 1, 2},
                    new double[]{1., 3., 1., 2., 2.});
        }
    });

    assertEquals("model must be in stage new", exception.getMessage());
}

@Test
void setParametersAfterPassLpThrowsException() {
    val parameters = List.<Parameter>of(new BooleanParameter("output_flag", false));

    val exception = assertThrows(IllegalStateException.class, () -> {
        try (val model = new Model()) {
            model.passLp(MINIMIZE, 3.,
                    new double[]{1., 1.},
                    new double[]{0., 1.},
                    new double[]{4., 1e30},
                    new double[]{-1e30, 5., 6.},
                    new double[]{7., 15., 1e30},
                    MatrixFormat.COLWISE,
                    new long[]{0, 2},
                    new long[]{1, 2, 0, 1, 2},
                    new double[]{1., 3., 1., 2., 2.});
            model.setParameters(parameters);
        }
    });

    assertEquals("model must be in stage new", exception.getMessage());
}

@Test
void solveBeforePassLpThrowsException() {
    val exception = assertThrows(IllegalStateException.class, () -> {
        try (val model = new Model()) {
            model.solve();
        }
    });

    assertEquals("model must not be in stage new", exception.getMessage());
}

@Test
void getColValueBeforeSolveThrowsException() {
    val exception = assertThrows(IllegalStateException.class, () -> {
        try (val model = new Model()) {
            model.colValue();
        }
    });

    assertEquals("model must be in stage solved", exception.getMessage());
}

@Test
void cleanupBeforePassLpThrowsException() {
    val exception = assertThrows(IllegalStateException.class, () -> {
        try (val model = new Model()) {
            model.cleanup();
        }
    });

    assertEquals("model must not be in stage new", exception.getMessage());
}
```

- [ ] **Step 10: Run all tests to verify they pass**

Run: `mvn test -Dtest=ModelTest -pl . 2>&1 | tail -20`
Expected: All 8 tests PASS

- [ ] **Step 11: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ModelTest.java
git commit -m "test: add state machine validation tests for Model"
```

---

### Task 5: Run full test suite

- [ ] **Step 12: Run all tests including BindingsTest and all existing tests**

Run: `mvn test 2>&1 | tail -30`
Expected: All tests PASS (ModelTest + BindingsTest + ParameterTest + enum tests)

- [ ] **Step 13: Commit (if any fixes needed)**

Only if changes were required in Step 12.
