package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A straight flight of steel stairs outside, from {@code x0} to {@code x1} across, going along: up from {@code foot}
 * high to {@code head} high, which it reaches at {@code end} along, climbing toward {@code way}, one or minus one. As
 * many risers as keep each no higher than {@link #RISER}, the last up onto what it reaches; each tread a plate
 * {@link #PLATE} thick and {@link #GOING} deep; carried on a stringer {@link #STRINGER} wide either side, sloping with
 * it. Finished in {@code frame}.
 *
 * @param x0    its left edge, across
 * @param x1    its right edge
 * @param end   where its head is, along
 * @param way   which way along it climbs
 * @param foot  how high its foot is
 * @param head  how high its head is
 * @param frame what it is finished in
 */
record Flight(double x0, double x1, double end, int way, double foot, double head, Finish frame) {

    /** How high a riser is, at most; how deep a tread; how thick its plate; a stringer, how wide and how deep. */
    static final double RISER = 18;
    static final double GOING = 28;
    static final double PLATE = 5;
    static final double STRINGER = 5;
    static final double DEEP = 25;

    /** How many risers it has. */
    int risers() {
        return (int) Math.ceil((head - foot) / RISER - 1e-9);
    }

    /** How far along its foot is. */
    double start() {
        return end - way * (risers() - 1) * GOING;
    }

    /** Its treads, and its stringers. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final int n = risers();
        final double riser = (head - foot) / n;
        for (int k = 1; k < n; k++) {
            final double a = end - way * (n - k) * GOING;
            final double b = end - way * (n - k - 1) * GOING;
            final double top = foot + k * riser;
            parts.add(new Part(new Elements.Steps(), frame.on(Timbers.INSTANCE.block("tread", Role.STRUCTURE,
                    frame.material(), x0, x1, Math.min(a, b), Math.max(a, b), top - PLATE, top))));
        }
        parts.add(stringer(x0 - STRINGER, x0));
        parts.add(stringer(x1, x1 + STRINGER));
        return parts;
    }

    /** It as a stair, from its foot to its head. */
    Structure.Stair stair() {
        final double x = (x0 + x1) / 2;
        return new Structure.Stair(new Structure.WayIn(x, start(), foot), new Structure.WayIn(x, end, head));
    }

    /** A stringer from {@code a} to {@code b} across, from its foot up to its head. */
    private Part stringer(final double a, final double b) {
        final double s = start();
        final List<Point> corners = new ArrayList<>(8);
        for (final double up : List.of(0.0, DEEP)) {
            corners.add(new Point(a, s, foot + up));
            corners.add(new Point(b, s, foot + up));
            corners.add(new Point(a, end, head - DEEP + up));
            corners.add(new Point(b, end, head - DEEP + up));
        }
        return new Part(new Elements.Beam(), frame.on(new Solid("stringer", Role.STRUCTURE, frame.material(),
                corners)));
    }
}
