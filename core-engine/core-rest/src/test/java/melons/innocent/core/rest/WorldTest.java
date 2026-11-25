package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The world at rest, and its check, with a simulator that moves nothing so that only the check is seen. At its
 * first tick every thing is checked once and each is found standing on what holds it; after that a tick in which
 * nothing happens checks nothing. A thing described with nothing under it is let go at the first tick. When what
 * holds a stack is destroyed, everything on it is let go in that same tick, the lowest first, and nothing else is
 * looked at; a thing held by two is let go when its middle is no longer over what is left. Things described as run
 * into each other are said so once. And no two things have one name, nor is a thing destroyed that is not there.
 */
class WorldTest {

    private static final double TICK = 1.0 / 120;
    private final Simulator unmoving = (rigid, now, around, seconds) -> new Simulator.Stepped(now, Optional.empty());

    private World stacked() {
        return new World(new Ground(), unmoving)
                .stood(new Rigid("pillar", new Box(40, 100, 40), 2_400), new Placed(new Spot(0, 50, 0)))
                .stood(new Rigid("lower", new Box(60, 60, 60), 600), new Placed(new Spot(0, 130, 0)))
                .stood(new Rigid("upper", new Box(60, 60, 60), 600), new Placed(new Spot(0, 190, 0)))
                .stood(new Rigid("aside", new Box(60, 60, 60), 600), new Placed(new Spot(500, 30, 0)));
    }

    @Test
    void everyThingIsCheckedOnceAtFirstAndThenOnlyWhenSomethingHappens() {
        final World world = stacked();
        assertTrue(world.ticked(List.of(), TICK).isEmpty(), "all of it is held, so nothing happens");
        assertEquals(4, world.checks());
        assertEquals(Set.of(new Ground().name()), world.under("pillar"));
        assertEquals(Set.of("pillar"), world.under("lower"));
        assertEquals(Set.of("lower"), world.under("upper"));
        for (int tick = 0; tick < 600; tick++) {
            assertTrue(world.ticked(List.of(), TICK).isEmpty());
        }
        assertEquals(4, world.checks(), "five seconds of nothing happening, and nothing checked");
        assertEquals(0, world.moving());
    }

    @Test
    void whenWhatHoldsAStackIsDestroyedAllOfItIsLetGoInThatTickAndNothingElseLookedAt() {
        final World world = stacked();
        world.ticked(List.of(), TICK);
        final List<Happened> happened = world.ticked(List.of("pillar"), TICK);
        assertEquals(List.of(new Happened.Destroyed("pillar"), new Happened.Unsettled("lower"),
                new Happened.Unsettled("upper")), happened, "the lowest first");
        assertEquals(6, world.checks(), "the two that stood on it, and not the one standing aside");
        assertFalse(world.resting("lower") || world.resting("upper"));
        assertTrue(world.resting("aside"));
        assertEquals(Set.of("aside", "lower", "upper"), world.things());
        assertTrue(world.under("upper").isEmpty(), "in motion, it stands on nothing");
    }

    @Test
    void aThingWithNothingUnderItIsLetGoAtTheFirstTickAndOneHeldByTwoWhenItsMiddleIsNoLongerHeld() {
        final World world = new World(new Ground(), unmoving)
                .stood(new Rigid("floating", new Box(50, 50, 50), 600), new Placed(new Spot(-900, 300, 0)))
                .stood(new Rigid("left", new Box(40, 100, 40), 2_400), new Placed(new Spot(-180, 50, 0)))
                .stood(new Rigid("right", new Box(40, 100, 40), 2_400), new Placed(new Spot(180, 50, 0)))
                .stood(new Rigid("beam", new Box(400, 20, 40), 7_850), new Placed(new Spot(0, 110, 0)));
        assertEquals(List.of(new Happened.Unsettled("floating")), world.ticked(List.of(), TICK));
        assertEquals(Set.of("left", "right"), world.under("beam"));
        assertEquals(List.of(new Happened.Destroyed("right"), new Happened.Unsettled("beam")),
                world.ticked(List.of("right"), TICK));
        assertTrue(world.resting("left"));
    }

    @Test
    void thingsRunIntoEachOtherAreSaidSoOnceAndNamesAreKeptApart() {
        final World world = new World(new Ground(), unmoving)
                .stood(new Rigid("one", new Box(100, 100, 100), 600), new Placed(new Spot(0, 50, 0)))
                .stood(new Rigid("other", new Box(100, 100, 100), 600), new Placed(new Spot(40, 50, 30)));
        assertEquals(List.of(new Happened.Overlapping("one", "other")), world.ticked(List.of(), TICK));
        assertTrue(world.ticked(List.of(), TICK).isEmpty(), "and not said again");
        assertThrows(IllegalArgumentException.class,
                () -> world.stood(new Rigid("one", new Box(1, 1, 1), 1), new Placed(new Spot(0, 0, 0))));
        assertThrows(IllegalArgumentException.class, () -> world.ticked(List.of("nobody"), TICK));
        assertEquals(new Moved(), world.moved("one"));
        assertEquals("one", world.thing("one").name());
    }
}
