package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A drum: one unit across and one high, its middle at the middle, every face turned out from its middle, its side
 * round and its rims softened, so it is a little less than the round column it stands in; and a drum that is not
 * round, or softened past its middle, is refused.
 */
class DrumTest {

    @Test
    void itIsAUnitAcrossAndHighWithEveryFaceTurnedOutAndItsRimsSoftened() {
        final Mesh drum = new Drum().formed();
        double widest = 0;
        double highest = 0;
        for (int triangle = 0; triangle < drum.triangles(); triangle++) {
            Vec3 middle = new Vec3(0, 0, 0);
            for (int corner = 0; corner < 3; corner++) {
                final Vec3 at = drum.corner(triangle, corner);
                middle = middle.plus(at.times(1.0 / 3));
                widest = Math.max(widest, Math.hypot(at.x(), at.z()));
                highest = Math.max(highest, Math.abs(at.y()));
            }
            assertTrue(drum.crossed(triangle).dot(middle) > 0, "triangle " + triangle + " faces out");
        }
        assertEquals(0.5, widest, 1e-9);
        assertEquals(0.5, highest, 1e-9);
        // a round column of the same size has an area of 1.5 pi; softening the rims takes a little off it
        assertTrue(drum.area() < 1.5 * Math.PI && drum.area() > 1.35 * Math.PI, "its area: " + drum.area());
        assertTrue(new Drum(24, 0).formed().area() > drum.area(), "a drum with sharp rims is a little bigger");
    }

    @Test
    void aDrumNotRoundOrSoftenedPastItsMiddleIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Drum(2, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new Drum(24, 0.5));
    }
}
