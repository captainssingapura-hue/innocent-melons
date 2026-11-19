package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.worlds.Meadow;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The campus's quarters, their ground only: the platform and a quarter a lab, as the organisation has its units, each
 * in a cover of its own; their plots convex, apart, clear of the road; the platform taking in the main office and every
 * department; the land covered so where they lie and nowhere else, its shape as it was.
 */
class QuarterTest {

    @Test
    void thePlatformAndAQuarterALabEachInACoverOfItsOwn() {
        assertEquals(List.of("Platform", "Landscape", "Networks", "Structures", "Machines", "Vehicles", "Life",
                "Objects"), Quarters.ALL.stream().map(Quarter::name).toList());
        final Set<Land.Cover> grounds = new HashSet<>();
        for (final Quarter q : Quarters.ALL) {
            assertTrue(grounds.add(q.ground()), q.name() + ": a cover of its own");
            assertFalse(List.of(Land.Cover.EARTH, Land.Cover.GRAVEL, Land.Cover.ASPHALT).contains(q.ground()),
                    q.name() + ": not a road's or a bank's");
        }
    }

    @Test
    void itsPlotsConvexApartAndClearOfTheRoad() {
        final List<Quarter.Plot> all = new ArrayList<>();
        Quarters.ALL.forEach(q -> all.addAll(q.plots()));
        for (final Quarter.Plot p : all) {
            final List<Vec2> o = p.outline();
            for (int k = 0; k < o.size(); k++) {
                final Vec2 a = o.get(k);
                final Vec2 b = o.get((k + 1) % o.size());
                final Vec2 c = o.get((k + 2) % o.size());
                assertTrue((b.x() - a.x()) * (c.z() - b.z()) - (b.z() - a.z()) * (c.x() - b.x()) > 0,
                        p.what() + ": convex, anticlockwise, at its corner " + (k + 1));
            }
        }
        for (int i = 0; i < all.size(); i++) {
            for (int j = i + 1; j < all.size(); j++) {
                assertTrue(apart(all.get(i), all.get(j)), all.get(i).what() + " and " + all.get(j).what());
            }
        }
        final double road = Village.wide("village road") / 2 + 200;
        for (final Quarter.Plot p : all) {
            for (final Vec2 v : Village.line("village road", Campus.HOUSED)) {
                assertFalse(p.inside(v.x(), v.z()) || p.fromEdge(v.x(), v.z()) < road,
                        p.what() + ": clear of the road at " + v);
            }
        }
    }

    @Test
    void thePlatformTakesInTheMainOfficeAndEveryDepartment() {
        final Quarter.Plot platform = Quarters.ALL.getFirst().plots().getFirst();
        final List<Place> places = new ArrayList<>(List.of(MainOffice.place()));
        final List<Double> reaches = new ArrayList<>(List.of(MainOffice.HUT.reach()));
        Department.ALL.forEach(d -> {
            places.add(d.place());
            reaches.add(d.hut().reach());
        });
        for (int k = 0; k < places.size(); k++) {
            final Place at = places.get(k);
            assertTrue(platform.inside(at.x(), at.z()) && platform.fromEdge(at.x(), at.z()) > reaches.get(k),
                    "building " + k + ": all of it on the platform");
        }
    }

    @Test
    void theLandCoveredWhereTheyLieAndNowhereElseItsShapeAsItWas() {
        final Land campus = Campus.land();
        final Land meadow = Meadow.land(Campus.HOUSED);
        for (final Quarter q : Quarters.ALL) {
            for (final Quarter.Plot p : q.plots()) {
                final Vec2 m = p.middle();
                assertEquals(1, campus.cover(q.ground(), m.x(), m.z()), 1e-9, p.what() + ": all its cover");
                for (final Quarter other : Quarters.ALL) {
                    if (other != q) {
                        assertEquals(0, campus.cover(other.ground(), m.x(), m.z()), 1e-9, p.what());
                    }
                }
                assertEquals(meadow.height(m.x(), m.z()), campus.height(m.x(), m.z()), 1e-9, p.what() + ": its shape");
            }
        }
        // at the well, on the green, none of them: the meadow's grass
        for (final Quarter q : Quarters.ALL) {
            assertEquals(0, campus.cover(q.ground(), Village.WELL.x(), Village.WELL.z()), 1e-9, q.name());
        }
        // thinning out into the grass at a plot's edge
        final Quarter.Plot structures = Quarters.ALL.get(3).plots().getFirst();
        final Vec2 edge = structures.outline().getFirst();
        final double half = campus.cover(Land.Cover.SAND, edge.x() + Quarter.EDGE / 2, edge.z() + 10_000);
        assertTrue(half > 0.3 && half < 0.7, "half its cover half way in from its edge: " + half);
    }

    /** Whether two convex outlines have a line between them: one's edge with the other all on its far side. */
    private static boolean apart(final Quarter.Plot a, final Quarter.Plot b) {
        return separates(a.outline(), b.outline()) || separates(b.outline(), a.outline());
    }

    private static boolean separates(final List<Vec2> one, final List<Vec2> other) {
        for (int k = 0; k < one.size(); k++) {
            final Vec2 p = one.get(k);
            final Vec2 q = one.get((k + 1) % one.size());
            final boolean allOutside = other.stream()
                    .allMatch(v -> (q.x() - p.x()) * (v.z() - p.z()) - (q.z() - p.z()) * (v.x() - p.x()) < 0);
            if (allOutside) {
                return true;
            }
        }
        return false;
    }
}
