package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

    /** Ground 10 m square, level 3 m up. */
    private record Level() implements Ground {
        public Extent extent() {
            return new Extent(0, 0, 1_000, 1_000);
        }

        public double height(final double x, final double z) {
            return 300;
        }
    }

    private final Mesh sheet = Sheet.INSTANCE.of(new Slope(), 200, 1);

    @Test
    void aPointEverySoFarEachWayTheLastAtTheFarEdges() {
        // 0, 200, .. 1000 across: six; 0, 200, 400, 600, 700 along: five, the last at the far edge
        assertEquals(30, sheet.points().size());
        assertTrue(sheet.points().contains(new Vec3(1_000, 500, 700)));
        assertTrue(sheet.points().contains(new Vec3(0, 0, 0)));
        assertTrue(sheet.points().contains(new Vec3(400, 200, 600)));
        assertThrows(IllegalArgumentException.class, () -> Sheet.INSTANCE.of(new Slope(), 0, 1));
        assertThrows(IllegalArgumentException.class, () -> Sheet.INSTANCE.of(new Slope(), 200, 0));
        assertTrue(Sheet.INSTANCE.of(new Slope(), 200, 3).points().contains(new Vec3(1_000, 1_500, 700)));
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
    void cutIntoPartsByTheLayerAtEachSquaresNearLeftCorner() {
        final Grid grid = new Grid(new Slope(), 200);
        final int[] layers = new int[grid.across() * grid.along()];
        for (int p = 0; p < layers.length; p++) {
            layers[p] = grid.x(p % grid.across()) < 500 ? 0 : 1;
        }
        final Map<Integer, Mesh> parts = Sheet.INSTANCE.parts(grid, 1, layers);
        // squares whose near left corners lie 0, 200 and 400 across in the first, 600 and 800 in the second; four rows
        assertEquals(List.of(0, 1), List.copyOf(parts.keySet()));
        assertEquals(2 * 3 * 4, parts.get(0).triangles());
        assertEquals(2 * 2 * 4, parts.get(1).triangles());
        assertEquals(4 * 5, parts.get(0).points().size(), "only the points its squares use");
    }

    @Test
    void eachPointFacingAsTheGroundSlopesThere() {
        final Vec3 facing = new Vec3(-0.5, 1, 0).unit();
        for (final Vec3 at : sheet.facings()) {
            assertEquals(0, at.minus(facing).length(), 1e-9);
        }
        // a level sheet faces straight up, at its edges too
        final Grid level = new Grid(new Level(), 100);
        assertEquals(0, level.rise(0, 0)[0], 1e-12);
        assertEquals(0, level.sample(3, 4).slope(), 1e-12);
        assertTrue(new Rolling().height(20_000, 35_000) > new Rolling().height(-100_000, -100_000),
                "the hill stands over the country round it");
    }
}
