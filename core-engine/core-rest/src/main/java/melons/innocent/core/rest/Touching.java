package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Where a thing is touched from below by what could hold it up: the ground, or another thing. Two boxes touch where
 * a corner of one lies on or just within a face of the other; edges crossing edges are not looked for. A touch holds
 * only where the thing is pushed upward by it, at least a little: a corner of the thing on a face of the other that
 * looks up, or a corner of the other in a face of the thing that looks down.
 *
 * @param slack how far apart two may be and still touch, in centimetres
 * @param skin  how far within a face a corner may be and still be on it: deeper, it is in the thing and not on it
 * @param steep how much of a push must be upward for it to hold, of one
 */
public record Touching(double slack, double skin, double steep) {

    /** A face a corner lies on: which way it pushes whatever is on it, and how far in the corner is. */
    private record Face(Spot pushes, double deep) {
    }

    /** Touching within a hundredth of a centimetre, a skin of twenty, and a push at least half upward. */
    public Touching() {
        this(0.01, 20, 0.5);
    }

    /** Where a thing of {@code shape} placed so touches the {@code ground}. */
    public List<Touch> grounded(final ShapeImpl<?> shape, final Placed placed, final Ground ground) {
        return switch (shape) {
            case Ellipsoid round -> new RoundTouch(slack, skin).grounded(round, placed, ground);
            case Box box -> cornered(box, placed, ground);
            case Slab slab -> cornered(slab.box(), placed, ground);
            case Shell shell -> List.of();
        };
    }

    /**
     * Where a thing of {@code shape} placed so is touched by {@code other}, called {@code on}, standing as said:
     * from below, for a thing with corners; wherever it meets it, for a round one. Nothing rests on a round thing
     * yet, so none touches it; and nothing meets a shell yet, which only whoever asks what is under a spot finds.
     */
    public List<Touch> held(final ShapeImpl<?> shape, final Placed placed, final String on,
                            final ShapeImpl<?> other, final Placed stands) {
        if (other instanceof Ellipsoid || other instanceof Shell || shape instanceof Shell
                || placed.at().minus(stands.at()).length() > shape.reaches() + other.reaches() + slack) {
            return List.of();
        }
        return switch (shape) {
            case Ellipsoid round -> new RoundTouch(slack, skin).met(round, placed, on, boxed(other), stands);
            case Box box -> cornered(box, placed, on, boxed(other), stands);
            case Slab slab -> cornered(slab.box(), placed, on, boxed(other), stands);
            case Shell shell -> List.of();
        };
    }

    /** Whether two things are run well into each other where they stand; never said of a round one. */
    public boolean overlapping(final ShapeImpl<?> shape, final Placed placed, final ShapeImpl<?> other,
                               final Placed stands) {
        return !(shape instanceof Ellipsoid) && !(other instanceof Ellipsoid)
                && !(shape instanceof Shell) && !(other instanceof Shell)
                && overlapping(boxed(shape), placed, boxed(other), stands);
    }

    /** The box a shape with corners meets other things as. */
    private Box boxed(final ShapeImpl<?> shape) {
        return switch (shape) {
            case Box box -> box;
            case Slab slab -> slab.box();
            case Ellipsoid round -> throw new IllegalArgumentException("a round thing has no corners");
            case Shell shell -> throw new IllegalArgumentException("a shell has no corners");
        };
    }

    /** Where a {@code box} placed so touches the {@code ground}: each corner on it or under it. */
    private List<Touch> cornered(final Box box, final Placed placed, final Ground ground) {
        final List<Touch> touches = new ArrayList<>();
        for (final Spot corner : placed.corners(box)) {
            final double deep = ground.level() - corner.up();
            if (deep >= -slack) {
                touches.add(new Touch(corner, new Spot(0, 1, 0), deep, ground.name()));
            }
        }
        return touches;
    }

    /** Where a {@code box} placed so is touched from below by {@code other}, called {@code on}, standing as said. */
    private List<Touch> cornered(final Box box, final Placed placed, final String on, final Box other,
                                 final Placed stands) {
        final List<Touch> touches = new ArrayList<>();
        for (final Spot corner : placed.corners(box)) {
            faced(corner, other, stands, 1).ifPresent(face ->
                    touches.add(new Touch(corner, face.pushes(), face.deep(), on)));
        }
        for (final Spot corner : stands.corners(other)) {
            faced(corner, box, placed, -1).ifPresent(face ->
                    touches.add(new Touch(corner, face.pushes(), face.deep(), on)));
        }
        return touches;
    }

    /**
     * Whether two boxes are run well into each other: more than a centimetre, whichever way they are looked along.
     * Two that do not overlap can be told apart along a face of one, a face of the other, or a line square to an
     * edge of each.
     */
    private boolean overlapping(final Box box, final Placed placed, final Box other, final Placed stands) {
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
        final Spot apart = stands.at().minus(placed.at());
        return ways.stream().allMatch(way ->
                reach(box, placed, way) + reach(other, stands, way) - Math.abs(apart.dot(way)) > 1);
    }

    /** How far a box placed so reaches from its middle along {@code way}. */
    private double reach(final Box box, final Placed placed, final Spot way) {
        final Spot lying = placed.lying(way);
        final Spot half = box.half();
        return Math.abs(lying.across()) * half.across() + Math.abs(lying.up()) * half.up()
                + Math.abs(lying.along()) * half.along();
    }

    /**
     * The face of a {@code box} that {@code corner} lies on, where it lies on one that pushes upward: of the faces
     * on the corner's side of the box, the one whose way out, {@code turned} about (1 as it is, -1 the other way),
     * is most upward. None, where the corner is clear of the box, too deep in it, or on no such face.
     */
    private Optional<Face> faced(final Spot corner, final Box box, final Placed placed, final double turned) {
        final Spot in = placed.within(corner);
        final double[] within = {in.across(), in.up(), in.along()};
        final Spot half = box.half();
        final double[] halves = {half.across(), half.up(), half.along()};
        Optional<Face> best = Optional.empty();
        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(within[axis]) > halves[axis] + slack) {
                return Optional.empty();
            }
            final double side = within[axis] < 0 ? -turned : turned;
            final Spot out = placed.turned().turned(new Spot(axis == 0 ? side : 0, axis == 1 ? side : 0,
                    axis == 2 ? side : 0));
            final double deep = halves[axis] - Math.abs(within[axis]);
            if (out.up() > steep && deep <= skin && (best.isEmpty() || out.up() > best.get().pushes().up())) {
                best = Optional.of(new Face(out, deep));
            }
        }
        return best;
    }
}
