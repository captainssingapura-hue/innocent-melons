package melons.innocent.glm.model;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A flat drawing's triangle cut into small convex pieces by lines ruled every {@code along} along and every
 * {@code up} up, so that laid on a curve each piece lies close to it. A line crossing a side two triangles share cuts
 * it at the same spot for each, the side taken the same way round, so the pieces meet without a gap.
 *
 * @param along how far apart the lines across are
 * @param up    how far apart the lines along are
 */
record Slicing(double along, double up) {

    /** {@code triangle} cut into pieces. */
    List<List<Spot>> sliced(final List<Spot> triangle) {
        List<List<Spot>> pieces = List.of(triangle);
        for (final boolean across : List.of(true, false)) {
            final double step = across ? along : up;
            double low = Double.MAX_VALUE;
            double high = -Double.MAX_VALUE;
            for (final Spot s : triangle) {
                low = Math.min(low, of(s, across));
                high = Math.max(high, of(s, across));
            }
            for (long k = (long) Math.floor(low / step) + 1; k * step < high; k++) {
                final List<List<Spot>> next = new ArrayList<>();
                for (final List<Spot> piece : pieces) {
                    next.addAll(halves(piece, k * step, across));
                }
                pieces = next;
            }
        }
        return pieces;
    }

    /** {@code piece} cut by the line at {@code at}, across if {@code across}, else along: the pieces either side. */
    private List<List<Spot>> halves(final List<Spot> piece, final double at, final boolean across) {
        final List<Spot> below = new ArrayList<>();
        final List<Spot> above = new ArrayList<>();
        for (int k = 0; k < piece.size(); k++) {
            final Spot a = piece.get(k);
            final Spot b = piece.get((k + 1) % piece.size());
            final double da = of(a, across) - at;
            final double db = of(b, across) - at;
            if (da <= 0) {
                below.add(a);
            }
            if (da >= 0) {
                above.add(a);
            }
            if (da < 0 && db > 0 || da > 0 && db < 0) {
                final Spot crossing = crossing(a, b, at, across);
                below.add(crossing);
                above.add(crossing);
            }
        }
        final List<List<Spot>> out = new ArrayList<>();
        for (final List<Spot> half : List.of(below, above)) {
            if (half.size() >= 3 && area(half) > 1e-9) {
                out.add(half);
            }
        }
        return out;
    }

    /** Where the side from {@code a} to {@code b} crosses the line at {@code at}, the side taken lowest first. */
    private Spot crossing(final Spot a, final Spot b, final double at, final boolean across) {
        final boolean flip = a.u() > b.u() || a.u() == b.u() && a.v() > b.v();
        final Spot p = flip ? b : a;
        final Spot q = flip ? a : b;
        final double t = (at - of(p, across)) / (of(q, across) - of(p, across));
        return across ? new Spot(at, p.v() + t * (q.v() - p.v())) : new Spot(p.u() + t * (q.u() - p.u()), at);
    }

    private double of(final Spot s, final boolean across) {
        return across ? s.u() : s.v();
    }

    private double area(final List<Spot> loop) {
        double twice = 0;
        for (int k = 0; k < loop.size(); k++) {
            final Spot a = loop.get(k);
            final Spot b = loop.get((k + 1) % loop.size());
            twice += a.u() * b.v() - b.u() * a.v();
        }
        return Math.abs(twice) / 2;
    }
}
