package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A thing driven, and what rides it. A flat wagon going steadily carries the crate, the watermelon and the ball that
 * rest on it: each stays where it is on the wagon, at rest though it moves over the ground, and nothing is checked
 * while it does. A watermelon thrown sideways from the wagon keeps the wagon's way as it flies: it comes down as far
 * out to the side as the throw alone would carry it, and as far on along the track as the wagon has gone meanwhile,
 * abreast of the place on the wagon it left; then it rolls on along the track. When the wagon stops dead the crate
 * stops with it and the watermelon rolls on off its front; when it sets off at once the crate goes with it and the
 * watermelon is left behind off its back. When the wagon is destroyed, what rode it flies on and falls. And a run
 * made twice is the same run.
 */
class CarriedTest {

    private static final double TICK = 1.0 / 120;
    private static final double GRAVITY = 981;
    private static final String GROUND = new Ground().name();
    /** Ten metres a second along the track. */
    private static final double FAST = 1_000;
    private final Rigid wagon = new Rigid("wagon", new Box(1_200, 100, 300), 800);
    private final Rigid crate = new Rigid("crate", new Box(60, 60, 60), 600);
    private final Rigid melon = new Rigid("melon", new Ellipsoid(26, 28, 36), 960);
    private final Rigid ball = new Rigid("ball", new Ellipsoid(30), 1_100);

    /** A wagon going so fast along the track, with a crate, a watermelon and a ball resting on it, a tick in. */
    private World laden(final double speed) {
        final World world = new World(new Ground(), new Loose())
                .driven(wagon, new Placed(new Spot(0, 50, 0)), going(speed))
                .stood(crate, new Placed(new Spot(-300, 130, 0)))
                .stood(melon, new Placed(new Spot(0, 114, 0)))
                .stood(ball, new Placed(new Spot(300, 115, 0)));
        assertTrue(world.ticked(List.of(), TICK).isEmpty(), "all of it rests on the wagon");
        return world;
    }

    private Moved going(final double speed) {
        return new Moved(new Spot(speed, 0, 0), new Spot(0, 0, 0));
    }

