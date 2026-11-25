package melons.innocent.doors.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.interaction.Action;
import melons.innocent.core.interaction.Use;
import melons.innocent.doors.swing.Design;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The walk's four doors, with no card: each of another design in a hole the wall leaves it; the door at hand opened
 * with T from the front and shut again from the back; its leaf in the way where it is at the moment; and a leaf
 * waiting for whoever stands where it would swing.
 */
class DoorsTest {

    private static final Vec3 FAR = new Vec3(0, 0, -400);
    private final Showing showing = new Showing();
    private final Doors doors = new Doors(showing.doorways());
    private final DoorHands hands = new DoorHands(doors);

    private void stepped(final double across, final double along, final double yaw, final String... keys) {
        hands.stepped(new Step(Set.of(), List.of(keys), 0, 0, false, 0, 0, false), new Vec3(across, 160, along), yaw);
    }

    private void ticked(final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            doors.ticked(FAR);
        }
    }

    @Test
    void theWallHoldsFourDoorsOfDifferentDesignsEachInTheHoleItAsksFor() {
        assertEquals(List.of("the glazed door", "the panelled door", "the glass door", "the flush door"),
                doors.doorways().stream().map(Doors.Doorway::name).toList());
        assertInstanceOf(Design.Glazed.class, doors.doorways().get(0).stood().door().design());
        assertInstanceOf(Design.Solid.class, doors.doorways().get(1).stood().door().design());
        assertInstanceOf(Design.Frameless.class, doors.doorways().get(2).stood().door().design());
        assertTrue(((Design.Solid) doors.doorways().get(3).stood().door().design()).flush());
        // nine runs: five from the ground up, and four over the doors
        assertEquals(9, showing.walls().size());
        assertEquals(4, showing.walls().stream().filter(run -> run.low() > 200).count());
        final double wide = showing.walls().get(1).length();
        assertEquals(doors.doorways().getFirst().stood().door().holeWide(), wide, 1e-9);
        // every frame lies within the wall's depth, whichever way its door opens
        for (final Doors.Doorway doorway : doors.doorways()) {
            for (final Block block : doorway.stood().inTheWay(doors.swung(0)).stream().skip(1).toList()) {
                assertEquals(506, block.z(), 1e-9, doorway.name());
            }
        }
    }

    @Test
    void theDoorAtHandIsOpenedWithTAndShutAgainFromItsOtherSide() {
        stepped(-450, 400, 0);
        assertEquals(Optional.of("the glazed door"), hands.atHand());
        assertEquals(List.of("the glazed door", "[T] Open or shut"), hands.hints());
        final Block shut = doors.inTheWay().getFirst();
        assertEquals(-450, shut.x(), 1e-9);
        stepped(-450, 400, 0, "T");
        assertEquals(Optional.of(new Use("the glazed door", Action.OPEN)), hands.last());
        assertTrue(doors.open(0));
        ticked(240);
        assertEquals(95, doors.swung(0).degrees(), 0, "as wide as it opens");
        assertEquals("the glazed door: open, 95 degrees", doors.said(0));
        final Block open = doors.inTheWay().getFirst();
        assertTrue(open.x() < -490 && open.z() > 540, "its leaf swung back out of the doorway: " + open);
        assertTrue(doors.changes() > 100, "what is in the way changed as it swung");
        // through it, turned round, and shut from behind
        stepped(-430, 640, 200);
        assertEquals(Optional.of("the glazed door"), hands.atHand());
        stepped(-430, 640, 200, "T");
        ticked(240);
        assertEquals(0, doors.swung(0).degrees(), 0);
        assertEquals("the glazed door: shut, 0 degrees", doors.said(0));
        assertFalse(doors.open(0));
        assertEquals(0, doors.swung(1).degrees(), 0, "no other door stirred");
    }

    @Test
    void aLeafWaitsForWhoeverStandsWhereItWouldSwingAndGoesOnOnceTheyHaveGone() {
        // the panelled door is pulled toward its front: someone close before it is in its way
        stepped(-150, 440, 0, "T");
        for (int tick = 0; tick < 240; tick++) {
            stepped(-150, 440, 0);
        }
        final double waiting = doors.swung(1).degrees();
        assertTrue(waiting > 5 && waiting < 70, "part open, it waits: " + waiting);
        assertEquals("the panelled door: waiting, " + Math.round(waiting) + " degrees", doors.said(1));
        for (int tick = 0; tick < 240; tick++) {
            stepped(-150, 300, 0);
        }
        assertEquals(95, doors.swung(1).degrees(), 0, "stepped back, it opens the rest of the way");
    }

    @Test
    void nothingIsAtHandFarFromEveryDoorAndTDoesNothingThere() {
        stepped(0, 0, 0, "T");
        assertEquals(Optional.empty(), hands.atHand());
        assertEquals(List.of(), hands.hints());
        assertEquals(Optional.empty(), hands.last());
        ticked(10);
        assertEquals(0, doors.changes());
    }
}
