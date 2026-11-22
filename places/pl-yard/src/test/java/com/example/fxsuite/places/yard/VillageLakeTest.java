package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Pond;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Mirroring;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The village's small lake: about twice the ground the village stands on; its kerb goes right round in runs of
 * some four metres, each beginning where the last ended and facing outward; its water is the engine's own still
 * water, wet within the kerb and dry outside it, its top below the kerb's; the sheet that is drawn of the water
 * lies level at that height, facing up, and reaches under the kerb; water is a material of the tree, and mirrors;
 * the lake lies before the village, behind where the avatar starts and clear of it and of every yard; and the
 * avatar walking at it from where it starts is stopped by the kerb.
 */
class VillageLakeTest {

    private final Village village = new Village(8);
    private final VillageLake lake = village.lake();
    private final Vec3 middle = lake.stands();

    @Test
    void itIsAboutTwiceTheGroundTheVillageStandsOn() {
        double left = 0;
        double right = 0;
        double near = Double.MAX_VALUE;
        double far = 0;
        for (final Village.Home home : village.homesteads()) {
            for (final Block wall : home.yard().inTheWay()) {
                left = Math.min(left, wall.x());
                right = Math.max(right, wall.x());
                near = Math.min(near, wall.z());
                far = Math.max(far, wall.z());
            }
        }
        final double stoodOn = (right - left) * (far - near);
        // within its kerb, as the sheet drawn of it is: a fan of flat triangles
        final double water = lake.sheet().area();
        assertTrue(water > 1.7 * stoodOn && water < 2.3 * stoodOn,
                water / 1e4 + " square metres of water to " + stoodOn / 1e4 + " of village");
    }

    @Test
    void itsKerbGoesRightRoundInRunsOfSomeFourMetresEachBeginningWhereTheLastEndedAndFacingOutward() {
        final List<Wall> kerb = lake.kerb();
        assertEquals(lake.sides(), kerb.size());
        double along = 0;
        for (int run = 0; run < kerb.size(); run++) {
            final Wall here = kerb.get(run);
            final Wall next = kerb.get((run + 1) % kerb.size());
            assertTrue(here.to().minus(next.from()).length() < 1e-9, "the next begins where this ends");
            assertTrue(here.out().dot(here.from().plus(here.to()).times(0.5).minus(middle)) > 0, "facing outward");
            assertEquals(0, here.out().dot(here.to().minus(here.from())), 1e-9, "square to its run");
            assertEquals(along, here.along(), 1e-9, "its surface going on from the last run's");
            assertEquals(0, here.low());
            assertTrue(here.length() > 150 && here.length() < 700, "some four metres: " + here.length());
            along += here.length();
        }
        assertEquals(lake.kerb(), kerb, "the same every time");
    }

    @Test
    void itsWaterIsTheEnginesStillWaterWetWithinTheKerbAndDryOutsideItsTopBelowTheKerbs() {
        final Pond water = lake.water();
        assertTrue(water.wet(Land.LEVEL, middle.x(), middle.z()), "wet at its middle");
        assertTrue(water.wet(Land.LEVEL, middle.x() + 0.4 * lake.across(), middle.z()), "and well toward its edge");
        assertTrue(!water.wet(Land.LEVEL, middle.x() + 0.56 * lake.across(), middle.z()), "dry outside the kerb");
        assertTrue(!water.wet(Land.LEVEL, 0, 0), "dry where the avatar starts");
        assertTrue(water.level() > 0 && water.level() < lake.kerb().getFirst().high(), "its top below the kerb's");
    }

    @Test
    void theSheetDrawnOfItLiesLevelAtTheWatersTopFacingUpAndReachesUnderTheKerb() {
        final Mesh sheet = lake.sheet();
        final Pond water = lake.water();
        for (final Vec3 point : sheet.points()) {
            assertEquals(water.level(), point.y(), 1e-9);
        }
        assertEquals(lake.sides(), sheet.triangles());
        for (int triangle = 0; triangle < sheet.triangles(); triangle++) {
            assertTrue(sheet.crossed(triangle).y() > 0, "facing up");
            assertEquals(1, sheet.facing(triangle, 1).y(), 1e-12, "and saying so");
            // its outer corners are past the water's edge, which is the kerb's inner face, and short of its outer
            final Vec3 corner = sheet.corner(triangle, 1);
            assertTrue(!water.wet(Land.LEVEL, corner.x(), corner.z()), "under the kerb: " + corner);
        }
    }

    @Test
    void waterIsAMaterialOfTheTreeAndMirrors() {
        assertTrue(new Materials().tree().has(new Materials.Water()));
        assertTrue(new Materials.Water().answer(Mirroring.class).isPresent());
    }

    @Test
    void itLiesBeforeTheVillageBehindWhereTheAvatarStartsAndClearOfItAndOfEveryYard() {
        final List<Block> kerb = lake.inTheWay();
        assertEquals(lake.sides(), kerb.size());
        double nearest = Double.MAX_VALUE;
        for (final Block run : kerb) {
            assertTrue(run.z() < -300, "behind where the avatar starts, the yards being ahead of it: " + run);
            nearest = Math.min(nearest, Math.hypot(run.x(), run.z()));
            assertEquals(0, run.bottom());
        }
        assertTrue(nearest > 350 && nearest < 900, "its near shore a few metres off: " + nearest);
        assertTrue(village.inTheWay().blocks().containsAll(kerb), "in the way with the rest of the village");
    }

    @Test
    void theAvatarWalkingAtItFromWhereItStartsIsStoppedByTheKerb() {
        final Obstacles inTheWay = village.inTheWay();
        final Controls ahead = new Controls(1, 0, 0, 0, true);
        // turned right round, to face the lake
        Avatar now = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 180), false);
        for (int step = 0; step < 4 * 120; step++) {
            now = now.step(ahead, Clock.STEP, inTheWay).avatar();
        }
        final com.example.fxsuite.avatar.sim.Vec3 at = now.body().position();
        assertTrue(at.z() < -300, "it walked up to the lake: " + at);
        assertTrue(at.z() > -900, "and no further than its near shore: " + at);
        assertTrue(!lake.water().wet(Land.LEVEL, at.x(), at.z()), "so it is not in the water");
    }
}
