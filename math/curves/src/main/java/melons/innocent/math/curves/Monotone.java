package melons.innocent.math.curves;

import com.example.fxsuite.math.existences.Curves;

/**
 * A monotone cubic curve: smoothly through its knots, never rising past them or falling short of them between, each
 * piece a cubic meeting the next at the same slope. Its slope at each knot is the weighted harmonic mean of the
 * pieces either side (Fritsch and Butland's), nothing where they rise and fall, so a level stretch stays level; at
 * its ends, the end piece's. Worked out a piece at a time from the knots beside it.
 *
 * @param knots what it passes through, in order
 */
public record Monotone(Knots knots) implements CurveImpl<Curves.MonotoneCubic> {

    @Override
    public Curves.MonotoneCubic kind() {
        return new Curves.MonotoneCubic();
    }

    @Override
    public double start() {
        return knots.start();
    }

    @Override
    public double end() {
        return knots.end();
    }

    @Override
    public double at(final double x) {
        final int i = knots.piece(within(x));
        final Knot a = knots.get(i);
        final Knot b = knots.get(i + 1);
        final double h = b.x() - a.x();
        final double t = (x - a.x()) / h;
        final double t2 = t * t;
        final double t3 = t2 * t;
        return (2 * t3 - 3 * t2 + 1) * a.y() + (t3 - 2 * t2 + t) * h * tangent(i)
                + (-2 * t3 + 3 * t2) * b.y() + (t3 - t2) * h * tangent(i + 1);
    }

    /** Its slope at knot {@code i}. */
    double tangent(final int i) {
        if (i == 0) {
            return knots.rise(0);
        }
        if (i == knots.size() - 1) {
            return knots.rise(i - 1);
        }
        final double before = knots.rise(i - 1);
        final double after = knots.rise(i);
        if (before * after <= 0) {
            return 0;
        }
        final double h0 = knots.get(i).x() - knots.get(i - 1).x();
        final double h1 = knots.get(i + 1).x() - knots.get(i).x();
        return 3 * (h0 + h1) / ((2 * h1 + h0) / before + (h1 + 2 * h0) / after);
    }
}
