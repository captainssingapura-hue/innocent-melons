package com.example.fxsuite.qingming.land;

import com.example.fxsuite.avatar.roads.Roadway;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.River;
import com.example.fxsuite.avatar.sim.Vec2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reach's land: the river cut into the plain under the bridge, its water as deep as the Song history has it, as
 * wide between its banks as the bridge's span; level where the bridge's ends, the wall and the gate will stand; no
 * river near the gate; the road to the bridge and the street from it through the gate, the river left to the bridge;
 * the towpaths laid.
 */
class ReachTest {

    private static final Land GROUND = Reach.INSTANCE.ground();

    @Test
    void theRiverCutIntoThePlainUnderTheBridgeAsDeepAndAsWideAsSaid() {
        assertEquals(-Rivers.DEPTH, GROUND.height(0, 0), 5, "its bed under the bridge");
        assertEquals(2_200, Rivers.TOP, 1, "between its banks' tops, the bridge's span");
        final River river = Reach.INSTANCE.rivers().getFirst();
        assertEquals(186, river.top(), 1, "6 chi of water");
        assertTrue(river.wide() > Rivers.BED && river.wide() < Rivers.TOP,
                "its water within its banks: " + river.wide());
        // the plain beside it, past its banks
        assertEquals(0, GROUND.height(0, Rivers.TOP / 2 + 500), 40, "the plain north of it");
        // its banks as cut, the towpaths along them throwing nothing down into it
        for (final double x : new double[] {-12_000, 6_000, 30_000}) {
            for (double z = -Rivers.TOP / 2; z <= Rivers.TOP / 2; z += 50) {
                final Vec2 at = Rivers.INSTANCE.line().stream().min(java.util.Comparator.comparingDouble(
                        p -> Math.abs(p.x() - x))).orElseThrow();
                assertEquals(GROUND.height(at.x(), at.z() + z), Reach.INSTANCE.land().height(at.x(), at.z() + z), 1,
                        "its bank at " + at.x() + ", " + (at.z() + z));
            }
        }
    }

    @Test
    void levelWhereTheBridgesEndsTheWallAndTheGateWillStandNoRiverNearTheGate() {
        for (final double[] p : Reach.INSTANCE.pads()) {
            final double x = (p[0] + p[1]) / 2;
            final double z = (p[2] + p[3]) / 2;
            assertEquals(0, GROUND.height(x, z), 1, "level at " + x + ", " + z);
        }
        for (final Vec2 at : Rivers.INSTANCE.line()) {
            assertTrue(Math.hypot(at.x() - Reach.WALL, at.z() - Reach.GATE_Z) > 20_000,
                    "the river far from the gate");
        }
    }

    @Test
    void theRoadToTheBridgesSouthEndTheStreetFromItsNorthEndThroughTheGateTheTowpathsLaid() {
        final List<Roadway> roads = Reach.INSTANCE.roads().roads();
        assertTrue(roads.size() >= 4, "the road, the street and their towpaths: " + roads.size());
        final Roadway road = roads.stream().filter(r -> r.name().equals("the road to the bridge")).findFirst()
                .orElseThrow();
        final Roadway street = roads.stream().filter(r -> r.name().equals("the street from the bridge")).findFirst()
                .orElseThrow();
        // each ends at the bridge's end, the river between them left to the bridge
        assertEquals(-1_600, road.point(road.size() - 1).z(), 1, "the road at the bridge's south end");
        assertEquals(1_600, street.point(0).z(), 1, "the street at its north end");
        for (final Roadway r : roads) {
            for (int i = 0; i < r.size(); i++) {
                assertTrue(Math.hypot(r.point(i).x(), r.point(i).z()) >= 1_500, r.name() + " not over the river");
            }
        }
        boolean west = false;
        for (int i = 0; i < street.size(); i++) {
            west |= street.point(i).x() < Reach.WALL - 1_000;
        }
        assertTrue(west, "the street through the gate");
    }
}
