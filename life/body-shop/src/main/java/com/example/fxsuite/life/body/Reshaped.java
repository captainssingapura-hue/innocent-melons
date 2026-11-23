package com.example.fxsuite.life.body;

/**
 * A shape reshaped about a point of its own, its anchor: stretched {@code scale} as far along each way from it, turned
 * by {@code turn} about it, moved by {@code shift}; and swollen all round by {@code swell}, cm. As far from a point,
 * near enough, as it was from where the point was, by its least stretch, so that it is never taken for nearer than it
 * is.
 *
 * @param inner  the shape, as it was
 * @param anchor the point it is reshaped about
 * @param shift  how far it is moved, cm
 * @param turn   how it is turned, after it is stretched
 * @param scale  how far it is stretched along x, y and z, each by so many times
 * @param swell  how far it is swollen, cm
 */
public record Reshaped(Field.Shape inner, V3 anchor, V3 shift, Rot turn, V3 scale, double swell)
        implements Field.Shape {

    @Override
    public double at(final double x, final double y, final double z) {
        final V3 q = turn.back().apply(new V3(x - anchor.x() - shift.x(), y - anchor.y() - shift.y(),
                z - anchor.z() - shift.z()));
        final double least = Math.min(scale.x(), Math.min(scale.y(), scale.z()));
        return inner.at(anchor.x() + q.x() / scale.x(), anchor.y() + q.y() / scale.y(), anchor.z() + q.z() / scale.z())
                * least - swell;
    }

    /** Its box: the box round its shape's corners, reshaped, grown by its swelling. */
    @Override
    public double[] box() {
        final double[] b = inner.box();
        final double[] out = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int k = 0; k < 8; k++) {
            final V3 corner = new V3(b[(k & 1) == 0 ? 0 : 3], b[(k & 2) == 0 ? 1 : 4], b[(k & 4) == 0 ? 2 : 5]);
            final V3 d = corner.minus(anchor);
            final V3 c = anchor.plus(shift).plus(turn.apply(new V3(d.x() * scale.x(), d.y() * scale.y(),
                    d.z() * scale.z())));
            final double[] v = {c.x(), c.y(), c.z()};
            for (int i = 0; i < 3; i++) {
                out[i] = Math.min(out[i], v[i] - swell);
                out[i + 3] = Math.max(out[i + 3], v[i] + swell);
            }
        }
        return out;
    }
}
