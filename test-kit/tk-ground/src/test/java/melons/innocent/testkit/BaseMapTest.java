package melons.innocent.testkit;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.form.Mesh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A base map. It holds its middle and nothing past how far it reaches, whatever its outline; its ground is a level
 * fan with a triangle for each side; its wall has a run for each side, far higher than anything flies; and the
 * avatar, walking any way from its middle for longer than it takes to cross, is still on it, having come up to its
 * edge. A map too small or too large is refused.
 */
class BaseMapTest {

    @Test
    void itHoldsItsMiddleAndNothingPastHowFarItReaches() {
        for (final Outline outline : Outline.values()) {
            final BaseMap map = new BaseMap(outline, 1000, Grounds.TURF);
            assertTrue(map.holds(0, 0), outline + " holds its middle");
            assertEquals(outline != Outline.OVAL, map.holds(0, 99_000), outline + ", as far as said along");
            assertFalse(map.holds(0, 101_000), outline + " ends there");
            assertFalse(map.holds(150_000, 150_000), outline + " holds nothing far off");
        }
        assertTrue(new BaseMap(Outline.SQUARE, 1000, Grounds.TURF).holds(99_000, 99_000), "a square to its corner");
        assertFalse(new BaseMap(Outline.CIRCLE, 1000, Grounds.TURF).holds(99_000, 99_000), "a circle not");
        assertFalse(new BaseMap(Outline.OVAL, 1000, Grounds.TURF).holds(0, 70_000), "an oval less far along");
        assertTrue(new BaseMap(Outline.OVAL, 1000, Grounds.TURF).holds(99_000, 0), "and as far as said across");
    }

    @Test
    void itsGroundIsALevelFanAndItsWallARunForEachSideFarHigherThanAnythingFlies() {
        for (final Outline outline : Outline.values()) {
            final BaseMap map = new BaseMap(outline, 50, Grounds.GRAVEL);
            final int sides = map.edge().size();
            final Mesh ground = map.formed();
            assertEquals(sides + 1, ground.points().size());
            assertEquals(3 * sides, ground.corners().size());
            assertTrue(ground.points().stream().allMatch(point -> point.y() == 0), "level, where height is nothing");
            assertEquals(sides, map.inTheWay().size());
            for (final Block run : map.inTheWay()) {
                assertTrue(run.top() >= 2_000_000 && run.bottom() == 0, "from the ground to twenty kilometres up");
                assertFalse(map.holds(run.x(), run.z()), "standing outside the edge");
            }
        }
    }

    @Test
    void theAvatarWalkingAnyWayFromItsMiddleIsStoppedAtItsEdge() {
        final Controls ahead = new Controls(1, 0, 0, 0, true);
        for (final Outline outline : Outline.values()) {
            final BaseMap map = new BaseMap(outline, 10, Grounds.TURF);
            final Obstacles inTheWay = new Obstacles(map.inTheWay());
            for (int heading = 0; heading < 360; heading += 45) {
                Avatar now = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), heading),
                        false);
                for (int step = 0; step < 2_400; step++) {
                    now = now.step(ahead, Clock.STEP, inTheWay).avatar();
                }
                final double x = now.body().position().x();
                final double z = now.body().position().z();
                assertTrue(map.holds(x, z), outline + ", heading " + heading + ": still on the map at " + x + ", "
                        + z);
                assertTrue(Math.hypot(x, z) > 300, "having left the middle: " + x + ", " + z);
            }
        }
    }

    @Test
    void aMapTooSmallOrTooLargeIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new BaseMap(Outline.CIRCLE, 5, Grounds.TURF));
        assertThrows(IllegalArgumentException.class, () -> new BaseMap(Outline.CIRCLE, 6_000, Grounds.TURF));
        assertEquals("The test ground: a circle reaching 1000 m from its middle, of turf",
                new BaseMap(Outline.CIRCLE, 1000, Grounds.TURF).said());
    }
}
