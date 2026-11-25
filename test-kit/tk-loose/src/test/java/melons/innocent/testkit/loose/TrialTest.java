package melons.innocent.testkit.loose;

import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Throw;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trial set out for the walk. Left alone, only its crate in the air falls, and the stack and the beam stand.
 * Its supports asked for once, the stack comes down onto the ground and the beam leans on the pillar left; asked
 * for again, the beam lies flat. With a time said, the same happens of itself. Begun again, it is as it was
 * described, its ramp at the slope then said. Its melon and its ball are held on the ramp by the chock; that taken
 * away, both roll down and out over the ground and come to rest there, while the crate beside them stays; and on a
 * ramp with no slope neither stirs. A thing thrown is followed: it rises as high and, on the level, comes down as
 * far as the reckoning says. A melon thrown sideways from the wagon as it goes comes down carried along its track.
 * And each thing is made of something the lab has.
 */
class TrialTest {

    private static final String GROUND = new Ground().name();

    private void ticked(final Trial trial, final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            trial.ticked();
        }
    }

    @Test
    void leftAloneOnlyTheCrateFallsAndAskedItsSupportsGoInTwoTakings() {
        final Trial trial = new Trial(-1, 12);
        ticked(trial, 240);
        assertEquals(0, trial.world().moving(), "the crate has come to rest and nothing else has stirred");
        assertEquals(Set.of(GROUND), trial.world().under("the crate in the air"));
        assertEquals(Set.of("the beam's left pillar", "the beam's right pillar"), trial.world().under("the beam"));
        final long changes = trial.changes();
        trial.take();
        ticked(trial, 300);
        assertTrue(trial.changes() > changes);
        assertFalse(trial.world().things().contains("the stack's pillar"));
        assertEquals(Set.of(GROUND), trial.world().under("the lowest crate"));
        assertEquals(Set.of("the middle crate"), trial.world().under("the highest crate"));
        assertEquals(Set.of("the beam's left pillar", GROUND), trial.world().under("the beam"));
        trial.take();
        ticked(trial, 360);
        assertEquals(Set.of(GROUND), trial.world().under("the beam"));
        assertEquals(10, trial.world().placed("the beam").orElseThrow().at().up(), 0.05);
        trial.take();
        ticked(trial, 10);
        assertEquals(0, trial.world().moving(), "a third asking finds nothing left to take");
    }

    @Test
    void theMelonAndTheBallRollWhenTheChockIsTakenAwayAndTheCrateBesideThemStays() {
        final Trial trial = new Trial(-1, 12);
        ticked(trial, 240);
        assertEquals(Set.of("the chock", "the ramp"), trial.world().under("the melon"));
        assertEquals(Set.of("the chock", "the ramp"), trial.world().under("the ball"));
        trial.take("the chock");
        ticked(trial, 120);
        assertTrue(trial.world().moved("the melon").velocity().along() < -80, "a second on, the melon is rolling");
        assertTrue(trial.world().moved("the ball").velocity().along() < -80, "and the ball");
        ticked(trial, 3_000);
        assertEquals(0, trial.world().moving(), "both have come to rest");
        assertEquals(Set.of(GROUND), trial.world().under("the melon"));
        assertEquals(Set.of(GROUND), trial.world().under("the ball"));
        assertTrue(trial.world().placed("the melon").orElseThrow().at().along() < 1_000,
                "out over the ground, past the foot of the ramp");
        assertEquals(15, trial.world().placed("the ball").orElseThrow().at().up(), 0.05);
        assertEquals(Set.of("the ramp"), trial.world().under("the crate on the ramp"));
        final Trial level = new Trial(-1, 0);
        ticked(level, 60);
        level.take("the chock");
        ticked(level, 600);
        assertEquals(0, level.world().moving(), "on a ramp with no slope, nothing rolls");
        assertEquals(170, level.world().placed("the ball").orElseThrow().at().along() - 1_450, 0.01);
    }

    @Test
    void aThingThrownIsFollowedAndComesDownWhereTheReckoningSays() {
        final Trial trial = new Trial(-1, 12);
        ticked(trial, 240);
        assertEquals(17, trial.movable().size(), "every thing but the ramp and the wagon can be thrown");
        assertTrue(trial.flown().isEmpty());
        final Throw thrown = new Throw(3_000, 180, 45);
        trial.thrown("the crate in the air", thrown);
        ticked(trial, 5);
        final Flown flown = trial.flown().orElseThrow();
        assertTrue(flown.carried().isEmpty() && flown.said().contains("still in the air"), flown.said());
        ticked(trial, 240);
        assertEquals(flown.reckonedRise(), flown.rose(), 0.05, "as high as the reckoning says");
        assertEquals(flown.reckonedCarry(), flown.carried().orElseThrow(), 4, "and as far, on the level");
        assertTrue(trial.world().placed("the crate in the air").orElseThrow().at().along() < 1_150, "toward the start");
        assertTrue(flown.said().startsWith("Thrown: the crate in the air at 3.2 m/s, 45 degrees up"), flown.said());
        // thrown up the slope, away from the chock it lies against
        trial.thrown("the melon", new Throw(800, 0, 45));
        ticked(trial, 600);
        final Flown melon = trial.flown().orElseThrow();
        assertEquals(melon.reckonedRise(), melon.rose(), 0.05);
        assertTrue(melon.carried().orElseThrow() > melon.reckonedCarry(), "from up on the ramp it carries further");
        trial.thrown("the ramp gone", thrown);
        trial.again(12);
        assertTrue(trial.flown().isEmpty(), "begun again, no throw is followed");
    }

    @Test
    void aMelonThrownSidewaysFromTheWagonComesDownCarriedAlongItsTrack() {
        final Trial trial = new Trial(-1, 12);
        ticked(trial, 60);
        assertEquals(Set.of("the wagon", "the wagon's front chock", "the wagon's rear chock"),
                trial.world().under("the wagon's melon"), "chocked before and behind");
        trial.driven(36);
        ticked(trial, 120);
        assertEquals(-2_000 + 1_000, trial.world().placed("the wagon's melon").orElseThrow().at().across(), 10,
                "a second on, ten metres along, carried");
        assertTrue(trial.world().resting("the wagon's melon"));
        trial.thrown("the wagon's melon", new Throw(300, 180, 35));
        ticked(trial, 360);
        final Flown flown = trial.flown().orElseThrow();
        assertEquals(flown.reckonedRise(), flown.rose(), 0.05, "it rises as the throw alone would have it");
        assertTrue(flown.carried().orElseThrow() > flown.reckonedCarry(),
                "out to the side, and a metre's fall further");
        assertTrue(flown.aside().orElseThrow() > 800, "and carried ten metres a second along the track as it flew");
        assertTrue(flown.said().contains("aside, carried on at 10.0 m/s"), flown.said());
        trial.driven(0);
        ticked(trial, 1_200);
        assertEquals(Set.of("the wagon"), trial.world().under("the wagon's crate"));
        assertEquals(Set.of(GROUND), trial.world().under("the wagon's ball"), "the ball, unchocked, rolled off");
    }

    @Test
    void withATimeSaidItsSupportsGoOfThemselvesAndBegunAgainItIsAsDescribed() {
        final Trial trial = new Trial(2, 12);
        ticked(trial, 239);
        assertTrue(trial.world().things().contains("the stack's pillar"), "not before two seconds are up");
        ticked(trial, 1);
        assertFalse(trial.world().things().contains("the stack's pillar"));
        assertTrue(trial.world().things().contains("the beam's left pillar"));
        ticked(trial, 240);
        assertFalse(trial.world().things().contains("the beam's left pillar"), "and the last, two seconds after");
        trial.again(20);
        assertEquals(19, trial.world().things().size());
        assertEquals(20, trial.slope(), 0);
        assertEquals(150, trial.world().placed("the crate in the air").orElseThrow().at().up(), 0);
        assertEquals(19, trial.made().size());
        assertTrue(trial.made().values().stream().allMatch(stuff -> stuff.dense() > 0 && stuff.material() != null));
    }
}
