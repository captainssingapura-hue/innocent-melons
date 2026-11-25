package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Reckoned;
import melons.innocent.core.rest.Reckoning;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Touch;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The loose body: the simulator of a thing nothing drives. Each tick it makes the check's own reckoning of the
 * thing's forces ({@link Reckoning}), takes the change of velocity or of turning that comes of it, and moves the
 * thing by what it then has, the new velocity first. So it does one of three things in a tick:
 *
 * <ul>
 * <li><b>It falls</b>, touched by nothing: its velocity gains gravity's pull, it goes on turning as it was, and
 *     it moves by both.</li>
 * <li><b>It tips</b>, touched but not balanced: its turning gains what its weight makes about the line it is held
 *     along, and it is carried round that line, which does not slip.</li>
 * <li><b>It is held</b>, balanced on what it touches: it does not move, and the tick is counted toward its rest.</li>
 * </ul>
 *
 * <p>A landing stops: where falling or tipping would carry it into what could hold it, it is set down just touching
 * and loses all its motion, to be reckoned afresh from there at the next tick. Nothing bounces and nothing slides,
 * and it meets only what is at rest or has itself landed: two loose bodies meeting in the air pass through each
 * other for now.
 *
 * @param reckoning the check it steps by
 * @param slack     how far into something it may be and count as only touching it, in centimetres
 */
public record Loose(Reckoning reckoning, double slack) implements Simulator {

    /** How many times a tipping's last tick is halved to find where it lands. */
    private static final int HALVINGS = 24;

    /** A loose body on the Earth, touching within a hundredth of a centimetre. */
    public Loose() {
        this(new Reckoning(), 0.01);
    }

    @Override
    public Stepped stepped(final Rigid rigid, final Mover now, final Around around, final double seconds) {
        final List<Touch> touches = around.touches(rigid, now.placed());
        final Reckoned reckoned = reckoning.of(rigid, now.placed(), touches, seconds);
        if (reckoned.held()) {
            // held: stopped where it is, and said to have landed if it was not already down
            return new Stepped(new Mover(now.placed(), new Moved(), true, now.still() + 1),
                    now.landed() ? Optional.empty() : Optional.of(touches.getFirst().on()));
        }
        return reckoned.hinge().map(hinge -> tipped(rigid, now, around, seconds, reckoned, hinge))
                .orElseGet(() -> fallen(rigid, now, around, seconds, reckoned));
    }

    /** Falling: its velocity changed, moved by it and turned as it was turning, and set down where it lands. */
    private Stepped fallen(final Rigid rigid, final Mover now, final Around around, final double seconds,
                           final Reckoned reckoned) {
        final Spot velocity = now.moved().velocity().plus(reckoned.velocity());
        final Spot spin = now.moved().spin();
        final Placed fell = new Placed(now.placed().at().plus(velocity.times(seconds)), spin.length() == 0
                ? now.placed().turned() : now.placed().turned().then(new Turn(spin, spin.length() * seconds)));
        final Optional<Touch> met = deepest(around.touches(rigid, fell));
        if (met.isEmpty()) {
            return new Stepped(new Mover(fell, new Moved(velocity, spin), false, 0), Optional.empty());
        }
        // lifted straight up until its deepest corner only touches
        final double lift = around.touches(rigid, fell).stream().filter(touch -> touch.deep() > slack)
                .mapToDouble(touch -> touch.deep() / touch.pushes().up()).max().orElse(0);
        return new Stepped(new Mover(fell.moved(new Spot(0, lift, 0)), new Moved(), true, 0),
                met.map(Touch::on));
    }

    /** Tipping: its turning about its hinge changed, carried round by it, and stopped where it lands. */
    private Stepped tipped(final Rigid rigid, final Mover now, final Around around, final double seconds,
                           final Reckoned reckoned, final Reckoned.Hinge hinge) {
        final double turning = now.moved().spin().dot(hinge.along()) + reckoned.spin().dot(hinge.along());
        final double angle = turning * seconds;
        final Placed round = carried(now.placed(), hinge, angle);
        final Optional<Touch> met = deepest(around.touches(rigid, round));
        if (met.isEmpty()) {
            final Spot spin = hinge.along().times(turning);
            return new Stepped(new Mover(round, new Moved(spin.cross(round.at().minus(hinge.through())), spin),
                    false, 0), Optional.empty());
        }
        double clear = 0;
        double into = angle;
        for (int halving = 0; halving < HALVINGS; halving++) {
            final double middle = (clear + into) / 2;
            if (deepest(around.touches(rigid, carried(now.placed(), hinge, middle))).isEmpty()) {
                clear = middle;
            } else {
                into = middle;
            }
        }
        return new Stepped(new Mover(carried(now.placed(), hinge, clear), new Moved(), true, 0),
                met.map(Touch::on));
    }

    /** {@code placed} carried round {@code hinge} by so many radians. */
    private Placed carried(final Placed placed, final Reckoned.Hinge hinge, final double angle) {
        return angle == 0 ? placed : placed.about(hinge.through(), new Turn(hinge.along(), angle));
    }

    /** The touch that is furthest into what it touches, of those that are in at all. */
    private Optional<Touch> deepest(final List<Touch> touches) {
        return touches.stream().filter(touch -> touch.deep() > slack).max(Comparator.comparingDouble(Touch::deep));
    }
}
