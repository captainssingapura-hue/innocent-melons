package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Outsider;
import melons.innocent.core.rest.Placed;

import java.util.Map;
import java.util.Optional;

/**
 * Whoever walks the trial, as the core's world is told of them each tick: the game engine's avatar, which the world
 * does not step, standing in it as a box as wide and as tall as the engine takes its body to be. Where it stands is
 * told at each step of the walk; how it is going is worked out from where it stood the step before. So what rolls
 * or flies in the trial finds the walker in its path.
 */
final class Walker {

    /** What the walker is called among what a thing may meet. */
    static final String NAME = "the walker";
    /** How long a tick is, in seconds. */
    private static final double TICK = 1.0 / 120;
    /** How wide and how tall the game engine takes its avatar's body to be, in centimetres. */
    private static final double WIDE = 64;
    private static final double TALL = 200;
    /** How slowly the walker may be going and be told as standing, in centimetres a second. */
    private static final double STANDING = 1;

    private Optional<Spot> feet = Optional.empty();
    private Optional<Spot> before = Optional.empty();
    private double facing;

    /** The walker now stands with its feet at {@code at}, facing {@code yaw} degrees round. */
    void stood(final Spot at, final double yaw) {
        before = feet;
        feet = Optional.of(at);
        facing = yaw;
    }

    /** The walker as the world is to be told of it this tick: nobody, before the walk's first step. */
    Map<String, Outsider> outsiders() {
        return feet.map(at -> Map.of(NAME, new Outsider(new Box(WIDE, TALL, WIDE),
                new Placed(at.plus(new Spot(0, TALL / 2, 0)), new Turn(new Spot(0, 1, 0), Math.toRadians(facing))),
                going(at))))
                .orElse(Map.of());
    }

    /**
     * How the walker is going, standing at {@code at} now: by where it stood the step before. Slower than a
     * centimetre a second it is told as standing: the game engine's avatar, come to a stand, still creeps for a long
     * while by a thousandth of that, and a thing stopped against it would be nudged on and never come to rest.
     */
    private Spot going(final Spot at) {
        final Spot going = before.map(was -> at.minus(was).times(1 / TICK)).orElse(new Spot(0, 0, 0));
        return going.length() < STANDING ? new Spot(0, 0, 0) : going;
    }
}
