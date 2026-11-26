package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;

import java.util.Optional;

/**
 * How a walker stands and goes at a tick: its state, a value, which the core keeps and only an engine's answer
 * replaces.
 *
 * @param feet   where its feet are, in the world
 * @param yaw    which way it faces, in degrees as a compass has them: nothing ahead, turning toward the right
 * @param going  how it is going over the ground, in centimetres a second
 * @param on     what it stands on; nothing, where it is in the air
 * @param stride how far it has gone on its feet, against whatever it stood on: what a gait is drawn from
 */
public record Standing(Spot feet, double yaw, Spot going, Optional<String> on, double stride) {

    /** A walker set down at {@code feet} facing {@code yaw}, not going anywhere, not yet standing on anything. */
    public Standing(final Spot feet, final double yaw) {
        this(feet, yaw, new Spot(0, 0, 0), Optional.empty(), 0);
    }

    /** Whether it is on its feet. */
    public boolean afoot() {
        return on.isPresent();
    }
}
