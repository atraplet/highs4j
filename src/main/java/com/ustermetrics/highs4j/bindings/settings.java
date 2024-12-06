// Generated by jextract

package com.ustermetrics.ecos4j.bindings;

import java.lang.invoke.*;
import java.lang.foreign.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.foreign.ValueLayout.*;
import static java.lang.foreign.MemoryLayout.PathElement.*;

/**
 * {@snippet lang=c :
 * struct settings {
 *     pfloat gamma;
 *     pfloat delta;
 *     pfloat eps;
 *     pfloat feastol;
 *     pfloat abstol;
 *     pfloat reltol;
 *     pfloat feastol_inacc;
 *     pfloat abstol_inacc;
 *     pfloat reltol_inacc;
 *     idxint nitref;
 *     idxint maxit;
 *     idxint verbose;
 *     idxint max_bk_iter;
 *     pfloat bk_scale;
 *     pfloat centrality;
 * }
 * }
 */
public class settings {

    settings() {
        // Should not be called directly
    }

    private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
        ecos_h.C_DOUBLE.withName("gamma"),
        ecos_h.C_DOUBLE.withName("delta"),
        ecos_h.C_DOUBLE.withName("eps"),
        ecos_h.C_DOUBLE.withName("feastol"),
        ecos_h.C_DOUBLE.withName("abstol"),
        ecos_h.C_DOUBLE.withName("reltol"),
        ecos_h.C_DOUBLE.withName("feastol_inacc"),
        ecos_h.C_DOUBLE.withName("abstol_inacc"),
        ecos_h.C_DOUBLE.withName("reltol_inacc"),
        ecos_h.C_LONG_LONG.withName("nitref"),
        ecos_h.C_LONG_LONG.withName("maxit"),
        ecos_h.C_LONG_LONG.withName("verbose"),
        ecos_h.C_LONG_LONG.withName("max_bk_iter"),
        ecos_h.C_DOUBLE.withName("bk_scale"),
        ecos_h.C_DOUBLE.withName("centrality")
    ).withName("settings");

    /**
     * The layout of this struct
     */
    public static final GroupLayout layout() {
        return $LAYOUT;
    }

    private static final OfDouble gamma$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("gamma"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat gamma
     * }
     */
    public static final OfDouble gamma$layout() {
        return gamma$LAYOUT;
    }

    private static final long gamma$OFFSET = 0;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat gamma
     * }
     */
    public static final long gamma$offset() {
        return gamma$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat gamma
     * }
     */
    public static double gamma(MemorySegment struct) {
        return struct.get(gamma$LAYOUT, gamma$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat gamma
     * }
     */
    public static void gamma(MemorySegment struct, double fieldValue) {
        struct.set(gamma$LAYOUT, gamma$OFFSET, fieldValue);
    }

    private static final OfDouble delta$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("delta"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat delta
     * }
     */
    public static final OfDouble delta$layout() {
        return delta$LAYOUT;
    }

    private static final long delta$OFFSET = 8;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat delta
     * }
     */
    public static final long delta$offset() {
        return delta$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat delta
     * }
     */
    public static double delta(MemorySegment struct) {
        return struct.get(delta$LAYOUT, delta$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat delta
     * }
     */
    public static void delta(MemorySegment struct, double fieldValue) {
        struct.set(delta$LAYOUT, delta$OFFSET, fieldValue);
    }

    private static final OfDouble eps$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("eps"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat eps
     * }
     */
    public static final OfDouble eps$layout() {
        return eps$LAYOUT;
    }

    private static final long eps$OFFSET = 16;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat eps
     * }
     */
    public static final long eps$offset() {
        return eps$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat eps
     * }
     */
    public static double eps(MemorySegment struct) {
        return struct.get(eps$LAYOUT, eps$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat eps
     * }
     */
    public static void eps(MemorySegment struct, double fieldValue) {
        struct.set(eps$LAYOUT, eps$OFFSET, fieldValue);
    }

    private static final OfDouble feastol$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("feastol"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat feastol
     * }
     */
    public static final OfDouble feastol$layout() {
        return feastol$LAYOUT;
    }

    private static final long feastol$OFFSET = 24;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat feastol
     * }
     */
    public static final long feastol$offset() {
        return feastol$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat feastol
     * }
     */
    public static double feastol(MemorySegment struct) {
        return struct.get(feastol$LAYOUT, feastol$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat feastol
     * }
     */
    public static void feastol(MemorySegment struct, double fieldValue) {
        struct.set(feastol$LAYOUT, feastol$OFFSET, fieldValue);
    }

    private static final OfDouble abstol$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("abstol"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat abstol
     * }
     */
    public static final OfDouble abstol$layout() {
        return abstol$LAYOUT;
    }

    private static final long abstol$OFFSET = 32;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat abstol
     * }
     */
    public static final long abstol$offset() {
        return abstol$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat abstol
     * }
     */
    public static double abstol(MemorySegment struct) {
        return struct.get(abstol$LAYOUT, abstol$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat abstol
     * }
     */
    public static void abstol(MemorySegment struct, double fieldValue) {
        struct.set(abstol$LAYOUT, abstol$OFFSET, fieldValue);
    }

    private static final OfDouble reltol$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("reltol"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat reltol
     * }
     */
    public static final OfDouble reltol$layout() {
        return reltol$LAYOUT;
    }

    private static final long reltol$OFFSET = 40;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat reltol
     * }
     */
    public static final long reltol$offset() {
        return reltol$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat reltol
     * }
     */
    public static double reltol(MemorySegment struct) {
        return struct.get(reltol$LAYOUT, reltol$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat reltol
     * }
     */
    public static void reltol(MemorySegment struct, double fieldValue) {
        struct.set(reltol$LAYOUT, reltol$OFFSET, fieldValue);
    }

    private static final OfDouble feastol_inacc$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("feastol_inacc"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat feastol_inacc
     * }
     */
    public static final OfDouble feastol_inacc$layout() {
        return feastol_inacc$LAYOUT;
    }

    private static final long feastol_inacc$OFFSET = 48;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat feastol_inacc
     * }
     */
    public static final long feastol_inacc$offset() {
        return feastol_inacc$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat feastol_inacc
     * }
     */
    public static double feastol_inacc(MemorySegment struct) {
        return struct.get(feastol_inacc$LAYOUT, feastol_inacc$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat feastol_inacc
     * }
     */
    public static void feastol_inacc(MemorySegment struct, double fieldValue) {
        struct.set(feastol_inacc$LAYOUT, feastol_inacc$OFFSET, fieldValue);
    }

    private static final OfDouble abstol_inacc$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("abstol_inacc"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat abstol_inacc
     * }
     */
    public static final OfDouble abstol_inacc$layout() {
        return abstol_inacc$LAYOUT;
    }

    private static final long abstol_inacc$OFFSET = 56;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat abstol_inacc
     * }
     */
    public static final long abstol_inacc$offset() {
        return abstol_inacc$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat abstol_inacc
     * }
     */
    public static double abstol_inacc(MemorySegment struct) {
        return struct.get(abstol_inacc$LAYOUT, abstol_inacc$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat abstol_inacc
     * }
     */
    public static void abstol_inacc(MemorySegment struct, double fieldValue) {
        struct.set(abstol_inacc$LAYOUT, abstol_inacc$OFFSET, fieldValue);
    }

    private static final OfDouble reltol_inacc$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("reltol_inacc"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat reltol_inacc
     * }
     */
    public static final OfDouble reltol_inacc$layout() {
        return reltol_inacc$LAYOUT;
    }

    private static final long reltol_inacc$OFFSET = 64;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat reltol_inacc
     * }
     */
    public static final long reltol_inacc$offset() {
        return reltol_inacc$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat reltol_inacc
     * }
     */
    public static double reltol_inacc(MemorySegment struct) {
        return struct.get(reltol_inacc$LAYOUT, reltol_inacc$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat reltol_inacc
     * }
     */
    public static void reltol_inacc(MemorySegment struct, double fieldValue) {
        struct.set(reltol_inacc$LAYOUT, reltol_inacc$OFFSET, fieldValue);
    }

    private static final OfLong nitref$LAYOUT = (OfLong)$LAYOUT.select(groupElement("nitref"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * idxint nitref
     * }
     */
    public static final OfLong nitref$layout() {
        return nitref$LAYOUT;
    }

    private static final long nitref$OFFSET = 72;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * idxint nitref
     * }
     */
    public static final long nitref$offset() {
        return nitref$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * idxint nitref
     * }
     */
    public static long nitref(MemorySegment struct) {
        return struct.get(nitref$LAYOUT, nitref$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * idxint nitref
     * }
     */
    public static void nitref(MemorySegment struct, long fieldValue) {
        struct.set(nitref$LAYOUT, nitref$OFFSET, fieldValue);
    }

    private static final OfLong maxit$LAYOUT = (OfLong)$LAYOUT.select(groupElement("maxit"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * idxint maxit
     * }
     */
    public static final OfLong maxit$layout() {
        return maxit$LAYOUT;
    }

    private static final long maxit$OFFSET = 80;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * idxint maxit
     * }
     */
    public static final long maxit$offset() {
        return maxit$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * idxint maxit
     * }
     */
    public static long maxit(MemorySegment struct) {
        return struct.get(maxit$LAYOUT, maxit$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * idxint maxit
     * }
     */
    public static void maxit(MemorySegment struct, long fieldValue) {
        struct.set(maxit$LAYOUT, maxit$OFFSET, fieldValue);
    }

    private static final OfLong verbose$LAYOUT = (OfLong)$LAYOUT.select(groupElement("verbose"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * idxint verbose
     * }
     */
    public static final OfLong verbose$layout() {
        return verbose$LAYOUT;
    }

    private static final long verbose$OFFSET = 88;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * idxint verbose
     * }
     */
    public static final long verbose$offset() {
        return verbose$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * idxint verbose
     * }
     */
    public static long verbose(MemorySegment struct) {
        return struct.get(verbose$LAYOUT, verbose$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * idxint verbose
     * }
     */
    public static void verbose(MemorySegment struct, long fieldValue) {
        struct.set(verbose$LAYOUT, verbose$OFFSET, fieldValue);
    }

    private static final OfLong max_bk_iter$LAYOUT = (OfLong)$LAYOUT.select(groupElement("max_bk_iter"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * idxint max_bk_iter
     * }
     */
    public static final OfLong max_bk_iter$layout() {
        return max_bk_iter$LAYOUT;
    }

    private static final long max_bk_iter$OFFSET = 96;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * idxint max_bk_iter
     * }
     */
    public static final long max_bk_iter$offset() {
        return max_bk_iter$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * idxint max_bk_iter
     * }
     */
    public static long max_bk_iter(MemorySegment struct) {
        return struct.get(max_bk_iter$LAYOUT, max_bk_iter$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * idxint max_bk_iter
     * }
     */
    public static void max_bk_iter(MemorySegment struct, long fieldValue) {
        struct.set(max_bk_iter$LAYOUT, max_bk_iter$OFFSET, fieldValue);
    }

    private static final OfDouble bk_scale$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("bk_scale"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat bk_scale
     * }
     */
    public static final OfDouble bk_scale$layout() {
        return bk_scale$LAYOUT;
    }

    private static final long bk_scale$OFFSET = 104;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat bk_scale
     * }
     */
    public static final long bk_scale$offset() {
        return bk_scale$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat bk_scale
     * }
     */
    public static double bk_scale(MemorySegment struct) {
        return struct.get(bk_scale$LAYOUT, bk_scale$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat bk_scale
     * }
     */
    public static void bk_scale(MemorySegment struct, double fieldValue) {
        struct.set(bk_scale$LAYOUT, bk_scale$OFFSET, fieldValue);
    }

    private static final OfDouble centrality$LAYOUT = (OfDouble)$LAYOUT.select(groupElement("centrality"));

    /**
     * Layout for field:
     * {@snippet lang=c :
     * pfloat centrality
     * }
     */
    public static final OfDouble centrality$layout() {
        return centrality$LAYOUT;
    }

    private static final long centrality$OFFSET = 112;

    /**
     * Offset for field:
     * {@snippet lang=c :
     * pfloat centrality
     * }
     */
    public static final long centrality$offset() {
        return centrality$OFFSET;
    }

    /**
     * Getter for field:
     * {@snippet lang=c :
     * pfloat centrality
     * }
     */
    public static double centrality(MemorySegment struct) {
        return struct.get(centrality$LAYOUT, centrality$OFFSET);
    }

    /**
     * Setter for field:
     * {@snippet lang=c :
     * pfloat centrality
     * }
     */
    public static void centrality(MemorySegment struct, double fieldValue) {
        struct.set(centrality$LAYOUT, centrality$OFFSET, fieldValue);
    }

    /**
     * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
     * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
     */
    public static MemorySegment asSlice(MemorySegment array, long index) {
        return array.asSlice(layout().byteSize() * index);
    }

    /**
     * The size (in bytes) of this struct
     */
    public static long sizeof() { return layout().byteSize(); }

    /**
     * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
     */
    public static MemorySegment allocate(SegmentAllocator allocator) {
        return allocator.allocate(layout());
    }

    /**
     * Allocate an array of size {@code elementCount} using {@code allocator}.
     * The returned segment has size {@code elementCount * layout().byteSize()}.
     */
    public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
        return reinterpret(addr, 1, arena, cleanup);
    }

    /**
     * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
     * The returned segment has size {@code elementCount * layout().byteSize()}
     */
    public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
        return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
    }
}

