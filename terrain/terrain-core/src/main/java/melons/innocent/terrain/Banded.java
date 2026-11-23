package melons.innocent.terrain;

import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * How high the ground of a topographic map is at a spot: read across the band it lies in, the ground between the
 * innermost contour round it and what lies directly inside that contour, its children, its spot heights and any
 * stretch of a break line as high as the band. The band's lines are paired, each pair one the ground goes up from and
 * one it goes up to, the contour round the spot or a break line among them; for each pair, a section across the band
 * from the one through the spot to the other, and on past each, a band's width, to what lies beyond it, the ground
 * along it a monotone curve through those heights; and the sections weighed by how near both their lines are. So on
 * every line the ground is as high as the line, never higher or lower between two of them than both, and across a
 * contour smooth, the section's slope there taken from the bands both sides; across a break line it turns at once.
 */
final class Banded {

    /** How far off the ground outside every contour is taken to be, as if a line, from every spot. */
    private static final double ROOT_REACH = 100_000;
    /** How near a line counts as on it, in the weights, in centimetres. */
    private static final double TOUCH = 1;

    private final Nesting nesting;
    private final List<BreakLine> breakLines;

    /** The ground of {@code nesting}'s contours and {@code breakLines}. */
    Banded(final Nesting nesting, final List<BreakLine> breakLines) {
        this.nesting = nesting;
        this.breakLines = List.copyOf(breakLines);
    }

    /**
     * A line bounding a band, seen from a spot in it.
     *
     * @param distance     how far off it is
     * @param height       how high the ground is on it, where it is nearest
     * @param low          whether the ground may go up from it across the band
     * @param high         whether the ground may go up to it
     * @param pairs        whether it is the contour round the band or a break line, which every pair has one of
     * @param beyondWidth  how far past it the next line is, the far side from the band: nothing, a break line's,
     *                     where the ground turns at once; less than nothing, a spot height's, as far as the section
     *                     is across, the ground falling away past a top (or rising past a hollow) as it came
     * @param beyondHeight how high the ground is there
     * @param share        how much it counts for, besides how near it is: one, but for a break line, which counts the
     *                     less the nearer the height there is to the top or the bottom of the band, and nothing at
     *                     either, so it comes in and goes out of the band smoothly where it crosses a contour
     */
    private record Line(double distance, double height, boolean low, boolean high, boolean pairs,
                        double beyondWidth, double beyondHeight, double share) {
    }

    /** How high the ground is at ({@code x}, {@code z}). */
    double height(final double x, final double z) {
        final List<Line> lines = lines(x, z);
        double sum = 0;
        double weights = 0;
        for (final Line from : lines) {
            for (final Line to : lines) {
                if (from != to && from.low() && to.high() && (from.pairs() || to.pairs())
                        && from.height() <= to.height()) {
                    final double weight = weight(from.distance()) * weight(to.distance()) * from.share() * to.share();
                    sum += weight * section(from, to);
                    weights += weight;
                }
            }
        }
        return weights > 0 ? sum / weights : nesting.height(nesting.within(x, z));
    }

    /** The lines bounding the band ({@code x}, {@code z}) lies in. */
    private List<Line> lines(final double x, final double z) {
        final int round = nesting.within(x, z);
        final double level = nesting.height(round);
        final boolean rises = nesting.rises(round);
        final List<Line> lines = new ArrayList<>();
        if (round == Nesting.ROOT) {
            lines.add(new Line(ROOT_REACH, level, true, true, true, 0, 0, 1));
        } else {
            final double distance = nesting.contour(round).loop().distance(x, z);
            final int parent = nesting.parent(round);
            final double beyond = parent == Nesting.ROOT ? 0
                    : Math.max(TOUCH, nesting.contour(parent).loop().distance(x, z) - distance);
            lines.add(new Line(distance, level, true, true, true, beyond, nesting.height(parent), 1));
        }
        double lowest = level;
        double highest = level;
        for (final int child : nesting.children(round)) {
            final double height = nesting.height(child);
            lines.add(new Line(nesting.contour(child).loop().distance(x, z), height, height < level || height == level
                    && !rises, height > level || height == level && rises, false, nesting.innerWidth(child),
                    nesting.innerHeight(child), 1));
            lowest = Math.min(lowest, height);
            highest = Math.max(highest, height);
        }
        for (final SpotHeight spot : nesting.spots(round)) {
            final double distance = Math.hypot(spot.at().x() - x, spot.at().z() - z);
            final double height = spot.height();
            lines.add(new Line(distance, height, height < level || height == level && !rises,
                    height > level || height == level && rises, false, -1, level, 1));
            lowest = Math.min(lowest, height);
            highest = Math.max(highest, height);
        }
        for (final BreakLine line : breakLines) {
            final BreakLine.Reach reach = line.reach(x, z);
            final double margin = (highest - lowest) / 4;
            final double share = margin > 0 ? Math.min(1, Math.min(reach.height() - lowest, highest - reach.height())
                    / margin) : 0;
            if (share > 0) {
                final boolean crest = line.kind() == BreakLine.Kind.CREST;
                lines.add(new Line(reach.distance(), reach.height(), !crest, crest, true, 0, 0, share * share));
            }
        }
        return lines;
    }

    /**
     * How much a line {@code distance} off counts for: as one over the distance to the sixth, so a line twice as far
     * off as another counts for a sixty-fourth as much, and one the spot is on, most.
     */
    private double weight(final double distance) {
        final double square = distance * distance + TOUCH * TOUCH;
        return 1 / (square * square * square);
    }

    /**
     * How high the ground is at the spot by the section from {@code from} through it to {@code to}: a monotone curve
     * through the height beyond {@code from}, its own, {@code to}'s, and the height beyond {@code to}.
     */
    private double section(final Line from, final Line to) {
        final double across = from.distance() + to.distance();
        if (!(across > 0)) {
            return from.height();
        }
        final List<Knot> knots = new ArrayList<>(4);
        if (from.beyondWidth() != 0) {
            knots.add(new Knot(-(from.beyondWidth() > 0 ? from.beyondWidth() : across), from.beyondHeight()));
        }
        knots.add(new Knot(0, from.height()));
        knots.add(new Knot(across, to.height()));
        if (to.beyondWidth() != 0) {
            knots.add(new Knot(across + (to.beyondWidth() > 0 ? to.beyondWidth() : across), to.beyondHeight()));
        }
        return new Monotone(new Knots(knots)).at(from.distance());
    }
}
