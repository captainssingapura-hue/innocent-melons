package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.layout.Field;
import com.example.fxsuite.vulkan.layout.Kind;
import com.example.fxsuite.vulkan.layout.Layout;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A round body formed as triangles: on the body, as big as it is said to be, every triangle facing out. */
class EllipsoidTest {

    private final Ellipsoid body = new Ellipsoid(36, 26, 28);
    private final Mesh formed = body.formed(24, 12);

    @Test
    void everyPointIsOnTheBodyAndFacesAwayFromItsMiddle() {
        for (int point = 0; point < formed.points().size(); point++) {
            final Vec3 at = formed.points().get(point);
            final double on = square(at.x() / 18) + square(at.y() / 13) + square(at.z() / 14);
            assertEquals(1, on, 1e-12, "on the body");
            assertEquals(1, formed.facings().get(point).length(), 1e-12, "the way it faces, one long");
            assertTrue(formed.facings().get(point).dot(at) > 0, "facing away from the middle");
        }
    }

    @Test
    void itIsAsLongTallAndWideAsItIsSaidToBe() {
        assertEquals(18, formed.points().stream().mapToDouble(Vec3::x).max().orElseThrow(), 1e-12);
        assertEquals(-18, formed.points().stream().mapToDouble(Vec3::x).min().orElseThrow(), 1e-12);
        assertEquals(13, formed.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-12);
        assertEquals(14, formed.points().stream().mapToDouble(Vec3::z).max().orElseThrow(), 1e-9);
    }

    @Test
    void aPointAtEachEndAndARingForEachBandBetween() {
        assertEquals(2 + 24 * 11, formed.points().size());
        assertEquals(2 * 24 + 2 * 24 * 10, formed.triangles());
    }

    @Test
    void everyTriangleIsWoundToFaceOutOfTheBody() {
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            final Vec3 middle = formed.corner(triangle, 0).plus(formed.corner(triangle, 1))
                    .plus(formed.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(formed.crossed(triangle).dot(middle) > 0, "triangle " + triangle + " faces out");
        }
    }

    @Test
    void itsTrianglesCoverNearlyAsMuchAsABallOfItsSizeWould() {
        final Mesh ball = new Ellipsoid(20, 20, 20).formed(64, 32);
        assertEquals(4 * Math.PI * 100, ball.area(), 4 * Math.PI * 100 * 0.01);
    }

    @Test
    void packedItsPointsAreSixNumbersEachAndItsCornersThreeToATriangle() {
        final Layout layout = new Layout("Point", List.of(new Field("point", Kind.TRIPLE),
                new Field("facing", Kind.TRIPLE)));
        final Packed packed = new Packed(layout, formed);
        final ByteBuffer points = packed.points();
        assertEquals(24 * formed.points().size(), points.remaining());
        assertEquals(18f, points.getFloat(0), "the first point, at its first end: across");
        assertEquals(1f, points.getFloat(12), "and facing along its line across");
        assertEquals(12 * formed.triangles(), packed.corners().remaining());
    }

    @Test
    void aLayoutAskingWhatAMeshHasNotIsRefused() {
        final Layout asking = new Layout("Point", List.of(new Field("point", Kind.TRIPLE),
                new Field("colour", Kind.TRIPLE)));
        assertThrows(IllegalArgumentException.class, () -> new Packed(asking, formed));
    }

    private double square(final double number) {
        return number * number;
    }
}
