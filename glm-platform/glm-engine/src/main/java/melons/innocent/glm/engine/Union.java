package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The outline round two overlapping outlines, each the smallest round its own spots ({@link Hull}), both drawn round
 * to the left: along the first while it is outside the second, across onto the second where they cross, along it
 * while it is outside the first, and back where they cross again; so where the two stand apart, a notch between them.
 *
 * @param first  the first outline, drawn round to the left
 * @param second the second, the same
 */
record Union(List<Spot> first, List<Spot> second) {

    private static final double CLOSE = 1e-9;

    Union {
        first = List.copyOf(first);
        second = List.copyOf(second);
    }

    /** It, drawn round to the left from the first's spot furthest outside the second. */
    List<Spot> outline() {
        int start = 0;
        for (int i = 0; i < first.size(); i++) {
            if (!within(first.get(i), second) && (within(first.get(start), second) || first.get(i).u()
                    > first.get(start).u())) {
                start = i;
            }
        }
        final List<Spot> made = new ArrayList<>();
        List<Spot> on = first;
        List<Spot> other = second;
        int at = start;
        for (int guard = 0; guard < 4 * (first.size() + second.size()); guard++) {
            final Spot a = on.get(at);
            final Spot b = on.get((at + 1) % on.size());
            if (on == first && at == start && !made.isEmpty()) {
                return made;
            }
            made.add(a);
            final int crossed = crossing(a, b, other);
            if (crossed < 0) {
                at = (at + 1) % on.size();
                continue;
            }
            made.add(cross(a, b, other.get(crossed), other.get((crossed + 1) % other.size())));
            final List<Spot> was = on;
            on = other;
            other = was;
            at = (crossed + 1) % on.size();
            if (on == first && at == start) {
                return made;
            }
        }
        throw new IllegalStateException("the two outlines do not cross twice");
    }

    /** Which edge of {@code outline} the edge from {@code a} to {@code b} crosses, going into it; none, -1. */
    private int crossing(final Spot a, final Spot b, final List<Spot> outline) {
        if (within(a, outline) || !within(b, outline)) {
            return -1;
        }
        for (int i = 0; i < outline.size(); i++) {
            final Spot c = outline.get(i);
            final Spot d = outline.get((i + 1) % outline.size());
            if (turn(c, d, a) * turn(c, d, b) < 0 && turn(a, b, c) * turn(a, b, d) < 0) {
                return i;
            }
        }
        return -1;
    }

    /** Where the line through {@code a} and {@code b} crosses the one through {@code c} and {@code d}. */
    private Spot cross(final Spot a, final Spot b, final Spot c, final Spot d) {
        final double t = turn(c, d, a) / (turn(c, d, a) - turn(c, d, b));
        return new Spot(a.u() + t * (b.u() - a.u()), a.v() + t * (b.v() - a.v()));
    }

    /** Whether {@code p} is within {@code outline}, drawn round to the left: to the left of every edge. */
    private boolean within(final Spot p, final List<Spot> outline) {
        for (int i = 0; i < outline.size(); i++) {
            if (turn(outline.get(i), outline.get((i + 1) % outline.size()), p) <= CLOSE) {
                return false;
            }
        }
        return true;
    }

    /** How far {@code p} turns to the left of the line from {@code a} to {@code b}, as a cross product. */
    private double turn(final Spot a, final Spot b, final Spot p) {
        return (b.u() - a.u()) * (p.v() - a.v()) - (b.v() - a.v()) * (p.u() - a.u());
    }
}
