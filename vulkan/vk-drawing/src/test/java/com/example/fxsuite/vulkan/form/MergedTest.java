package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Meshes made one. Two panels, one left where it is and one moved and turned a quarter about the vertical: the one
 * mesh has both panels' points and triangles; the first's as they were; the second's points moved and turned and
 * the way they face turned with them, where on the surface each is kept as it was, and its corners counted on from
 * where its points begin; and its area is both panels' together.
 */
class MergedTest {

    @Test
    void twoPanelsMadeOneKeepTheirPointsMovedAndTurnedTheirOnsAsTheyWereAndTheirCornersCountedOn() {
        final Mesh panel = new Panel(2, 2, 2).formed();
        final Mat4 moved = new Mat4(new Vec3(10, 0, 0)).times(new Turned(new Vec3(0, 1, 0), 90).matrix());
        final Mesh one = new Merged(List.of(new Merged.Placed(panel, new Mat4()),
                new Merged.Placed(panel, moved))).mesh();
        final int points = panel.points().size();
        assertEquals(2 * points, one.points().size());
        assertEquals(2 * panel.triangles(), one.triangles());
        assertEquals(panel.points().getFirst(), one.points().getFirst(), "the first as it was");
        assertEquals(2 * panel.area(), one.area(), 1e-9, "both panels' area");
        for (int point = 0; point < points; point++) {
            final Vec3 was = panel.points().get(point);
            final Vec3 now = one.points().get(points + point);
            assertEquals(10 + was.z(), now.x(), 1e-9, "moved, and turned a quarter");
            assertEquals(was.y(), now.y(), 1e-9);
            assertEquals(-was.x(), now.z(), 1e-9);
            assertEquals(panel.facings().get(point).z(), one.facings().get(points + point).x(), 1e-9,
                    "facing turned with it");
            assertEquals(panel.ons().get(point), one.ons().get(points + point), "where on its surface, kept");
        }
        assertEquals(points + panel.corners().getFirst(), one.corners().get(panel.corners().size()),
                "the second's corners counted on");
    }
}
