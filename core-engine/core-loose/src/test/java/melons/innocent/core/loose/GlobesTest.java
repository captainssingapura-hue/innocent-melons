package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Body;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Core;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Globe;
import melons.innocent.core.rest.Gravity;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Next;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.State;
import melons.innocent.core.rest.World;
import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gravity is the world's, and is handed to the engine with the thing: so the one engine, made once and told nothing
 * of where it is, serves on the Earth, on the Moon and on Mars. A thing falls as its world pulls; a drop takes as
 * long, and a throw goes as far, as that pull makes it; two things in one world fall alike; and a world is on the
 * Earth unless it is said otherwise.
 */
class GlobesTest {

    private final Loose loose = new Loose();
    private final Core<Why> core = new Core<>(loose);
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    private final Rigid melon = new Rigid("melon", new Ellipsoid(36, 28, 26), 960);
    private final Simulator.Around nothing = (rigid, placed) -> List.of();

    /** A world on {@code globe} with the crate at rest on the ground and the melon beside it. */
    private State on(final Globe globe, final double up) {
        return new State(new Ground(), new Gravity(globe), new Ticker())
                .stood(crate, new Placed(new Spot(0, up, 0)))
                .stood(melon, new Placed(new Spot(200, up, 0)));
    }

    /** How many ticks from {@code from} until the crate has come down, what is {@code asked} done at the first. */
    private int until(final State from, final Asked asked) {
        State state = from;
        for (int tick = 0; tick < 2_000; tick++) {
            final Next next = core.ticked(state, tick == 0 ? asked : new Asked());
            if (next.stamped().happened().stream().anyMatch(one -> one instanceof Happened.Landed landed
                    && landed.thing().equals("crate"))) {
                return tick + 1;
            }
            state = next.state();
        }
        throw new AssertionError("it never came down");
    }

    @Test
    void theGlobesPullAsTheyDoAndAWorldIsOnTheEarthUnlessSaid() {
        assertEquals(981, new Gravity().pull(), 0);
        assertEquals(981, Globe.EARTH.pull(), 0);
        assertEquals(162, new Gravity(Globe.MOON).pull(), 0);
        assertEquals(372, new Gravity(Globe.MARS).pull(), 0);
        assertEquals(new Gravity(), new State(new Ground(), new Ticker()).gravity());
        assertEquals(new Gravity(), new World(new Ground(), loose).gravity());
        assertEquals(new Gravity(Globe.MOON),
                new World(new Ground(), new Gravity(Globe.MOON), loose, new Ticker()).gravity());
        assertThrows(IllegalArgumentException.class, () -> new Gravity(-1));
    }

    @Test
    void theOneEngineIsHandedTheWorldsGravityAndFallsByIt() {
        final Tick tick = new Ticker().first();
        final Simulator.Mover still = new Simulator.Mover(new Placed(new Spot(0, 150, 0)));
        for (final Globe globe : Globe.values()) {
            final Simulator.Mover next = loose.simulated(new Body(crate, still, nothing, new Gravity(globe)), tick,
                    List.of()).state().mover();
            assertEquals(-globe.pull() * tick.seconds(), next.moved().velocity().up(), 1e-12, globe.name());
            assertEquals(150 - globe.pull() * tick.seconds() * tick.seconds() / 2, next.placed().at().up(), 1e-12);
        }
        final Simulator.Mover adrift = loose.simulated(new Body(crate, still, nothing, new Gravity(0)), tick,
                List.of()).state().mover();
        assertEquals(still.placed(), adrift.placed(), "where nothing pulls, it stays where it is let go");
    }

    @Test
    void aDropTakesAsLongAsItsWorldsPullMakesIt() {
        for (final Globe globe : Globe.values()) {
            // let go with its underside two and a half metres up
            final double reckoned = Math.sqrt(2 * 250 / globe.pull()) * 120;
            assertEquals(reckoned, until(on(globe, 275), new Asked()), 2, globe + ", in ticks");
        }
    }

    @Test
    void aThrowGoesAsFarAsItsWorldsPullLetsIt() {
        final Asked thrown = new Asked(List.of(), Map.of("crate",
                new Moved(new Spot(0, 300, 600), new Spot(0, 0, 0))), Map.of());
        for (final Globe globe : Globe.values()) {
            State state = on(globe, 25);
            for (int tick = 0; tick < 1_200; tick++) {
                state = core.ticked(state, tick == 0 ? thrown : new Asked()).state();
            }
            assertTrue(state.resting().containsKey("crate"), globe + ": down and at rest");
            assertEquals(2 * 300 * 600 / globe.pull(), state.placed("crate").orElseThrow().at().along(), 8,
                    globe + ": as far as a throw of that speed and rise goes there, without air");
        }
    }

    @Test
    void twoThingsInOneWorldFallAlikeWhateverTheyAre() {
        State state = on(Globe.MOON, 400);
        for (int tick = 0; tick < 60; tick++) {
            state = core.ticked(state, new Asked()).state();
        }
        assertEquals(state.moved("crate").velocity().up(), state.moved("melon").velocity().up(), 1e-9);
        assertEquals(-162 * 0.5, state.moved("crate").velocity().up(), 1e-9, "half a second of the Moon's pull");
    }
}