    private void run(final World world, final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            world.ticked(List.of(), TICK);
        }
    }

    private double across(final World world, final String name) {
        return world.placed(name).orElseThrow().at().across();
    }

    @Test
    void whatRestsOnAWagonGoingSteadilyIsCarriedAtRestInItsFrame() {
        final World world = laden(FAST);
        assertEquals(Set.of("wagon"), world.under("melon"));
        final long checked = world.checks();
        run(world, 239);
        assertEquals(2_000, across(world, "wagon"), 1e-6, "two seconds on, twenty metres along");
        assertEquals(1_700, across(world, "crate"), 1e-6, "the crate where it was on the wagon");
        assertEquals(2_000, across(world, "melon"), 1e-6);
        assertEquals(2_300, across(world, "ball"), 1e-6);
        assertEquals(114, world.placed("melon").orElseThrow().at().up(), 0);
        assertTrue(world.resting("crate") && world.resting("melon") && world.resting("ball"));
        assertEquals(0, world.moving(), "nothing is in motion: they are at rest on what carries them");
        assertEquals(checked, world.checks(), "and nothing has been checked meanwhile");
        assertEquals(going(FAST), world.moved("melon"), "though over the ground the melon goes as the wagon does");
    }

    @Test
    void aMelonThrownSidewaysFromTheWagonKeepsTheWagonsWayAsItFlies() {
        final World world = laden(FAST);
        run(world, 59);
        final Throw sideways = new Throw(0.5 * melon.kilograms() * 36, 180, 30);
        final Spot from = world.placed("melon").orElseThrow().at();
        assertEquals(List.of(new Happened.Thrown("melon")),
                world.ticked(List.of(), Map.of("melon", sideways.of(melon)), TICK));
        assertEquals(FAST, world.moved("melon").velocity().across(), 1e-9, "it has the wagon's speed along the track");
        assertEquals(-600 * Math.cos(Math.toRadians(30)), world.moved("melon").velocity().along(), 1e-9,
                "and its own out to the side");
        int ticks = 1;
        while (!world.ticked(List.of(), TICK).contains(new Happened.Landed("melon", GROUND))) {
            ticks++;
            assertTrue(ticks < 600, "it comes down");
        }
        ticks++;
        final double rising = 600 * Math.sin(Math.toRadians(30));
        final double aloft = (rising + Math.sqrt(rising * rising + 2 * GRAVITY * 100)) / GRAVITY;
        assertEquals(aloft, ticks * TICK, 2 * TICK, "as long in the air as a fall of a metre more than its rise takes");
        final Spot down = world.placed("melon").orElseThrow().at();
        assertEquals(600 * Math.cos(Math.toRadians(30)) * aloft, from.along() - down.along(), 12,
                "as far out to the side as the throw alone carries it");
        assertEquals(FAST * aloft, down.across() - from.across(), 20,
                "and as far on along the track as the wagon went");
        assertEquals(across(world, "wagon"), down.across(), 20, "so it comes down abreast of where it left the wagon");
        run(world, 3_000);
        assertTrue(world.resting("melon") && world.under("melon").equals(Set.of(GROUND)));
        assertTrue(across(world, "melon") > down.across() + 300, "having rolled on along the track after it landed");
        assertTrue(world.resting("crate") && world.under("crate").equals(Set.of("wagon")), "the crate rides on");
    }

    @Test
    void whenTheWagonStopsDeadTheCrateStopsWithItAndTheMelonRollsOnOffItsFront() {
        final World world = laden(FAST);
        run(world, 59);
        final List<Happened> happened = world.ticked(new Asked(List.of(), Map.of(), Map.of("wagon", going(0))), TICK);
        assertEquals(List.of(new Happened.Unsettled("ball"), new Happened.Unsettled("crate"),
                new Happened.Unsettled("melon"), new Happened.Driven("wagon")), happened.subList(0, 4));
        final double stopped = across(world, "wagon");
        run(world, 3_000);
        assertEquals(stopped, across(world, "wagon"), 0, "the wagon has not moved since");
        assertTrue(world.resting("crate"));
        assertEquals(stopped - 300, across(world, "crate"), 0.5, "the crate stopped with it, where it stood on it");
        assertEquals(Set.of("wagon"), world.under("crate"));
        assertTrue(across(world, "melon") > stopped + 600, "the melon rolled on, off the wagon's front");
        assertEquals(Set.of(GROUND), world.under("melon"));
        assertEquals(Set.of(GROUND), world.under("ball"));
    }

    @Test
    void whenTheWagonSetsOffAtOnceTheCrateGoesWithItAndTheMelonIsLeftBehind() {
        // the crate ahead of the melon, so that nothing is behind the melon to stop it
        final World world = new World(new Ground(), new Loose())
                .driven(wagon, new Placed(new Spot(0, 50, 0)), going(0))
                .stood(crate, new Placed(new Spot(300, 130, 0)))
                .stood(melon, new Placed(new Spot(0, 114, 0)));
        run(world, 30);
        world.ticked(new Asked(List.of(), Map.of(), Map.of("wagon", going(FAST))), TICK);
        run(world, 600);
        assertTrue(world.resting("crate"));
        assertEquals(across(world, "wagon") + 300, across(world, "crate"), 0.5, "the crate rides where it stood");
        assertEquals(going(FAST), world.moved("crate"));
        assertTrue(across(world, "melon") < across(world, "wagon") - 600, "the melon is off the wagon's back");
        assertEquals(14, world.placed("melon").orElseThrow().at().up(), 5, "and on the ground");
        assertThrows(IllegalArgumentException.class, () -> world.ticked(
                new Asked(List.of(), Map.of(), Map.of("crate", going(FAST))), TICK), "a crate is not driven");
        assertThrows(IllegalArgumentException.class, () -> world.ticked(List.of(),
                Map.of("wagon", new Throw(100, 0, 45).of(wagon)), TICK), "and a wagon is not thrown");
    }

    @Test
    void whenTheWagonIsDestroyedWhatRodeItFliesOnAndFalls() {
        final World world = laden(FAST);
        run(world, 59);
        final double from = across(world, "crate");
        final List<Happened> happened = world.ticked(List.of("wagon"), TICK);
        assertTrue(happened.containsAll(List.of(new Happened.Unsettled("crate"), new Happened.Unsettled("melon"),
                new Happened.Destroyed("wagon"))));
        assertEquals(FAST, world.moved("crate").velocity().across(), 1e-9, "it keeps the wagon's way");
        run(world, 600);
        assertFalse(world.things().contains("wagon"));
        assertEquals(FAST * Math.sqrt(2 * 100 / GRAVITY), across(world, "crate") - from, 20,
                "the crate came down as far on as it went in the time a metre's fall takes, and stayed there");
        assertEquals(30, world.placed("crate").orElseThrow().at().up(), 0.05);
    }

    @Test
    void aRunMadeTwiceIsTheSameRunTickForTick() {
        final World one = laden(FAST);
        final World other = laden(FAST);
        for (int tick = 0; tick < 900; tick++) {
            final Asked asked = new Asked(List.of(), tick == 20 ? Map.of("melon", new Throw(400, 200, 40).of(melon))
                    : Map.of(), tick == 200 ? Map.of("wagon", going(0)) : Map.of());
            assertEquals(one.ticked(asked, TICK), other.ticked(asked, TICK));
            for (final String name : List.of("wagon", "crate", "melon", "ball")) {
                assertEquals(one.placed(name), other.placed(name), name + " at tick " + tick);
            }
        }
    }
}
