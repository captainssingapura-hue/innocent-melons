package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Mode;
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
 * The trial's things handled: a watermelon taken up with T and put down again elsewhere; the menu G opens, its mark
 * moved by the arrows; a melon readied to be thrown from that menu, its power gathered while the trigger is held
 * and the melon thrown when it is let go, breaking a crate and, thrown harder, bursting into slices itself; the
 * state left without throwing; a melon cut where it lies; an apple eaten; a crate offered and too heavy; and what
 * is held gone with a trial begun again.
 */
class HandlingTest {

    private static final String GROUND = new Ground().name();
    private static final String MELON = "the near melon";

    private final Trial trial = new Trial(-1, 12);
    private final Handling handling = new Handling(trial);
    /** How far up the walker looks, in the steps that follow. */
    private double looked;

    /** A step with {@code keys} pressed, the walker's eye over the spot so far across and along, facing so. */
    private void stepped(final double across, final double along, final double yaw, final String... keys) {
        handling.stepped(new Step(Set.of(), List.of(keys), 0, 0, false, 0, 0, false),
                new Spot(across, 160, along), yaw, looked);
        trial.ticked();
    }

    /** So many steps with the trigger held, the walker standing there. */
    private void pulled(final double across, final double along, final double yaw, final int steps) {
        for (int step = 0; step < steps; step++) {
            handling.stepped(new Step(Set.of("FIRE"), List.of(), 0, 0, false, 0, 0, false),
                    new Spot(across, 160, along), yaw, looked);
            trial.ticked();
        }
    }

