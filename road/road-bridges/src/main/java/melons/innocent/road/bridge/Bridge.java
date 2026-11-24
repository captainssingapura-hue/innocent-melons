package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import melons.innocent.road.surface.Course;
import melons.innocent.road.surface.Section;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * A bridge on piers along {@code course}, standing on {@code ground}: its deck ({@link Deck}), {@code depth} deep, the
 * road on it; at each of {@code piers}, so far along it, a cap of concrete across under the deck, on one wide column
 * under its middle or two under its sides ({@link Piers}), each column standing from the ground under it up to the
 * cap, as tall as the ground there leaves it; where the ground leaves no room for a column, a plinth from the ground
 * up to the deck instead, or nothing where the deck is on the ground; at each end an abutment across under the deck,
 * from the ground at its lowest there up to the deck. Level or climbing: on level ground under a level deck every
 * column alike, otherwise each its own height.
 *
 * @param section what it carries across
 * @param course  its line, and how high its deck's top is along it, the road laid on it
 * @param depth   how deep its deck is
 * @param piers   how far along it each pier stands
 * @param kind    how its piers stand
 * @param ground  what it stands on
 */
public record Bridge(Section section, Course course, double depth, List<Double> piers, Piers kind, Ground ground) {

    /** A pier's cap: how deep, how long along, how far in from the deck's edges it stops. */
    static final double CAP_DEEP = 150;
    static final double CAP_ALONG = 250;
    static final double CAP_IN = 100;
    /** A single column, across and along; twin columns, square, and how far in from the deck's edges they stand. */
    static final double SINGLE_ACROSS = 300;
    static final double SINGLE_ALONG = 200;
    static final double TWIN = 160;
    static final double TWIN_IN = 300;
    /** How thick an abutment is, along; how tall a column is, at least, or a plinth stands instead. */
    static final double ABUTMENT = 150;
    static final double SHORTEST = 50;

    public Bridge {
        piers = List.copyOf(piers);
        if (kind == null || ground == null) {
            throw new IllegalArgumentException("a bridge its piers of some kind, on ground");
        }
    }

    /** Its deck. */
    Deck deck() {
        return new Deck(section, course, depth);
    }

    /** It as a thing built: its road come onto at its far end, on its deck. */
    public Structure structure() {
        final List<Part> parts = parts(true);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                deck().waysIn());
    }

    /**
     * Its parts: its deck, the road on it, its piers, an abutment at its start, and one at its far end if {@code far}:
     * none there where it meets another on what holds that other up.
     */
    List<Part> parts(final boolean far) {
        final Deck deck = deck();
        final List<Part> parts = new ArrayList<>(deck.parts());
        piers.forEach(s -> parts.addAll(pier(deck, s)));
        final double length = course.length();
        if (far) {
            parts.addAll(abutment(deck, length - ABUTMENT, length));
        }
        parts.addAll(abutment(deck, 0, ABUTMENT));
        return parts;
    }

    /** The pier {@code s} along: its cap, and its column or columns down to the ground; or a plinth. */
    private List<Part> pier(final Deck deck, final double s) {
        final Blocks blocks = new Blocks(course);
        final double half = deck.half();
        final double s0 = s - CAP_ALONG / 2;
        final double s1 = s + CAP_ALONG / 2;
        final List<Double> at = kind == Piers.SINGLE ? List.of(0.0) : List.of(-(half - TWIN_IN), half - TWIN_IN);
        final double along = kind == Piers.SINGLE ? SINGLE_ALONG : TWIN;
        final double top = Math.max(deck.under(s - along / 2), deck.under(s + along / 2)) - CAP_DEEP;
        final double highest = at.stream().mapToDouble(x -> ground(s, x)).max().orElseThrow();
        if (top - highest < SHORTEST) {
            return plinth(blocks, "plinth", -half + CAP_IN, half - CAP_IN, s0, s1);
        }
        final List<Part> parts = new ArrayList<>();
        parts.add(blocks.laid(new Elements.Beam(), "pier cap", -half + CAP_IN, half - CAP_IN, s0, s1,
                -depth - CAP_DEEP, -depth));
        for (final double x : at) {
            parts.add(blocks.upright(new Elements.Column(), "column", s, x,
                    kind == Piers.SINGLE ? SINGLE_ACROSS : TWIN, along, ground(s, x), top));
        }
        return parts;
    }

    /** An abutment from {@code s0} to {@code s1} along, across under the deck, from the ground at its lowest to it. */
    private List<Part> abutment(final Deck deck, final double s0, final double s1) {
        return plinth(new Blocks(course), "abutment", -deck.half(), deck.half(), s0, s1);
    }

    /** A plinth from {@code x0} to {@code x1}, {@code s0} to {@code s1}, from the ground to the deck; or none. */
    private List<Part> plinth(final Blocks blocks, final String name, final double x0, final double x1, final double s0,
                              final double s1) {
        double foot = Double.POSITIVE_INFINITY;
        double under = Double.POSITIVE_INFINITY;
        for (final double s : List.of(s0, s1)) {
            for (final double x : List.of(x0, (x0 + x1) / 2, x1)) {
                foot = Math.min(foot, ground(s, x));
            }
            under = Math.min(under, course.at(s).up() - depth);
        }
        return under - foot < 1 ? List.of() : List.of(blocks.footed(new Elements.Abutment(), name, x0, x1, s0,
                s1, foot, -depth));
    }

    /** How high the ground is {@code x} across from its line, {@code s} along it. */
    private double ground(final double s, final double x) {
        final Point p = course.beside(s, x, 0);
        return ground.height(p.across(), p.along());
    }
}
