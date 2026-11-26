package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Outsider;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.World;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Things in motion that meet what stands in their path, put right by the first correction of a tick. A melon rolled
 * at someone standing stops against them and comes to rest there, whichever way it lies; with nobody there it
 * rolls on past. Someone coming at a rolling melon takes it along, and a melon thrown from against someone leaves
 * them freely. A melon rolled into one at rest strikes it, and both go on together; two that meet head on stop,
 * touching; the heavier of two keeps more of its speed. Nothing ends inside anything. And a run made twice is the
 * same run.
 */
class CollidedTest {

    private static final double TICK = 1.0 / 120;
    private final Rigid melon = new Rigid("melon", new Ellipsoid(36, 28, 26), 960);
    private final Rigid other = new Rigid("other", new Ellipsoid(36, 28, 26), 960);
    /** Someone standing at the world's middle, facing along: as wide and as tall as a walker is taken to be. */
    private final Outsider standing = new Outsider(new Box(64, 200, 64), new Placed(new Spot(0, 100, 0)),
            new Spot(0, 0, 0));

    /** What a tick is asked when nothing is, but for who is there. */
    private Asked there(final Map<String, Outsider> outsiders) {
        return new Asked(List.of(), List.of(), Map.of(), Map.of(), outsiders);
    }

    /** A world with {@code rigid} lying so far along, turned so far about the line across, set going along. */
    private World rolled(final Rigid rigid, final double along, final double tilt, final double going,
                         final Map<String, Outsider> outsiders) {
        final World world = new World(new Ground(), new Loose()).stood(rigid, new Placed(new Spot(0, 20, along),
                new Turn(new Spot(1, 0, 0), Math.toRadians(tilt))));
        world.ticked(new Asked(List.of(), List.of(), Map.of(rigid.name(), new Moved(new Spot(0, 0, going),
                new Spot(0, 0, 0))), Map.of(), outsiders), TICK);
        return world;
    }

    private List<Happened> run(final World world, final int ticks, final Map<String, Outsider> outsiders) {
        final List<Happened> all = new ArrayList<>();
        for (int tick = 0; tick < ticks; tick++) {
            all.addAll(world.ticked(there(outsiders), TICK));
        }
        return all;
    }

    private double along(final World world, final Rigid rigid) {
        return world.placed(rigid.name()).orElseThrow().at().along();
    }

    @Test
    void aMelonRolledAtSomeoneStandingStopsAgainstThemAndComesToRestWhicheverWayItLies() {
        for (final double tilt : new double[] {0, 15, 35, 55, 75, 90}) {
            final World world = rolled(melon, 120, tilt, -150, Map.of("someone", standing));
            run(world, 120 * 20, Map.of("someone", standing));
            assertTrue(world.resting("melon"), "at rest, lying " + tilt + " degrees over");
            // their front is thirty-two along, and the melon reaches thirteen to eighteen from its middle
            assertTrue(along(world, melon) > 44.9 && along(world, melon) < 50.1,
                    "against them and not in them: " + along(world, melon));
            assertEquals(0, world.moved("melon").velocity().length(), 0);
            final double up = world.placed("melon").orElseThrow().at().up();
            assertTrue(up > 12.9 && up < 18.1, "lying on the ground: " + up);
        }
    }

    @Test
    void withNobodyInItsWayItRollsOnPast() {
        final World world = rolled(melon, 120, 0, -150, Map.of());
        run(world, 120 * 20, Map.of());
        assertTrue(world.resting("melon"));
        assertTrue(along(world, melon) < 20, "on past where they would have stood: " + along(world, melon));
    }

    @Test
    void someoneComingUpBehindARollingMelonTakesItAlong() {
        final World world = rolled(melon, 60, 0, 80, Map.of());
        double fastest = 0;
        double someone = 0;
        for (int tick = 0; tick < 120; tick++) {
            // someone a metre and a half a second along, from the world's middle: faster than the melon rolls
            someone = 150 * tick * TICK;
            final Outsider coming = new Outsider(new Box(64, 200, 64), new Placed(new Spot(0, 100, someone)),
                    new Spot(0, 0, 150));
            world.ticked(there(Map.of("someone", coming)), TICK);
            fastest = Math.max(fastest, world.moved("melon").velocity().along());
            assertTrue(along(world, melon) > someone + 32 + 12.9 - 0.1, "never inside them, at tick " + tick);
        }
        assertTrue(fastest > 140, "brought to their speed: " + fastest);
        assertTrue(along(world, melon) < someone + 32 + 14.1 + 12, "and still just before them, not thrown ahead: "
                + (along(world, melon) - someone));
    }

    @Test
    void aMelonThrownFromAgainstSomeoneLeavesThemFreely() {
        // lying against their front, and thrown away from them
        final World world = rolled(melon, 45, 0, 300, Map.of("someone", standing));
        run(world, 60, Map.of("someone", standing));
        assertTrue(along(world, melon) > 150, "half a second on it is well away: " + along(world, melon));
    }

