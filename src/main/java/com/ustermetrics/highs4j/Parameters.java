package com.ustermetrics.ecos4j;

import lombok.Builder;
import lombok.val;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A parameter object for <a href="https://github.com/embotech/ecos">ECOS</a> solver options.
 * <p>
 * If {@link Model#setParameters(Parameters parameters)} is not called, then solver defaults are applied.
 *
 * @param feasTol      the primal/dual infeasibility tolerance.
 * @param absTol       the absolute tolerance on the duality gap.
 * @param relTol       the relative tolerance on the duality gap.
 * @param feasTolInacc the inaccurate solution feasibility tolerance.
 * @param absTolInacc  the inaccurate solution absolute tolerance.
 * @param relTolInacc  the inaccurate solution relative tolerance.
 * @param nItRef       the number of iterative refinement steps.
 * @param maxIt        the maximum number of iterations.
 * @param verbose      the verbose mode.
 * @see <a href="https://github.com/embotech/ecos">ECOS</a>
 */
@Builder
public record Parameters(Double feasTol, Double absTol, Double relTol, Double feasTolInacc, Double absTolInacc,
                         Double relTolInacc, Integer nItRef, Integer maxIt, Boolean verbose) {

    public Parameters {
        val errMsg = "%s must be null or positive";
        checkArgument(feasTol == null || feasTol > 0., errMsg, "feasTol");
        checkArgument(absTol == null || absTol > 0., errMsg, "absTol");
        checkArgument(relTol == null || relTol > 0., errMsg, "relTol");
        checkArgument(feasTolInacc == null || feasTolInacc > 0., errMsg, "feasTolInacc");
        checkArgument(absTolInacc == null || absTolInacc > 0., errMsg, "absTolInacc");
        checkArgument(relTolInacc == null || relTolInacc > 0., errMsg, "relTolInacc");
        checkArgument(nItRef == null || nItRef > 0, errMsg, "nItRef");
        checkArgument(maxIt == null || maxIt > 0, errMsg, "maxIt");
    }

}
