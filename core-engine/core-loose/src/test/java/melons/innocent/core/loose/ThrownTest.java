package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.Throw;
import melons.innocent.core.rest.World;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Things thrown. A watermelon thrown from the ground flies the curve its energy and its angle give: as long in the
 * air, as high and as far as the reckoning without air says. Four times the energy carries it four times as far,
 * and thirty degrees up as far as sixty, less than forty-five. A crate, a watermelon and a ball leaving at one speed
 * fly one curve; given one energy the heavier goes less far, by as much as it is heavier. A crate stays where it
 * comes down, and a watermelon rolls on. A ball thrown at a wall stops against it and drops to the ground. A ball
 * thrown down fast lands on a thin slab and does not pass through it. What stood on a thing thrown is let go; what
 * never moves is not thrown; and a run made twice is the same run.
 */
class ThrownTest {

    private static final double TICK = 1.0 / 120;
    private static final double GRAVITY = 981;
    private static final String GROUND = new Ground().name();
    private final Rigid melon = new Rigid("melon", new Ellipsoid(36, 28, 26), 960);
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    private final Rigid ball = new Rigid("ball", new Ellipsoid(30), 1_100);

    /** How a thing flew: how many ticks it was in the air, how high its middle rose, and where it came down. */
    private record Flew(int ticks, double rose, Spot down) {
    }

    /** A world with {@code rigid} at rest on the ground, its middle so high, a tick in. */
    private World with(final Rigid rigid, final double high) {
        final World world = new World(new Ground(), new Loose()).stood(rigid, new Placed(new Spot(0, high, 0)));
        assertTrue(world.ticked(List.of(), TICK).isEmpty());
        return world;
    }

    /** {@code rigid} thrown as {@code thrown} says, and followed until it comes down. */
    private Flew thrown(final World world, final Rigid rigid, final Throw thrown) {
        final double from = world.placed(rigid.name()).orElseThrow().at().up();
        assertEquals(List.of(new Happened.Thrown(rigid.name())),
                world.ticked(List.of(), Map.of(rigid.name(), thrown.of(rigid)), TICK));
        double highest = from;
        for (int ticks = 2; ticks < 6_000; ticks++) {
            final List<Happened> happened = world.ticked(List.of(), TICK);
            highest = Math.max(highest, world.placed(rigid.name()).orElseThrow().at().up());
            if (happened.stream().anyMatch(one -> one instanceof Happened.Landed)) {
                return new Flew(ticks, highest - from, world.placed(rigid.name()).orElseThrow().at());
            }
        }
        throw new AssertionError(rigid.name() + " never came down");
    }

    @Test
    void aThrownMelonFliesTheCurveItsEnergyAndAngleGive() {
        final Throw thrown = new Throw(500, 0, 45);
        final double speed = thrown.speed(melon);
        assertEquals(100 * Math.sqrt(1_000 / melon.kilograms()), speed, 1e-9, "what 500 joules give its mass");
        final Flew flew = thrown(with(melon, 14), melon, thrown);
        final double rise = speed * Math.sin(Math.toRadians(45));
        assertEquals(2 * rise / GRAVITY, flew.ticks() * TICK, 2 * TICK, "as long in the air");
        assertEquals(rise * rise / (2 * GRAVITY), flew.rose(), 0.05, "as high, to half a millimetre");
        assertEquals(speed * speed / GRAVITY, flew.down().along(), 0.015 * speed * speed / GRAVITY, "as far");
        assertEquals(0, flew.down().across(), 1e-9, "and straight ahead");
    }

    @Test
    void fourTimesTheEnergyCarriesItFourTimesAsFarAndThirtyDegreesAsFarAsSixty() {
        final double little = thrown(with(melon, 14), melon, new Throw(200, 0, 45)).down().along();
        final double more = thrown(with(melon, 14), melon, new Throw(800, 0, 45)).down().along();
        final double most = thrown(with(melon, 14), melon, new Throw(3_200, 0, 45)).down().along();
        assertEquals(4, more / little, 0.12);
        assertEquals(4, most / more, 0.12);
        final double low = thrown(with(melon, 14), melon, new Throw(800, 0, 30)).down().along();
        final double steep = thrown(with(melon, 14), melon, new Throw(800, 0, 60)).down().along();
        assertEquals(low, steep, 0.03 * low);
        assertTrue(more > low && more > steep, "forty-five degrees up goes the furthest");
        final Spot aside = thrown(with(melon, 14), melon, new Throw(800, 90, 45)).down();
        assertEquals(more, aside.across(), 0.02 * more, "thrown to the right, it goes as far to the right");
        assertEquals(0, aside.along(), 1e-6);
    }

    @Test
    void aCrateAMelonAndABallLeavingAtOneSpeedFlyOneCurveAndTheHeavierGoesLessFarOnOneEnergy() {
        final Flew boxed = thrown(with(crate, 25), crate, new Throw(0.5 * crate.kilograms() * 64, 0, 50));
        final Flew round = thrown(with(melon, 14), melon, new Throw(0.5 * melon.kilograms() * 64, 0, 50));
        final Flew rubber = thrown(with(ball, 15), ball, new Throw(0.5 * ball.kilograms() * 64, 0, 50));
        assertEquals(boxed.rose(), round.rose(), 1e-6, "as high, a crate and a melon");
        assertEquals(boxed.rose(), rubber.rose(), 1e-6);
        assertEquals(boxed.ticks(), round.ticks(), 1);
        assertEquals(boxed.down().along(), round.down().along(), 0.02 * boxed.down().along(), "and as far");
        assertEquals(boxed.down().along(), rubber.down().along(), 0.02 * boxed.down().along());
        final double far = thrown(with(melon, 14), melon, new Throw(1_000, 0, 45)).down().along();
        final double near = thrown(with(crate, 25), crate, new Throw(1_000, 0, 45)).down().along();
        assertEquals(crate.kilograms() / melon.kilograms(), far / near, 0.06 * far / near,
                "on one energy the crate goes less far, by as much as it is heavier");
    }

