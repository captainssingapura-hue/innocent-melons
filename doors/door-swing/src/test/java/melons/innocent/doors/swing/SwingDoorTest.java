package melons.innocent.doors.swing;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.mechanism.Swung;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A swing door from its typed description: what its parts are made of for each design, that a leaf's parts lie
 * within the leaf, which way each hanging swings, the hole a wall leaves for it, what of it is in the way, and when
 * someone stands where its leaf is about to swing.
 */
class SwingDoorTest {

    private final Size size = new Size(90, 210, 4.4);
    private final SwingDoor<Design.Glazed> glazed = new SwingDoor<>(
            new Design.Glazed(new Wood.Varnished(), Fittings.BRONZE, 2, 3, 0.3), size,
            new Swing(Hung.LEFT, Opens.AWAY));
    private final SwingDoor<Design.Solid> solid = new SwingDoor<>(
            new Design.Solid(new Wood.Painted(0x1f4a37), Fittings.STEEL, 2, 3), size,
            new Swing(Hung.RIGHT, Opens.TOWARD));
    private final SwingDoor<Design.Frameless> glass = new SwingDoor<>(new Design.Frameless(Fittings.STEEL),
            new Size(95, 215, 1.2), new Swing(Hung.LEFT, Opens.AWAY, 90));

    private List<String> made(final SwingDoor<?> door) {
        return door.parts().stream().map(part -> part.moves() + " " + part.of().name()).sorted().toList();
    }

    @Test
    void eachDesignIsMadeOfWhatItSaysAndOnlyAFramedDoorHasFixedParts() {
        assertEquals(List.of("FIXED bronze", "FIXED timber", "LEAF bronze", "LEAF glass", "LEAF timber"), made(glazed));
        assertEquals(List.of("FIXED gloss-paint", "FIXED steel", "LEAF gloss-paint", "LEAF steel"), made(solid));
        assertEquals(List.of("LEAF glass", "LEAF steel"), made(glass));
        final DoorPart painted = solid.parts().stream()
                .filter(part -> part.of().equals(new Materials.GlossPaint())).findFirst().orElseThrow();
        assertEquals(0x1f / 255.0, painted.tint().x(), 1e-12, "told the paint's colour");
        assertEquals(new Vec3(1, 1, 1), glazed.parts().getFirst().tint());
        assertTrue(new Design.Solid(new Wood.Varnished(), Fittings.STEEL, 0, 0).flush());
        assertEquals(List.of("FIXED steel", "FIXED timber", "LEAF steel", "LEAF timber"), made(new SwingDoor<>(
                new Design.Solid(new Wood.Varnished(), Fittings.STEEL, 0, 0), size, new Swing(Hung.LEFT, Opens.AWAY))));
    }

    @Test
    void aLeafsPartsLieWithinTheLeafAndItsHandleJustProudOfItsFaces() {
        for (final SwingDoor<?> door : List.of(glazed, solid, glass)) {
            for (final DoorPart part : door.parts()) {
                if (part.moves() == Moves.LEAF) {
                    for (final Vec3 point : part.mesh().points()) {
                        assertTrue(Math.abs(point.x()) <= door.size().wide() / 2 + 1e-9, part.name());
                        assertTrue(point.y() >= 0.8 - 1e-9 && point.y() <= 0.8 + door.size().high() + 1e-9);
                        assertTrue(Math.abs(point.z()) <= door.size().thick() / 2 + 6.2 + 1e-9, part.name());
                    }
                }
            }
        }
    }

