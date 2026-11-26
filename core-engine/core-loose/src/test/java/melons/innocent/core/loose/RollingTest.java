package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Reckoning;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.World;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Things that roll. A ball let go on a slope gains speed at five sevenths of what a thing sliding freely would, and
 * turns just as fast as it goes, so it does not slip. A watermelon lying on level ground stays there, and a crate
 * lying on the slope stays there. A watermelon held on the slope by a chock is at rest until the chock is taken
 * away; then it rolls down the slope, on across the ground, slows, and comes to rest lying on the ground far from
 * where it began. A ball that rolls into a crate at the foot of the slope stops against it. And a run made twice is
 * the same run.
 */
class RollingTest {

    private static final double TICK = 1.0 / 120;
    private static final double TILT = Math.toRadians(15);
    private static final String GROUND = new Ground().name();
    private final Turn tilted = new Turn(new Spot(1, 0, 0), -TILT);
    /** The slab's top's middle: so high that the lower edge of its top is on the ground. */
    private final Spot top = new Spot(0, 250 * Math.sin(TILT), 0);

    /** A world with a slab five metres along tilted so its far end is the higher, its top's lower edge on the ground. */
    private World ramped(final Loose loose) {
        return new World(new Ground(), loose).stood(new Rigid("ramp", new Slab(300, 20, 500), 2_400),
                new Placed(top.minus(tilted.turned(new Spot(0, 10, 0))), tilted));
    }

    /** Where a thing stands whose middle is so far over the slab's top, so far across it and up the slope. */
    private Placed onTheRamp(final double across, final double over, final double upSlope) {
        return new Placed(top.plus(tilted.turned(new Spot(across, over, upSlope))), tilted);
    }

    private List<Happened> run(final World world, final int ticks, final List<String> destroyedFirst) {
        final List<Happened> all = new ArrayList<>(world.ticked(destroyedFirst, TICK));
        for (int tick = 1; tick < ticks; tick++) {
            all.addAll(world.ticked(List.of(), TICK));
        }
        return all;
    }

    @Test
    void aBallOnASlopeGainsSpeedAtFiveSeventhsOfASlidingThingsAndDoesNotSlip() {
        final World world = ramped(new Loose(new Reckoning(981, 0.01, 0), 0.01))
                .stood(new Rigid("ball", new Ellipsoid(40), 1_100), onTheRamp(0, 20, 200));
        assertEquals(List.of(new Happened.Unsettled("ball")), world.ticked(List.of(), TICK));
        run(world, 59, List.of());
        final Moved moved = world.moved("ball");
        final double gaining = 5.0 / 7 * 981 * Math.sin(TILT);
        assertEquals(gaining * 60 * TICK, moved.velocity().length(), gaining * 60 * TICK * 0.005,
                "half a second in, to within half a hundredth");
        assertEquals(moved.velocity().length(), moved.spin().length() * 20, moved.velocity().length() * 0.005,
                "turning as fast as it goes: rolling, not slipping");
        assertTrue(moved.velocity().along() < 0 && moved.velocity().up() < 0, "down the slope");
        assertEquals(0, moved.velocity().across(), 1e-9);
        final Spot from = onTheRamp(0, 20, 200).at();
        assertEquals(0.5 * gaining * 0.25, world.placed("ball").orElseThrow().at().minus(from).length(), 1.5,
                "having come as far as that speed takes it");
    }

    @Test
    void aMelonOnLevelGroundStaysAndACrateLyingOnTheSlopeStays() {
        final World world = ramped(new Loose())
                .stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960), new Placed(new Spot(600, 14, 0)))
                .stood(new Rigid("crate", new Box(50, 50, 50), 600), onTheRamp(0, 25, 100));
        assertTrue(run(world, 360, List.of()).isEmpty(), "three seconds, and nothing happens");
        assertEquals(Set.of(GROUND), world.under("melon"));
        assertEquals(Set.of("ramp"), world.under("crate"));
    }

    private World chocked() {
        return ramped(new Loose())
                .stood(new Rigid("chock", new Box(280, 34, 10), 600), onTheRamp(0, 17, 150))
                .stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960), onTheRamp(-60, 14, 168));
    }

    @Test
    void aMelonHeldByAChockRollsDownWhenItIsTakenAwayAndComesToRestOnTheGround() {
        final World world = chocked();
        assertTrue(run(world, 120, List.of()).isEmpty(), "held by the chock, it is at rest");
        assertEquals(Set.of("chock", "ramp"), world.under("melon"));
        final List<Happened> happened = run(world, 120, List.of("chock"));
        assertEquals(List.of(new Happened.Destroyed("chock"), new Happened.Unsettled("melon")),
                happened.subList(0, 2));
        assertTrue(world.moved("melon").velocity().along() < -100, "a second on, it is rolling down the slope");
        final List<Happened> after = run(world, 3_000, List.of());
        assertEquals(new Happened.Rested("melon", Set.of(GROUND)), after.getLast());
        final Placed lies = world.placed("melon").orElseThrow();
        assertTrue(lies.at().along() < -250 * Math.cos(TILT) - 200,
                "well out past the foot of the slope: " + lies.at().along());
        assertTrue(lies.at().up() > 12.9 && lies.at().up() < 18.1, "lying on the ground: " + lies.at().up());
        assertEquals(new Moved(), world.moved("melon"));
        assertEquals(0, world.moving());
    }

    @Test
    void aBallThatRollsIntoACrateAtTheFootOfTheSlopeStopsAgainstIt() {
        final double face = -250 * Math.cos(TILT) - 150;
        final World world = ramped(new Loose())
                .stood(new Rigid("crate", new Box(60, 60, 60), 600), new Placed(new Spot(0, 30, face - 30)))
                .stood(new Rigid("ball", new Ellipsoid(40), 1_100), onTheRamp(0, 20, 100));
        final List<Happened> happened = run(world, 1_200, List.of());
        assertEquals(new Happened.Unsettled("ball"), happened.getFirst());
        assertTrue(world.resting("ball"), "at rest in the end");
        assertEquals(face + 20, world.placed("ball").orElseThrow().at().along(), 0.5, "against the crate's face");
        assertEquals(20, world.placed("ball").orElseThrow().at().up(), 0.05);
        assertTrue(world.resting("crate"), "which has not stirred");
        assertEquals(face - 30, world.placed("crate").orElseThrow().at().along(), 0);
    }

    @Test
    void aRunMadeTwiceIsTheSameRunTickForTick() {
        final World one = chocked();
        final World other = chocked();
        for (int tick = 0; tick < 900; tick++) {
            final List<String> destroyed = tick == 30 ? List.of("chock") : List.of();
            assertEquals(one.ticked(destroyed, TICK), other.ticked(destroyed, TICK));
            assertEquals(one.placed("melon"), other.placed("melon"), "at tick " + tick);
            assertEquals(one.moved("melon"), other.moved("melon"));
        }
    }
}
