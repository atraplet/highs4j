package com.ustermetrics.ecos4j.bindings;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;

import static com.ustermetrics.ecos4j.bindings.ecos_h.*;
import static java.lang.foreign.MemorySegment.NULL;
import static org.junit.jupiter.api.Assertions.*;

class BindingsTest {

    @Test
    void versionReturnsNonEmptyString() {
        val version = ECOS_ver().getString(0);

        assertFalse(version.isEmpty());
    }

    @Test
    void solvePortfolioOptimizationProblemReturnsExpectedSolution() {
        val n = 5;
        val m = 10;
        val p = 1;
        val l = 5;
        val nCones = 1;
        val nExC = 0;

        try (val arena = Arena.ofConfined()) {
            // Set up the portfolio optimization problem described
            // here https://www.ustermetrics.com/post/convex-optimization-in-java-with-project-panama.
            val qSeg = arena.allocateFrom(C_LONG_LONG, new long[]{5});
            val gprSeg = arena.allocateFrom(C_DOUBLE, -1., -0.15, -1., -0.02, -0.198997487421324, -1., -0.1,
                    -0.16583123951776996, -0.158113883008419, -1., -0.15, -0.10552897060221729, -0.17392527130926083,
                    -0.16159714218895202, 1., -1.);
            val gjcSeg = arena.allocateFrom(C_LONG_LONG, 0, 2, 5, 9, 14, 16);
            val girSeg = arena.allocateFrom(C_LONG_LONG, 0, 6, 1, 6, 7, 2, 6, 7, 8, 3, 6, 7, 8, 9, 4, 5);
            val aprSeg = arena.allocateFrom(C_DOUBLE, 1., 1., 1., 1.);
            val ajcSeg = arena.allocateFrom(C_LONG_LONG, 0, 1, 2, 3, 4, 4);
            val airSeg = arena.allocateFrom(C_LONG_LONG, 0, 0, 0, 0);
            val cSeg = arena.allocateFrom(C_DOUBLE, -0.05, -0.06, -0.08, -0.06, 0.);
            val hSeg = arena.allocateFrom(C_DOUBLE, 0., 0., 0., 0., 0.2, 0., 0., 0., 0., 0.);
            val bSeg = arena.allocateFrom(C_DOUBLE, 1.);

            val workSeg = ECOS_setup(n, m, p, l, nCones, qSeg, nExC, gprSeg, gjcSeg, girSeg, aprSeg, ajcSeg, airSeg,
                    cSeg, hSeg, bSeg).reinterpret(pwork.sizeof(), arena, null);
            assertNotEquals(NULL, workSeg);

            val stgsSeg = pwork.stgs(workSeg).reinterpret(settings.sizeof(), arena, null);
            settings.verbose(stgsSeg, 0);

            val status = ECOS_solve(workSeg);
            assertEquals(0, status);

            val tol = 1e-8;
            val x = pwork.x(workSeg).reinterpret(C_DOUBLE.byteSize() * n, arena, null).toArray(C_DOUBLE);
            assertArrayEquals(new double[]{0.24879020572078372, 0.049684806182020855, 0.7015249845663684,
                    3.5308169265756875e-09, 0.19999999978141014}, x, tol);

            val infoSeg = pwork.info(workSeg).reinterpret(stats.sizeof(), arena, null);
            val cost = stats.pcost(infoSeg);
            assertEquals(-0.07154259763411892, cost, tol);

            ECOS_cleanup(workSeg, 0);
        }
    }

}
