package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;

import java.util.Optional;

/**
 * Whatever provides surfaces to move over: a floor, a ramp, a deck, the ground, later a road, a stair and a roof. It
 * is asked in its own frame and answers in its own frame, knowing nothing of where it is in the world, how it is
 * moving, or what kind of mover asks. A new kind of provider is one more of these, and no mover learns of it.
 */
public interface Provider {

    /** What it is called. */
    String name();

    /**
     * Its surface under {@code spot}, the first met going {@code down} from there, no further than {@code reach}:
     * all in its own frame, {@code down} one long. None, where it has no surface there.
     */
    Optional<Patch> under(Spot spot, Spot down, double reach);
}
