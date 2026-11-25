package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * Where a round thing touches what is flat. It has no corner to lie on a face: it touches a flat thing at the one
 * spot of it that reaches furthest toward that thing, so on the ground at its lowest spot, and on a face of a box at
 * the spot nearest that face, where that spot is within the face. Every face it meets is said, whichever way that
 * face pushes it: the ground or a slope under it, and a crate or a chock it has come up against beside it.
 *
 * @param slack how far apart two may be and still touch, in centimetres
 * @param skin  how far within a face it may be and still be on it
 */
public record RoundTouch(double slack, double skin) {

    /** Where {@code round} placed so touches the {@code ground}: its lowest spot, on it or under it. */
    public List<Touch> grounded(final Ellipsoid round, final Placed placed, final Ground ground) {
        final Spot lowest = furthest(round, placed, new Spot(0, -1, 0));
        final double deep = ground.level() - lowest.up();
        return deep >= -slack ? List.of(new Touch(lowest, new Spot(0, 1, 0), deep, ground.name())) : List.of();
    }

    /** Where {@code round} placed so meets a {@code box}, called {@code on}, standing as said: a spot to a face. */
    public List<Touch> met(final Ellipsoid round, final Placed placed, final String on, final Box box,
                           final Placed stands) {
        final List<Touch> touches = new ArrayList<>();
        final Spot half = box.half();
        final double[] halves = {half.across(), half.up(), half.along()};
        for (int face = 0; face < 6; face++) {
            final int axis = face / 2;
            final double side = face % 2 == 0 ? 1 : -1;
            final Spot out = stands.turned().turned(new Spot(axis == 0 ? side : 0, axis == 1 ? side : 0,
                    axis == 2 ? side : 0));
            final Spot middle = stands.at().plus(out.times(halves[axis]));
            final Spot spot = furthest(round, placed, out.times(-1));
            final double deep = -spot.minus(middle).dot(out);
            // on this face: its own middle outside it, its nearest spot at it, and that spot within the face
            if (placed.at().minus(middle).dot(out) > 0 && deep >= -slack && deep <= skin
                    && within(stands.within(spot), halves, axis)) {
                touches.add(new Touch(spot, out, deep, on));
            }
        }
        return touches;
    }

    /** The spot of {@code round}, placed so, that reaches furthest the {@code way} said, in the world. */
    private Spot furthest(final Ellipsoid round, final Placed placed, final Spot way) {
        return placed.at().plus(placed.turned().turned(round.furthest(placed.lying(way))));
    }

    /** Whether a spot, said in a box's own frame, is within the face across the way numbered {@code axis}. */
    private boolean within(final Spot in, final double[] halves, final int axis) {
        final double[] its = {in.across(), in.up(), in.along()};
        for (int other = 0; other < 3; other++) {
            if (other != axis && Math.abs(its[other]) > halves[other] + slack) {
                return false;
            }
        }
        return true;
    }
}
