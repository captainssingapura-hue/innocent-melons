package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * How two boxes are run into each other, whichever way they are turned. Two that are apart can be told apart
 * looking along a face of one, a face of the other, or a line square to an edge of each; two that cannot are run
 * into each other, and the way they are least run in is the way to part them.
 *
 * @param box    one box
 * @param placed where it is
 * @param other  the other box
 * @param stands where that is
 */
record Overlap(Box box, Placed placed, Box other, Placed stands) {

    /**
     * How two boxes are parted.
     *
     * @param pushes the way the first is pushed to part them: straight away from the second, along the way they
     *               are least run in
     * @param deep   how far they are run in along that way, in centimetres
     */
    record Parting(Spot pushes, double deep) {
    }

    /** How the two are parted, where they are run into each other at all: nothing, where they are apart. */
    Optional<Parting> parting() {
        final Spot apart = placed.at().minus(stands.at());
        Optional<Parting> least = Optional.empty();
        for (final Spot way : ways()) {
            final double between = apart.dot(way);
            final double deep = reach(box, placed, way) + reach(other, stands, way) - Math.abs(between);
            if (deep <= 0) {
                return Optional.empty();
            }
            if (least.isEmpty() || deep < least.get().deep()) {
                least = Optional.of(new Parting(between < 0 ? way.times(-1) : way, deep));
            }
        }
        return least;
    }

    /** Where the first box meets the second, pushed as {@code pushes} says: the middle of its corners furthest in. */
    Spot meets(final Spot pushes) {
        final List<Spot> corners = placed.corners(box);
        final double furthest = corners.stream().mapToDouble(corner -> corner.dot(pushes)).min().orElseThrow();
        Spot sum = new Spot(0, 0, 0);
        int count = 0;
        for (final Spot corner : corners) {
            if (corner.dot(pushes) - furthest < 0.5) {
                sum = sum.plus(corner);
                count++;
            }
        }
        return sum.times(1.0 / count);
    }

    /** The ways two boxes may be told apart along: three faces of each, and each edge of one crossed with each. */
    private List<Spot> ways() {
        final List<Spot> ways = new ArrayList<>();
        for (int axis = 0; axis < 3; axis++) {
            final Spot unit = new Spot(axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0);
            ways.add(placed.turned().turned(unit));
            ways.add(stands.turned().turned(unit));
        }
        for (int mine = 0; mine < 6; mine += 2) {
            for (int theirs = 1; theirs < 6; theirs += 2) {
                final Spot square = ways.get(mine).cross(ways.get(theirs));
                if (square.length() > 1e-9) {
                    ways.add(square.unit());
                }
            }
        }
        return ways;
    }

    /** How far a box placed so reaches from its middle along {@code way}. */
    private double reach(final Box of, final Placed where, final Spot way) {
        final Spot lying = where.lying(way);
        final Spot half = of.half();
        return Math.abs(lying.across()) * half.across() + Math.abs(lying.up()) * half.up()
                + Math.abs(lying.along()) * half.along();
    }
}
