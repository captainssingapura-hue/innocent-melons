package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A world's ticks. It is ticked as the game engine steps unless its ticker says otherwise; it counts its own ticks,
 * the first from nothing; what happened in a tick is given back stamped with that tick; and the engine that steps
 * its things is handed the world's own tick, the very one being taken, and nothing else of time.
 */
class TickedTest {

    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    private final Placed inTheAir = new Placed(new Spot(0, 150, 0));

    @Test
    void aWorldIsTickedAsTheGameEngineStepsUnlessItsTickerSaysOtherwise() {
        final Simulator still = (rigid, now, around, tick) -> new Simulator.Stepped(now, Optional.empty());
        assertEquals(new Tick(0, 120), new World(new Ground(), still).tick());
        assertEquals(new Tick(0, 60), new World(new Ground(), still, new Ticker(60)).tick());
    }

    @Test
    void aWorldCountsItsOwnTicksAndStampsWhatHappenedWithTheTickItHappenedIn() {
        final Simulator still = (rigid, now, around, tick) -> new Simulator.Stepped(now, Optional.empty());
        final World world = new World(new Ground(), still).stood(crate, inTheAir);
        final Stamped first = world.ticked(new Asked());
        assertEquals(new Tick(0, 120), first.tick(), "the first tick is counted from nothing");
        assertEquals(List.of(new Happened.Unsettled("crate")), first.happened());
        assertEquals(1, world.tick().count(), "one taken");
        final Stamped second = world.ticked(new Asked());
        assertEquals(new Tick(1, 120), second.tick());
        assertEquals(List.of(), second.happened());
        for (int more = 0; more < 118; more++) {
            world.ticked(new Asked());
        }
        assertEquals(120, world.tick().count());
        assertEquals(1, world.tick().elapsed(), 0, "a second on, to the last bit");
    }

    @Test
    void theEngineIsHandedTheWorldsOwnTickAndTheVeryOneBeingTaken() {
        final List<Tick> handed = new ArrayList<>();
        final Simulator telling = (rigid, now, around, tick) -> {
            handed.add(tick);
            return new Simulator.Stepped(now, Optional.empty());
        };
        final World world = new World(new Ground(), telling, new Ticker(60)).stood(crate, inTheAir);
        for (int taken = 0; taken < 3; taken++) {
            world.ticked(new Asked());
        }
        assertEquals(List.of(new Tick(0, 60), new Tick(1, 60), new Tick(2, 60)), handed,
                "let go in the first tick and stepped in it, and in each after, by a sixtieth of a second");
    }
}
