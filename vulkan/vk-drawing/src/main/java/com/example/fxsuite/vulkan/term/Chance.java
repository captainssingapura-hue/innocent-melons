package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * What stands for chance: numbers that look thrown at random and are not, the same every time and the same in the
 * shader as in Java, bit for bit. Worked in whole numbers only, which both keep exactly, and never in the parts of
 * numbers, which they round differently.
 */
public enum Chance implements Op {

    /**
     * A number from 0 up to, not reaching, 1, for a cell: done to the cell's two whole numbers (which column, which
     * row) and a third to tell one use of a cell from another. Neighbouring cells' numbers have nothing to do with
     * each other.
     */
    SCATTERED;

    private static final int ACROSS = 0x9E3779B1;
    private static final int UP = 0x85EBCA77;
    private static final int USE = 0xC2B2AE3D;
    private static final int FIRST = 0x7FEB352D;
    private static final int SECOND = 0x846CA68B;
    private static final double PARTS = 16_777_216;

    @Override
    public int size(final List<Integer> of) {
        if (!of.equals(List.of(1, 1, 1))) {
            throw new IllegalArgumentException(this + " is done to three numbers, not " + of);
        }
        return 1;
    }

    @Override
    public double[] worked(final List<double[]> of) {
        int mixed = (int) of.get(0)[0] * ACROSS ^ (int) of.get(1)[0] * UP ^ (int) of.get(2)[0] * USE;
        mixed ^= mixed >>> 16;
        mixed *= FIRST;
        mixed ^= mixed >>> 15;
        mixed *= SECOND;
        mixed ^= mixed >>> 16;
        return new double[] {(mixed >>> 8) / PARTS};
    }

    @Override
    public String written(final List<String> of) {
        return "scattered(%s, %s, %s)".formatted(of.toArray());
    }

    /** The same working as {@link #worked}, in the shader's language: its whole numbers wrap as Java's do. */
    @Override
    public List<String> declared() {
        return List.of(
                "float scattered(float across, float up, float use) {",
                "    uint mixed = uint(int(across)) * 0x9E3779B1u ^ uint(int(up)) * 0x85EBCA77u"
                        + " ^ uint(int(use)) * 0xC2B2AE3Du;",
                "    mixed ^= mixed >> 16;",
                "    mixed *= 0x7FEB352Du;",
                "    mixed ^= mixed >> 15;",
                "    mixed *= 0x846CA68Bu;",
                "    mixed ^= mixed >> 16;",
                "    return float(mixed >> 8) / 16777216.0;",
                "}");
    }
}
