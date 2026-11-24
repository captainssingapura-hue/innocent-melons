package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * A straight stretch of road, {@code length} long, its surface as {@code section} has it, laid on level ground: its
 * line along, its middle at nought, its strips in pieces {@link #PIECE} long, the lines painted on them; what is in the
 * way of it, stood on and driven on, its solids as they are. Come onto at its far end, along, in the middle of its
 * first lane from the left, where its pavement is low, clear of anything down its middle.
 *
 * @param section its surface across
 * @param length  how long it is
 */
public record Stretch(Section section, double length) {

    /** How long a piece of a strip is, at most. */
    static final double PIECE = 1_000;

    public Stretch {
        if (section == null || !(length > 0)) {
            throw new IllegalArgumentException("a stretch of some section, some way long: " + section + ", "
                    + length);
        }
    }

    /** It as a thing built. */
    public Structure structure() {
        final List<Part> parts = new ArrayList<>();
        final Pieces pieces = new Pieces(section);
        for (double z = -length / 2; z < length / 2 - 1e-6; z += PIECE) {
            parts.addAll(pieces.along(new Shapes.Run(z, Math.min(z + PIECE, length / 2))));
        }
        parts.addAll(new Markings(section).along(length));
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                List.of(new Structure.WayIn(firstLane(), length / 2, 0)));
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
