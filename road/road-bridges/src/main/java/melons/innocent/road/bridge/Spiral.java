package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.existence.Elements;
import melons.innocent.road.surface.Placed;
import melons.innocent.road.surface.Section;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * A spiral ramp: its deck ({@link Deck}), {@code depth} deep, climbing round along {@code line}, a helix placed where
 * it is wanted, the road on it following it; standing on {@code ground}. Held up at stations round its middle, about
 * {@link #BAY} apart along its line: at each, a column outside its outer edge standing from the ground up to its
 * topmost turn there, and inside its inner edge another, or its core ({@link Carried}); and under each turn passing
 * the station a beam across from the one to the other, the turn on it. Where a turn is too low for a beam under it,
 * a plinth from the ground up to it instead, or nothing where it lies on the ground. Its core, if it has one, a wall
 * {@link #WALL} thick round its middle, from the ground up to its topmost beams.
 *
 * @param section what it carries
 * @param line    its line, a helix placed, and how high its deck's top is along it
 * @param depth   how deep its deck is
 * @param carried how its turns are held up
 * @param ground  what it stands on
 */
public record Spiral(Section section, Placed<Helix> line, double depth, Carried carried, Ground ground) {

    /** How far apart its stations are along its line, about; how far apart its core's pieces are round it, about. */
    static final double BAY = 1_500;
    static final double ROUND = 300;
    /** A column, square; how far clear of the deck's edge it stands; a beam, how deep and how long along. */
    static final double COLUMN = 80;
    static final double GAP = 50;
    static final double BEAM_DEEP = 100;
    static final double BEAM_ALONG = 80;
    /** How thick its core's wall is; how tall a column is, at least, or a plinth stands instead. */
    static final double WALL = 40;
    static final double SHORTEST = 50;

    public Spiral {
        if (section == null || line == null || carried == null || ground == null || !(depth > 0)) {
            throw new IllegalArgumentException("a spiral ramp carrying some road round, some way deep, held up some"
                    + " way, on ground");
        }
    }

    /** Its line, where it is worked out, about its middle. */
    public Helix helix() {
        return line.course();
    }

    /** Its deck. */
    Deck deck() {
        return new Deck(section, line, depth);
    }

    /** How far its core's wall is from its middle, outside; none if it has none. */
    public double core() {
        return carried == Carried.CORE ? helix().radius() - deck().half() - GAP : 0;
    }

    /** It as a thing built: its road come onto at its top, on its deck. */
    public Structure structure() {
        final List<Part> parts = parts();
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                deck().waysIn());
    }

    /** Its parts: its deck, the road on it, its beams, its columns, and its core if it has one. */
    List<Part> parts() {
        final Deck deck = deck();
        final List<Part> parts = new ArrayList<>(deck.parts());
        stations().forEach(a -> parts.addAll(station(deck, a)));
        if (carried == Carried.CORE) {
            parts.addAll(walls(deck));
        }
        return parts;
    }

    /** How far round its middle each station is, in radians: evenly round, and one where its top ends. */
    List<Double> stations() {
        final int n = Math.max(4, (int) Math.round(2 * Math.PI * helix().radius() / BAY));
        final List<Double> stations = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            stations.add(2 * Math.PI * k / n);
        }
        final double end = helix().round(helix().length()) % (2 * Math.PI);
        if (stations.stream().noneMatch(a -> Math.abs(a - end) < 1e-6 || Math.abs(a - end) > 2 * Math.PI - 1e-6)) {
            stations.add(end);
        }
        return stations;
    }

    /** How far along its line each turn passes {@code a} round. */
    List<Double> passes(final double a) {
        final List<Double> passes = new ArrayList<>();
        for (double s = a * helix().radius(); s <= helix().length() + 1e-6; s += 2 * Math.PI * helix().radius()) {
            passes.add(Math.min(s, helix().length()));
        }
        return passes;
    }

    /** The station {@code a} round: a beam under each turn passing it, or a plinth, and its columns. */
    private List<Part> station(final Deck deck, final double a) {
        final Blocks blocks = new Blocks(line);
        final double out = -(deck.half() + GAP + COLUMN);
        final double in = carried == Carried.CORE ? deck.half() + GAP + WALL : deck.half() + GAP + COLUMN;
        final List<Part> parts = new ArrayList<>();
        double top = Double.NEGATIVE_INFINITY;
        double at = 0;
        for (final double s : passes(a)) {
            final double s0 = Math.max(0, s - BEAM_ALONG / 2);
            final double s1 = Math.min(helix().length(), s + BEAM_ALONG / 2);
            final double bottom = deck.under(s) - BEAM_DEEP;
            if (bottom - Math.max(ground(s, out), ground(s, in)) >= SHORTEST) {
                parts.add(blocks.laid(new Elements.Beam(), "beam", out, in, s0, s1, -depth - BEAM_DEEP, -depth));
                top = Math.max(top, bottom + BEAM_DEEP / 2);
                at = s;
            } else {
                parts.addAll(plinth(deck, blocks, s0, s1));
            }
        }
        if (top > Double.NEGATIVE_INFINITY) {
            parts.add(column(blocks, at, out + COLUMN / 2, top));
            if (carried == Carried.COLUMNS) {
                parts.add(column(blocks, at, in - COLUMN / 2, top));
            }
        }
        return parts;
    }

    private Part column(final Blocks blocks, final double s, final double x, final double top) {
        return blocks.upright(new Elements.Column(), "column", s, x, COLUMN, COLUMN, ground(s, x), top);
    }

    /** A plinth under the deck from {@code s0} to {@code s1}, from the ground up to it; none where it is on it. */
    private List<Part> plinth(final Deck deck, final Blocks blocks, final double s0, final double s1) {
        final double half = deck.half();
        final double foot = Math.min(Math.min(ground(s0, -half), ground(s0, half)),
                Math.min(ground(s1, -half), ground(s1, half)));
        return Math.min(deck.under(s0), deck.under(s1)) - foot < 1 ? List.of()
                : List.of(blocks.footed(new Elements.Abutment(), "plinth", -half, half, s0, s1, foot, -depth));
    }

    /** Its core's wall, piece by piece round its middle, from the ground up into its topmost beams. */
    private List<Part> walls(final Deck deck) {
        final Blocks blocks = new Blocks(line);
        final double outside = core();
        final double inside = outside - WALL;
        final double top = deck.under(helix().length()) - BEAM_DEEP / 2;
        final int n = Math.max(12, (int) Math.round(2 * Math.PI * outside / ROUND));
        final List<Part> parts = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            final double s0 = 2 * Math.PI * k / n * helix().radius();
            final double s1 = 2 * Math.PI * (k + 1) / n * helix().radius();
            final List<Point> plan = List.of(plan(s0, inside), plan(s0, outside), plan(s1, inside), plan(s1, outside));
            final double middle = (s0 + s1) / 2;
            parts.add(blocks.upright(new Elements.BearingWall(), "core", plan,
                    ground(middle, helix().radius() - (inside + outside) / 2), top));
        }
        return parts;
    }

    /** Where it is {@code r} from its middle, as far round as {@code s} along its line is. */
    private Point plan(final double s, final double r) {
        return line.beside(s, helix().radius() - r, 0);
    }

    /** How high the ground is {@code x} across from its line, {@code s} along it. */
    private double ground(final double s, final double x) {
        final Point p = line.beside(s, x, 0);
        return ground.height(p.across(), p.along());
    }
}
