package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A flat upright face formed as triangles: standing on its ground, as big as it is said to be, facing back. */
class PanelTest {

    private final Mesh formed = new Panel(300, 225, 3.7).formed();

    @Test
    void itStandsOnItsGroundWithItsMiddleOverTheFramesMiddle() {
        assertEquals(-150, formed.points().stream().mapToDouble(Vec3::x).min().orElseThrow(), 1e-9);
        assertEquals(150, formed.points().stream().mapToDouble(Vec3::x).max().orElseThrow(), 1e-9);
        assertEquals(0, formed.points().stream().mapToDouble(Vec3::y).min().orElseThrow(), 1e-9);
        assertEquals(225, formed.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9);
        assertTrue(formed.points().stream().allMatch(point -> point.z() == 0), "flat");
    }

    @Test
    void itsTrianglesCoverItExactlyAndEveryOneFacesBack() {
        assertEquals(300.0 * 225, formed.area(), 1e-6);
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            assertTrue(formed.crossed(triangle).z() < 0, "triangle " + triangle + " faces back along the frame");
            assertEquals(new Vec3(0, 0, -1), formed.facing(triangle, 0));
        }
    }

    @Test
    void itIsFormedInSquaresNoBiggerThanAsked() {
        // 300 / 3.7 is a little over 81, so 82 across; 225 / 3.7 a little under 61, so 61 up
        assertEquals(2 * 82 * 61, formed.triangles());
        assertEquals(2, new Panel(300, 225, 300).formed().triangles(), "one square, where one is big enough");
    }
}
