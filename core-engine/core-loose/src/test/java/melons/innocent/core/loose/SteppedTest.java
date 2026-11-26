package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Body;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Landing;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Still;
import melons.innocent.core.rest.Touch;
import melons.innocent.core.rest.Touching;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The loose body's two functions, each asked by itself with no world round it: the stepping of a tick, and the first
 * correction of that stepping. Each is handed the thing with how it stands and moves and what is round it, and the
 * tick, and gives how the thing stands and moves a tick on, its explanation, and its landing if it landed. Handed
 * the same it gives the same; it takes from the tick only how long it is; and what it gives for the thing is the
 * very same whether it explains or not.
 */
class SteppedTest {

    private final Loose loose = new Loose();
    private final Loose explaining = new Loose(new Explaining(true));
    private final Ticker ticker = new Ticker();
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    private final Rigid melon = new Rigid("melon", new Ellipsoid(36, 28, 26), 960);
    /** Nothing round it at all, and the level ground under it. */
    private final Simulator.Around nothing = (rigid, placed) -> List.of();
    private final Simulator.Around ground = (rigid, placed) ->
            new Touching().grounded(rigid.shape(), placed, new Ground());

    /** The crate in the air a metre and a half up, going as {@code velocity} says, with nothing round it. */
    private Body flying(final Spot velocity) {
        return new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 150, 0)),
                new Moved(velocity, new Spot(0, 0, 0)), false, 0), nothing);
    }

    /** A collision of the crate, half way along its tick's path at {@code there}, with what goes as {@code going}. */
    private Collision met(final Spot there, final Spot going) {
        return new Collision("crate", "the wall", 0.5, new Placed(there),
                new Touch(there.plus(new Spot(0, 0, 25)), new Spot(0, 0, -1), 0, "the wall", going));
    }

    @Test
    void aCrateTouchedByNothingFallsForAsLongAsTheTickItIsHandedIs() {
        for (final Tick tick : List.of(ticker.first(), new Ticker(60).first())) {
            final Explained<Rigid, Body, Why> stepped = loose.simulated(flying(new Spot(0, 0, 0)), tick, List.of());
            final double seconds = tick.seconds();
            assertEquals(-981 * seconds, stepped.state().mover().moved().velocity().up(), 1e-12);
            assertEquals(150 - 981 * seconds * seconds / 2, stepped.state().mover().placed().at().up(), 1e-12,
                    "gone by the mean of what it had and what it has, so it is on the curve");
            assertEquals(List.of(), stepped.said());
        }
    }

    @Test
    void whichTickItIsMakesNoDifferenceToALooseBodyOnlyHowLongItIs() {
        final Body thrown = flying(new Spot(120, 300, -40));
        assertEquals(loose.simulated(thrown, ticker.first(), List.of()),
                loose.simulated(thrown, ticker.at(5_000_000), List.of()));
    }

    @Test
    void aCrateStandingOnTheGroundIsHeldWhereItIsAndTheTickCountedTowardItsRest() {
        final Body standing = new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 25, 0))), ground);
        final Explained<Rigid, Body, Why> stepped = loose.simulated(standing, ticker.first(), List.of());
        final Simulator.Mover next = stepped.state().mover();
        assertEquals(standing.mover().placed(), next.placed());
        assertTrue(next.moved().still() && next.landed());
        assertEquals(1, next.still());
        assertEquals(List.of(new Landing(new Ground().name())), stepped.said(), "landed, not having been down");
        final Explained<Rigid, Body, Why> again = loose.simulated(stepped.state(), ticker.at(1), List.of());
        assertEquals(2, again.state().mover().still());
        assertEquals(List.of(), again.said(), "and not said to land a second time");
    }

    @Test
    void aCrateFallingFastIsTakenThroughTheTickInPartsAndSetDownOnTheGroundItWouldHavePassed() {
        final Body falling = new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 40, 0)),
                new Moved(new Spot(0, -6_000, 0), new Spot(0, 0, 0)), false, 0), ground);
        final Explained<Rigid, Body, Why> stepped = loose.simulated(falling, ticker.first(), List.of());
        assertEquals(25, stepped.state().mover().placed().at().up(), 0.02, "on the ground, not half a metre under");
        assertEquals(List.of(new Landing(new Ground().name())), stepped.said());
        assertTrue(stepped.state().mover().landed());
    }

    @Test
    void whatItGivesForTheThingIsTheVerySameWhetherItExplainsOrNot() {
        final List<Body> bodies = List.of(flying(new Spot(120, 300, -40)),
                new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 25, 0))), ground),
                new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 40, 0)),
                        new Moved(new Spot(0, -6_000, 0), new Spot(0, 0, 0)), false, 0), ground),
                new Body(melon, new Simulator.Mover(new Placed(new Spot(0, 14, 0)),
                        new Moved(new Spot(0, 0, 200), new Spot(0, 0, 0)), false, 0), ground));
        for (final Body body : bodies) {
            final Explained<Rigid, Body, Why> silent = loose.simulated(body, ticker.first(), List.of());
            final Explained<Rigid, Body, Why> told = explaining.simulated(body, ticker.first(), List.of());
            assertEquals(silent.state(), told.state());
            assertEquals(silent.said(), told.said());
            assertSame(Why.Unsaid.UNSAID, silent.why(), "off, the one empty explanation every time");
        }
    }

    @Test
    void itsExplanationIsTypedToTheWayItCameOutAndNestedAsItsEnginesRan() {
        final Tick tick = ticker.first();
        final Why flew = explaining.simulated(flying(new Spot(0, 0, 0)), tick, List.of()).why();
        assertEquals(new Why.Flew(new Spot(0, -981 * tick.seconds(), 0)), flew, "gravity's pull for the tick");
        final Why held = explaining.simulated(new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 25, 0))),
                ground), tick, List.of()).why();
        assertEquals(new Why.Held(List.of(new Ground().name()), 1), held);
        final Why fast = explaining.simulated(new Body(crate, new Simulator.Mover(new Placed(new Spot(0, 40, 0)),
                new Moved(new Spot(0, -6_000, 0), new Spot(0, 0, 0)), false, 0), ground), tick, List.of()).why();
        final Why.InParts parts = assertInstanceOf(Why.InParts.class, fast, "taken through the tick in parts");
        assertEquals(7, parts.parts().size(), "fifty centimetres in a tick, at most eight to a part");
        assertInstanceOf(Why.CameDown.class, parts.parts().stream()
                .filter(part -> !(part instanceof Why.Flew)).findFirst().orElseThrow(), "in one of which it landed");
        final Why rolled = explaining.simulated(new Body(melon, new Simulator.Mover(new Placed(new Spot(0, 14, 0)),
                new Moved(new Spot(0, 0, 200), new Spot(0, 0, 0)), false, 0), ground), tick, List.of()).why();
        final Why.Rolled first = assertInstanceOf(Why.Rolled.class, rolled, "handed on to rolling");
        assertEquals(List.of(new Ground().name()), first.on());
    }

    @Test
    void theFirstCorrectionTakesAwayOnlyTheGoingIntoWhatWasMetAndKeepsTheRestOfThePath() {
        final Body now = flying(new Spot(0, 0, 300));
        final Tick tick = ticker.first();
        final Explained<Rigid, Body, Why> regular = explaining.simulated(now, tick, List.of());
        final Spot from = now.mover().placed().at();
        final Spot there = from.plus(regular.state().mover().placed().at().minus(from).times(0.5));
        final Explained<Rigid, Body, Why> corrected = explaining.correctionS1(now, tick, regular,
                List.of(met(there, new Spot(0, 0, 0))));
        final Simulator.Mover next = corrected.state().mover();
        assertEquals(0, next.moved().velocity().along(), 1e-12, "no longer going into the wall");
        assertEquals(regular.state().mover().moved().velocity().up(), next.moved().velocity().up(), 0,
                "falling as it was");
        assertEquals(there.along(), next.placed().at().along(), 1e-12, "no further than where it met it");
        assertEquals(regular.state().mover().placed().at().up(), next.placed().at().up(), 1e-12,
                "and as far down as its path took it");
        assertEquals(new Why.Corrected(regular.why(), List.of("the wall"), new Spot(0, 0, 300)), corrected.why(),
                "explained as its stepping put right, with what it met and the going it lost");
        assertEquals(corrected.state(), loose.correctionS1(now, tick, loose.simulated(now, tick, List.of()),
                List.of(met(there, new Spot(0, 0, 0)))).state(), "the same whether it explains or not");
    }

    @Test
    void meetingWhatGoesTheSameWayMoreSlowlyItIsLeftGoingAsThatGoes() {
        final Body now = flying(new Spot(0, 0, 300));
        final Explained<Rigid, Body, Why> regular = loose.simulated(now, ticker.first(), List.of());
        final Explained<Rigid, Body, Why> corrected = loose.correctionS1(now, ticker.first(), regular,
                List.of(met(now.mover().placed().at(), new Spot(0, 0, 100))));
        assertEquals(100, corrected.state().mover().moved().velocity().along(), 1e-12);
    }

    @Test
    void leavingWhatItTouchesThereIsNothingToPutRightAndTheSteppingIsGivenBack() {
        final Body now = flying(new Spot(0, 0, -300));
        final Explained<Rigid, Body, Why> regular = loose.simulated(now, ticker.first(), List.of());
        assertSame(regular, loose.correctionS1(now, ticker.first(), regular,
                List.of(met(now.mover().placed().at(), new Spot(0, 0, 0)))));
    }

    @Test
    void aSimulatorThatCorrectsNothingGivesItsSteppingBack() {
        final Still still = new Still();
        final Body now = flying(new Spot(0, 0, 300));
        final Explained<Rigid, Body, Still.Nothing> regular = still.simulated(now, ticker.first(), List.of());
        assertSame(regular, still.correctionS1(now, ticker.first(), regular,
                List.of(met(now.mover().placed().at(), new Spot(0, 0, 0)))));
    }
}
