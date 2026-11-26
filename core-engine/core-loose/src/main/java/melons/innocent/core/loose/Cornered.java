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
 * How a thing with corners goes among things that stand still, for a tick or a part of one: the engine under the
 * loose body for what does not roll. It makes the check's own reckoning of the thing's forces and does one of three
 * things. It flies, touched by nothing or thrown up off what it stood on: its velocity gains gravity's pull, it goes
 * on turning as it was, and it moves by both ({@link Flight}). It tips, touched but not balanced: its turning gains
 * what its weight makes about the line it is held along, and it is carried round that line, which does not slip. Or
 * it is held, balanced on what it touches: it does not move, and the tick is counted toward its rest.
 *
 * <p>A landing stops: where falling or tipping would carry it into what could hold it, it is set down just touching
 * and loses all its motion, to be reckoned afresh from there at the next tick. Nothing bounces and nothing slides.
 *
 * @param reckoning the check it steps by
 * @param slack     how far into something it may be and count as only touching it, in centimetres
 * @param whys      its explanations, as the run's switch has them
 */
record Cornered(Reckoning reckoning, double slack, Whys whys) {

    /** How many times a tipping's last tick is halved to find where it lands. */
    private static final int HALVINGS = 24;

    /** {@code rigid}, standing and moving as {@code now} says, so many {@code seconds} on: flown, tipped or held. */
    Out stepped(final Rigid rigid, final Simulator.Mover now, final Simulator.Around around, final double seconds) {
        final List<Touch> touches = around.touches(rigid, now.placed());
        // thrown up off what it stood on, it is in flight though it still touches it
        if (!touches.isEmpty() && touches.stream().allMatch(touch ->
                now.moved().velocity().dot(touch.pushes()) > 0)) {
            return fallen(rigid, now, around, seconds);
        }
        final Reckoned reckoned = reckoning.of(rigid, now.placed(), touches, seconds);
        if (reckoned.held()) {
            // held: stopped where it is, and said to have landed if it was not already down
            return new Out(new Simulator.Mover(now.placed(), new Moved(), true, now.still() + 1),
                    now.landed() ? Optional.empty() : Optional.of(touches.getFirst().on()),
                    whys.of(() -> new Why.Held(touches.stream().map(Touch::on).distinct().toList(),
                            now.still() + 1)));
        }
        return reckoned.hinge().map(hinge -> tipped(rigid, now, around, seconds, reckoned, hinge))
                .orElseGet(() -> fallen(rigid, now, around, seconds));
    }

    /** In flight ({@link Flight}): gravity's pull taken, moved by what it then has, and set down where it lands. */
    private Out fallen(final Rigid rigid, final Simulator.Mover now, final Simulator.Around around,
                       final double seconds) {
        final Flight flight = new Flight(reckoning.gravity());
        final Moved flying = flight.pulled(now.moved(), seconds);
        final Placed fell = flight.carried(now.placed(), now.moved(), flying, seconds);
        final Optional<Touch> met = deepest(around.touches(rigid, fell));
        if (met.isEmpty()) {
            return new Out(new Simulator.Mover(fell, flying, false, 0), Optional.empty(),
                    whys.of(() -> new Why.Flew(flying.velocity().minus(now.moved().velocity()))));
        }
        // lifted straight up until its deepest corner only touches
        final double lift = around.touches(rigid, fell).stream().filter(touch -> touch.deep() > slack)
                .mapToDouble(touch -> touch.deep() / touch.pushes().up()).max().orElse(0);
        // stopped against what it has come down on, which may itself be going
        return new Out(new Simulator.Mover(fell.moved(new Spot(0, lift, 0)),
                new Moved(met.get().going(), new Spot(0, 0, 0)), true, 0), met.map(Touch::on),
                whys.of(() -> new Why.CameDown(met.get().on(), flying.velocity().minus(met.get().going()))));
    }

    /** Tipping: its turning about its hinge changed, carried round by it, and stopped where it lands. */
    private Out tipped(final Rigid rigid, final Simulator.Mover now, final Simulator.Around around,
                       final double seconds, final Reckoned reckoned, final Reckoned.Hinge hinge) {
        final double turning = now.moved().spin().dot(hinge.along()) + reckoned.spin().dot(hinge.along());
        final double angle = turning * seconds;
        final Placed round = carried(now.placed(), hinge, angle);
        final Optional<Touch> met = deepest(around.touches(rigid, round));
        if (met.isEmpty()) {
            final Spot spin = hinge.along().times(turning);
            return new Out(new Simulator.Mover(round,
                    new Moved(spin.cross(round.at().minus(hinge.through())), spin), false, 0), Optional.empty(),
                    whys.of(() -> new Why.Tipped(reckoned, angle, Optional.empty())));
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
        final double turned = clear;
        return new Out(new Simulator.Mover(carried(now.placed(), hinge, clear), new Moved(), true, 0),
                met.map(Touch::on), whys.of(() -> new Why.Tipped(reckoned, turned, met.map(Touch::on))));
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
