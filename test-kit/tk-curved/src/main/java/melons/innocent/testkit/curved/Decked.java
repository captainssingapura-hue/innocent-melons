package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.sim.Pavement;
import com.example.fxsuite.avatar.sim.Volume;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.sheet.Lift;

import java.util.ArrayList;
import java.util.List;

/**
 * A shell, or a band of its height, as the game engine may stand its avatar on it: the engine's own pavement is
 * told its top and its underside at any spot, and these are the sheet's own, so the avatar goes over the curve
 * itself and not over steps. A pavement has one top over a spot, so a shell that passes over itself is cut into
 * bands of height ({@link #banded}), each thinner than the least it climbs between two passes: within a band it is
 * over any spot once, and where one band ends the next begins at the same height. The shell stands upright, turned
 * about the upright only.
 *
 * @param shell  the shell
 * @param placed where it stands in the world
 * @param from   how high the band begins, in the shell's own frame
 * @param to     how high it ends, which is not part of it
 * @param bounds the box round the whole shell, seen from above
 * @param low    how low any of the shell is, in the world
 * @param high   how high any of it is
 */
record Decked(Shell shell, Placed placed, double from, double to, Volume.Bounds bounds, double low, double high)
        implements Pavement.Laid {

    /** How many places each way the shell is looked at to find its box and how it passes over itself. */
    private static final int LOOKS = 64;

    /** The whole of {@code shell} standing as {@code placed} says. */
    Decked(final Shell shell, final Placed placed) {
        this(shell, placed, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /** The band of {@code shell} from so high to so high, standing as {@code placed} says. */
    Decked(final Shell shell, final Placed placed, final double from, final double to) {
        this(shell, placed, from, to, new Decked(shell, placed, from, to, new Volume.Bounds(0, 0, 0, 0), 0, 0)
                .boxed());
    }

    private Decked(final Shell shell, final Placed placed, final double from, final double to, final double[] box) {
        this(shell, placed, from, to, new Volume.Bounds(box[0], box[1], box[2], box[3]), box[4], box[5]);
    }

    /** Its leftmost, rightmost, nearest and furthest in the world, its lowest and its highest. */
    private double[] boxed() {
        final double[] box = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (int far = 0; far <= LOOKS; far++) {
            for (int over = 0; over <= LOOKS; over++) {
                final Spot at = placed.at().plus(placed.turned().turned(
                        shell.sheet().at((double) far / LOOKS, (double) over / LOOKS)));
                box[0] = Math.min(box[0], at.across());
                box[1] = Math.max(box[1], at.across());
                box[2] = Math.min(box[2], at.along());
                box[3] = Math.max(box[3], at.along());
                box[4] = Math.min(box[4], at.up() - shell.thick());
                box[5] = Math.max(box[5], at.up());
            }
        }
        return box;
    }

    /**
     * The whole shell as pavements the engine can stand on: itself, where it never passes over itself; else its
     * bands of height, each half as thick as the least it climbs between two passes over a spot.
     */
    List<Decked> banded() {
        double least = Double.POSITIVE_INFINITY;
        for (int far = 0; far <= LOOKS; far++) {
            for (int over = 0; over <= LOOKS; over += LOOKS / 4) {
                final Spot at = shell.sheet().at((double) far / LOOKS, (double) over / LOOKS);
                final List<Lift> lifts = shell.sheet().over(at.across(), at.along());
                for (int pass = 1; pass < lifts.size(); pass++) {
                    least = Math.min(least, lifts.get(pass).high() - lifts.get(pass - 1).high());
                }
            }
        }
        if (Double.isInfinite(least)) {
            return List.of(this);
        }
        final List<Decked> bands = new ArrayList<>();
        final double lowest = low - placed.at().up() + shell.thick();
        for (double band = lowest; band <= high - placed.at().up(); band += least / 2) {
            bands.add(new Decked(shell, placed, band, band + least / 2, bounds, low, high));
        }
        return bands;
    }

    @Override
    public double top(final double x, final double z) {
        final Spot own = placed.within(new Spot(x, placed.at().up(), z));
        for (final Lift lift : shell.sheet().over(own.across(), own.along())) {
            if (lift.high() >= from && lift.high() < to) {
                return placed.at().up() + lift.high();
            }
        }
        return Double.NaN;
    }

    @Override
    public double underside(final double x, final double z) {
        return top(x, z) - shell.thick();
    }
}
