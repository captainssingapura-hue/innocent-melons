package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A house with no roof: its walls drawn as they stand, every face turned outward; the surface laid round it going on
 * round a corner; and the same walls in the way of the engine's own avatar, which is stopped by the wall beside the
 * doorway and walks in through the doorway under its lintel.
 */
class HouseTest {

    private final Walled house = new Walled(new Vec3(0, 0, 1000), 0, 720, 540, 300, 30, 120, 220);

    @Test
    void itsWallsAreSixRunsEachOfSixFlatFacesTurnedTheWayTheySayTheyFace() {
        final Mesh formed = house.formed();
        assertEquals(6, house.walls().size());
        assertEquals(6 * 6 * 2, formed.triangles());
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            assertEquals(1, formed.crossed(triangle).unit().dot(formed.facing(triangle, 0)), 1e-9);
        }
        // the front's outer face, less the doorway: 7.2 m by 3 m, less 1.2 m by 2.2 m
        double front = 0;
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            final boolean facesFront = formed.facing(triangle, 0).z() < -0.5;
            front += facesFront && formed.corner(triangle, 0).z() < 731 ? formed.area(triangle) : 0;
        }
        assertEquals(720 * 300 - 120 * 220, front, 1e-6);
    }

    @Test
    void theSurfaceIsLaidRoundItSoFarRoundAndSoFarUpAndGoesOnRoundACorner() {
        final Wall front = house.walls().get(2);
        final Wall side = house.walls().get(3);
        // the front ends 720 cm round, at the corner; the side begins a wall's thickness on, where its own face does
        assertEquals(720, front.along() + front.length(), 1e-9);
        assertEquals(720 + 30, side.along(), 1e-9);
        final Mesh formed = house.formed();
        for (int point = 0; point < formed.points().size(); point++) {
            final Vec3 where = formed.points().get(point);
            final boolean outerFront = formed.facings().get(point).z() < -0.5 && where.z() < 731;
            if (outerFront) {
                assertEquals(where.x() + 360, formed.ons().get(point).x(), 1e-9, "so far round");
                assertEquals(where.y(), formed.ons().get(point).y(), 1e-9, "so far up");
            }
        }
    }

    @Test
    void theAvatarIsStoppedByTheWallBesideTheDoorwayAndWalksInThroughTheDoorway() {
        final Obstacles inTheWay = new Obstacles(house.inTheWay());
        final Controls ahead = new Controls(1, 0, 0, 0, true);
        // the front's outer face is 730 cm ahead; the avatar is 32 cm round
        final Avatar stopped = walked(new Avatar(Body.standing(sim(200, 0), 0), false), ahead, inTheWay);
        assertTrue(stopped.body().position().z() < 730 - 30, "outside still: " + stopped.body().position());
        assertTrue(stopped.body().position().z() > 600, "having come up to the wall");
        final Avatar inside = walked(new Avatar(Body.standing(sim(0, 0), 0), false), ahead, inTheWay);
        assertTrue(inside.body().position().z() > 760, "through the doorway: " + inside.body().position());
        assertTrue(inside.body().position().z() < 1270 - 30, "and stopped by the back wall, inside");
        assertEquals(0, inside.body().position().x(), 1e-6, "straight through");
    }

    /** {@code avatar} after four seconds of {@code controls}, by the engine's own steps. */
    private Avatar walked(final Avatar avatar, final Controls controls, final Obstacles inTheWay) {
        Avatar now = avatar;
        for (int step = 0; step < 480; step++) {
            now = now.step(controls, Clock.STEP, inTheWay).avatar();
        }
        return now;
    }

    private com.example.fxsuite.avatar.sim.Vec3 sim(final double x, final double z) {
        return new com.example.fxsuite.avatar.sim.Vec3(x, 0, z);
    }
}
