package melons.innocent.road.bridge;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import melons.innocent.road.surface.Course;
import melons.innocent.road.surface.Section;
import melons.innocent.road.surface.Stretch;

import java.util.ArrayList;
import java.util.List;

/**
 * What carries a road along {@code course}, high over the ground: a deck of concrete {@code depth} deep under it, its
 * top where the course is, as wide as the road and a parapet {@link #PARAPET} wide either side; a parapet along each
 * edge, {@link #HIGH} over the deck; and the road's surface on it, following it ({@link Stretch}). Laid in pieces
 * {@link #PIECE} long at most, shorter where its course curves or climbs. What holds it up stands under it.
 *
 * @param section what it carries
 * @param course  its line, and how high its top is along it
 * @param depth   how deep it is
 */
record Deck(Section section, Course course, double depth) {

    /** How wide a parapet is, and how high over the deck; how long a piece of the deck is, at most. */
    static final double PARAPET = 50;
    static final double HIGH = 110;
    static final double PIECE = 1_000;

    Deck {
        if (section == null || course == null || !(depth > 0)) {
            throw new IllegalArgumentException("a deck carrying some road along some course, some way deep");
        }
    }

    /** How far it reaches from its line either way, its parapets with it. */
    double half() {
        return section.wide() / 2 + PARAPET;
    }

    /** How high its underside is {@code s} along it, under its line. */
    double under(final double s) {
        return course.at(s).up() - depth;
    }

    /** Its parts: the road on it, it in its pieces, and its parapets. */
    List<Part> parts() {
        final Blocks blocks = new Blocks(course);
        final List<Part> parts = new ArrayList<>(new Stretch(section, course).parts());
        final List<Double> cuts = course.cuts(PIECE);
        for (int k = 1; k < cuts.size(); k++) {
            final double s0 = cuts.get(k - 1);
            final double s1 = cuts.get(k);
            parts.add(blocks.laid(new Elements.Decking(), "deck", -half(), half(), s0, s1, -depth, 0));
            parts.add(blocks.laid(new Elements.Parapet(), "parapet", -half(), -half() + PARAPET, s0, s1, 0, HIGH));
            parts.add(blocks.laid(new Elements.Parapet(), "parapet", half() - PARAPET, half(), s0, s1, 0, HIGH));
        }
        return parts;
    }

    /** Where it is come onto: the road on it, at its far end. */
    List<Structure.WayIn> waysIn() {
        return new Stretch(section, course).structure().waysIn();
    }
}
