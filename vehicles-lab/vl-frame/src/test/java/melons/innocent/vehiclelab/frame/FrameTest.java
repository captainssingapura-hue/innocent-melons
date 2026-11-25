package melons.innocent.vehiclelab.frame;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A frame of tubes: put together by name, in pairs either side of its middle; its length of tube, its mass and where
 * its mass is; what is wrong with one that is in pieces or has a node no rod meets; how much it leans on its welds;
 * its form, a tube for each rod and a ball for each joint; and a rod to a node there is not refused.
 */
class FrameTest {

    private final Frame.Tube tube = new Frame.Tube(1, 0.2);

    /** A square of four tubes, a metre each side, flat on the ground, braced across one diagonal. */
    private Frame square() {
        return new Framing("square", tube).pair("front", 50, 50, 0).pair("back", 50, -50, 0)
                .across("front").across("back").rods("front", "back").rod("front, left", "back, right").framed();
    }

    @Test
    void itsTubeItsMassAndWhereItsMassIs() {
        final Frame square = square();
        assertEquals(List.of(), square.problems());
        assertEquals(4 * 100 + 100 * Math.sqrt(2), square.steel(), 1e-9);
        assertEquals(square.steel() * tube.area() * Frame.STEEL, square.mass(), 1e-9);
        assertEquals(0, square.centre().across(), 1e-9);
        assertEquals(0, square.centre().up(), 1e-9);
        assertEquals(3, square.rodsAt(square.node("front, left")).size());
    }

    @Test
    void whatIsWrongWithIt() {
        final Frame loose = new Framing("loose", tube).node("a", 0, 0, 0).node("b", 0, 10, 0).node("c", 0, 20, 0)
                .node("d", 0, 30, 0).rod("a", "b").rod("c", "d").rod("b", "a").framed();
        assertTrue(loose.problems().contains("in pieces: not every node is joined to the rest"));
        assertTrue(loose.problems().stream().anyMatch(p -> p.startsWith("two rods between")));
        assertFalse(loose.connected());
        assertThrows(IllegalArgumentException.class, () -> new Framing("bad", tube).node("a", 0, 0, 0)
                .rod("a", "nowhere").framed());
        assertThrows(IllegalArgumentException.class, () -> new Frame.Tube(1, 1));
    }

    @Test
    void howMuchItLeansOnItsWelds() {
        // four nodes free three ways each, five rods, and six ways it moves as a whole: one way left to fold
        assertEquals(1, square().slack());
    }

    @Test
    void itsFormATubeForEachRodAndABallAtEachJoint() {
        final Frame square = square();
        final FrameForm form = new FrameForm(square, 8);
        assertFalse(form.mesh().points().isEmpty());
        assertTrue(form.mesh().points().stream().allMatch(p -> Math.abs(p.y()) <= tube.radius() + 1e-9),
                "as thick as its tube, no thicker");
        assertEquals(new com.example.fxsuite.vulkan.space.Vec3(50, 0, -50), form.at(square.nodes().get(
                square.node("back, right"))), "across as x, up as y, along as z");
    }
}
