package com.example.fxsuite.life.body.bone.skull;

/**
 * Each tooth, from the middle back, on the right, in the head's frame: where it is in the upper arch (out from the
 * middle and ahead), how broad, how tall and how deep its crown; and the same for the lower.
 */
public enum Tooth {
    CENTRAL_INCISOR(0.45, 8.55, 0.85, 1.05, 0.7, 0.3, 8.25, 0.55, 0.9, 0.6),
    LATERAL_INCISOR(1.25, 8.15, 0.65, 0.9, 0.6, 0.85, 7.95, 0.6, 0.9, 0.62),
    CANINE(1.95, 7.5, 0.75, 1.0, 0.8, 1.5, 7.4, 0.7, 1.0, 0.75),
    FIRST_PREMOLAR(2.35, 6.85, 0.7, 0.8, 0.9, 2.0, 6.75, 0.7, 0.8, 0.8),
    SECOND_PREMOLAR(2.65, 6.15, 0.68, 0.75, 0.9, 2.4, 6.05, 0.72, 0.75, 0.82),
    FIRST_MOLAR(2.95, 5.25, 1.0, 0.7, 1.1, 2.75, 5.15, 1.1, 0.7, 1.05),
    SECOND_MOLAR(3.15, 4.25, 0.9, 0.65, 1.05, 3.0, 4.15, 1.0, 0.65, 1.0),
    THIRD_MOLAR(3.25, 3.3, 0.85, 0.6, 1.0, 3.1, 3.2, 0.95, 0.6, 0.95);

    private final double[] upper;
    private final double[] lower;

    Tooth(final double ux, final double uz, final double ub, final double ut, final double ud, final double lx,
          final double lz, final double lb, final double lt, final double ld) {
        this.upper = new double[] {ux, uz, ub, ut, ud};
        this.lower = new double[] {lx, lz, lb, lt, ld};
    }

    /** Out from the middle, cm, in the upper arch or the lower. */
    public double out(final boolean up) {
        return (up ? upper : lower)[0];
    }

    /** Ahead. */
    public double ahead(final boolean up) {
        return (up ? upper : lower)[1];
    }

    /** How broad its crown, along the arch. */
    public double broad(final boolean up) {
        return (up ? upper : lower)[2];
    }

    /** How tall. */
    public double tall(final boolean up) {
        return (up ? upper : lower)[3];
    }

    /** How deep, across the arch. */
    public double deep(final boolean up) {
        return (up ? upper : lower)[4];
    }
}
