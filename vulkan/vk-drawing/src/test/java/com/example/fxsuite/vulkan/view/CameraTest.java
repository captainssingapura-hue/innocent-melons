package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The camera's arithmetic, as the card will be given it: what is looked at in the middle of the picture, right to the
 * right and up higher, depth running backwards from 1, and a triangle facing the camera going round clockwise.
 */
class CameraTest {

    private static final double ASPECT = 4.0 / 3;

    private final Vec3 lookedAt = new Vec3(120_000, 13, -45_000);
    private final Camera camera = new Orbit(lookedAt, -35, 22, 120).camera(40, 1);
    private final Mat4 seen = camera.seen(ASPECT);

    @Test
    void whatIsLookedAtFallsInTheMiddleOfThePicture() {
        final Vec3 shown = seen.clip(camera.from(lookedAt)).shown();
        assertEquals(0, shown.x(), 1e-9);
        assertEquals(0, shown.y(), 1e-9);
    }

    @Test
    void theEyeIsAsFarOffAsTheOrbitSaysHoweverFarTheWorldsMiddleIs() {
        assertEquals(120, camera.from(lookedAt).length(), 1e-9);
    }

    @Test
    void whatIsToItsRightFallsToTheRightAndWhatIsUpOfItFallsHigher() {
        final Vec3 right = seen.clip(camera.from(lookedAt.plus(camera.right().times(10)))).shown();
        final Vec3 up = seen.clip(camera.from(lookedAt.plus(camera.up().times(10)))).shown();
        assertTrue(right.x() > 0, "right of the middle");
        assertEquals(0, right.y(), 1e-9);
        assertTrue(up.y() < 0, "the picture counts down from its top, so higher is less");
        assertEquals(0, up.x(), 1e-9);
    }

    @Test
    void depthRunsBackwardsFromOneAtTheNearestItSeesTowardNothingFarOff() {
        final double nearest = seen.clip(camera.ahead().times(camera.near())).shown().z();
        final double near = seen.clip(camera.ahead().times(100)).shown().z();
        final double far = seen.clip(camera.ahead().times(3_000_000)).shown().z();
        assertEquals(1, nearest, 1e-12);
        assertTrue(near < nearest && far < near && far > 0, "deeper in number is nearer: " + near + ", " + far);
    }

    @Test
    void itsThreeWaysAreOneLongAndSquareToEachOther() {
        assertEquals(1, camera.right().length(), 1e-12);
        assertEquals(1, camera.up().length(), 1e-12);
        assertEquals(1, camera.ahead().length(), 1e-12);
        assertEquals(0, camera.right().dot(camera.up()), 1e-12);
        assertEquals(0, camera.right().dot(camera.ahead()), 1e-12);
        assertEquals(0, camera.up().dot(camera.ahead()), 1e-12);
    }

    /**
     * A triangle wound as a mesh winds its own, its second side crossed with its third facing the camera, has less
     * than no area by the card's reckoning (which counts a picture down from its top): the card is told that the
     * front of a triangle is the side it goes round clockwise from.
     */
    @Test
    void aTriangleFacingTheCameraGoesRoundClockwiseOnThePicture() {
        final Vec3 a = lookedAt;
        final Vec3 b = lookedAt.plus(camera.up().times(10));
        final Vec3 c = lookedAt.plus(camera.right().times(10));
        assertTrue(b.minus(a).cross(c.minus(a)).dot(camera.ahead()) < 0, "its face is toward the camera");
        final Vec3[] shown = {seen.clip(camera.from(a)).shown(), seen.clip(camera.from(b)).shown(),
                seen.clip(camera.from(c)).shown()};
        double twice = 0;
        for (int k = 0; k < 3; k++) {
            final Vec3 here = shown[k];
            final Vec3 next = shown[(k + 1) % 3];
            twice += here.x() * next.y() - next.x() * here.y();
        }
        assertTrue(-twice / 2 < 0, "less than no area, as the card reckons it: clockwise");
    }
}
