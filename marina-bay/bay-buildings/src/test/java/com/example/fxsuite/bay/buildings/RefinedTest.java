package com.example.fxsuite.bay.buildings;

import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.lifts.Built;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.bay.land.MarinaBay;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bay's refined towers, one list: each named once, as its model names it; each on the bay's land, none standing
 * in another; each drawn by its own keys, none needing more than it is given.
 */
class RefinedTest {

    private static final List<Standing> STANDING = Refined.standing();

    @Test
    void eachNamedOnceAsItsModelNamesIt() {
        final HashSet<String> names = new HashSet<>();
        for (int k = 0; k < Refined.TOWERS.size(); k++) {
            final Refined.Tower t = Refined.TOWERS.get(k);
            assertTrue(names.add(t.name()), t.name() + " twice");
            assertEquals(t.name(), STANDING.get(k).tower().name());
        }
        assertEquals(24, names.size());
    }

    @Test
    void eachOnTheBaysLandNoneStandingInAnother() {
        final Land land = MarinaBay.land();
        final List<List<double[]>> feet = new ArrayList<>();
        for (final Standing s : STANDING) {
            assertTrue(land.height(s.at().x(), s.at().z()) > MarinaBay.SEA, s.tower().name() + " in the sea");
            final List<double[]> foot = new ArrayList<>();
            for (final double[] p : s.tower().outline()) {
                foot.add(s.world(p[0], p[1]));
            }
            feet.add(foot);
        }
        for (int i = 0; i < feet.size(); i++) {
            for (int j = i + 1; j < feet.size(); j++) {
                assertFalse(overlap(feet.get(i), feet.get(j)),
                        STANDING.get(i).tower().name() + " and " + STANDING.get(j).tower().name());
            }
        }
    }

    @Test
    void eachDrawnByItsOwnKeysNoneNeedingMoreThanItIsGiven() {
        for (int k = 0; k < STANDING.size(); k++) {
            final Standing s = STANDING.get(k);
            assertEquals(Refined.KEYS + k * Standing.KEYS, s.key());
            final Built built = s.built();
            assertTrue(built.keyed().size() + built.panes().size() <= Standing.KEYS, s.tower().name() + ": "
                    + (built.keyed().size() + built.panes().size()) + " keys");
        }
    }

    /** Whether two outlines overlap: an edge of one crossing one of the other's, or one inside the other. */
    private static boolean overlap(final List<double[]> a, final List<double[]> b) {
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (cross(a.get(i), a.get((i + 1) % a.size()), b.get(j), b.get((j + 1) % b.size()))) {
                    return true;
                }
            }
        }
        return inside(a.getFirst(), b) || inside(b.getFirst(), a);
    }

    private static boolean cross(final double[] p, final double[] q, final double[] r, final double[] s) {
        return side(p, q, r) * side(p, q, s) < 0 && side(r, s, p) * side(r, s, q) < 0;
    }

    private static double side(final double[] p, final double[] q, final double[] r) {
        return Math.signum((q[0] - p[0]) * (r[1] - p[1]) - (q[1] - p[1]) * (r[0] - p[0]));
    }

    static boolean inside(final double[] p, final List<double[]> outline) {
        boolean in = false;
        for (int i = 0, j = outline.size() - 1; i < outline.size(); j = i++) {
            final double[] a = outline.get(i);
            final double[] b = outline.get(j);
            if ((a[1] > p[1]) != (b[1] > p[1]) && p[0] < (b[0] - a[0]) * (p[1] - a[1]) / (b[1] - a[1]) + a[0]) {
                in = !in;
            }
        }
        return in;
    }

    /** How far {@code p} is from the nearest edge of {@code outline}. */
    static double clearance(final double[] p, final List<double[]> outline) {
        double least = Double.POSITIVE_INFINITY;
        for (int i = 0; i < outline.size(); i++) {
            final double[] a = outline.get(i);
            final double[] b = outline.get((i + 1) % outline.size());
            final double dx = b[0] - a[0];
            final double dz = b[1] - a[1];
            final double t = Math.clamp(((p[0] - a[0]) * dx + (p[1] - a[1]) * dz) / (dx * dx + dz * dz), 0, 1);
            least = Math.min(least, Math.hypot(p[0] - a[0] - t * dx, p[1] - a[1] - t * dz));
        }
        return least;
    }
}
