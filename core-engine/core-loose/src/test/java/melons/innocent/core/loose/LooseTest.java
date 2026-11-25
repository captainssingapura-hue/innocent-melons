package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.World;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The loose body and the check together, in the three scenes the paper names. A crate described a metre in the air
 * is let go at the first tick, is a tick later where its new velocity has taken it, falls as things fall, lands on
 * the ground and comes to rest there. A stack on a pillar comes down when the pillar is destroyed, all let go in the
 * one tick, landing the lowest first, and stands at rest again as a stack on the ground. A beam on two pillars tips
 * about the one left when the other is destroyed, and comes to rest leaning on it with its far end on the ground;
 * that one destroyed too, it comes down flat. A run made twice is the same run, tick for tick. And once all is at
 * rest again, ticks check nothing.
 */
class LooseTest {

    private static final double TICK = 1.0 / 120;
    private static final String GROUND = new Ground().name();

    private List<Happened> run(final World world, final int ticks, final List<String> destroyedFirst) {
        final List<Happened> all = new ArrayList<>(world.ticked(destroyedFirst, TICK));
        for (int tick = 1; tick < ticks; tick++) {
            all.addAll(world.ticked(List.of(), TICK));
        }
        return all;
    }

    private double lowest(final World world, final String name) {
        return world.placed(name).orElseThrow().corners((Box) world.thing(name).shape()).stream()
                .mapToDouble(Spot::up).min().orElseThrow();
    }

    @Test
    void aCrateDescribedInTheAirFallsAndComesToRestOnTheGround() {
        final World world = new World(new Ground(), new Loose())
                .stood(new Rigid("crate", new Box(100, 100, 100), 600), new Placed(new Spot(0, 150, 0)));
        assertEquals(List.of(new Happened.Unsettled("crate")), world.ticked(List.of(), TICK));
        assertEquals(-981 * TICK, world.moved("crate").velocity().up(), 1e-12, "its velocity, changed for the tick");
        assertEquals(150 - 981 * TICK * TICK / 2, world.placed("crate").orElseThrow().at().up(), 1e-12,
                "and where gravity's pull has taken it in that tick");
        final List<Happened> fell = run(world, 52, List.of());
        assertTrue(fell.isEmpty(), "for 0.44 of a second it only falls: " + fell);
        assertTrue(world.moved("crate").velocity().up() < -430, "at over four metres a second by then");
        final List<Happened> came = run(world, 40, List.of());
        assertEquals(List.of(new Happened.Landed("crate", GROUND), new Happened.Rested("crate", Set.of(GROUND))),
                came);
        assertTrue(world.resting("crate"));
        assertEquals(50, world.placed("crate").orElseThrow().at().up(), 0.02, "standing on the ground");
        assertEquals(Set.of(GROUND), world.under("crate"));
        final long checked = world.checks();
        run(world, 600, List.of());
        assertEquals(checked, world.checks(), "at rest again, nothing is checked");
    }

    private World stacked() {
        return new World(new Ground(), new Loose())
                .stood(new Rigid("pillar", new Box(40, 100, 40), 2_400), new Placed(new Spot(0, 50, 0)))
                .stood(new Rigid("first", new Box(60, 60, 60), 600), new Placed(new Spot(0, 130, 0)))
                .stood(new Rigid("second", new Box(60, 60, 60), 600), new Placed(new Spot(0, 190, 0)))
                .stood(new Rigid("third", new Box(60, 60, 60), 600), new Placed(new Spot(0, 250, 0)));
    }

    @Test
    void aStackOnAPillarComesDownWhenThePillarIsDestroyedAndStandsAsAStackOnTheGround() {
        final World world = stacked();
        assertTrue(run(world, 30, List.of()).isEmpty(), "as described, it all stands");
        final List<Happened> happened = run(world, 240, List.of("pillar"));
        assertEquals(List.of(new Happened.Destroyed("pillar"), new Happened.Unsettled("first"),
                new Happened.Unsettled("second"), new Happened.Unsettled("third")), happened.subList(0, 4),
                "all let go in the tick the pillar goes");
        assertEquals(List.of(new Happened.Landed("first", GROUND), new Happened.Landed("second", "first"),
                new Happened.Landed("third", "second")),
                happened.stream().filter(one -> one instanceof Happened.Landed).toList(), "landing the lowest first");
        assertEquals(0, world.moving(), "all of it at rest again");
        assertEquals(30, world.placed("first").orElseThrow().at().up(), 0.02);
        assertEquals(90, world.placed("second").orElseThrow().at().up(), 0.04);
        assertEquals(150, world.placed("third").orElseThrow().at().up(), 0.06);
        assertEquals(Set.of(GROUND), world.under("first"));
        assertEquals(Set.of("first"), world.under("second"));
        assertEquals(Set.of("second"), world.under("third"));
        assertEquals(0, world.placed("third").orElseThrow().at().across(), 1e-9, "having come straight down");
    }

