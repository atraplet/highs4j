package com.ustermetrics.ecos4j;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.lang.Math.log;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Nested
    class PortfolioOptimizationTest {

        private static long l;
        private static long[] q;
        private static long nExC;
        private static double[] gpr;
        private static long[] gjc;
        private static long[] gir;
        private static double[] apr;
        private static long[] ajc;
        private static long[] air;
        private static double[] c;
        private static double[] h;
        private static double[] b;

        @BeforeAll
        static void setup() {
            // Set up the portfolio optimization problem described
            // here https://www.ustermetrics.com/post/convex-optimization-in-java-with-project-panama.
            l = 5;
            q = new long[]{5};
            nExC = 0;
            gpr = new double[]{-1., -0.15, -1., -0.02, -0.198997487421324, -1., -0.1, -0.16583123951776996,
                    -0.158113883008419, -1., -0.15, -0.10552897060221729, -0.17392527130926083, -0.16159714218895202,
                    1., -1.};
            gjc = new long[]{0, 2, 5, 9, 14, 16};
            gir = new long[]{0, 6, 1, 6, 7, 2, 6, 7, 8, 3, 6, 7, 8, 9, 4, 5};
            apr = new double[]{1., 1., 1., 1.};
            ajc = new long[]{0, 1, 2, 3, 4, 4};
            air = new long[]{0, 0, 0, 0};
            c = new double[]{-0.05, -0.06, -0.08, -0.06, 0.};
            h = new double[]{0., 0., 0., 0., 0.2, 0., 0., 0., 0., 0.};
            b = new double[]{1.};
        }

        @Test
        void solveProblemReturnsExpectedSolution() {
            try (val model = new Model()) {
                val parameters = Parameters.builder()
                        .verbose(false)
                        .build();
                model.setParameters(parameters);
                model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);

                val status = model.optimize();

                assertEquals(Status.OPTIMAL, status);
                val tol = 1e-8;
                assertEquals(-0.07154259763411892, model.pCost(), tol);
                assertEquals(-0.07154259780733442, model.dCost(), tol);
                assertEquals(0., model.pRes(), tol);
                assertEquals(0., model.dRes(), tol);
                assertEquals(0., model.pInf(), tol);
                assertEquals(0., model.dInf(), tol);
                assertEquals(Double.NaN, model.pInfRes());
                assertEquals(1.2947737439869422, model.dInfRes(), tol);
                assertEquals(0., model.gap(), tol);
                assertEquals(6.711950875290626E-8, model.relGap(), tol);
                assertEquals(9, model.iter());
                assertArrayEquals(new double[]{0.24879020572078372, 0.049684806182020855, 0.7015249845663684,
                        3.5308169265756875e-09, 0.19999999978141014}, model.x(), tol);
                assertArrayEquals(new double[]{0.03522878195201535}, model.y(), tol);
                assertArrayEquals(new double[]{3.125617924926641E-10, 1.472951150087448E-9, 1.2023771946482373E-10,
                        0.0196064963760253, 0.1815690792765954, 0.18156907919877024, -0.09847478764444928,
                        -0.11458297215050496, -0.10070148546711516, -8.650194730018541E-7}, model.z(), tol);
                assertArrayEquals(new double[]{0.24879020593422796, 0.049684806395458275, 0.7015249847798138,
                        3.743961973367573E-9, 4.3180008617757377E-10, 0.20000000011767824, 0.10846472596852022,
                        0.12622190970967948, 0.1109208399512843, 5.661444066326766E-10}, model.s(), tol);
            }
        }

        @Test
        void solveProblemWithVerboseParameterTrueReturnsOptimal() {
            try (val model = new Model()) {
                val parameters = Parameters.builder()
                        .verbose(true)
                        .build();
                model.setParameters(parameters);
                model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);

                val status = model.optimize();

                assertEquals(Status.OPTIMAL, status);
            }
        }

        @Test
        void solveModifiedProblemWithMaxitLimitReturnsMaxitStatus() {
            try (val model = new Model()) {
                val parameters = Parameters.builder()
                        .maxIt(5)
                        .verbose(false)
                        .build();
                model.setParameters(parameters);
                model.setup(l, q, nExC, gpr, gjc, gir, c, h);

                val status = model.optimize();
                model.cleanup();

                assertEquals(Status.MAXIT, status);
            }
        }

        @Test
        void setupAfterOptimizeThrowsException() {
            val exception = assertThrows(IllegalStateException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);
                    model.setParameters(Parameters.builder().verbose(false).build());
                    model.optimize();
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h);
                }
            });

            assertEquals("model must be in stage new", exception.getMessage());
        }

        @Test
        void setParametersAfterSetupThrowsException() {
            val exception = assertThrows(IllegalStateException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);
                    model.setParameters(Parameters.builder().build());
                }
            });

            assertEquals("model must be in stage new", exception.getMessage());
        }

        @Test
        void setupWithInvalidPositiveOrthantDimensionThrowsException() {
            val exception = assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(-1, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, b);
                }
            });

            assertEquals("dimension of the positive orthant must be non-negative", exception.getMessage());
        }

        @Test
        void setupWithInvalidNumberOfExponentialConesThrowsException() {
            val exception = assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, -1, gpr, gjc, gir, c, h, apr, ajc, air, b);
                }
            });

            assertEquals("number of exponential cones must be non-negative", exception.getMessage());
        }

        @Test
        void setupWithInvalidAprThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, new double[]{}, ajc, air, b);
                }
            });
        }

        @Test
        void setupWithInvalidAjcThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, new long[]{}, air, b);
                }
            });
        }

        @Test
        void setupWithInvalidAirThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, new long[]{}, b);
                }
            });
        }

        @Test
        void setupWithInvalidBThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                try (val model = new Model()) {
                    model.setup(l, q, nExC, gpr, gjc, gir, c, h, apr, ajc, air, new double[]{});
                }
            });
        }

    }

    @Test
    void solveInfeasibleProblemReturnsPrimalInfeasibleStatus() {
        // Set up the infeasible problem described
        // here https://github.com/atraplet/ecos/blob/develop/test/infeasibleProblems/infeasible1.h.
        val l = 2;
        val q = new long[]{};
        val gpr = new double[]{-1., 1.};
        val gjc = new long[]{0, 2};
        val gir = new long[]{0, 1};
        val c = new double[]{-1.};
        val h = new double[]{-2., 1.};

        try (val model = new Model()) {
            val parameters = Parameters.builder()
                    .verbose(false)
                    .build();
            model.setParameters(parameters);
            model.setup(l, q, 0, gpr, gjc, gir, c, h);

            val status = model.optimize();

            assertEquals(Status.PINF, status);
        }
    }

    @Test
    void solveExponentialConeProblemReturnsExpectedSolution() {
        // Set up the exponential cone problem described
        // here https://github.com/atraplet/ecos/blob/develop/test/exponential/log_ax_x.h.
        // There is a bug in the description. The problem is:
        // minimize exp(a * x) - x, a = 0.3
        // Written with an exponential cone, the problem is:
        // minimize -x + y, exp(a * x) <= y, a = 0.3
        // or equivalently
        // minimize -x + y, (a * x, y, 1) \in K_e, a = 0.3
        val a = 0.3;
        val nExC = 1;
        val q = new long[]{};
        val gpr = new double[]{-a, -1.};
        val gjc = new long[]{0, 1, 2};
        val gir = new long[]{0, 1};
        val c = new double[]{-1., 1.};
        val h = new double[]{0., 0., 1.};

        try (val model = new Model()) {
            val parameters = Parameters.builder()
                    .feasTol(1e-12)
                    .verbose(false)
                    .build();
            model.setParameters(parameters);
            model.setup(0, q, nExC, gpr, gjc, gir, c, h);

            val status = model.optimize();

            assertEquals(Status.OPTIMAL, status);
            val tol = 1e-11;
            assertArrayEquals(new double[]{-log(a) / a, 1. / a}, model.x(), tol);
        }
    }

    @Test
    void optimizeWithoutSetupThrowsException() {
        val exception = assertThrows(IllegalStateException.class, () -> {
            try (val model = new Model()) {
                model.optimize();
            }
        });

        assertEquals("model must not be in stage new", exception.getMessage());
    }

    @Test
    void getPrimalVariablesWithoutSetupThrowsException() {
        val exception = assertThrows(IllegalStateException.class, () -> {
            try (val model = new Model()) {
                model.x();
            }
        });

        assertEquals("model must be in stage optimized", exception.getMessage());
    }

    @Test
    void cleanupWithoutSetupThrowsException() {
        val exception = assertThrows(IllegalStateException.class, () -> {
            try (val model = new Model()) {
                model.cleanup();
            }
        });

        assertEquals("model must not be in stage new", exception.getMessage());
    }

    @Test
    void versionReturnsNonEmptyString() {
        val version = Model.version();

        assertFalse(version.isEmpty());
    }

}
