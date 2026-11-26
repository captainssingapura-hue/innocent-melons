package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * A container's walls, between its rails and its posts, {@link #THICK} thick and set {@link #INSET} in from its
 * frame's face, finished as its skin is; where solid, ribbed outside as a container's sides are, a rib every
 * {@link #RIBS}, flush with its frame. Along its window's side, a long window across the middle {@link #BAND} of it,
 * over a wall {@link #SILL} high, glass between mullions no more than {@link #PANE} apart; at its door's end a doorway
 * {@link #DOOR_WIDE} wide and {@link #DOOR_HIGH} high in the middle of the wall, open, its floor running on into it;
 * at its other end glass across the whole of it, a mullion up its middle.
 *
 * @param box the container
 */
record Skin(Container box) {

    /** How thick a wall is; how far in from the frame's face it is set. */
    static final double THICK = 4;
    static final double INSET = 2;
    /** How far apart its ribs are; how wide each is. */
    static final double RIBS = 30;
    static final double RIB = 8;
    /** How much of its side its long window takes; how high the wall under it is; how far apart its mullions are. */
    static final double BAND = 0.6;
    static final double SILL = 45;
    static final double PANE = 150;
    static final double MULLION = 6;
    /** Its doorway: how wide, how high. */
    static final double DOOR_WIDE = 100;
    static final double DOOR_HIGH = 210;

    /** Its walls, its windows and its ribs. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final double l = box.halfLong() - Container.POST;
        for (final int side : List.of(-1, 1)) {
            if (side == box.window()) {
                final double band = BAND * l;
                parts.addAll(plain(side, -l, -band));
                parts.addAll(plain(side, band, l));
                parts.addAll(window(side, band));
            } else {
                parts.addAll(plain(side, -l, l));
            }
            parts.addAll(side == box.door() ? doorway(side) : glazed(side));
        }
        return parts;
    }

    /** The side {@code side}'s wall from {@code v0} to {@code v1} along, solid, ribbed outside. */
    private List<Part> plain(final int side, final double v0, final double v1) {
        final List<Part> parts = new ArrayList<>();
        final double w = box.halfWide() - INSET;
        parts.add(side(new Elements.Wall(), "wall", side, w - THICK, w, v0, v1, Container.RAIL, top()));
        final int ribs = (int) Math.floor((v1 - v0) / RIBS);
        for (int k = 0; k < ribs; k++) {
            final double v = v0 + (k + 0.5) * (v1 - v0) / ribs;
            parts.add(side(new Elements.Wall(), "rib", side, w, w + INSET, v - RIB / 2, v + RIB / 2,
                    Container.RAIL, top()));
        }
        return parts;
    }

    /** The side {@code side}'s long window, from {@code -band} to {@code band} along. */
    private List<Part> window(final int side, final double band) {
        final List<Part> parts = new ArrayList<>();
        final double w = box.halfWide() - INSET;
        final double sill = Container.RAIL + SILL;
        parts.add(side(new Elements.Wall(), "sill wall", side, w - THICK, w, -band, band, Container.RAIL, sill));
        final double u0 = Math.min(side * (w - THICK / 2 - 1), side * (w - THICK / 2 + 1));
        parts.add(box.at().glass(new Elements.Window(), "window", u0, u0 + 2, -band, band, sill, top()));
        final int panes = (int) Math.ceil(2 * band / PANE);
        for (int k = 0; k <= panes; k++) {
            final double v = -band + k * 2 * band / panes;
            parts.add(side(new Elements.Window(), "mullion", side, w - THICK, w, v - MULLION / 2, v + MULLION / 2,
                    sill, top(), box.frame()));
        }
        return parts;
    }

    /** The end {@code end}'s wall, a doorway in its middle. */
    private List<Part> doorway(final int end) {
        final double w = box.halfWide() - Container.POST;
        final double head = Container.RAIL + DOOR_HIGH;
        return List.of(end(new Elements.Wall(), "end wall", end, -w, -DOOR_WIDE / 2, Container.RAIL, top()),
                end(new Elements.Wall(), "end wall", end, DOOR_WIDE / 2, w, Container.RAIL, top()),
                end(new Elements.Wall(), "over the door", end, -DOOR_WIDE / 2, DOOR_WIDE / 2, head, top()));
    }

    /** The end {@code end} glazed across, a mullion up its middle. */
    private List<Part> glazed(final int end) {
        final double w = box.halfWide() - Container.POST;
        final double v = box.halfLong() - INSET - THICK / 2;
        final double v0 = Math.min(end * (v - 1), end * (v + 1));
        final double m0 = Math.min(end * (v - THICK / 2), end * (v + THICK / 2));
        return List.of(box.at().glass(new Elements.Window(), "end window", -w, w, v0, v0 + 2, Container.RAIL, top()),
                box.at().box(new Elements.Window(), "mullion", Role.FRAME, box.frame(), -MULLION / 2, MULLION / 2,
                        m0, m0 + THICK, Container.RAIL, top()));
    }

    /** How high its walls reach, under its top rails. */
    private double top() {
        return Size.HIGH - Container.RAIL;
    }

    private Part side(final Leaf<?> element, final String name, final int side,
                      final double in, final double out, final double v0, final double v1, final double y0,
                      final double y1) {
        return side(element, name, side, in, out, v0, v1, y0, y1, box.skin());
    }

    /** A piece on the side {@code side}, from {@code in} to {@code out} across from its middle. */
    private Part side(final Leaf<?> element, final String name, final int side,
                      final double in, final double out, final double v0, final double v1, final double y0,
                      final double y1, final Finish finish) {
        return box.at().box(element, name, Role.WALL, finish, Math.min(side * in, side * out),
                Math.max(side * in, side * out), v0, v1, y0, y1);
    }

    /** A piece of the end {@code end}'s wall, from {@code u0} to {@code u1} across. */
    private Part end(final Leaf<?> element, final String name, final int end,
                     final double u0, final double u1, final double y0, final double y1) {
        final double v = box.halfLong() - INSET;
        return box.at().box(element, name, Role.WALL, box.skin(), u0, u1, Math.min(end * (v - THICK), end * v),
                Math.max(end * (v - THICK), end * v), y0, y1);
    }
}