    private World bridged() {
        return new World(new Ground(), new Loose())
                .stood(new Rigid("left", new Box(40, 100, 40), 2_400), new Placed(new Spot(-180, 50, 0)))
                .stood(new Rigid("right", new Box(40, 100, 40), 2_400), new Placed(new Spot(180, 50, 0)))
                .stood(new Rigid("beam", new Box(400, 20, 40), 7_850), new Placed(new Spot(0, 110, 0)));
    }

    @Test
    void aBeamOnTwoPillarsTipsAboutTheOneLeftAndLeansOnItWithItsFarEndOnTheGround() {
        final World world = bridged();
        assertTrue(run(world, 30, List.of()).isEmpty());
        assertEquals(Set.of("left", "right"), world.under("beam"));
        final List<Happened> happened = run(world, 240, List.of("right"));
        assertEquals(List.of(new Happened.Destroyed("right"), new Happened.Unsettled("beam")),
                happened.subList(0, 2));
        assertTrue(happened.contains(new Happened.Landed("beam", GROUND)), "its far end comes down on the ground");
        assertEquals(new Happened.Rested("beam", Set.of("left", GROUND)), happened.getLast());
        assertEquals(0, lowest(world, "beam"), 0.02);
        final Placed leaning = world.placed("beam").orElseThrow();
        // turned about the pillar's edge until its end, 360 cm from that edge, has come down the pillar's 100 cm
        final double leans = Math.asin(100.0 / 360);
        assertEquals(Math.cos(leans), leaning.turned().turned(new Spot(1, 0, 0)).across(), 1e-4);
        assertEquals(-Math.sin(leans), leaning.turned().turned(new Spot(1, 0, 0)).up(), 1e-4, "its far end the lower");
        assertEquals(0, leaning.at().along(), 1e-9, "and it has not gone sideways");
        assertTrue(world.resting("left"), "the pillar it leans on has not stirred");
    }

    @Test
    void theOtherPillarDestroyedTooTheBeamComesDownFlat() {
        final World world = bridged();
        run(world, 240, List.of("right"));
        final List<Happened> happened = run(world, 360, List.of("left"));
        assertEquals(List.of(new Happened.Destroyed("left"), new Happened.Unsettled("beam")),
                happened.subList(0, 2));
        assertEquals(new Happened.Rested("beam", Set.of(GROUND)), happened.getLast());
        assertEquals(10, world.placed("beam").orElseThrow().at().up(), 0.05, "lying on the ground");
        assertEquals(1, Math.abs(world.placed("beam").orElseThrow().turned().turned(new Spot(1, 0, 0)).across()),
                1e-4, "and level");
    }

    @Test
    void whicheverPillarGoesFirstAndWheneverTheOtherGoesTheBeamEndsFlatOnTheGround() {
        final List<String> astray = new ArrayList<>();
        for (final String first : List.of("left", "right")) {
            final String second = first.equals("left") ? "right" : "left";
            for (int between = 0; between <= 150; between++) {
                final World world = bridged();
                run(world, 5, List.of());
                run(world, Math.max(1, between), between == 0 ? List.of(first, second) : List.of(first));
                run(world, 600, between == 0 ? List.of() : List.of(second));
                final Placed ended = world.placed("beam").orElseThrow();
                if (!world.resting("beam") || Math.abs(ended.at().up() - 10) > 0.1) {
                    astray.add(first + " first, the other " + between + " ticks after: at rest "
                            + world.resting("beam") + ", its middle " + ended.at().up() + " up");
                }
            }
        }
        assertEquals(List.of(), astray, "the beam lies flat on the ground whatever the order and the timing");
    }

    @Test
    void aRunMadeTwiceIsTheSameRunTickForTick() {
        final World one = bridged();
        final World other = bridged();
        for (int tick = 0; tick < 400; tick++) {
            final List<String> destroyed = tick == 20 ? List.of("right") : tick == 200 ? List.of("left") : List.of();
            assertEquals(one.ticked(destroyed, TICK), other.ticked(destroyed, TICK));
            assertEquals(one.placed("beam"), other.placed("beam"), "at tick " + tick);
            assertEquals(one.moved("beam"), other.moved("beam"));
        }
        assertFalse(one.resting("right") || one.things().contains("left"));
    }
}
