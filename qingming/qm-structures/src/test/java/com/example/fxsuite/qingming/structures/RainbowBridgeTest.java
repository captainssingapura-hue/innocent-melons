package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.qingming.land.Reach;
import com.example.fxsuite.qingming.land.Rivers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Rainbow Bridge as its historians read it: its arch of 21 groups, eleven of four members and ten of three, tied by
 * five logs across; its crown some 5 m over the water, its span 20 m between its end walls; its deck walked up and
 * over, no steeper than a ramp stood on, meeting the street at both ends, the river's passage open under it; 23 posts a
 * side; four poles.
 */
class RainbowBridgeTest {

    private static final List<Solid> SOLIDS = RainbowBridge.solids();
    private static final double WATER = -Rivers.DEPTH + Rivers.WATER;

    private static long count(final String name) {
        return SOLIDS.stream().filter(s -> s.name().equals(name)).count();
    }

    @Test
    void itsArchOfTwentyOneGroupsElevenOfFourAndTenOfThreeTiedByFiveLogsAcross() {
        assertEquals(22, count("arch four long"), "two long members to each of 11 groups");
        assertEquals(22, count("arch four short"), "two short");
        assertEquals(20, count("arch three side"), "two side members to each of 10 groups");
        assertEquals(10, count("arch three top"), "one top");
        assertEquals(5, count("arch across"));
        assertEquals(2 * RainbowBridge.FOOT, 2_000, 1, "its span between its end walls");
        // the outermost groups of four: the first and the last across are even
        assertEquals(0, (RainbowBridge.GROUPS - 1) % 2);
    }

    @Test
    void itsCrownSomeFiveMetresOverTheWaterItsDeckMeetingTheStreetNoSteeperThanARamp() {
        final double soffit = RainbowBridge.deck(0) - RainbowBridge.BOARD - RainbowBridge.LOG - WATER;
        assertTrue(soffit > 480 && soffit < 560, "its arch's crown over the water: " + soffit);
        assertEquals(0, RainbowBridge.deck(RainbowBridge.REACH), 1e-9);
        assertEquals(0, RainbowBridge.deck(-RainbowBridge.REACH), 1e-9);
        assertEquals(0, Reach.ground().height(0, RainbowBridge.REACH), 30, "the street where it lands, north");
        assertEquals(0, Reach.ground().height(0, -RainbowBridge.REACH), 30, "and south");
        double steepest = 0;
        for (double z = -RainbowBridge.REACH; z < RainbowBridge.REACH; z += 10) {
            steepest = Math.max(steepest, Math.abs(RainbowBridge.deck(z + 10) - RainbowBridge.deck(z)) / 10);
        }
        assertTrue(steepest < Math.tan(Math.toRadians(35)), "its deck's steepest: " + steepest);
    }

    @Test
    void walkedUpAndOverTheRiversPassageOpenUnderIt() {
        final Volume deck = RainbowBridge.shape().getFirst();
        assertEquals(RainbowBridge.CROWN, deck.topAt(0, 0, 20), 5, "its crown walked on");
        assertEquals(RainbowBridge.deck(600), deck.topAt(0, 600, 20), 30, "its slope walked up");
        // in the passage, over the water: its floor the bed, its soffit over a boat's head
        final double floor = deck.topAt(0, 0, 20, WATER + 100);
        final double soffit = deck.undersideAt(0, 0, 20, WATER + 100);
        assertTrue(floor < WATER, "the passage's floor under the water: " + floor);
        assertTrue(soffit > WATER + 400, "4 m clear over the water: " + (soffit - WATER));
    }

    @Test
    void twentyThreePostsASideFourPoles() {
        assertEquals(2 * (RainbowBridge.POSTS - 2), count("post"));
        assertEquals(4, count("end post"));
        assertEquals(4, count("pole"));
    }
}
