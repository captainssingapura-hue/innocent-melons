package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The ground as the card draws it: a point every so far each way, the last at the far edges, each as high as the
 * ground; two triangles to a square, every one facing up; each point facing as the ground slopes there.
 */
class SheetTest {

    /** Ground 10 m across and 7 m along, rising 1 m for every 2 m across. */
    private record Slope() implements Ground {
        public Extent extent() {
            return new Extent(0, 0, 1_000, 700);
        }

        public double height(final double x, final double z) {
            return x / 2;
        }
    }

    private final Mesh sheet = Sheet.INSTANCE.of(new Slope(), 200);

    @Test
    void aPointEverySoFarEachWayTheLastAtTheFarEdges() {
        // 0, 200, .. 1000 across: six; 0, 200, 400, 600, 700 along: five, the last at the far edge
        assertEquals(30, sheet.points().size());
        assertEquals(new Vec3(1_000, 500, 700), sheet.points().get(29));
        assertEquals(new Vec3(0, 0, 0), sheet.points().getFirst());
        assertEquals(new Vec3(400, 200, 600), sheet.points().get(3 * 6 + 2));
        assertThrows(IllegalArgumentException.class, () -> Sheet.INSTANCE.of(new Slope(), 0));
    }

    @Test
    void twoTrianglesToASquareEveryOneFacingUp() {
        assertEquals(2 * 5 * 4, sheet.triangles());
        for (int t = 0; t < sheet.triangles(); t++) {
            assertTrue(sheet.crossed(t).y() > 0, "triangle " + t + " faces up");
        }
        assertEquals(1_000 * 700 * Math.hypot(1, 0.5), sheet.area(), 1e-6);
    }

    @Test
    void eachPointFacingAsTheGroundSlopesThere() {
        final Vec3 facing = new Vec3(-0.5, 1, 0).unit();
        for (final Vec3 at : sheet.facings()) {
            assertEquals(0, at.minus(facing).length(), 1e-9);
        }
        // at the hill's top the ground lies nearly level, the country's swell under it, and faces nearly up
        assertEquals(0, Sheet.INSTANCE.facing(new Rolling(), 20_000, 35_000, 10).minus(new Vec3(0, 1, 0)).length(),
                0.05);
        assertTrue(new Rolling().height(20_000, 35_000) > new Rolling().height(-100_000, -100_000),
                "the hill stands over the country round it");
    }
}
