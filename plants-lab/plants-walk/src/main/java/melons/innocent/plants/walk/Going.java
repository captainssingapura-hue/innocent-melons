package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.visibility.Sharpness;
import melons.innocent.plants.tree.Stage;

/**
 * A forest looked at by an eye that is going somewhere.
 *
 * <p>It looks ahead a while, so many seconds of going on as it goes now, and a square is drawn at the level it will
 * need at the nearest the eye comes to it in that while: what the eye is making for has its detail before the eye
 * is there, added while it is still too small to be seen coming, and what it is leaving behind, or will pass far
 * off, is at what its distance needs and no more.
 *
 * <p>How sharply the eye sees is the core engine's to say, a number that falls smoothly with its speed
 * ({@link Sharpness#overall}). Here it comes to one of two things, and nothing between: every level there is, or,
 * once the engine's number is below what it is at a speed the walk sets, every level but the finest, so that what
 * would have had every twig has its twigs gone and all else is as it was. Never two below what a thing needs.
 *
 * @param observer how the eye is moving
 * @param window   how many seconds ahead it looks
 * @param fast     whether it is going too fast to make twigs out, so nothing is drawn at its finest
 */
public record Going(Motion observer, double window, boolean fast) implements Looking {

    /** A kilometre an hour, in centimetres a second. */
    private static final double HOURLY = 100_000.0 / 3_600;

    public Going {
        if (!(window >= 0)) {
            throw new IllegalArgumentException("looking some while ahead, or none: " + window + " s");
        }
    }

    /**
     * An eye moving as {@code moving} says, looking {@code window} seconds ahead, nothing at its finest once it
     * sees less sharply than an eye going {@code from} kilometres an hour does.
     */
    public Going(final Motion moving, final double window, final double from) {
        this(moving, window, new Sharpness().overall(moving) < new Sharpness().overall(new Motion(moving.at(),
                new Spot(0, 0, from * HOURLY), 0)));
    }

    @Override
    public Stage finest() {
        return fast ? Stage.TWIGS_GONE : Stage.FINE;
    }

    @Override
    public Vec3 soon(final Vec3 eye, final Vec3 middle) {
        final Spot going = observer.going();
        final double fast = going.across() * going.across() + going.along() * going.along();
        if (fast <= 0) {
            return eye;
        }
        // the nearest it comes to the square, going straight on: no earlier than now, no later than its window
        final double when = Math.clamp(((middle.x() - eye.x()) * going.across() + (middle.z() - eye.z())
                * going.along()) / fast, 0, window);
        return new Vec3(eye.x() + going.across() * when, eye.y(), eye.z() + going.along() * when);
    }
}
