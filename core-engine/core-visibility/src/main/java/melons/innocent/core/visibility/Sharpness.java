package melons.innocent.core.visibility;

import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;

/**
 * How sharply an observer could see a thing as it now moves: a share, from all of the detail an eye could make out
 * of it were everything still, 1, down toward none as the thing streaks across the view. It changes smoothly with
 * how the observer moves and where the thing is, and never jumps.
 *
 * <p>It is the eye's finest angle against the finest angle it can tell apart of this thing as things are: the
 * eye's own, and the smear the thing's sweep across the view lays over it while the eye gathers. So a thing far
 * ahead of a fast observer is as sharp as if the observer stood still, and one close beside it is a blur. The
 * sweep is the least any part of the thing has, so nothing of it is taken to be less sharp than it is; a thing the
 * observer is inside of is wholly sharp.
 *
 * <p>Only the observer's going counts, not its turning: an eye follows a view that swings and sees it sharp, and a
 * forest gone coarse at every turn of the head would be seen. It says nothing of pictures: what draws takes the
 * finest angle told apart ({@link #told}), with the angle one of its own pixels covers, to the smallest feature
 * worth drawing. Nor of what stands between the two, nor yet of the light.
 *
 * @param eye what the observer's eye can do
 */
public record Sharpness(Eye eye) {

    /** A person's eye. */
    public Sharpness() {
        this(new Eye());
    }

    /**
     * How sharply {@code observer} sees everything taken together, for how fast it is going: all of it standing
     * still, half of it at the speed its eye is halved at, and less and less beyond, smoothly and never by a jump.
     * One number for the whole view, where the other is for a thing: the plain way to draw everything a little
     * less finely the faster the observer goes, whatever is where.
     */
    public double overall(final Motion observer) {
        return 1 / (1 + observer.speed() / eye.halved());
    }

    /** How sharply {@code observer} could see {@code thing}: from next to nothing up to 1. */
    public double of(final Motion observer, final Seen thing) {
        return eye.finest() / told(observer, thing);
    }

    /** The finest angle {@code observer} can tell apart of {@code thing}, in degrees: the eye's own, and the smear. */
    public double told(final Motion observer, final Seen thing) {
        return eye.finest() + sweeps(observer, thing) * eye.gathers();
    }

    /**
     * How fast {@code thing} sweeps across {@code observer}'s view by the observer's going, in degrees a second:
     * the least any part of it does, which is nothing where the observer is within it or headed straight at it.
     */
    public double sweeps(final Motion observer, final Seen thing) {
        final Spot to = thing.middle().minus(observer.at());
        final double far = to.length() + thing.reach();
        if (to.length() <= thing.reach() || far <= 0) {
            return 0;
        }
        // across the way to its middle the observer goes so fast; no part of it is further across than its reach
        // takes off that, nor nearer than its far side
        final double square = Math.max(0, observer.going().cross(to).length() - observer.speed() * thing.reach());
        return Math.toDegrees(square / (far * far));
    }
}
