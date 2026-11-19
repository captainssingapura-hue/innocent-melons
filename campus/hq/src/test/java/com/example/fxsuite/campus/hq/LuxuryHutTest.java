package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every hut of luxury on the campus, the main office's and each department's: on its plinth, its steps up to it before
 * its door each a step's height, none overlapping; its canopy over its door and under its eave; furnished within its
 * walls, standing on its floor, nothing in its doorway.
 */
class LuxuryHutTest {

    /** The main office's hut and each department's. */
    static List<LuxuryHut> all() {
        final List<LuxuryHut> all = new ArrayList<>(List.of(MainOffice.HUT));
        Department.ALL.forEach(d -> all.add(d.hut()));
        return all;
    }

    @Test
    void onItsPlinthItsStepsUpToItBeforeItsDoorEachAStepHigh() {
        final int steps = (int) Math.round(LuxuryHut.PLINTH / LuxuryHut.STEP);
        assertEquals(LuxuryHut.PLINTH, steps * LuxuryHut.STEP, 1e-9, "risers all one height");
        for (final LuxuryHut hut : all()) {
            final List<Solid> treads = hut.grounds().stream().filter(s -> s.name().startsWith("step ")).toList();
            assertEquals(steps - 1, treads.size(), hut.name() + ": a step fewer than its risers, the plinth the top one");
            for (int s = 1; s < steps; s++) {
                final int n = s;
                final Solid step = treads.stream().filter(t -> t.name().equals("step " + n)).findFirst().orElseThrow();
                assertEquals(LuxuryHut.PLINTH - s * LuxuryHut.STEP, top(step), 1e-9, hut.name() + ", step " + s);
                assertTrue(hut.stepTo(s) > hut.plinth(), hut.name() + ", step " + s + " before the plinth");
                // the next step down begins where this one ends: no two of them overlapping, no faces in one plane
                if (s > 1) {
                    assertEquals(hut.stepTo(s - 1), hut.stepFrom(s), 1e-9, hut.name());
                }
            }
            assertTrue(hut.stepsWide() > hut.size().door(), hut.name() + ": its steps wider than its door");
        }
    }

    @Test
    void itsCanopyOverItsDoorAndUnderItsEaveItsColumnsOnItsPlinth() {
        for (final LuxuryHut hut : all()) {
            final double door = LuxuryHut.PLINTH + hut.size().doorHigh();
            assertTrue(hut.canopy() > door, hut.name() + ": its canopy over its door");
            // its eave's underside at the wall's top, at the least: its walls' height, less its roof's thickness
            assertTrue(hut.canopy() + LuxuryHut.CANOPY_THICK < LuxuryHut.PLINTH + hut.size().walls() - 30,
                    hut.name() + ": its canopy under its eave");
            assertTrue(hut.porchOut() + LuxuryHut.COLUMN / 2 < hut.plinth(), hut.name() + ": its columns on its plinth");
            assertTrue(hut.porchSide() - LuxuryHut.COLUMN / 2 > hut.size().door() / 2, hut.name()
                    + ": its columns either side of its door, not in it");
        }
    }

    @Test
    void furnishedWithinItsWallsOnItsFloorItsDoorwayClear() {
        for (final LuxuryHut hut : all()) {
            final double inside = hut.inside() - LuxuryHut.CLEAR;
            // the doorway, from its middle out through the walls, so wide: nothing stands in it
            final double door = hut.size().door() / 2 + 20;
            for (final Solid s : hut.furnished()) {
                for (final Point p : s.corners()) {
                    assertTrue(Math.hypot(p.across(), p.along()) <= inside + 1e-6,
                            hut.name() + ", " + s.name() + ": within its walls");
                    assertTrue(p.up() >= LuxuryHut.FLOOR - 1e-9, hut.name() + ", " + s.name() + ": on its floor");
                }
                if (!s.name().equals("rug")) {
                    assertFalse(s.corners().stream().anyMatch(p -> Math.abs(p.across()) < door
                            && p.along() > hut.inside() - 150), hut.name() + ", " + s.name() + ": not in its doorway");
                }
            }
        }
    }

    @Test
    void itsFurnitureInTheWayEachPieceAsItsFootprint() {
        for (final LuxuryHut hut : all()) {
            final long pieces = switch (hut.interior()) {
                case LuxuryHut.Executive e -> 1 + 1 + 2 + 1 + 4 + 1;
                case LuxuryHut.Team t -> 1 + 1 + 1 + 2L * t.seats() + (t.sofa() == 0 ? 0 : 1);
            };
            assertEquals(pieces, hut.footprints().size(), hut.name() + ": its desk, its chairs, its tables, its sofa");
        }
    }

    private static double top(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
