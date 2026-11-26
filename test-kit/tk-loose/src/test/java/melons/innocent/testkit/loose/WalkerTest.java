package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Outsider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Whoever walks the trial, as the core's world is told of them. Before the walk's first step nobody is told of;
 * from then on the walker is a box standing on its feet, turned as it faces, going as it went since the step
 * before, and standing where it hardly moves. And what it is for: the ramp's melon, its chock taken away, rolls
 * down and on past where nobody stands, and stops against whoever stands below the ramp, and comes to rest there.
 */
class WalkerTest {

    /** Below the ramp's lower edge by a metre, in the way the ramp's melon rolls down. */
    private final Spot below = new Spot(1520, 0, 1100);

    @Test
    void nobodyIsToldOfBeforeTheWalksFirstStep() {
        assertEquals(Map.of(), new Walker().outsiders());
    }

    @Test
    void theWalkerIsABoxOnItsFeetTurnedAsItFacesAndGoingAsItWentSinceTheStepBefore() {
        final Walker walker = new Walker();
        walker.stood(new Spot(100, 0, 200), 90);
        final Outsider first = walker.outsiders().get(Walker.NAME);
        assertEquals(0, first.placed().at().minus(new Spot(100, 100, 200)).length(), 1e-12, "its middle a metre up");
        assertEquals(0, first.going().length(), 0, "going nowhere, at its first step");
        assertEquals(200, first.box().up(), 0);
        // turned a quarter round, what was its front faces across
        assertEquals(1, Math.abs(first.placed().turned().turned(new Spot(0, 0, 1)).across()), 1e-9);
        walker.stood(new Spot(100, 0, 201), 90);
        assertEquals(0, walker.outsiders().get(Walker.NAME).going().minus(new Spot(0, 0, 120)).length(), 1e-9,
                "a centimetre in a tick is a hundred and twenty a second");
    }

    @Test
    void aWalkerHardlyMovingIsToldAsStanding() {
        final Walker walker = new Walker();
        walker.stood(new Spot(100, 0, 200), 0);
        walker.stood(new Spot(100, 0, 200.00001), 0);
        assertEquals(0, walker.outsiders().get(Walker.NAME).going().length(), 0);
    }

    @Test
    void theRampsMelonRollsDownAndOnPastWhereNobodyStands() {
        final Trial trial = rolledDown(false);
        assertTrue(trial.world().resting("the melon"));
        assertTrue(trial.world().placed("the melon").orElseThrow().at().along() < below.along() - 100,
                "well past: " + trial.world().placed("the melon").orElseThrow().at());
    }

    @Test
    void theRampsMelonStopsAgainstWhoeverStandsBelowTheRampAndComesToRestThere() {
        final Trial trial = rolledDown(true);
        final Spot at = trial.world().placed("the melon").orElseThrow().at();
        assertTrue(trial.world().resting("the melon"), "at rest");
        // the walker's front is thirty-two from its middle, and a melon reaches thirteen to eighteen from its own
        final double before = at.along() - below.along();
        assertTrue(before > 44.9 && before < 50.1, "against the walker and not in it: " + before);
        assertEquals(below.across(), at.across(), 20, "where it rolled down");
        assertFalse(trial.world().placed("the ball").orElseThrow().at().along() > below.along() - 100,
                "the ball beside it, with nobody in its way, rolled on");
    }

    /** The trial twenty seconds after the ramp's chock is taken away, with the walker standing below or nobody. */
    private Trial rolledDown(final boolean stood) {
        final Trial trial = new Trial(-1, 12);
        trial.take("the chock");
        for (int tick = 0; tick < 120 * 20; tick++) {
            if (stood) {
                trial.stood(below, 0);
            }
            trial.ticked();
        }
        return trial;
    }
}
