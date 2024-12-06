package com.ustermetrics.ecos4j;

import com.ustermetrics.ecos4j.bindings.pwork;
import com.ustermetrics.ecos4j.bindings.settings;
import com.ustermetrics.ecos4j.bindings.stats;
import lombok.NonNull;
import lombok.val;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;
import static com.ustermetrics.ecos4j.bindings.ecos_h.*;
import static java.lang.Math.toIntExact;
import static java.lang.foreign.MemorySegment.NULL;

/**
 * An optimization model which can be optimized with the <a href="https://github.com/embotech/ecos">ECOS</a> solver.
 * <p>
 * In order to control the lifecycle of native memory, {@link Model} implements the {@link AutoCloseable}
 * interface and should be used with the <i>try-with-resources</i> statement.
 */
public class Model implements AutoCloseable {

    private enum Stage {NEW, SETUP, OPTIMIZED}

    private final Arena arena = Arena.ofConfined();
    private Stage stage = Stage.NEW;
    private Parameters parameters;
    private long n;
    private long m;
    private long p;
    private MemorySegment workSeg;
    private MemorySegment stgsSeg;
    private MemorySegment infoSeg;
    private Status status;

    /**
     * @return the version of the <a href="https://github.com/embotech/ecos">ECOS</a> solver.
     */
    @NonNull
    public static String version() {
        return ECOS_ver().getString(0);
    }

