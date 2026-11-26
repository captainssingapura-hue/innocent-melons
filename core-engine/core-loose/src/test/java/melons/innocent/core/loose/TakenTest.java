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
 * Things taken up out of the world and stood in it again. A thing taken up is nowhere in the world, and what stood
 * on it is let go in the same tick, as when a support is destroyed. Stood again a little over the ground, it comes
 * down and rests there. Stood again and thrown in the one tick, it leaves from where it was stood with the throw's
 * motion and whatever motion it is given besides, as a thing thrown from the hands of someone walking is.
 */
class TakenTest {

    private static final double TICK = 1.0 / 120;
    private static final String GROUND = new Ground().name();
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    private final Rigid melon = new Rigid("melon", new Ellipsoid(36, 28, 26), 960);

    private Asked taking(final String name) {
        return new Asked(List.of(), List.of(name), Map.of(), Map.of());
    }

    private void settled(final World world) {
        for (int tick = 0; tick < 600 && world.moving() > 0; tick++) {
            world.ticked(List.of(), TICK);
        }
        assertEquals(0, world.moving(), "everything at rest again");
    }

    @Test
    void aThingTakenUpIsOutOfTheWorldAndWhatStoodOnItIsLetGo() {
        final World world = new World(new Ground(), new Loose())
                .stood(crate, new Placed(new Spot(0, 25, 0)))
                .stood(melon, new Placed(new Spot(0, 64, 0)));
        assertTrue(world.ticked(List.of(), TICK).isEmpty());
        assertEquals(Set.of("crate"), world.under("melon"));
        assertEquals(List.of(new Happened.Taken("crate"), new Happened.Unsettled("melon")),
                world.ticked(taking("crate"), TICK));
        assertEquals(Set.of("melon"), world.things());
        assertTrue(world.placed("crate").isEmpty());
        settled(world);
        assertEquals(14, world.placed("melon").orElseThrow().at().up(), 0.05, "the melon down on the ground");
    }

    @Test
    void aThingTakenUpAndStoodAgainALittleOverTheGroundComesDownAndRests() {
        final World world = new World(new Ground(), new Loose()).stood(crate, new Placed(new Spot(0, 25, 0)));
        world.ticked(List.of(), TICK);
        world.ticked(taking("crate"), TICK);
        assertFalse(world.things().contains("crate"));
        final long checked = world.checks();
        assertTrue(world.ticked(List.of(), TICK).isEmpty());
        assertEquals(checked, world.checks(), "nothing to check while it is in the hands");
        world.stood(crate, new Placed(new Spot(300, 27, 100)));
        assertEquals(List.of(new Happened.Unsettled("crate")), world.ticked(List.of(), TICK));
        settled(world);
        final Spot down = world.placed("crate").orElseThrow().at();
        assertEquals(25, down.up(), 0.05);
        assertEquals(300, down.across(), 1e-9);
        assertEquals(Set.of(GROUND), world.under("crate"));
    }

    @Test
    void aThingStoodAgainAndThrownInTheOneTickLeavesWithTheThrowAndTheWayItWasCarried() {
        final World world = new World(new Ground(), new Loose()).stood(melon, new Placed(new Spot(0, 14, 0)));
        world.ticked(List.of(), TICK);
        world.ticked(taking("melon"), TICK);
        final Throw thrown = new Throw(300, 0, 30);
        final Spot walking = new Spot(150, 0, 0);
        final Moved leaving = new Moved(thrown.of(melon).velocity().plus(walking), new Spot(0, 0, 0));
        world.stood(melon, new Placed(new Spot(0, 110, 40)));
        assertEquals(List.of(new Happened.Thrown("melon")),
                world.ticked(new Asked(List.of(), Map.of("melon", leaving), Map.of()), TICK));
        // a tick on it has gone as it left, less a tick of gravity's pull
        final Spot going = world.moved("melon").velocity();
        assertEquals(150, going.across(), 1e-9);
        assertEquals(leaving.velocity().along(), going.along(), 1e-9);
        assertEquals(leaving.velocity().up() - 981 * TICK, going.up(), 1e-6);
        int ticks = 0;
        while (world.placed("melon").orElseThrow().at().up() > 14.5 && ticks++ < 2_000) {
            world.ticked(List.of(), TICK);
        }
        final Spot down = world.placed("melon").orElseThrow().at();
        // as far aside as it was carried while it flew, and well out the way it was thrown
        assertEquals(150 * (ticks + 1) * TICK, down.across(), 3);
        assertTrue(down.along() > 400, "thrown ahead: " + down.along());
    }

    @Test
    void whatIsNotThereIsNotTakenUp() {
        final World world = new World(new Ground(), new Loose()).stood(crate, new Placed(new Spot(0, 25, 0)));
        assertThrows(IllegalArgumentException.class, () -> world.ticked(taking("melon"), TICK));
    }
}
