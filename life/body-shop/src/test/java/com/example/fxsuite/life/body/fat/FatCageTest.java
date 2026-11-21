package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The fat's cage every body's the same, a lean one's and a fat one's: the same points, the same faces; the fatter
 * one's points further out; each, but where a line in met nothing, on its fat's outside.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FatCageTest {

    private final Rig bind = new Rig(Proportions.PAPERS, BoundPose.INSTANCE.pose(), false);
    private final Nearness near = new Nearness(FatBeneath.INSTANCE.of(MuscleBody.INSTANCE.make(Proportions.PAPERS,
            true, 1), bind));
    private final FatTarget leanTarget = new FatTarget(near, new FatMap(1.5), bind);
    private final FatTarget fatTarget = new FatTarget(near, new FatMap(12), bind);
    private final FatCage.Built lean = FatCage.INSTANCE.of(leanTarget);
    private final FatCage.Built fat = FatCage.INSTANCE.of(fatTarget);

    @Test
    void everyBodysCageHasTheSamePointsAndFaces() {
        assertEquals(lean.cage().size(), fat.cage().size());
        assertEquals(lean.cage().faces().size(), fat.cage().faces().size());
        for (int f = 0; f < lean.cage().faces().size(); f++) {
            assertArrayEquals(lean.cage().faces().get(f), fat.cage().faces().get(f));
        }
        assertArrayEquals(lean.regions(), fat.regions());
    }

    @Test
    void itClosesOverTheWholeBodyEveryEdgeBetweenTwoFaces() {
        final java.util.Map<Long, Integer> sides = new java.util.HashMap<>();
        for (final int[] f : lean.cage().faces()) {
            for (int k = 0; k < f.length; k++) {
                final long a = Math.min(f[k], f[(k + 1) % f.length]);
                final long b = Math.max(f[k], f[(k + 1) % f.length]);
                sides.merge(a << 32 | b, 1, Integer::sum);
            }
        }
        assertTrue(sides.values().stream().allMatch(n -> n == 2), "no hole, no edge shared by three");
        final java.util.Set<Long> ways = new java.util.HashSet<>();
        for (final int[] f : lean.cage().faces()) {
            for (int k = 0; k < f.length; k++) {
                final int next = f[(k + 1) % f.length];
                assertTrue(ways.add((long) f[k] << 32 | next), "each face round the same way out as its neighbours: "
                        + lean.cage().name(f[k]) + " to " + lean.cage().name(next));
            }
        }
        assertArrayEquals(lean.kept(), fat.kept(), "the same hands and feet");
    }

    @Test
    void theFatterOnesPointsLieFurtherOut() {
        int further = 0;
        for (int i = 0; i < lean.cage().size(); i++) {
            further += out(fat.cage().points().get(i)) > out(lean.cage().points().get(i)) ? 1 : 0;
        }
        assertTrue(further > 0.9 * lean.cage().size(), further + " of " + lean.cage().size() + " further out");
    }

    @Test
    void itsPointsLieOnTheFatsOutsideButTheHandsAndFeet() {
        int on = 0;
        int all = 0;
        for (int i = 0; i < lean.cage().size(); i++) {
            if (!lean.kept()[i]) {
                final V3 p = lean.cage().points().get(i);
                on += Math.abs(leanTarget.clearance(p, lean.regions()[i], p.x() < 0 ? -1 : 1)) < 0.02 ? 1 : 0;
                all++;
            }
        }
        assertTrue(on > 0.9 * all, on + " of " + all + " on the fat's outside");
    }

    /** How far {@code p} is out from what is beneath it, the muscles, the bones, the belly. */
    private double out(final V3 p) {
        return near.nearest(p, 20, i -> true).distance();
    }
}
