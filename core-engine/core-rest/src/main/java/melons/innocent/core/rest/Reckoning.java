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
 * <p>A thing that rolls is held a little further out than one that does not: what it rolls on gives a little
 * under it, so on level ground and on the gentlest slopes it stays where it is put.
 *
 * @param gravity how fast a falling thing gains speed, in centimetres a second each second
 * @param slack   how far outside the ground it is held on a thing's middle may be and still be held, in centimetres
 * @param rolling how much further a thing that rolls is held, as a share of how far it reaches from its middle; and
 *                how much of gravity's pull is taken from its speed as it rolls
 */
public record Reckoning(double gravity, double slack, double rolling) {

    /**
     * As things fall on the Earth, 981 centimetres a second each second, held within a hundredth of a centimetre,
     * and what rolls held and slowed by a twentieth.
     */
    public Reckoning() {
        this(981, 0.01, 0.05);
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
        final double give = rigid.rolls() ? Math.max(slack, rolling * rigid.shape().reaches()) : slack;
        if (held.holds(placed.at(), give)) {
            return new Reckoned();
        }
        final Hull.Edge edge = held.nearest(placed.at());
        final Spot arm = placed.at().minus(edge.from());
        // what its weight does to turn it about the spot it is held at
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
