package com.example.fxsuite.life.body;

/**
 * A shape moved: mirrored across its middle ({@code x} the other way) if said, turned by {@code turn}, its origin put
 * at {@code at}; and swollen all round by {@code swell}, cm. Moved and turned, it is as far from a point as it was from
 * where the point was.
 *
 * @param inner    the shape, where it was
 * @param turn     how it is turned, after it is mirrored
 * @param at       where its origin is put
 * @param mirrored whether it is mirrored first
 * @param swell    how far it is swollen, cm
 */
public record Moved(Field.Shape inner, Rot turn, V3 at, boolean mirrored, double swell) implements Field.Shape {

    @Override
    public double at(final double x, final double y, final double z) {
        final V3 back = turn.back().apply(new V3(x - at.x(), y - at.y(), z - at.z()));
        return inner.at(mirrored ? -back.x() : back.x(), back.y(), back.z()) - swell;
    }

    /** Its box: the box round its shape's corners, moved, grown by its swelling. */
    @Override
    public double[] box() {
        final double[] b = inner.box();
        final double[] out = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int k = 0; k < 8; k++) {
            final double x = b[(k & 1) == 0 ? 0 : 3];
            final V3 corner = new V3(mirrored ? -x : x, b[(k & 2) == 0 ? 1 : 4], b[(k & 4) == 0 ? 2 : 5]);
            final V3 c = at.plus(turn.apply(corner));
            final double[] v = {c.x(), c.y(), c.z()};
            for (int i = 0; i < 3; i++) {
                out[i] = Math.min(out[i], v[i] - swell);
                out[i + 3] = Math.max(out[i + 3], v[i] + swell);
            }
        }
        return out;
    }
}
