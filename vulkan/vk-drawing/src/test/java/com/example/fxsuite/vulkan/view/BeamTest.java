package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The sun's light as it falls on a part of the world: what it falls round in the middle of its picture and half way
 * through it; the square's edges at the picture's edges; what is nearer the sun deeper in number, from 1 to nothing;
 * nothing moved in the picture by going along the light; a triangle facing the sun going round clockwise, as one
 * facing the camera does; a sun straight overhead seen as well as any; and a beam settled lying a whole number of
 * pixels from the world's middle, having moved less than a pixel.
 */
class BeamTest {

    private final Vec3 round = new Vec3(1200, 0, -3400);
    private final Beam beam = new Beam(round, new Vec3(0.45, -1, 0.35), 8000, 6000);
    private final Mat4 seen = beam.seen();

    @Test
    void whatItFallsRoundIsInTheMiddleOfItsPictureAndHalfWayThroughIt() {
        final Vec3 shown = seen.clip(round).shown();
        assertEquals(0, shown.x(), 1e-12);
        assertEquals(0, shown.y(), 1e-12);
        assertEquals(0.5, shown.z(), 1e-12);
    }

    @Test
    void theSquaresEdgesAreThePicturesEdgesAndUpInItIsHigherOnThePicture() {
        final Vec3 right = seen.clip(round.plus(beam.right().times(4000))).shown();
        final Vec3 up = seen.clip(round.plus(beam.up().times(4000))).shown();
        assertEquals(1, right.x(), 1e-12);
        assertEquals(0, right.y(), 1e-12);
        assertEquals(0, up.x(), 1e-12);
        assertEquals(-1, up.y(), 1e-12, "the card counts down from the top");
    }

    @Test
    void whatIsNearerTheSunIsDeeperInNumberAndGoingAlongTheLightMovesNothingInThePicture() {
        final Vec3 nearest = seen.clip(round.minus(beam.ahead().times(6000))).shown();
        final Vec3 furthest = seen.clip(round.plus(beam.ahead().times(6000))).shown();
        assertEquals(1, nearest.z(), 1e-12);
        assertEquals(0, furthest.z(), 1e-12);
        assertEquals(0, nearest.x(), 1e-12);
        assertEquals(0, furthest.y(), 1e-12);
        // a thing above the ground is nearer the sun than the ground under it
        assertTrue(seen.clip(round.plus(new Vec3(0, 300, 0))).shown().z() > 0.5);
    }

    @Test
    void aTriangleFacingTheSunGoesRoundClockwiseInItsPictureAsOneFacingTheCameraDoesInTheCameras() {
        final Vec3 a = round;
        final Vec3 b = round.plus(beam.up().times(10));
        final Vec3 c = round.plus(beam.right().times(10));
        assertTrue(b.minus(a).cross(c.minus(a)).dot(beam.ahead()) < 0, "its face is toward the sun");
        final Vec3[] shown = {seen.clip(a).shown(), seen.clip(b).shown(), seen.clip(c).shown()};
        final double turned = (shown[1].x() - shown[0].x()) * (shown[2].y() - shown[0].y())
                - (shown[1].y() - shown[0].y()) * (shown[2].x() - shown[0].x());
        assertTrue(turned > 0, "clockwise, the picture counted down from its top");
    }

    @Test
    void aSunStraightOverheadIsSeenAsWellAsAnyItsThreeWaysOneLongAndSquare() {
        for (final Vec3 towards : new Vec3[] {new Vec3(0, -1, 0), new Vec3(0.45, -1, 0.35), new Vec3(1, -0.01, 0)}) {
            final Beam one = new Beam(round, towards, 8000, 6000);
            assertEquals(1, one.right().length(), 1e-12);
            assertEquals(1, one.up().length(), 1e-12);
            assertEquals(0, one.right().dot(one.ahead()), 1e-12);
            assertEquals(0, one.up().dot(one.ahead()), 1e-12);
            assertEquals(0, one.right().dot(one.up()), 1e-12);
            assertEquals(1, one.right().cross(one.up()).dot(one.ahead()), 1e-12, "handed as the camera is");
        }
    }

    @Test
    void settledItLiesAWholeNumberOfPixelsFromTheWorldsMiddleHavingMovedLessThanAPixel() {
        final int pixels = 4096;
        final double pixel = beam.pixel(pixels);
        assertEquals(8000.0 / 4096, pixel, 1e-12);
        final Beam settled = beam.settled(pixels);
        final double across = settled.round().dot(beam.right()) / pixel;
        final double up = settled.round().dot(beam.up()) / pixel;
        assertEquals(Math.rint(across), across, 1e-9);
        assertEquals(Math.rint(up), up, 1e-9);
        assertTrue(settled.round().minus(round).length() < pixel, "moved less than a pixel");
        assertEquals(round.dot(beam.ahead()), settled.round().dot(beam.ahead()), 1e-9, "and not along the light");
        // two beams a hair apart settle to the same place in the picture: a shadow's edge does not creep
        final Beam beside = new Beam(round.plus(new Vec3(0.2, 0, -0.3)), beam.towards(), 8000, 6000).settled(pixels);
        assertEquals(settled.round().dot(beam.right()), beside.round().dot(beam.right()), 1e-9);
        assertEquals(settled.round().dot(beam.up()), beside.round().dot(beam.up()), 1e-9);
    }

    @Test
    void noneShinesNoWayOrOnNothing() {
        assertThrows(IllegalArgumentException.class, () -> new Beam(round, new Vec3(0, 0, 0), 8000, 6000));
        assertThrows(IllegalArgumentException.class, () -> new Beam(round, new Vec3(0, -1, 0), 0, 6000));
        assertThrows(IllegalArgumentException.class, () -> new Beam(round, new Vec3(0, -1, 0), 8000, 0));
    }
}