    private void ticked(final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            trial.ticked();
        }
    }

    private List<String> offered() {
        return handling.offers().stream().map(Offer::said).toList();
    }

    /** What is held, or at hand there and then taken up, readied to be thrown: Throw chosen from the menu. */
    private void readied(final double across, final double along, final double yaw) {
        if (handling.held().isEmpty()) {
            stepped(across, along, yaw, "T");
        }
        stepped(across, along, yaw, "G");
        stepped(across, along, yaw, "DOWN");
        stepped(across, along, yaw, "T");
        assertEquals(Optional.of(Mode.THROWING), handling.mode());
    }

    @Test
    void aMelonAtHandIsTakenUpWithTAndPutDownAgainBeforeTheWalker() {
        ticked(60);
        stepped(-90, 220, 0);
        assertEquals(Optional.of(MELON), handling.atHand());
        assertEquals(List.of("Carry", "Cut", "Crush"), offered());
        assertEquals(List.of(MELON, "[T] Carry   [G] MORE"), handling.hints());
        stepped(-90, 220, 0, "T");
        assertFalse(trial.world().things().contains(MELON), "out of the world, in the hands");
        assertEquals(MELON, handling.held().orElseThrow().name());
        assertEquals(List.of("Put down", "Crush", "Throw"), offered());
        assertEquals(List.of("HOLDING " + MELON, "[T] Put down   [G] MORE"), handling.hints());
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
    void theMenuListsWhatElseCanBeDoneItsMarkIsMovedAndItIsShutOrLeftBehind() {
        ticked(60);
        stepped(-90, 220, 0, "T");
        stepped(-90, 220, 0, "G");
        assertEquals(List.of(MELON, "> Crush", "  Throw", "UP - DOWN: CHOOSE   [T] DO IT   [G] CLOSE"),
                handling.hints());
        stepped(-90, 220, 0, "DOWN");
        assertEquals(Action.THROW, handling.menu().orElseThrow().chosen().orElseThrow().action());
        stepped(-90, 220, 0, "UP");
        assertEquals(Action.CRUSH, handling.menu().orElseThrow().chosen().orElseThrow().action());
        stepped(-90, 220, 0, "G");
        assertTrue(handling.menu().isEmpty(), "G again shuts it, and nothing is done");
        assertEquals(MELON, handling.held().orElseThrow().name());
        // put down, the menu of a thing at hand is another, and walking away from the thing shuts it
        stepped(-90, 220, 0, "T");
        ticked(240);
        stepped(-90, 220, 0, "G");
        assertEquals(List.of("Cut", "Crush"), handling.menu().orElseThrow().offers().stream().map(Offer::label)
                .toList());
        stepped(0, -600, 180);
        assertTrue(handling.menu().isEmpty());
        stepped(0, -600, 180, "G");
        assertTrue(handling.said().endsWith("Last: nothing at hand"), handling.said());
    }

    @Test
    void aMelonReadiedIsThrownAsHardAsTheTriggerWasHeldAndBreaksACrateOrBurstsItself() {
        ticked(60);
        readied(-90, 220, 0);
        assertEquals(150, handling.holding().orElseThrow().at().up(), 1e-9, "held up, readied");
        assertEquals("Throwing " + MELON, handling.hints().getFirst());
        assertEquals("[............] 0%", handling.hints().get(1));
        assertTrue(handling.curve().isEmpty(), "no power yet, no flight to show");
        handling.full(600);
        looked = -20;
        pulled(0, 900, 0, 90);
        assertEquals(0.5, handling.power(), 1e-9, "half the time, half the power");
        assertEquals("[******......] 50%", handling.hints().get(1));
        final double half = handling.curve().getLast().along();
        pulled(0, 900, 0, 120);
        assertEquals(1, handling.power(), 0, "and no more than all of it, however long it is held");
        assertTrue(handling.curve().getLast().along() > half + 100, "the flight shown reaches further");
        assertEquals(MELON, handling.held().orElseThrow().name(), "still in the hands while the trigger is held");
        stepped(0, 900, 0);
        assertTrue(handling.held().isEmpty() && handling.mode().isEmpty(), "let go, it is thrown");
        assertTrue(handling.said().endsWith("Last: Thrown with 600 J, 0 degrees up: " + MELON), handling.said());
        assertTrue(trial.world().things().contains(MELON), "back in the world as it is thrown");
        ticked(40);
        assertFalse(trial.world().things().contains("the lowest crate"), trial.struck().orElse("no blow"));
        assertTrue(trial.world().things().contains(MELON), "a melon stands the blow at this speed");
        ticked(600);
        assertEquals(Set.of("the stack's pillar"), trial.world().under("the middle crate"), "the stack came down");
        // the spare one, thrown harder at what is left of the stack
        looked = 0;
        readied(-160, 220, 0);
        assertEquals("the spare melon", handling.held().orElseThrow().name());
        handling.full(1_500);
        looked = -20;
        pulled(0, 900, 0, 200);
        stepped(0, 900, 0);
        ticked(120);
        assertFalse(trial.world().things().contains("the spare melon"), trial.struck().orElse("no blow"));
        assertFalse(trial.world().things().contains("the middle crate"), trial.struck().orElseThrow());
        assertTrue(trial.world().things().contains("slice 1 of the spare melon"));
        ticked(600);
        for (int slice = 1; slice <= 4; slice++) {
            assertEquals(Set.of(GROUND), trial.world().under("slice " + slice + " of the spare melon"),
                    "its slices have fallen and lie at rest");
        }
    }

    @Test
    void theStateIsLeftWithTheThingStillHeldAndThrownWithLittlePowerItFallsShort() {
        ticked(60);
        readied(-90, 220, 0);
        stepped(-90, 220, 0, "G");
        assertTrue(handling.mode().isEmpty(), "G leaves the state");
        assertEquals(MELON, handling.held().orElseThrow().name(), "with the melon still in the hands");
        assertEquals(105, handling.holding().orElseThrow().at().up(), 1e-9, "lowered again");
        pulled(-90, 220, 0, 60);
        assertEquals(MELON, handling.held().orElseThrow().name(), "the trigger does nothing out of the state");
        readied(-90, 220, 0);
        stepped(-90, 220, 0, "T");
        assertTrue(handling.mode().isEmpty() && handling.held().isPresent(), "T leaves it too, and puts nothing down");
        readied(0, -300, 180);
        handling.full(600);
        pulled(0, -300, 180, 18);
        stepped(0, -300, 180);
        assertTrue(handling.said().endsWith("Last: Thrown with 60 J, 20 degrees up: " + MELON), handling.said());
        ticked(600);
        final double fell = trial.world().placed(MELON).orElseThrow().at().along();
        assertTrue(fell < -340 && fell > -1_400, "down a little before the walker and rolled on: " + fell);
    }

    @Test
    void aMelonIsCutFromTheMenuIntoFourSlicesWhereItLiesAndASliceIsCarriedAndEaten() {
        ticked(60);
        stepped(-90, 220, 0, "G");
        stepped(-90, 220, 0, "T");
        assertTrue(handling.said().endsWith("Last: Cut: " + MELON), handling.said());
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
    void anAppleIsEatenWhereItLiesAndACrateIsOfferedThoughItIsTooHeavy() {
        ticked(240);
        stepped(-20, 250, 0);
        assertEquals(Optional.of("the apple"), handling.atHand());
        assertEquals(List.of("Carry", "Eat"), offered());
        stepped(-20, 250, 0, "G");
        assertEquals(List.of("the apple", "> Eat", "UP - DOWN: CHOOSE   [T] DO IT   [G] CLOSE"), handling.hints());
        stepped(-20, 250, 0, "T");
        stepped(-20, 250, 0);
        assertFalse(trial.world().things().contains("the apple"), "eaten as it lay");
        stepped(-700, 1_090, 0);
        assertEquals(Optional.of("the crate in the air"), handling.atHand());
        assertEquals(List.of("Carry (too heavy at 600 kg)"), offered());
        assertEquals(List.of("the crate in the air", "[T] Carry - too heavy at 600 kg"), handling.hints());
        stepped(-700, 1_090, 0, "T");
        assertTrue(handling.held().isEmpty());
        assertTrue(trial.world().things().contains("the crate in the air"));
        stepped(-700, 1_090, 0, "G");
        assertTrue(handling.menu().isEmpty());
        assertTrue(handling.said().endsWith("Last: nothing more to do with the crate in the air"), handling.said());
    }

    @Test
    void whatIsHeldGoesWhenTheTrialIsBegunAgain() {
        ticked(60);
        readied(-90, 220, 0);
        trial.again(12);
        stepped(-90, 220, 0);
        assertTrue(handling.held().isEmpty() && handling.mode().isEmpty());
        assertEquals(Optional.of(MELON), handling.atHand(), "as it was described, on the ground again");
    }
}
