package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.List;
import java.util.Optional;

/**
 * The check: a thing's weight, pulling down at its middle of mass, against what holds it up where it is touched from
 * below. It comes to one of three ends. Its middle is over the ground it is held on, and it is at rest. Nothing
 * touches it, and it falls: its velocity changes by gravity's pull for the tick. Or it is touched, but its middle is
 * outside the ground it is held on, and it tips about the nearest stretch of that ground's edge: its turning changes
 * by the turning its weight makes about that line, over how hard it is to turn about it.
 *
 * <p>What holds does not slip and does not give: friction and whether a support can bear its load are not reckoned
 * yet. Nor is what stands on a thing: only its own weight is weighed.
 *
 * @param gravity how fast a falling thing gains speed, in centimetres a second each second
 * @param slack   how far outside the ground it is held on a thing's middle may be and still be held, in centimetres
 */
public record Reckoning(double gravity, double slack) {

    /** As things fall on the Earth, 981 centimetres a second each second, held within a hundredth of a centimetre. */
    public Reckoning() {
        this(981, 0.01);
    }

    /**
     * What becomes of {@code rigid}, placed so and touched from below as {@code touches} say, over a tick of so
     * many {@code seconds}.
     */
    public Reckoned of(final Rigid rigid, final Placed placed, final List<Touch> touches, final double seconds) {
        if (touches.isEmpty()) {
            return new Reckoned(new Spot(0, -gravity * seconds, 0), new Spot(0, 0, 0), Optional.empty());
        }
        final Hull held = new Hull(touches.stream().map(Touch::at).toList());
        if (held.holds(placed.at(), slack)) {
            return new Reckoned();
        }
        final Hull.Edge edge = held.nearest(placed.at());
        final Spot arm = placed.at().minus(edge.from());
        // what its weight does to turn it about the spot it is held at: kilograms, centimetres squared, a second squared
        final Spot turning = arm.cross(new Spot(0, -1, 0)).times(rigid.kilograms() * gravity);
        if (turning.length() < 1e-9) {
            return new Reckoned();
        }
        final Spot along = edge.corner() ? turning.unit() : edge.to().minus(edge.from()).unit();
        final double about = turning.dot(along);
        if (Math.abs(about) < 1e-9) {
            return new Reckoned();
        }
        final double off = arm.minus(along.times(arm.dot(along))).length();
        final double hard = rigid.turnsHard(placed.lying(along)) + rigid.kilograms() * off * off;
        return new Reckoned(new Spot(0, 0, 0), along.times(about / hard * seconds),
                Optional.of(new Reckoned.Hinge(edge.from(), along)));
    }
}
