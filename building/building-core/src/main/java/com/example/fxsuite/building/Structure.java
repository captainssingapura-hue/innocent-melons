package com.example.fxsuite.building;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Occupied;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Through;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 * What an implementation builds, in its own frame (across, along, up; centimetres; its front toward along), said in
 * the taxonomy's terms so that it can be checked whoever built it: its parts, each an element; what of it is in the
 * way; its spaces, each of a kind and for a use; and its ways in.
 *
 * @param parts  what it is made of
 * @param shape  what it is in the way, stood on and walked into
 * @param rooms  its spaces
 * @param waysIn where it is come into from outside
 */
public record Structure(List<Part> parts, List<Volume> shape, List<Room> rooms, List<WayIn> waysIn) {

    /** How near a way in must be to a space's edge to be a way into it. */
    static final double NEAR = 50;

    /** A part: the element it is ({@link Elements}), and it as a solid, drawn. */
    public record Part(Leaf<?> element, Solid solid) {
    }

    /**
     * A space: its kind ({@link Spaces}), what it is for ({@link Uses}), its floor's outline and how high its floor is.
     */
    public record Room(Leaf<?> kind, Leaf<?> use, List<Vec2> outline, double level) {

        public Room {
            outline = List.copyOf(outline);
        }
    }

    /** A way in: where, across and along, and how high. */
    public record WayIn(double across, double along, double level) {
    }

    public Structure {
        parts = List.copyOf(parts);
        shape = List.copyOf(shape);
        rooms = List.copyOf(rooms);
        waysIn = List.copyOf(waysIn);
    }

    /** Its parts as solids, to be drawn. */
    public List<Solid> solids() {
        return parts.stream().map(Part::solid).toList();
    }

    /** Its parts that are {@code element}. */
    public List<Solid> solids(final Leaf<?> element) {
        return parts.stream().filter(p -> p.element().equals(element)).map(Part::solid).toList();
    }

    /**
     * Everything wrong with it as a thing that works, all of it; none if nothing is. It is made of something; each
     * part is an element, each space a kind of space for a use, as the taxonomy has them; a space people spend their
     * time in ({@link Occupied}) has a way in; a space gone through ({@link Through}) has two, its ends.
     */
    public List<String> problems() {
        final List<String> problems = new ArrayList<>();
        if (parts.isEmpty()) {
            problems.add("made of nothing");
        }
        parts.stream().map(Part::element).distinct().filter(e -> !Elements.TREE.has(e))
                .forEach(e -> problems.add(e.title() + " is no element"));
        for (final Room room : rooms) {
            final String what = room.kind().title() + " for " + room.use().title();
            if (!Spaces.TREE.has(room.kind())) {
                problems.add(room.kind().title() + " is no kind of space");
            }
            if (!Uses.TREE.has(room.use())) {
                problems.add(room.use().title() + " is no use");
            }
            final long ways = waysIn.stream().filter(w -> into(w, room)).count();
            if (Taxonomy.answer(room.use(), Occupied.class).isPresent() && ways < 1) {
                problems.add(what + ": people spend their time there, and there is no way in");
            }
            if (Taxonomy.answer(room.use(), Through.class).isPresent() && ways < 2) {
                problems.add(what + ": gone through, and it has " + ways + " of its two ends");
            }
        }
        return problems;
    }

    /** Whether {@code way} leads into {@code room}: at its floor's height, inside its outline or at its edge. */
    private static boolean into(final WayIn way, final Room room) {
        if (Math.abs(way.level() - room.level()) > NEAR) {
            return false;
        }
        final List<Vec2> o = room.outline();
        boolean inside = false;
        double nearest = Double.POSITIVE_INFINITY;
        for (int i = 0, j = o.size() - 1; i < o.size(); j = i++) {
            final Vec2 a = o.get(i);
            final Vec2 b = o.get(j);
            if (a.z() > way.along() != b.z() > way.along()
                    && way.across() < (b.x() - a.x()) * (way.along() - a.z()) / (b.z() - a.z()) + a.x()) {
                inside = !inside;
            }
            final double dx = b.x() - a.x();
            final double dz = b.z() - a.z();
            final double t = Math.clamp(((way.across() - a.x()) * dx + (way.along() - a.z()) * dz)
                    / (dx * dx + dz * dz), 0, 1);
            nearest = Math.min(nearest, Math.hypot(way.across() - a.x() - t * dx, way.along() - a.z() - t * dz));
        }
        return inside || nearest <= NEAR;
    }
}
