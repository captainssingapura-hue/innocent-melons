package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A row of the same subject at different distances: side by side about the row's middle, each as from twice as far
 * as the one before; and the far one over each really that many times as far from the eye, seen just above the near
 * one wherever the eye is.
 */
class RowTest {

    private final Row row = new Row(new Vec3(0, 13, 0), 6, 46, 40);

    @Test
    void theRowStandsSideBySideAboutItsMiddle() {
        assertEquals(new Vec3(-115, 13, 0), row.near(0));
        assertEquals(new Vec3(115, 13, 0), row.near(5));
        assertEquals(46, row.near(3).minus(row.near(2)).length(), 1e-9);
    }

    @Test
    void eachIsShownAsFromTwiceAsFarAsTheOneBeforeIt() {
        assertEquals(1, row.times(0, 1));
        assertEquals(32, row.times(5, 1));
        assertEquals(12, row.times(2, 3));
    }

    @Test
    void theFarOneIsThatManyTimesAsFarFromTheEyeAndSeenJustAboveTheNearOne() {
        final Camera camera = new Orbit(new Vec3(0, 33, 0), 25, 12, 200).camera(40, 1);
        final Vec3 above = row.near(4).plus(new Vec3(0, 40, 0));
        final Vec3 far = row.far(4, 16, camera.eye());
        assertEquals(16 * above.minus(camera.eye()).length(), far.minus(camera.eye()).length(), 1e-6);
        final Vec3 seenAbove = camera.seen(1).clip(camera.from(above)).shown();
        final Vec3 seenFar = camera.seen(1).clip(camera.from(far)).shown();
        assertEquals(seenAbove.x(), seenFar.x(), 1e-9);
        assertEquals(seenAbove.y(), seenFar.y(), 1e-9);
        assertEquals(above, row.far(4, 1, camera.eye()), "as far as it is, it stands just above the near one");
    }
}
