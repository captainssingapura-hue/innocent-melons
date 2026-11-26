package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;

import java.util.ArrayList;
import java.util.List;

/**
 * A melon cut: four slices in a row along its length as it lies, each a slab of its flesh where its part of the
 * melon was. A melon lying as it grew, its top still upward, is cut in its own frame, so its slices stand square on
 * whatever it lay on, a slope too. One that has rolled over is cut upright, its slices a little over what it lay on,
 * and they come down on that. They are a little smaller than the melon they are cut from, so they stand clear of
 * each other and of what was round it. A slice is a box for now, since a wedge is not yet a shape the engine
 * reckons with.
 */
record Slices() {

    /** How many slices a melon is cut into. */
    private static final int PIECES = 4;

    /** One slice: the thing, and where it stands. */
    record Slice(Rigid rigid, Placed placed) {
    }

    /** The slices of {@code whole}, lying as {@code placed} says; none, of a thing that is not round. */
    List<Slice> of(final Rigid whole, final Placed placed) {
        if (!(whole.shape() instanceof Ellipsoid round)) {
            return List.of();
        }
        final boolean lengthways = round.along() > round.across();
        final double length = Math.max(round.across(), round.along());
        final double girth = Math.min(round.up(), Math.min(round.across(), round.along()));
        final Box box = new Box(length / PIECES - 1, 0.8 * girth, 0.8 * girth);
        final Placed first = placed.turned().turned(new Spot(0, 1, 0)).up() > 0.8
                ? asItGrew(round, placed, lengthways, box) : upright(round, placed, lengthways, box);
        // the way the row runs: the way a slice's thickness lies
        final Spot row = first.turned().turned(new Spot(1, 0, 0));
        final List<Slice> slices = new ArrayList<>();
        for (int piece = 0; piece < PIECES; piece++) {
            slices.add(new Slice(new Rigid("slice " + (piece + 1) + " of " + whole.name(), box, Stuff.FLESH.dense()),
                    first.moved(row.times((piece - (PIECES - 1) / 2.0) * length / PIECES))));
        }
        return slices;
    }

    /** Where the middle of the row is, turned as the melon is: half a centimetre over what its underside lay on. */
    private Placed asItGrew(final Ellipsoid round, final Placed placed, final boolean lengthways, final Box box) {
        final Turn turned = lengthways ? new Turn(new Spot(0, 1, 0), -Math.PI / 2).then(placed.turned())
                : placed.turned();
        return new Placed(placed.at().plus(placed.turned().turned(
                new Spot(0, box.up() / 2 + 0.5 - round.up() / 2, 0))), turned);
    }

    /** Where the middle of the row is, upright, the row along the melon's length as that lies over the ground. */
    private Placed upright(final Ellipsoid round, final Placed placed, final boolean lengthways, final Box box) {
        final Spot lies = placed.turned().turned(lengthways ? new Spot(0, 0, 1) : new Spot(1, 0, 0));
        final Spot level = new Spot(lies.across(), 0, lies.along());
        final Spot row = level.length() < 0.2 ? new Spot(1, 0, 0) : level.unit();
        // its lowest spot, as it is turned; and the slices a little over that, since what it lay on may slope
        final double lowest = placed.at().up() + placed.turned().turned(
                round.furthest(placed.lying(new Spot(0, -1, 0)))).up();
        return new Placed(new Spot(placed.at().across(), lowest + 0.3 * box.up() + box.up() / 2,
                placed.at().along()), new Turn(new Spot(0, 1, 0), Math.atan2(-row.along(), row.across())));
    }
}
