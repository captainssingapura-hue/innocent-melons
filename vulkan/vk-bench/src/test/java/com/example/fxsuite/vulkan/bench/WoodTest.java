package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The wood of giant watermelons, and the view that follows the avatar through it: the same wood every time, its
 * middle clear, its melons giant and lying on the ground; a melon in the way of the engine's own avatar, which runs
 * into it and slides round it, never through it; and the view behind the avatar, turned by the mouse, never under
 * the ground.
 */
class WoodTest {

    private final Wood wood = new Wood(11, 1100);

    @Test
    void theWoodIsTheSameEveryTimeItsMiddleClearAndItsMelonsGiant() {
        final List<Wood.Giant> giants = wood.giants();
        assertEquals(giants, new Wood(11, 1100).giants());
        assertTrue(giants.size() > 60 && giants.size() < 110, "most squares have one: " + giants.size());
        for (final Wood.Giant giant : giants) {
            assertTrue(giant.times() >= 10 && giant.times() <= 20, "ten to twenty times a melon: " + giant);
            assertTrue(Math.hypot(giant.x(), giant.z()) > 700, "none where the avatar starts: " + giant);
            // a melon's middle is 13 cm up when it lies; a giant's that many times as high
            assertEquals(13 * giant.times(), wood.stands(giant, 13).clip(
                    new com.example.fxsuite.vulkan.space.Vec3(0, 0, 0)).shown().y(), 1e-9);
        }
    }

    @Test
    void theEnginesAvatarRunsIntoAMelonAndSlidesRoundItNeverThroughIt() {
        final Obstacles inTheWay = wood.obstacles();
        final Block melon = inTheWay.blocks().getFirst();
        assertEquals(wood.giants().size(), inTheWay.blocks().size());
        // from fifteen metres off, running straight at the melon's middle for five seconds
        Body body = Body.standing(new Vec3(melon.x(), 0, melon.z() - 1500), 0);
        final Controls running = new Controls(1, 0, 0, 0, true);
        for (int step = 0; step < 600; step++) {
            body = body.step(running, Clock.STEP, inTheWay);
            assertTrue(!melon.covers(body.position().x(), body.position().z(), 0), "never inside it: " + body);
        }
        final Body free = Body.standing(new Vec3(melon.x(), 0, melon.z() - 1500), 0).step(running, 5);
        assertTrue(Math.abs(body.position().x() - melon.x()) > 100, "turned aside by it: " + body.position());
        assertTrue(body.position().z() < free.position().z() - 100, "and slowed by it: " + body.position());
    }

    @Test
    void theViewIsTheEnginesOwnBehindTheAvatarAndAboveItsHead() {
        final Body body = Body.standing(new Vec3(100, 0, 200), 90);
        final Camera camera = new Following(body).camera();
        assertTrue(camera.eye().x() < 100 - 500, "behind it, which faces right: " + camera.eye());
        assertTrue(camera.eye().y() > 160, "above its head, looking down on it: " + camera.eye());
    }
}
