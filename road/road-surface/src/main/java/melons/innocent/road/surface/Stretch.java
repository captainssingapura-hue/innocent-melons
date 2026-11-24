package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A stretch of road, its surface as {@code section} has it, laid along {@code course}: on the ground, or on a bridge's
 * deck, straight or curving, level or climbing as what carries it has its course. Its strips in pieces {@link #PIECE}
 * long at most, shorter where its course would have them so, the lines painted on them; what is in the way of it,
 * stood on and driven on, its solids as they are. Come onto at its far end, in the middle of its first lane from the
 * left, where its pavement is low, clear of anything down its middle.
 *
 * @param section its surface across
 * @param course  what it is laid along
 */
public record Stretch(Section section, Course course) {

    /** How long a piece of a strip is, at most. */
    static final double PIECE = 1_000;

    public Stretch {
        if (section == null || course == null) {
            throw new IllegalArgumentException("a stretch of some section, along some course: " + section + ", "
                    + course);
        }
    }

    /** A straight one, {@code length} long, its middle at nought, laid on what is level, {@code level} high. */
    public Stretch(final Section section, final double length, final double level) {
        this(section, new Straight(length, level));
    }

    /** One laid on the ground, where height is nought. */
    public Stretch(final Section section, final double length) {
        this(section, length, 0);
    }

    /** It as a thing built. */
    public Structure structure() {
        final List<Part> parts = parts();
        final Point in = course.beside(course.length(), firstLane(), 0);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                List.of(new Structure.WayIn(in.across(), in.along(), in.up())));
    }

    /** Its parts: its strips, and the lines painted on them. */
    public List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final Pieces pieces = new Pieces(section, course);
        final List<Double> cuts = course.cuts(PIECE);
        for (int k = 1; k < cuts.size(); k++) {
            parts.addAll(pieces.along(new Shapes.Run(cuts.get(k - 1), cuts.get(k))));
        }
        final boolean bends = Double.isFinite(course.piece());
        parts.addAll(new Markings(section, course, bends ? cuts : List.of()).along());
        return parts;
    }

    /** How far across from its line the middle of its first lane from the left is; its line, if it has none. */
    private double firstLane() {
        for (int k = 0; k < section.strips().size(); k++) {
            if (section.strips().get(k).kind() == Strip.Kind.LANE) {
                return (section.edge(k) + section.edge(k + 1)) / 2;
            }
        }
        return 0;
    }
}