    @Test
    void theLeafSwingsToTheSideItOpensTowardAboutTheEdgeItIsHungOn() {
        for (final Hung hung : Hung.values()) {
            for (final Opens opens : Opens.values()) {
                final SwingDoor<Design.Frameless> door = new SwingDoor<>(new Design.Frameless(Fittings.STEEL),
                        new Size(100, 200, 2), new Swing(hung, opens, 90));
                final Stood stood = door.stood(new Vec3(0, 0, 0), 0);
                final Vec3 shut = stood.reached(new Swung());
                assertEquals(0, shut.x(), 1e-9, "shut, its middle is the doorway's");
                assertEquals(0, shut.z(), 1e-9);
                final Vec3 open = stood.reached(new Swung(90, 0));
                final int runs = hung == Hung.LEFT ? 1 : -1;
                final int side = opens == Opens.AWAY ? 1 : -1;
                // its thickness lies within the opening, its face on the hinges' line
                assertEquals(-runs * 49, open.x(), 1e-9, hung + " " + opens + ": beside its hinge edge");
                assertEquals(side * 51, open.z(), 1e-9, hung + " " + opens + ": out the side it opens toward");
                final Block leaf = stood.inTheWay(new Swung(90, 0)).getFirst();
                assertEquals(open.x(), leaf.x(), 1e-9);
                assertEquals(open.z(), leaf.z(), 1e-9);
                assertEquals(90, Math.abs(leaf.yaw()), 1e-9, "square to the wall");
            }
        }
    }

    @Test
    void aWallLeavesAHoleForTheLeafAndItsFrameAndWhatIsInTheWayIsTheLeafTheJambsAndTheHead() {
        assertEquals(90 + 2 * (0.3 + 6), glazed.holeWide(), 1e-9);
        assertEquals(0.8 + 210 + 0.3 + 6, glazed.holeHigh(), 1e-9);
        assertEquals(96, glass.holeWide(), 1e-9);
        final List<Block> framed = glazed.stood(new Vec3(300, 0, 600), 0).inTheWay(new Swung());
        assertEquals(4, framed.size());
        assertEquals(300, framed.getFirst().x(), 1e-9);
        assertEquals(45, framed.getFirst().halfWidth(), 1e-9);
        assertEquals(300 - 48.3, framed.get(1).x(), 1e-9);
        assertEquals(300 + 48.3, framed.get(2).x(), 1e-9);
        assertTrue(framed.get(3).bottom() > 205, "the head is walked under");
        assertEquals(1, glass.stood(new Vec3(0, 0, 0), 0).inTheWay(new Swung()).size(), "a glass door is its leaf");
        // turned a quarter round, what is in the way turns with it
        final Block turned = glazed.stood(new Vec3(0, 0, 0), 90).inTheWay(new Swung()).get(1);
        assertEquals(-3.8, turned.x(), 1e-9, "its frame reaching back from the leaf's face");
        assertEquals(48.3, Math.abs(turned.z()), 1e-9);
        assertEquals(90, turned.yaw(), 1e-9);
    }

    @Test
    void someoneStandingWhereTheLeafIsAboutToSwingIsInItsWayAndSomeoneBehindTheWallIsNot() {
        final Stood stood = glazed.stood(new Vec3(0, 0, 0), 0);
        final Vec3 inside = new Vec3(0, 0, 60);
        final Vec3 front = new Vec3(0, 0, -60);
        assertFalse(stood.sweeps(new Swung(), true, inside, 30), "shut, the leaf is not yet near them");
        assertTrue(stood.sweeps(new Swung(40, 60), true, inside, 30), "opening, it has come up to them");
        assertFalse(stood.sweeps(new Swung(40, 60), true, front, 30), "it never swings to the front");
        assertFalse(stood.sweeps(new Swung(40, -60), false, inside, 30), "shutting, it is leaving them");
        assertTrue(stood.sweeps(new Swung(95, 0), false, new Vec3(-25, 0, 50), 30), "but not one in its way back");
        assertFalse(stood.sweeps(new Swung(95, 0), true, inside, 30), "as wide as it goes, it is going nowhere");
        assertFalse(stood.sweeps(new Swung(40, 60), true, new Vec3(0, 0, 300), 30), "out of its reach");
    }

    @Test
    void itsHingeOpensAsWideAsItsSwingSaysAndALeafIsOfASizeToWalkThrough() {
        assertEquals(95, glazed.hinged().widest(), 0);
        assertEquals(90, glass.hinged().widest(), 0);
        assertThrows(IllegalArgumentException.class, () -> new Size(20, 210, 4));
        assertThrows(IllegalArgumentException.class, () -> new Swing(Hung.LEFT, Opens.AWAY, 10));
        assertThrows(IllegalArgumentException.class, () -> new Design.Glazed(new Wood.Varnished(), Fittings.STEEL,
                0, 3, 0.3));
    }
}