    /**
     * Sets the <a href="https://github.com/embotech/ecos">ECOS</a> solver options.
     * <p>
     * If not called, then solver defaults are applied.
     *
     * @param parameters the parameter object for the solver options.
     */
    public void setParameters(@NonNull Parameters parameters) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        this.parameters = parameters;
    }

    /**
     * Set up the {@link Model} data for a convex second-order cone program (SOCP) of type
     * <pre>
     * minimize        c'x
     * subject to      Gx + s = h
     *                 Ax = b
     *                 s in K
     * </pre>
     * where x are the primal variables, s are slack variables, c, G, h, A, and b are the model data, and K is the
     * convex cone. The cone K is the Cartesian product of the positive orthant cone, second-order, and exponential
     * cones.
     *
     * @param l    the dimension of the positive orthant.
     * @param q    the dimensions of the second-order cones.
     * @param nExC the number of exponential cones.
     * @param gpr  the sparse G matrix data (Column Compressed Storage CCS).
     * @param gjc  the sparse G matrix column index (CCS).
     * @param gir  the sparse G matrix row index (CCS). Entries within each column need to appear in order of
     *             increasing row index.
     * @param c    the cost function weights.
     * @param h    the right-hand-side of the cone constraints.
     * @param apr  the (optional) sparse A matrix data (CCS).
     * @param ajc  the (optional) sparse A matrix column index (CCS).
     * @param air  the (optional) sparse A matrix row index (CCS). Entries within each column need to appear in order
     *             of increasing row index.
     * @param b    the (optional) right-hand-side of the equalities.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public void setup(long l, long @NonNull [] q, long nExC, double @NonNull [] gpr, long @NonNull [] gjc,
                      long @NonNull [] gir, double @NonNull [] c, double @NonNull [] h, double[] apr, long[] ajc,
                      long[] air, double[] b) {
        checkArguments(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);
        unsafeSetup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);
    }

    private static void checkArguments(long l, long[] q, long nExC, double[] gpr, long[] gjc, long[] gir, double[] c,
                                       double[] h, double[] apr, long[] ajc, long[] air, double[] b) {
        checkCone(l, q, nExC, h);
        checkMatrix(gpr, gjc, gir, h.length, c.length, "G");
        checkArgument(apr != null && ajc != null && air != null && b != null || apr == null && ajc == null && air == null && b == null,
                "all arguments of the equalities must be supplied together or must be null together");
        if (apr != null) {
            checkMatrix(apr, ajc, air, b.length, c.length, "A");
        }
    }

    private static void checkCone(long l, long[] q, long nExC, double[] h) {
        checkArgument(l >= 0, "dimension of the positive orthant must be non-negative");
        checkArgument(q.length == 0 || Arrays.stream(q).allMatch(i -> i > 0),
                "second-order cone dimensions must have zero length or each dimension must be positive");
        checkArgument(nExC >= 0, "number of exponential cones must be non-negative");
        checkArgument(h.length == l + Arrays.stream(q).sum() + 3 * nExC,
                "dimension of the convex cone K must be equal to the sum of the positive orthant dimension, the " +
                        "second-order cone dimensions, and three times the number of exponential cones");
    }

    private static void checkMatrix(double[] mpr, long[] mjc, long[] mir, int nRows, int nCols, String mName) {
        checkArgument(nRows > 0, "matrix %s: number of rows must be positive", mName);
        checkArgument(nCols > 0, "matrix %s: number of columns must be positive", mName);
        val nnz = mpr.length;
        checkArgument(0 < nnz && nnz <= nRows * nCols,
                "matrix %s: number of non-zero entries must be greater than zero and less equal than the number of " +
                        "rows times the number of columns", mName);
        checkArgument(mir.length == nnz,
                "matrix %s: number of entries in the row index must be equal to the number of non-zero entries",
                mName);
        checkArgument(mjc.length == nCols + 1,
                "length of the column index must be equal to the number of columns plus one", mName);
        checkArgument(Arrays.stream(mir).allMatch(i -> 0 <= i && i < nRows),
                "matrix %s: entries of the row index must be greater equal zero and less than the number of rows",
                mName);
        checkArgument(mjc[0] == 0 && mjc[mjc.length - 1] == nnz,
                "matrix %s: the first entry of the column index must be equal to zero and the last entry must be " +
                        "equal to the number of non-zero entries", mName);
        checkArgument(IntStream.range(0, mjc.length - 1).allMatch(i ->
                        0 <= mjc[i] && mjc[i] <= nnz && mjc[i] <= mjc[i + 1]
                                && IntStream.range(toIntExact(mjc[i]), toIntExact(mjc[i + 1]) - 1).allMatch(j -> mir[j] < mir[j + 1])),
                "matrix %s: entries of the column index must be greater equal zero, less equal than the number of " +
                        "non-zero entries, and must be ordered, entries of the row index within each column must be " +
                        "strictly ordered", mName);
    }

    /**
     * Set up the {@link Model} data.
     * <p>
     * Same as
     * {@link Model#setup(long l, long[] q, long nExC, double[] gpr, long[] gjc, long[] gir, double[] c, double[] h, double[] apr, long[] ajc, long[] air, double[] b)}
     * without equality constraint, i.e. {@code apr}, {@code ajc}, {@code air}, and {@code b} are {@code null}.
     *
     * @param l    the dimension of the positive orthant.
     * @param q    the dimensions of the second-order cones.
     * @param nExC the number of exponential cones.
     * @param gpr  the sparse G matrix data (Column Compressed Storage CCS).
     * @param gjc  the sparse G matrix column index (CCS).
     * @param gir  the sparse G matrix row index (CCS). Entries within each column need to appear in order of
     *             increasing row index.
     * @param c    the cost function weights.
     * @param h    the right-hand-side of the cone constraints.
     */
    public void setup(long l, long @NonNull [] q, long nExC, double @NonNull [] gpr, long @NonNull [] gjc,
                      long @NonNull [] gir, double @NonNull [] c, double @NonNull [] h) {
        setup(l, q, nExC, gpr, gjc, gir, c, h, null, null, null, null);
    }

    /**
     * Unsafe set up the {@link Model} data.
     * <p>
     * Same as
     * {@link Model#setup(long l, long[] q, long nExC, double[] gpr, long[] gjc, long[] gir, double[] c, double[] h, double[] apr, long[] ajc, long[] air, double[] b)}
     * without any precondition checks on its arguments.
     * <p>
     * <b>Warning: Setting the arguments incorrectly may lead to incorrect results in the best case. In the worst
     * case, it can crash the JVM and may silently result in memory corruption.</b>
     *
     * @param l    the dimension of the positive orthant.
     * @param q    the dimensions of the second-order cones.
     * @param nExC the number of exponential cones.
     * @param gpr  the sparse G matrix data (Column Compressed Storage CCS).
     * @param gjc  the sparse G matrix column index (CCS).
     * @param gir  the sparse G matrix row index (CCS). Entries within each column need to appear in order of
     *             increasing row index.
     * @param c    the cost function weights.
     * @param h    the right-hand-side of the cone constraints.
     * @param apr  the (optional) sparse A matrix data (CCS).
     * @param ajc  the (optional) sparse A matrix column index (CCS).
     * @param air  the (optional) sparse A matrix row index (CCS). Entries within each column need to appear in order
     *             of increasing row index.
     * @param b    the (optional) right-hand-side of the equalities.
     */
    public void unsafeSetup(long l, long @NonNull [] q, long nExC, double @NonNull [] gpr, long @NonNull [] gjc,
                            long @NonNull [] gir, double @NonNull [] c, double @NonNull [] h, double[] apr, long[] ajc,
                            long[] air, double[] b) {
        checkState(stage == Stage.NEW, "model must be in stage new");

        n = c.length;
        m = h.length;
        p = apr != null ? b.length : 0;
        val nCones = q.length;

        val qSeg = arena.allocateFrom(C_LONG_LONG, q);
        val gprSeg = arena.allocateFrom(C_DOUBLE, gpr);
        val gjcSeg = arena.allocateFrom(C_LONG_LONG, gjc);
        val girSeg = arena.allocateFrom(C_LONG_LONG, gir);
        val aprSeg = apr != null ? arena.allocateFrom(C_DOUBLE, apr) : NULL;
        val ajcSeg = ajc != null ? arena.allocateFrom(C_LONG_LONG, ajc) : NULL;
        val airSeg = air != null ? arena.allocateFrom(C_LONG_LONG, air) : NULL;
        val cSeg = arena.allocateFrom(C_DOUBLE, c);
        val hSeg = arena.allocateFrom(C_DOUBLE, h);
        val bSeg = b != null ? arena.allocateFrom(C_DOUBLE, b) : NULL;

        workSeg = ECOS_setup(n, m, p, l, nCones, qSeg, nExC, gprSeg, gjcSeg, girSeg, aprSeg, ajcSeg, airSeg, cSeg,
                hSeg, bSeg).reinterpret(pwork.sizeof(), arena, null);
        verify(!NULL.equals(workSeg));

        stgsSeg = pwork.stgs(workSeg).reinterpret(settings.sizeof(), arena, null);
        infoSeg = pwork.info(workSeg).reinterpret(stats.sizeof(), arena, null);

        setParameters();

        stage = Stage.SETUP;
    }

    private void setParameters() {
        if (parameters != null) {
            Optional.ofNullable(parameters.feasTol())
                    .ifPresent(p -> settings.feastol(stgsSeg, p));
            Optional.ofNullable(parameters.absTol())
                    .ifPresent(p -> settings.abstol(stgsSeg, p));
            Optional.ofNullable(parameters.relTol())
                    .ifPresent(p -> settings.reltol(stgsSeg, p));
            Optional.ofNullable(parameters.feasTolInacc())
                    .ifPresent(p -> settings.feastol_inacc(stgsSeg, p));
            Optional.ofNullable(parameters.absTolInacc())
                    .ifPresent(p -> settings.abstol_inacc(stgsSeg, p));
            Optional.ofNullable(parameters.relTolInacc())
                    .ifPresent(p -> settings.reltol_inacc(stgsSeg, p));
            Optional.ofNullable(parameters.maxIt())
                    .ifPresent(p -> settings.maxit(stgsSeg, p));
            Optional.ofNullable(parameters.nItRef())
                    .ifPresent(p -> settings.nitref(stgsSeg, p));
            Optional.ofNullable(parameters.verbose())
                    .ifPresent(p -> settings.verbose(stgsSeg, p ? 1 : 0));
        }
    }

    /**
     * Optimizes this {@link Model} with the <a href="https://github.com/embotech/ecos">ECOS</a> solver.
     *
     * @return the solver status.
     */
    public Status optimize() {
        checkState(stage != Stage.NEW, "model must not be in stage new");

        val status = ECOS_solve(workSeg);
        if (settings.verbose(stgsSeg) == 1) {
            fflush(NULL);
        }

        this.status = Status.valueOf((int) status);
        stage = Stage.OPTIMIZED;

        return this.status;
    }

    /**
     * Cleanup: free this {@link Model} native memory.
     */
    public void cleanup() {
        checkState(stage != Stage.NEW, "model must not be in stage new");
        ECOS_cleanup(workSeg, 0);
        stage = Stage.NEW;
    }

    /**
     * @return the primal objective of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double pCost() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.pcost(infoSeg);
    }

    /**
     * @return the dual objective of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double dCost() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.dcost(infoSeg);
    }

    /**
     * @return the primal residual on inequalities and equalities of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double pRes() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.pres(infoSeg);
    }

    /**
     * @return the dual residual of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double dRes() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.dres(infoSeg);
    }

    /**
     * @return the primal infeasibility measure of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double pInf() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.pinf(infoSeg);
    }

    /**
     * @return the dual infeasibility measure of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double dInf() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.dinf(infoSeg);
    }

    /**
     * @return the residual as primal infeasibility certificate of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double pInfRes() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.pinfres(infoSeg);
    }

    /**
     * @return the residual as dual infeasibility certificate of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double dInfRes() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.dinfres(infoSeg);
    }

    /**
     * @return the duality gap of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double gap() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.gap(infoSeg);
    }

    /**
     * @return the relative duality gap of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double relGap() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.relgap(infoSeg);
    }

    /**
     * @return the performed number of iterations until this {@link Model} was optimized.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public long iter() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return stats.iter(infoSeg);
    }

    /**
     * @return the primal variables of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double @NonNull [] x() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return pwork.x(workSeg).reinterpret(C_DOUBLE.byteSize() * n, arena, null).toArray(C_DOUBLE);
    }

    /**
     * @return the dual variables for the equality constraints of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double @NonNull [] y() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return pwork.y(workSeg).reinterpret(C_DOUBLE.byteSize() * p, arena, null).toArray(C_DOUBLE);
    }

    /**
     * @return the dual variables for the inequality constraints of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double @NonNull [] z() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return pwork.z(workSeg).reinterpret(C_DOUBLE.byteSize() * m, arena, null).toArray(C_DOUBLE);
    }

    /**
     * @return the slack variables of this optimized {@link Model}.
     * @see <a href="https://github.com/embotech/ecos">ECOS</a>
     */
    public double @NonNull [] s() {
        checkStageIsOptimizedAndStatusIsNotFatal();
        return pwork.s(workSeg).reinterpret(C_DOUBLE.byteSize() * m, arena, null).toArray(C_DOUBLE);
    }

    private void checkStageIsOptimizedAndStatusIsNotFatal() {
        checkState(stage == Stage.OPTIMIZED, "model must be in stage optimized");
        checkState(status != Status.FATAL, "solver status must not be fatal");
    }

    @Override
    public void close() {
        if (stage != Stage.NEW) {
            ECOS_cleanup(workSeg, 0);
        }
        arena.close();
    }

}
