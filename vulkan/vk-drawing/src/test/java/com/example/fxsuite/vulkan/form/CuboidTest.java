package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A box, and what turns and stretches it: its six flat faces each facing out, as big all over as a box is; a
 * turning that takes one way of a frame to the next and leaves its own line where it is; and a stretching that makes
 * a box a unit each way as big as is asked.
 */
class CuboidTest {

    @Test
    void aBoxHasSixFlatFacesOfFourPointsEachFacingOutAndIsAsBigAllOverAsABoxIs() {
        final Mesh box = new Cuboid(2, 4, 6).formed();
        assertEquals(24, box.points().size());
        assertEquals(12, box.triangles());
        assertEquals(2 * (2 * 4 + 4 * 6 + 2 * 6), box.area(), 1e-9);
        for (int triangle = 0; triangle < box.triangles(); triangle++) {
            final Vec3 out = box.crossed(triangle).unit();
            assertTrue(out.dot(box.corner(triangle, 0)) > 0, "faces away from the middle");
            assertEquals(1, out.dot(box.facing(triangle, 0)), 1e-9, "and the way its points say it faces");
        }
    }

    @Test
    void aQuarterTurnAboutUpTakesAheadToTheRightAndLeavesUpWhereItIs() {
        final Mat4 quarter = new Turned(new Vec3(0, 1, 0), 90).matrix();
        near(new Vec3(1, 0, 0), quarter.clip(new Vec3(0, 0, 1)).shown());
        near(new Vec3(0, 1, 0), quarter.clip(new Vec3(0, 1, 0)).shown());
    }

    @Test
    void aTurnAboutAPlaceLeavesThatPlaceWhereItIs() {
        final Vec3 pivot = new Vec3(3, -105, 2);
        final Mat4 turn = new Turned(new Vec3(1, 0, 0), 37).matrix(pivot);
        near(pivot, turn.clip(pivot).shown());
        assertEquals(10, turn.clip(pivot.plus(new Vec3(0, 10, 0))).shown().minus(pivot).length(), 1e-9);
    }

    @Test
    void aStretchingMakesABoxAUnitEachWayAsBigAsIsAsked() {
        final Mat4 sized = new Sized(2, 4, 6).matrix();
        near(new Vec3(1, 2, 3), sized.clip(new Vec3(0.5, 0.5, 0.5)).shown());
        near(new Vec3(5, 5, 5), new Sized(10).matrix().clip(new Vec3(0.5, 0.5, 0.5)).shown());
    }

    private void near(final Vec3 wanted, final Vec3 was) {
        assertEquals(0, wanted.minus(was).length(), 1e-9, wanted + ", not " + was);
    }
}
