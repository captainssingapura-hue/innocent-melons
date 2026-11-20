package com.example.fxsuite.qingming.land;

import com.example.fxsuite.avatar.roads.Decks;
import com.example.fxsuite.avatar.roads.Roadway;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.River;
import com.example.fxsuite.avatar.sim.Vec2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reach's land: the river cut into the plain under the bridge, its water as deep as the Song history has it, as
 * wide between its banks as the bridge's span; level where the bridge's ends, the wall and the gate will stand; no
 * river near the gate; the road over the bridge on a deck and along the street through the gate; the towpaths laid.
 */
class ReachTest {

    private static final Land GROUND = Reach.ground();

    @Test
    void theRiverCutIntoThePlainUnderTheBridgeAsDeepAndAsWideAsSaid() {
        assertEquals(-Rivers.DEPTH, GROUND.height(0, 0), 5, "its bed under the bridge");
        assertEquals(2_200, Rivers.TOP, 1, "between its banks' tops, the bridge's span");
        final River river = Reach.rivers().getFirst();
        assertEquals(186, river.top(), 1, "6 chi of water");
        assertTrue(river.wide() > Rivers.BED && river.wide() < Rivers.TOP, "its water within its banks: " + river.wide());
        // the plain beside it, past its banks
        assertEquals(0, GROUND.height(0, Rivers.TOP / 2 + 500), 40, "the plain north of it");
    }

    @Test
    void levelWhereTheBridgesEndsTheWallAndTheGateWillStandNoRiverNearTheGate() {
        for (final double[] p : Reach.PADS) {
            final double x = (p[0] + p[1]) / 2;
            final double z = (p[2] + p[3]) / 2;
            assertEquals(0, GROUND.height(x, z), 1, "level at " + x + ", " + z);
        }
        for (final Vec2 at : Rivers.line()) {
            assertTrue(Math.hypot(at.x() - Reach.GATE.x(), at.z() - Reach.GATE.z()) > 20_000, "the river far from the gate");
        }
    }

    @Test
    void theRoadOverTheBridgeOnADeckAndThroughTheGateTheTowpathsLaid() {
        final List<Roadway> roads = Reach.roads().roads();
        assertTrue(roads.size() >= 3, "the road and its towpaths: " + roads.size());
        assertFalse(Decks.pieces(Reach.roads(), Reach.DECK_KEYS, true).isEmpty(), "a deck over the river");
        final Roadway road = roads.stream().filter(r -> r.name().contains("bridge")).findFirst().orElseThrow();
        boolean west = false;
        for (int i = 0; i < road.size(); i++) {
            west |= road.point(i).x() < Reach.WALL - 1_000;
        }
        assertTrue(west, "the street through the gate");
        // over the river, the road carried, not on the ground, and over its water
        double over = Double.NEGATIVE_INFINITY;
        boolean carried = false;
        for (int i = 0; i < road.size(); i++) {
            if (Math.abs(road.point(i).z()) < 500 && Math.abs(road.point(i).x()) < 500) {
                over = Math.max(over, road.level(i));
                carried |= i + 1 < road.size() && !road.grounded(i);
            }
        }
        assertTrue(carried, "carried over the river");
        assertTrue(over > -Rivers.DEPTH + Rivers.WATER, "the deck over the water: " + over);
    }
}
