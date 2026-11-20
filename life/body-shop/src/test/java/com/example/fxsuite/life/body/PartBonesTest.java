package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The game's skeleton, part by part: every part on the right or down the middle has its bones, a left part its
 * right's; the chest its bones and its cartilages apart; the finer the grid, the more triangles; a level found once and
 * kept; the forearm's radius turned with it, reaching the wrist.
 */
class PartBonesTest {

    private final SkeletonLevels levels = new SkeletonLevels();
    private final Detail coarse = new Detail(1.6);

    @Test
    void everyPartHasItsBonesALeftOneItsRights() {
        final Map<Part, List<PartBones.Piece>> level = levels.at(coarse);
        for (final Part p : Part.values()) {
            if (p.side() >= 0) {
                assertFalse(level.get(p).isEmpty(), p + " has bones");
                assertTrue(level.get(p).stream().allMatch(piece -> piece.mesh().triangleCount() > 0), p.said());
            } else {
                assertSame(levels.of(p.other(), coarse), levels.of(p, coarse), "the left " + p.said() + " the right's");
            }
        }
        assertEquals(java.util.Set.of(Paint.IVORY, Paint.CARTILAGE),
                level.get(Part.CHEST).stream().map(PartBones.Piece::paint).collect(java.util.stream.Collectors.toSet()),
                "the chest's bones and its cartilages, apart");
    }

    @Test
    void theFinerTheGridTheMoreTrianglesEachLevelKept() {
        final long few = levels.triangles(coarse);
        final long more = levels.triangles(new Detail(1.0));
        assertTrue(more > 2 * few, few + " at 16 mm, " + more + " at 10 mm");
        assertSame(levels.at(coarse), levels.at(new Detail(1.6)), "found once, kept");
    }

    @Test
    void theRadiusTurnedWithTheForearmReachesTheWrist() {
        final Mesher.Mesh forearm = levels.of(Part.RIGHT_FOREARM, coarse).getFirst().mesh();
        final V3 wrist = Rig.twisted(Rig.RADIUS_BOTTOM, Puppet.TWIST, 1);
        double nearest = Double.MAX_VALUE;
        final float[] p = forearm.points();
        for (int i = 0; i < p.length; i += 3) {
            nearest = Math.min(nearest, new V3(p[i], p[i + 1], p[i + 2]).minus(wrist).length());
        }
        assertTrue(nearest < 1.5, "the radius's foot where the rig twists it: " + nearest + " cm off");
    }
}
