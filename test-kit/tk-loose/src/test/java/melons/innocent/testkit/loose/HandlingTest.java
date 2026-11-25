package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Offer;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trial's things handled: a watermelon by the walk's beginning taken up with T and put down again elsewhere;
 * thrown at the stack, breaking a crate and, thrown harder, bursting into slices itself; cut where it lies; an apple
 * carried and eaten; a crate offered and too heavy; and what is held gone with a trial begun again.
 */
class HandlingTest {

    private static final String GROUND = new Ground().name();
    private static final String MELON = "the near melon";

    private final Trial trial = new Trial(-1, 12);
    private final Handling handling = new Handling(trial);

    /** A step with {@code keys} pressed, the walker's eye over the spot so far across and along, facing so. */
    private void stepped(final double across, final double along, final double yaw, final String... keys) {
        handling.stepped(new Step(Set.of(), List.of(keys), 0, 0, false, 0, 0, false),
                new Spot(across, 160, along), yaw);
        trial.ticked();
    }

    private void ticked(final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            trial.ticked();
        }
    }

    private List<String> offered() {
        return handling.offers().stream().map(Offer::said).toList();
    }

    @Test
    void aMelonAtHandIsTakenUpWithTAndPutDownAgainBeforeTheWalker() {
        ticked(60);
        stepped(-90, 220, 0);
        assertEquals(Optional.of(MELON), handling.atHand());
        assertEquals(List.of("Carry", "Cut", "Crush"), offered());
        assertEquals(List.of(MELON, "[T] Carry   Cut   Crush"), handling.hints());
        stepped(-90, 220, 0, "T");
        assertFalse(trial.world().things().contains(MELON), "out of the world, in the hands");
        assertEquals(MELON, handling.held().orElseThrow().name());
        assertEquals(List.of("Put down", "Crush", "Throw"), offered());
        assertEquals(List.of("HOLDING " + MELON, "[T] Put down   Crush   [X] Throw"), handling.hints());
        assertTrue(handling.said().startsWith("In hand: the near melon, a watermelon: Put down, Crush, Throw"),
                handling.said());
        // held, it goes where the hands go
        stepped(300, 0, 90);
        assertEquals(300 + 30 + 18, handling.holding().orElseThrow().at().across(), 1e-9);
        assertEquals(105, handling.holding().orElseThrow().at().up(), 1e-9);
        stepped(300, 0, 90, "T");
        assertTrue(handling.held().isEmpty());
        ticked(600);
        assertEquals(0, trial.world().moving());
        assertEquals(Set.of(GROUND), trial.world().under(MELON));
        assertEquals(14, trial.world().placed(MELON).orElseThrow().at().up(), 0.05);
        assertTrue(trial.world().placed(MELON).orElseThrow().at().across() > 340, "put down before the walker");
    }

    @Test
    void aMelonThrownAtTheStackBreaksACrateAndThrownHarderBurstsIntoSlicesItself() {
        ticked(60);
        stepped(-90, 220, 0, "T");
        handling.throwing(600, 8);
        stepped(0, 900, 0, "X");
        assertTrue(trial.world().things().contains(MELON), "back in the world as it is thrown");
        ticked(40);
        assertFalse(trial.world().things().contains("the lowest crate"), trial.struck().orElse("no blow"));
        assertTrue(trial.world().things().contains(MELON), "a melon stands six hundred joules");
        assertTrue(trial.struck().orElseThrow().contains("the lowest crate broke"), trial.struck().orElseThrow());
        ticked(600);
        assertEquals(Set.of("the stack's pillar"), trial.world().under("the middle crate"), "the stack came down");
        // the spare one, thrown harder at what is left of the stack
        stepped(-160, 220, 0, "T");
        assertEquals("the spare melon", handling.held().orElseThrow().name());
        handling.throwing(1_500, 5);
        stepped(0, 900, 0, "X");
        ticked(120);
        assertFalse(trial.world().things().contains("the spare melon"), trial.struck().orElse("no blow"));
        assertFalse(trial.world().things().contains("the middle crate"), trial.struck().orElseThrow());
        assertTrue(trial.world().things().contains("slice 1 of the spare melon"));
        assertTrue(trial.world().things().contains("slice 4 of the spare melon"));
        ticked(600);
        for (int slice = 1; slice <= 4; slice++) {
            assertEquals(Set.of(GROUND), trial.world().under("slice " + slice + " of the spare melon"),
                    "its slices have fallen and lie at rest");
        }
    }

    @Test
    void aMelonIsCutIntoFourSlicesWhereItLiesAndASliceIsCarriedAndEaten() {
        ticked(60);
        stepped(-90, 220, 0);
        handling.asked(Action.CUT);
        stepped(-90, 220, 0);
        assertFalse(trial.world().things().contains(MELON));
        ticked(600);
        assertEquals(0, trial.world().moving());
        for (int slice = 1; slice <= 4; slice++) {
            final String name = "slice " + slice + " of " + MELON;
            assertEquals(Set.of(GROUND), trial.world().under(name), name);
            assertEquals(300, trial.world().placed(name).orElseThrow().at().along(), 1, "where the melon lay");
        }
        stepped(-90, 220, 0);
        final String slice = handling.atHand().orElseThrow();
        assertTrue(slice.startsWith("slice "), slice);
        assertEquals(List.of("Carry", "Eat"), offered());
        stepped(-90, 220, 0, "T");
        assertEquals(List.of("Put down", "Eat"), offered());
        handling.asked(Action.EAT);
        stepped(-90, 220, 0);
        assertTrue(handling.held().isEmpty());
        assertFalse(trial.world().things().contains(slice), "eaten, it is gone");
        // a melon on the ramp, cut where it lies on the slope
        stepped(1_520, 1_530, 0);
        assertEquals(Optional.of("the melon"), handling.atHand());
        handling.asked(Action.CUT);
        stepped(1_520, 1_530, 0);
        ticked(1_800);
        assertEquals(0, trial.world().moving(), "its slices have settled on the ramp or by it");
    }

    @Test
    void anAppleIsCarriedAndEatenAndACrateIsOfferedThoughItIsTooHeavy() {
        ticked(240);
        stepped(-20, 250, 0);
        assertEquals(Optional.of("the apple"), handling.atHand());
        assertEquals(List.of("Carry", "Eat"), offered());
        stepped(-20, 250, 0, "X");
        assertTrue(handling.said().contains("the apple is not to throw as it lies"), handling.said());
        stepped(-20, 250, 0, "T");
        assertEquals(List.of("Put down", "Eat"), offered());
        handling.asked(Action.EAT);
        stepped(-20, 250, 0);
        assertTrue(handling.held().isEmpty());
        assertFalse(trial.world().things().contains("the apple"));
        stepped(-700, 1_090, 0);
        assertEquals(Optional.of("the crate in the air"), handling.atHand());
        assertEquals(List.of("Carry (too heavy at 600 kg)"), offered());
        assertEquals(List.of("the crate in the air", "[T] Carry - too heavy at 600 kg"), handling.hints());
        stepped(-700, 1_090, 0, "T");
        assertTrue(handling.held().isEmpty());
        assertTrue(trial.world().things().contains("the crate in the air"));
        stepped(0, -500, 180);
        assertEquals("Nothing at hand" + System.lineSeparator()
                + "Last: Carry (too heavy at 600 kg): the crate in the air", handling.said());
    }

    @Test
    void whatIsHeldGoesWhenTheTrialIsBegunAgain() {
        ticked(60);
        stepped(-90, 220, 0, "T");
        assertTrue(handling.held().isPresent());
        trial.again(12);
        stepped(-90, 220, 0);
        assertTrue(handling.held().isEmpty());
        assertEquals(Optional.of(MELON), handling.atHand(), "as it was described, on the ground again");
    }
}
