package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;

import java.util.ArrayList;
import java.util.List;

/**
 * A shipping container made a room: {@code size} long, standing as {@code at} places it. Its frame finished in
 * {@code frame}: a post {@link #POST} square at each corner, the whole of its height, and a rail along each edge of
 * its floor and of its roof; its floor, timber, its top flush with the rails under it; its roof a panel sunk a little
 * between the rails over it; its walls, its skin, finished in {@code skin} ({@link Skin}): at the end {@code door}
 * shows, a doorway in its wall; at the other end glass; along the side {@code window} shows, a long window. Its floor
 * is a room to live in, come into at its door.
 *
 * @param size   how long it is
 * @param at     where it stands
 * @param door   which end its door is at, its length's far end, one, or its near end, minus one
 * @param window which side its long window is on, across its width, one or minus one
 * @param skin   what its walls and roof are finished in
 * @param frame  what its frame is finished in
 */
record Container(Size size, Placing at, int door, int window, Finish skin, Finish frame) {

    /** A post, square; a rail, square; how high its floor's top is over its underside. */
    static final double POST = 15;
    static final double RAIL = 15;
    static final double FLOOR = RAIL;
    /** How deep its roof sits under the rails' top, and how thick it is. */
    static final double SUNK = 4;
    static final double ROOF = 6;

    /** How far it reaches from its middle across, and along. */
    double halfWide() {
        return Size.WIDE / 2;
    }

    double halfLong() {
        return size.length() / 2;
    }

    /** How high its underside is; its top. */
    double under() {
        return at.y();
    }

    double top() {
        return at.y() + Size.HIGH;
    }

    /** How high its floor's top is. */
    double level() {
        return at.y() + FLOOR;
    }

    /** Whether ({@code x}, {@code z}) is over it or under it, seen from above. */
    boolean covers(final double x, final double z) {
        final Vec2 a = at.at(-halfWide(), -halfLong());
        final Vec2 b = at.at(halfWide(), halfLong());
        return x >= Math.min(a.x(), b.x()) && x <= Math.max(a.x(), b.x()) && z >= Math.min(a.z(), b.z())
                && z <= Math.max(a.z(), b.z());
    }

    /** Where it wants holding up, {@code inset} in from its edges: its corners, and its long sides' middles. */
    List<Vec2> bearings(final double inset) {
        final List<Vec2> points = new ArrayList<>();
        for (final double u : List.of(-halfWide() + inset, halfWide() - inset)) {
            for (final double v : List.of(-halfLong() + inset, 0.0, halfLong() - inset)) {
                points.add(at.at(u, v));
            }
        }
        return points;
    }

    /** Its parts: its frame, its floor, its roof and its skin. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final double w = halfWide();
        final double l = halfLong();
        final double h = Size.HIGH;
        for (final int a : List.of(-1, 1)) {
            for (final int b : List.of(-1, 1)) {
                parts.add(at.box(new Elements.Column(), "corner post", Role.FRAME, frame, edge(a, w, POST),
                        edge(a, w, 0), edge(b, l, POST), edge(b, l, 0), 0, h));
            }
            for (final double y : List.of(0.0, h - RAIL)) {
                parts.add(at.box(new Elements.Beam(), "side rail", Role.FRAME, frame, edge(a, w, POST), edge(a, w, 0),
                        -l + POST, l - POST, y, y + RAIL));
                parts.add(at.box(new Elements.Beam(), "end rail", Role.FRAME, frame, -w + POST, w - POST,
                        edge(a, l, POST), edge(a, l, 0), y, y + RAIL));
            }
        }
        parts.add(at.box(new Elements.Floor(), "floor", Role.STRUCTURE, Finish.TIMBER, -w + POST, w - POST, -l + POST,
                l - POST, 0, FLOOR));
        parts.add(at.box(new Elements.Roof(), "roof", Role.ROOF, skin, -w + POST, w - POST, -l + POST, l - POST,
                h - SUNK - ROOF, h - SUNK));
        parts.addAll(new Skin(this).parts());
        return parts;
    }

    /** Its floor as a room to live in. */
    Structure.Room room() {
        final double w = halfWide() - Skin.INSET - Skin.THICK;
        final double l = halfLong() - Skin.INSET - Skin.THICK;
        return new Structure.Room(new Spaces.Room(), new Uses.Homes(), List.of(at.at(-w, -l), at.at(w, -l),
                at.at(w, l), at.at(-w, l)), level());
    }

    /** Where it is come into: at its door, on its floor. */
    Structure.WayIn wayIn() {
        final Vec2 p = at.at(0, door * halfLong());
        return new Structure.WayIn(p.x(), p.z(), level());
    }

    /** {@code half} out from its middle on the side {@code side}, and {@code in} back in from there. */
    private double edge(final int side, final double half, final double in) {
        return side * (half - in);
    }
}