    @Test
    void aMelonRolledIntoOneAtRestStrikesItAndBothGoOnTogether() {
        final World world = rolled(melon, 0, 0, 200, Map.of()).stood(other, new Placed(new Spot(0, 14, 200)));
        boolean struck = false;
        for (int tick = 0; tick < 120 * 30; tick++) {
            final List<Happened> happened = world.ticked(new Asked(), TICK);
            if (happened.contains(new Happened.Struck("other", "melon"))) {
                struck = true;
                final double mine = world.moved("melon").velocity().along();
                assertTrue(mine > 20, "still going on: " + mine);
                assertEquals(mine, world.moved("other").velocity().along(), 0.5, "at one speed, the two alike");
            }
            assertTrue(along(world, other) - along(world, melon) > 25.9, "never inside each other");
        }
        assertTrue(struck, "the one at rest was struck");
        assertTrue(world.resting("melon") && world.resting("other"), "and both have come to rest");
        assertTrue(along(world, other) > 210, "the struck one moved on: " + along(world, other));
    }

    @Test
    void twoMelonsMeetingHeadOnStopWhereTheyTouch() {
        final World world = rolled(melon, 0, 0, 150, Map.of()).stood(other, new Placed(new Spot(0, 14, 200)));
        world.ticked(new Asked(List.of(), List.of(), Map.of("other", new Moved(new Spot(0, 0, -150),
                new Spot(0, 0, 0))), Map.of()), TICK);
        double nearest = Double.MAX_VALUE;
        double met = 0;
        double closing = 0;
        for (int tick = 0; tick < 120 * 30; tick++) {
            world.ticked(new Asked(), TICK);
            final double apart = along(world, other) - along(world, melon);
            if (apart < nearest) {
                nearest = apart;
                met = (along(world, other) + along(world, melon)) / 2;
                closing = world.moved("melon").velocity().along() - world.moved("other").velocity().along();
            }
        }
        // a melon is twenty-six to twenty-eight through, as it has rolled over
        assertTrue(nearest > 25.9 && nearest < 28.1, "they touched, neither in the other: " + nearest);
        assertEquals(0, closing, 5, "and touching, neither goes on into the other nor is thrown back from it");
        assertEquals(100, met, 12, "about half way between where they began");
        assertTrue(world.resting("melon") && world.resting("other"), "and both have come to rest");
        // one that stopped on its narrow side rocks back onto its broad one, so they may end a little apart
        assertTrue(along(world, other) - along(world, melon) < 45, "near where they met");
    }

    @Test
    void ofTwoThatMeetTheHeavierKeepsMoreOfItsSpeed() {
        final Rigid heavy = new Rigid("heavy", new Ellipsoid(30), 4_000);
        final Rigid light = new Rigid("light", new Ellipsoid(30), 500);
        assertEquals(8, kept(heavy, light) / (1 - kept(heavy, light)), 0.8, "eight parts in nine, as it weighs");
        assertEquals(1.0 / 8, kept(light, heavy) / (1 - kept(light, heavy)), 0.02, "and one in nine the other way");
    }

    /** How much of its speed {@code rolled} has left, of what it had the tick before, when it strikes {@code met}. */
    private double kept(final Rigid rolled, final Rigid met) {
        final World world = rolled(rolled, 0, 0, 200, Map.of()).stood(met, new Placed(new Spot(0, 15, 150)));
        double before = 0;
        for (int tick = 0; tick < 600; tick++) {
            final List<Happened> happened = world.ticked(new Asked(), TICK);
            if (happened.contains(new Happened.Struck(met.name(), rolled.name()))) {
                return world.moved(rolled.name()).velocity().along() / before;
            }
            before = world.moved(rolled.name()).velocity().along();
        }
        throw new IllegalStateException(rolled.name() + " never struck " + met.name());
    }

    @Test
    void aSimulatorThatCorrectsNothingGivesItsSteppingBack() {
        final Simulator still = (rigid, now, around, seconds) -> new Simulator.Stepped(now, Optional.empty());
        final Simulator.Mover now = new Simulator.Mover(new Placed(new Spot(0, 14, 0)));
        final Simulator.Stepped stepped = still.stepped(melon, now, (rigid, placed) -> List.of(), TICK);
        assertSame(stepped, still.correctionS1(melon, now, (rigid, placed) -> List.of(), TICK, stepped, List.of()));
    }

    @Test
    void aRunMadeTwiceIsTheSameRunTickForTick() {
        final List<String> first = traced();
        assertEquals(first, traced());
        assertTrue(first.stream().anyMatch(line -> line.contains("Struck")), "and things met in it");
    }

    private List<String> traced() {
        final World world = rolled(melon, 0, 15, 200, Map.of("someone", standing))
                .stood(other, new Placed(new Spot(0, 14, 200)));
        final List<String> trace = new ArrayList<>();
        for (int tick = 0; tick < 600; tick++) {
            trace.add(world.ticked(there(Map.of("someone", standing)), TICK) + " " + world.placed("melon") + " "
                    + world.moved("melon") + " " + world.placed("other") + " " + world.moved("other"));
        }
        return trace;
    }
}
