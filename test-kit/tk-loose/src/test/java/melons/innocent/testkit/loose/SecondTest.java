package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.walk.Standing;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The second figure, the core's own walker in the loose bodies' trial. It is in no tick until it is set down;
 * it is set down beside the avatar at the first step; from the very same keys it walks by the core, standing on the
 * trial's ground; it turns as the avatar turns; and the trial's things go on as they did with it there.
 */
class SecondTest {

    private final Spot start = new Spot(-600, 0, -300);

    /** A trial with the second figure asked for, stepped so many times with the keys {@code held}. */
    private Trial stepped(final int ticks, final Set<String> held, final double yaw) {
        final Trial trial = new Trial(-1, 12);
        for (int tick = 0; tick < ticks; tick++) {
            trial.stood(start, yaw);
            trial.second().asked(held, List.of(), start, yaw);
            trial.ticked();
        }
        return trial;
    }

    @Test
    void itIsInNoTickUntilItIsSetDown() {
        assertFalse(new Trial(-1, 12).second().set());
        assertTrue(stepped(1, Set.of(), 0).second().set());
        assertTrue(stepped(1, Set.of(), 0).second().boxed(), "and set down so, it is a second figure, drawn as a box");
    }

    @Test
    void itIsSetDownBesideTheAvatarAndStandsOnTheGround() {
        final Standing standing = stepped(30, Set.of(), 0).second().standing().orElseThrow();
        assertEquals(0, standing.feet().minus(start.plus(new Spot(150, 0, 0))).length(), 1e-9,
                "a metre and a half to its right, and not moved, asked nothing");
        assertEquals(Optional.of("the ground"), standing.on());
    }

    @Test
    void fromTheSameKeysItRunsAheadAndWalksWithShiftHeld() {
        final Standing running = stepped(120, Set.of("W"), 0).second().standing().orElseThrow();
        assertEquals(760, running.going().along(), 0.5, "at a run, as the avatar goes unless Shift is held");
        assertTrue(running.feet().along() > start.along() + 600);
        final Standing walking = stepped(120, Set.of("W", "SHIFT"), 0).second().standing()
                .orElseThrow();
        assertEquals(320, walking.going().along(), 0.5);
    }

    @Test
    void itFacesAsTheAvatarFacesAndGoesThatWay() {
        final Standing turned = stepped(120, Set.of("W", "SHIFT"), 90).second().standing()
                .orElseThrow();
        assertEquals(90, turned.yaw(), 1e-9);
        assertEquals(320, turned.going().across(), 0.5, "ahead, facing right, is across");
    }

    @Test
    void theTrialsThingsGoOnAsTheyDidWithItThere() {
        final Trial with = stepped(600, Set.of(), 0);
        final Trial without = new Trial(-1, 12);
        for (int tick = 0; tick < 600; tick++) {
            without.stood(start, 0);
            without.ticked();
        }
        assertEquals(without.world().state(), with.world().state(), "the very same state of every thing");
        assertFalse(with.world().things().contains(Second.NAME), "it is a walker, and no thing of the world");
    }
}
