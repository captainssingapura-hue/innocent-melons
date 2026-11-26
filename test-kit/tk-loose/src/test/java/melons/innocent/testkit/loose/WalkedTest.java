package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.walk.Standing;
import melons.innocent.core.walk.Wish;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A walk whose figure goes by the core. Each step of the walk is one tick of the trial's world with the walker in
 * it, and nothing of the old engine is stepped; the keys of a step are what the walker is asked; it runs unless
 * Shift is held and turns as the step turns it; the walk says its head, its feet and its figure where the core's
 * walker is; it is the walk's own figure and not a second one; and begun again, it is set down where it started.
 */
class WalkedTest {

    private final Spot start = new Spot(-600, 0, -300);

    private Walked walk(final String script, final Trial trial) {
        return new Walked(new Scripted(script), Optional.empty(), new Skies(new Asked(1)), trial, new Bot(), start,
                0);
    }

    /** The walk taken to its script's end, a sixtieth of a second at a time. */
    private void played(final Walked walked) {
        for (int frame = 0; frame < 6_000 && !walked.over(); frame++) {
            walked.stepped(1.0 / 60);
        }
        assertTrue(walked.over(), "its script has run out");
    }

    @Test
    void keysAreTurnedIntoWhatAWalkerIsAskedOutsideTheCore() {
        assertEquals(new Wish(1, 0, 0, true, false), new Keyed(Set.of("W"), List.of()).wish(0));
        assertEquals(new Wish(-1, 1, 2.5, false, true),
                new Keyed(Set.of("S", "D", "SHIFT"), List.of("SPACE")).wish(2.5));
        assertEquals(new Wish(0, 0, 0, true, false), new Keyed(Set.of("W", "S", "A", "D"), List.of()).wish(0),
                "ahead and back together are nowhere, and so are left and right");
    }

    @Test
    void eachStepOfTheWalkIsOneTickOfTheTrialsWorldWithTheWalkerInIt() {
        final Trial trial = new Trial(-1, 12);
        final Walked walked = walk("SHIFT+W:1", trial);
        assertTrue(trial.second().set() && !trial.second().boxed(), "set down as the walk's own figure");
        played(walked);
        assertTrue(walked.steps() >= 120, "a second of walking is a hundred and twenty steps at the least");
        assertEquals(walked.steps(), trial.world().tick().count(), "and each step was one tick of the world");
        final Standing standing = trial.second().standing().orElseThrow();
        assertEquals(320, standing.going().along(), 0.5, "walking, with Shift held");
        assertEquals(Optional.of("the ground"), standing.on());
        assertEquals(standing.feet().up() + 160, walked.head(1).y(), 1e-9, "its head over its feet");
        assertEquals(standing.feet().along(), walked.foot(1).z(), 1e-9);
        assertEquals(standing.feet().across(), walked.head(1).x(), 1e-9);
        assertFalse(walked.figures(1).isEmpty(), "and its figure is drawn there");
        assertTrue(walked.camera(1).eye().z() < standing.feet().along(), "followed from behind");
    }

    @Test
    void itRunsUnlessShiftIsHeldAndTurnsAsTheStepTurnsIt() {
        final Trial running = new Trial(-1, 12);
        played(walk("W:1", running));
        assertEquals(760, running.second().standing().orElseThrow().going().along(), 0.5);
        final Trial turned = new Trial(-1, 12);
        final Walked walked = walk("@90:1;SHIFT+W:1", turned);
        played(walked);
        assertEquals(90, turned.second().standing().orElseThrow().yaw(), 0.01, "a quarter round to the right");
        assertEquals(90, walked.yaw(1), 0.01);
        assertEquals(320, turned.second().standing().orElseThrow().going().across(), 0.5,
                "and ahead is then across");
    }

    @Test
    void begunAgainItIsSetDownWhereItStarted() {
        final Trial trial = new Trial(-1, 12);
        final Walked walked = walk("W:1;:0.5", trial);
        for (int frame = 0; frame < 80; frame++) {
            walked.stepped(1.0 / 60);
        }
        assertTrue(trial.second().standing().orElseThrow().feet().along() > start.along() + 200);
        trial.again(12);
        walked.stepped(1.0 / 60);
        assertEquals(0, trial.second().standing().orElseThrow().feet().minus(start).length(), 20,
                "back at its start, with the trial");
    }
}
