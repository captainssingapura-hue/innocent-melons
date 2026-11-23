package com.example.fxsuite.life.body;

/**
 * A shape with a piece grafted into it: within {@code region}, more than {@code soft} in from its side, the piece in
 * place of the shape, its hollows as well as what stands out of it; outside the region, the shape; between, the one
 * going over into the other, as a graft is smoothed into the skin round it.
 *
 * @param base   the shape grafted onto
 * @param piece  what is grafted in, a solid at least as far round as the region
 * @param region where it is grafted
 * @param soft   how far in from the region's side the shape goes over into the piece, cm
 */
public record Graft(Field.Shape base, Field.Shape piece, Field.Shape region, double soft) implements Field.Shape {

    @Override
    public double at(final double x, final double y, final double z) {
        final double in = -region.at(x, y, z) / soft;
        if (in <= 0) {
            return base.at(x, y, z);
        }
        if (in >= 1) {
            return piece.at(x, y, z);
        }
        final double w = in * in * (3 - 2 * in);
        return base.at(x, y, z) * (1 - w) + piece.at(x, y, z) * w;
    }

    /** Its box: the shape's, and the piece's within the region. */
    @Override
    public double[] box() {
        final double[] a = base.box();
        final double[] b = region.box();
        return new double[] {Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.min(a[2], b[2]), Math.max(a[3], b[3]),
            Math.max(a[4], b[4]), Math.max(a[5], b[5])};
    }
}
