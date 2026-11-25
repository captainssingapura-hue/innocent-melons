package melons.innocent.testkit.loose;

import melons.innocent.core.rest.Ground;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trial set out for the walk. Left alone, only its crate in the air falls, and the stack and the beam stand.
 * Its supports asked for once, the stack comes down onto the ground and the beam leans on the pillar left; asked
 * for again, the beam lies flat. With a time said, the same happens of itself. Begun again, it is as it was
 * described. And each thing is made of something the lab has.
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
        final Trial trial = new Trial(-1);
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
    void withATimeSaidItsSupportsGoOfThemselvesAndBegunAgainItIsAsDescribed() {
        final Trial trial = new Trial(2);
        ticked(trial, 239);
        assertTrue(trial.world().things().contains("the stack's pillar"), "not before two seconds are up");
        ticked(trial, 1);
        assertFalse(trial.world().things().contains("the stack's pillar"));
        assertTrue(trial.world().things().contains("the beam's left pillar"));
        ticked(trial, 240);
        assertFalse(trial.world().things().contains("the beam's left pillar"), "and the last, two seconds after");
        trial.again();
        assertEquals(8, trial.world().things().size());
        assertEquals(150, trial.world().placed("the crate in the air").orElseThrow().at().up(), 0);
        assertEquals(8, trial.made().size());
        assertTrue(trial.made().values().stream().allMatch(stuff -> stuff.dense() > 0 && stuff.material() != null));
    }
}
