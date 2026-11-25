package melons.innocent.glm.model;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * An outline drawn flat, along {@code u} and up {@code v}, as a panel's is before it is pressed: drawn in all round by
 * so much, as a door is inside its opening by its shut gap; or cut by a line square to the drawing, along or up, and
 * the part one side of it kept, as a window is the part of a door above its waist.
 *
 * @param spots its corners, in order round it, either way
 */
record Loop(List<Spot> spots) {

    /** How far a sharp corner may reach out as it is drawn in, as many times as it is drawn in. */
    private static final double MITRE = 3;

    Loop {
        final List<Spot> kept = new ArrayList<>();
        for (final Spot s : spots) {
            if (kept.isEmpty() || kept.getLast().distance(s) > 1e-9) {
                kept.add(s);
            }
        }
        if (kept.size() > 1 && kept.getFirst().distance(kept.getLast()) < 1e-9) {
            kept.removeLast();
        }
        spots = List.copyOf(kept);
    }

    /** It drawn in all round by {@code by}, each side moved in square to itself, the corners mitred. */
    Loop inset(final double by) {
        final double turn = area() > 0 ? 1 : -1;
        final int n = spots.size();
        final List<Spot> out = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            final Spot before = inward(spots.get((k + n - 1) % n), spots.get(k), turn);
            final Spot after = inward(spots.get(k), spots.get((k + 1) % n), turn);
            final double meet = 1 + before.u() * after.u() + before.v() * after.v();
            final double length = Math.hypot(before.u() + after.u(), before.v() + after.v());
            final Spot p = spots.get(k);
            if (length < 1e-9) {
                out.add(p);
                continue;
            }
            final double reach = Math.min(by * Math.sqrt(2 / Math.max(meet, 1e-12)), MITRE * by);
            out.add(new Spot(p.u() + (before.u() + after.u()) / length * reach,
                    p.v() + (before.v() + after.v()) / length * reach));
        }
        return new Loop(out);
    }

    /** The part of it at or above {@code at} up, or below it if not {@code above}. */
    Loop up(final double at, final boolean above) {
        return cut(at, false, above);
    }

    /** The part of it at or ahead of {@code at} along, or behind it if not {@code ahead}. */
    Loop along(final double at, final boolean ahead) {
        return cut(at, true, ahead);
    }

    /** How high its top is at {@code at} along: the highest of its sides crossing there; or none, nought. */
    double top(final double at) {
        double high = 0;
        for (int k = 0; k < spots.size(); k++) {
            final Spot a = spots.get(k);
            final Spot b = spots.get((k + 1) % spots.size());
            if ((a.u() - at) * (b.u() - at) <= 0 && a.u() != b.u()) {
                high = Math.max(high, a.v() + (at - a.u()) / (b.u() - a.u()) * (b.v() - a.v()));
            }
        }
        return high;
    }

    /** How large it is, positive if it runs round to the left. */
    double area() {
        double twice = 0;
        for (int k = 0; k < spots.size(); k++) {
            final Spot a = spots.get(k);
            final Spot b = spots.get((k + 1) % spots.size());
            twice += a.u() * b.v() - b.u() * a.v();
        }
        return twice / 2;
    }

    /** The part of it on one side of a line square to the drawing, across {@code u} if {@code across}, else up. */
    private Loop cut(final double at, final boolean across, final boolean beyond) {
        final List<Spot> out = new ArrayList<>();
        final int n = spots.size();
        for (int k = 0; k < n; k++) {
            final Spot a = spots.get(k);
            final Spot b = spots.get((k + 1) % n);
            final double da = (across ? a.u() : a.v()) - at;
            final double db = (across ? b.u() : b.v()) - at;
            final boolean inA = beyond ? da >= 0 : da <= 0;
            final boolean inB = beyond ? db >= 0 : db <= 0;
            if (inA) {
                out.add(a);
            }
            if (inA != inB && Math.abs(da - db) > 1e-12) {
                final double t = da / (da - db);
                out.add(across ? new Spot(at, a.v() + t * (b.v() - a.v())) : new Spot(a.u() + t * (b.u() - a.u()), at));
            }
        }
        return new Loop(out);
    }

    /** The way square to the side from {@code a} to {@code b}, into the outline, a unit long. */
    private Spot inward(final Spot a, final Spot b, final double turn) {
        final double du = b.u() - a.u();
        final double dv = b.v() - a.v();
        final double length = Math.hypot(du, dv);
        return new Spot(-dv / length * turn, du / length * turn);
    }
}
