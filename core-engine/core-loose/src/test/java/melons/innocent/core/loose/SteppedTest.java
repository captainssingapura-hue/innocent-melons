package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Touch;
import melons.innocent.core.rest.Touching;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The loose body's two functions, each asked by itself with no world round it: the stepping of a tick, and the first
 * correction of that stepping. Each is handed the thing, how it stands and moves, what is round it, and the tick,
 * and says how the thing stands and moves a tick on; handed the same it says the same, and it takes from the tick
 * only how long it is.
 */
class SteppedTest {

    private final Loose loose = new Loose();
    private final Ticker ticker = new Ticker();
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    /** Nothing round it at all, and the level ground under it. */
    private final Simulator.Around nothing = (rigid, placed) -> List.of();
    private final Simulator.Around ground = (rigid, placed) ->
            new Touching().grounded(rigid.shape(), placed, new Ground());

    /** The crate in the air a metre and a half up, going as {@code velocity} says. */
    private Simulator.Mover flying(final Spot velocity) {
        return new Simulator.Mover(new Placed(new Spot(0, 150, 0)), new Moved(velocity, new Spot(0, 0, 0)), false, 0);
    }

    /** A collision of the crate, half way along its tick's path at {@code there}, with what goes as {@code going}. */
    private Collision met(final Spot there, final Spot going) {
        return new Collision("crate", "the wall", 0.5, new Placed(there),
                new Touch(there.plus(new Spot(0, 0, 25)), new Spot(0, 0, -1), 0, "the wall", going));
    }

    @Test
    void aCrateTouchedByNothingFallsForAsLongAsTheTickItIsHandedIs() {
        for (final Tick tick : List.of(ticker.first(), new Ticker(60).first())) {
            final Simulator.Stepped stepped = loose.stepped(crate, flying(new Spot(0, 0, 0)), nothing, tick);
            final double seconds = tick.seconds();
            assertEquals(-981 * seconds, stepped.next().moved().velocity().up(), 1e-12, "gravity's pull for the tick");
            assertEquals(150 - 981 * seconds * seconds / 2, stepped.next().placed().at().up(), 1e-12,
                    "and gone by the mean of what it had and what it has, so it is on the curve");
            assertEquals(Optional.empty(), stepped.on());
        }
    }

    @Test
    void whichTickItIsMakesNoDifferenceToALooseBodyOnlyHowLongItIs() {
        final Simulator.Mover thrown = flying(new Spot(120, 300, -40));
        assertEquals(loose.stepped(crate, thrown, nothing, ticker.first()),
                loose.stepped(crate, thrown, nothing, ticker.at(5_000_000)));
    }

    @Test
    void aCrateStandingOnTheGroundIsHeldWhereItIsAndTheTickCountedTowardItsRest() {
        final Simulator.Mover standing = new Simulator.Mover(new Placed(new Spot(0, 25, 0)));
        final Simulator.Stepped stepped = loose.stepped(crate, standing, ground, ticker.first());
        assertEquals(standing.placed(), stepped.next().placed());
        assertTrue(stepped.next().moved().still() && stepped.next().landed());
        assertEquals(1, stepped.next().still());
        assertEquals(Optional.of(new Ground().name()), stepped.on(), "said to have landed, not having been down");
        assertEquals(2, loose.stepped(crate, stepped.next(), ground, ticker.at(1)).next().still());
    }

    @Test
    void aCrateFallingFastIsTakenThroughTheTickInPartsAndSetDownOnTheGroundItWouldHavePassed() {
        final Simulator.Mover falling = new Simulator.Mover(new Placed(new Spot(0, 40, 0)),
                new Moved(new Spot(0, -6_000, 0), new Spot(0, 0, 0)), false, 0);
        final Simulator.Stepped stepped = loose.stepped(crate, falling, ground, ticker.first());
        assertEquals(25, stepped.next().placed().at().up(), 0.02, "standing on the ground, not half a metre under");
        assertEquals(Optional.of(new Ground().name()), stepped.on());
        assertTrue(stepped.next().landed());
    }

    @Test
    void theFirstCorrectionTakesAwayOnlyTheGoingIntoWhatWasMetAndKeepsTheRestOfThePath() {
        final Simulator.Mover now = flying(new Spot(0, 0, 300));
        final Tick tick = ticker.first();
        final Simulator.Stepped regular = loose.stepped(crate, now, nothing, tick);
        final Spot there = now.placed().at().plus(regular.next().placed().at().minus(now.placed().at()).times(0.5));
        final Simulator.Stepped corrected = loose.correctionS1(crate, now, nothing, tick, regular,
                List.of(met(there, new Spot(0, 0, 0))));
        assertEquals(0, corrected.next().moved().velocity().along(), 1e-12, "no longer going into the wall");
        assertEquals(regular.next().moved().velocity().up(), corrected.next().moved().velocity().up(), 0,
                "falling as it was");
        assertEquals(there.along(), corrected.next().placed().at().along(), 1e-12, "no further than where it met it");
        assertEquals(regular.next().placed().at().up(), corrected.next().placed().at().up(), 1e-12,
                "and as far down as its path took it");
    }

    @Test
    void meetingWhatGoesTheSameWayMoreSlowlyItIsLeftGoingAsThatGoes() {
        final Simulator.Mover now = flying(new Spot(0, 0, 300));
        final Simulator.Stepped regular = loose.stepped(crate, now, nothing, ticker.first());
        final Simulator.Stepped corrected = loose.correctionS1(crate, now, nothing, ticker.first(), regular,
                List.of(met(now.placed().at(), new Spot(0, 0, 100))));
        assertEquals(100, corrected.next().moved().velocity().along(), 1e-12);
    }

    @Test
    void leavingWhatItTouchesThereIsNothingToPutRightAndTheSteppingIsGivenBack() {
        final Simulator.Mover now = flying(new Spot(0, 0, -300));
        final Simulator.Stepped regular = loose.stepped(crate, now, nothing, ticker.first());
        assertSame(regular, loose.correctionS1(crate, now, nothing, ticker.first(), regular,
                List.of(met(now.placed().at(), new Spot(0, 0, 0)))));
    }

    @Test
    void aSimulatorThatCorrectsNothingGivesItsSteppingBack() {
        final Simulator plain = (rigid, now, around, tick) -> new Simulator.Stepped(now, Optional.empty());
        final Simulator.Mover now = flying(new Spot(0, 0, 300));
        final Simulator.Stepped regular = plain.stepped(crate, now, nothing, ticker.first());
        assertSame(regular, plain.correctionS1(crate, now, nothing, ticker.first(), regular,
                List.of(met(now.placed().at(), new Spot(0, 0, 0)))));
    }
}