    @Test
    void aCrateStaysWhereItComesDownAndAMelonRollsOn() {
        final World boxes = with(crate, 25);
        final Spot down = thrown(boxes, crate, new Throw(3_000, 0, 40)).down();
        for (int tick = 0; tick < 240; tick++) {
            boxes.ticked(List.of(), TICK);
        }
        assertTrue(boxes.resting("crate"));
        assertEquals(down.along(), boxes.placed("crate").orElseThrow().at().along(), 0.5);
        assertEquals(25, boxes.placed("crate").orElseThrow().at().up(), 0.05);
        final World rounds = with(melon, 14);
        final Spot landed = thrown(rounds, melon, new Throw(500, 0, 40)).down();
        for (int tick = 0; tick < 3_000; tick++) {
            rounds.ticked(List.of(), TICK);
        }
        assertTrue(rounds.resting("melon"));
        assertTrue(rounds.placed("melon").orElseThrow().at().along() > landed.along() + 100,
                "the melon has rolled on from where it came down");
        assertEquals(Set.of(GROUND), rounds.under("melon"));
    }

    @Test
    void aBallThrownAtAWallStopsAgainstItAndDropsToTheGround() {
        final World world = new World(new Ground(), new Loose())
                .stood(new Rigid("wall", new Box(200, 300, 40), 2_400), new Placed(new Spot(0, 150, 300)))
                .stood(ball, new Placed(new Spot(0, 15, 0)));
        world.ticked(List.of(), TICK);
        world.ticked(List.of(), Map.of("ball", new Throw(0.5 * ball.kilograms() * 36, 0, 30).of(ball)), TICK);
        double furthest = 0;
        for (int tick = 0; tick < 1_800; tick++) {
            world.ticked(List.of(), TICK);
            furthest = Math.max(furthest, world.placed("ball").orElseThrow().at().along());
        }
        assertEquals(265, furthest, 0.6, "its middle came no nearer the wall's face than it is round");
        assertTrue(world.resting("ball"), "and it has not hung there: it is at rest");
        assertEquals(15, world.placed("ball").orElseThrow().at().up(), 0.05, "on the ground");
        assertTrue(world.resting("wall"));
    }

    @Test
    void aBallThrownDownFastLandsOnAThinSlabAndWhatStoodOnAThrownThingIsLetGo() {
        final World world = new World(new Ground(), new Loose())
                .stood(new Rigid("shelf", new Slab(300, 10, 300), 2_400), new Placed(new Spot(0, 200, 0)))
                .stood(ball, new Placed(new Spot(0, 613, 0)));
        final List<Happened> first = world.ticked(List.of(),
                Map.of("ball", new Throw(0.5 * ball.kilograms() * 3_600, 0, -90).of(ball)), TICK);
        assertEquals(List.of(new Happened.Thrown("ball")), first, "sixty metres a second, straight down");
        boolean landed = false;
        for (int tick = 0; tick < 240; tick++) {
            landed |= world.ticked(List.of(), TICK).contains(new Happened.Landed("ball", "shelf"));
        }
        assertTrue(landed, "it came down on the shelf");
        assertEquals(220, world.placed("ball").orElseThrow().at().up(), 0.05, "and lies on it");
        final World stack = new World(new Ground(), new Loose())
                .stood(crate, new Placed(new Spot(0, 25, 0)))
                .stood(new Rigid("upper", new Box(50, 50, 50), 600), new Placed(new Spot(0, 75, 0)));
        stack.ticked(List.of(), TICK);
        assertEquals(List.of(new Happened.Thrown("crate"), new Happened.Unsettled("upper")),
                stack.ticked(List.of(), Map.of("crate", new Throw(4_000, 0, 60).of(crate)), TICK));
        assertThrows(IllegalArgumentException.class, () -> world.ticked(List.of(),
                Map.of("shelf", new Throw(10, 0, 45).of(ball)), TICK), "what never moves is not thrown");
        assertThrows(IllegalArgumentException.class, () -> world.ticked(List.of(),
                Map.of("nobody", new Throw(10, 0, 45).of(ball)), TICK));
        assertThrows(IllegalArgumentException.class, () -> new Throw(-1, 0, 45));
    }

    @Test
    void aRunMadeTwiceIsTheSameRunTickForTick() {
        final World one = with(melon, 14);
        final World other = with(melon, 14);
        for (int tick = 0; tick < 900; tick++) {
            final Map<String, melons.innocent.core.rest.Moved> thrown = tick == 10
                    ? Map.of("melon", new Throw(2_500, 30, 55).of(melon)) : Map.of();
            assertEquals(one.ticked(List.of(), thrown, TICK), other.ticked(List.of(), thrown, TICK));
            assertEquals(one.placed("melon"), other.placed("melon"), "at tick " + tick);
            assertEquals(one.moved("melon"), other.moved("melon"));
        }
    }
}
