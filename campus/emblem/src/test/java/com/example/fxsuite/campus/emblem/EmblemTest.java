package com.example.fxsuite.campus.emblem;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmblemTest {

    private static final double R = 100;
    private static final double E = 1e-6;

    private static Emblem whole() {
        return new Emblem(R, Set.of(), 0, false, false);
    }

    /** Each octant: three layers, a lune and a band at a time, and three faces' seeds. */
    private static final int PER_OCTANT = 3 * (90 / Emblem.STEP) * (90 / Emblem.STEP) + 3 * Emblem.SEEDS;

    @Test
    void wholeIsEightOctantsAndAHalo() {
        assertEquals(8 * PER_OCTANT, whole().solids().size());
        assertEquals(8 * PER_OCTANT + Emblem.HALO_PIECES, new Emblem(R, Set.of(), 0, false, true).solids().size());
    }

    @Test
    void octantsAreNumberedAndNamedByTheirSides() {
        assertEquals(0, Emblem.octant(false, false, false));
        assertEquals(7, Emblem.octant(true, true, true));
        assertEquals("upper south-east", Emblem.name(Emblem.octant(true, false, true)));
        assertEquals("lower north-west", Emblem.name(Emblem.octant(false, true, false)));
    }

    @Test
    void eachOctantsSkinIsOnItsOwnSide() {
        final Emblem e = whole();
        for (int o = 0; o < 8; o++) {
            for (final Solid s : e.octantSolids(o)) {
                if (s.name().endsWith("seed")) {
                    continue;
                }
                for (final Point p : s.corners()) {
                    assertTrue(Emblem.sign(o, 1) * p.across() >= -E, s.name() + " east or west");
                    assertTrue(Emblem.sign(o, 2) * p.along() >= -E, s.name() + " north or south");
                    assertTrue(Emblem.sign(o, 4) * (p.up() - e.middle()) >= -E, s.name() + " upper or lower");
                }
            }
        }
    }

    @Test
    void itsSeedsStandOutOfItsCutFacesALittle() {
        final Emblem e = whole();
        for (int o = 0; o < 8; o++) {
            for (final Solid s : e.seeds(o)) {
                for (final Point p : s.corners()) {
                    assertTrue(Emblem.sign(o, 1) * p.across() >= -Emblem.SEED_OUT - E, s.name());
                    assertTrue(Emblem.sign(o, 2) * p.along() >= -Emblem.SEED_OUT - E, s.name());
                    assertTrue(Emblem.sign(o, 4) * (p.up() - e.middle()) >= -Emblem.SEED_OUT - E, s.name());
                    assertTrue(distance(p, e) < R * (1 - Emblem.RIND - Emblem.CREAM), s.name() + " in its flesh");
                }
            }
        }
    }

    @Test
    void itsRindIsAtItsRadius() {
        final Emblem e = whole();
        double most = 0;
        for (final Solid s : e.solids()) {
            for (final Point p : s.corners()) {
                most = Math.max(most, distance(p, e));
            }
        }
        assertEquals(R, most, E);
    }

    @Test
    void aLiftedOctantIsMovedOutAlongItsDiagonal() {
        final int o = Emblem.octant(true, false, true);
        final double lift = 60;
        final List<Solid> still = whole().octantSolids(o);
        final List<Solid> all = new Emblem(R, Set.of(o), lift, false, false).solids();
        final List<Solid> moved = all.stream().filter(s -> s.name().startsWith(Emblem.name(o))).toList();
        assertEquals(still.size(), moved.size());
        final double d = lift / Math.sqrt(3);
        for (int k = 0; k < still.size(); k++) {
            final Point a = still.get(k).corners().get(0);
            final Point b = moved.get(k).corners().get(0);
            assertEquals(d, b.across() - a.across(), E);
            assertEquals(-d, b.along() - a.along(), E);
            assertEquals(d, b.up() - a.up(), E);
        }
        assertEquals(8 * PER_OCTANT, all.size());
    }

    @Test
    void cutAwayLeavesExactlyThoseOut() {
        final List<Solid> quarter = new Emblem(R, Emblem.QUARTER, 30, true, false).solids();
        assertEquals(6 * PER_OCTANT, quarter.size());
        for (final int o : Emblem.QUARTER) {
            assertTrue(quarter.stream().noneMatch(s -> s.name().startsWith(Emblem.name(o))));
        }
    }

    @Test
    void aQuarterCutOutLeavesItsUpperEastEmpty() {
        final Emblem e = new Emblem(R, Emblem.QUARTER, 0, true, false);
        for (final Solid s : e.solids()) {
            for (final Point p : s.corners()) {
                // the seeds of the faces left bare stand out of them a little, into it, as seeds do
                final double in = Emblem.SEED_OUT + E;
                final boolean inside = p.across() > in && p.up() - e.middle() > in;
                assertTrue(!inside, s.name() + " in the quarter cut out");
            }
        }
    }

    @Test
    void itsHaloIsOverItsTop() {
        final Emblem e = new Emblem(R, Set.of(), 0, false, true);
        for (final Solid s : e.haloSolids()) {
            for (final Point p : s.corners()) {
                assertTrue(p.up() > e.middle() + R, s.name());
            }
        }
    }

    @Test
    void itIsNotMadeOfOctantsItHasNot() {
        assertThrows(IllegalArgumentException.class, () -> new Emblem(R, Set.of(8), 0, false, false));
        assertThrows(IllegalArgumentException.class, () -> new Emblem(R, Set.of(), -1, false, false));
    }

    private static double distance(final Point p, final Emblem e) {
        return Math.sqrt(p.across() * p.across() + p.along() * p.along()
                + (p.up() - e.middle()) * (p.up() - e.middle()));
    }
}
