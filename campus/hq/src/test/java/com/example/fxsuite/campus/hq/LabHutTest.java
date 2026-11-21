package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The labs' libraries on the campus: each a hut in its own quarter, wholly on its plot; standing on the lowest of the
 * ground round it, the slope no more than its plinth takes up; its door to the road, clear of it; finished as no
 * department is; a piece of the campus by its own key.
 */
class LabHutTest {

    @Test
    void eachWhollyOnItsQuartersPlot() {
        for (final LabHut lab : LabHut.ALL) {
            final Quarter.Plot plot = lab.inQuarter().plots().getFirst();
            final double reach = lab.hut().reach();
            for (int k = 0; k < 16; k++) {
                final double a = 2 * Math.PI * k / 16;
                final double x = lab.east() + reach * Math.sin(a);
                final double z = lab.north() + reach * Math.cos(a);
                assertTrue(plot.inside(x, z), lab.name() + ": on " + lab.quarter() + "'s plot all round");
            }
        }
    }

    @Test
    void onTheLowestOfItsGroundItsPlinthTakingUpTheSlope() {
        for (final LabHut lab : LabHut.ALL) {
            final Place at = lab.place();
            final double reach = lab.hut().reach();
            for (int k = 0; k < 32; k++) {
                final double a = 2 * Math.PI * k / 32;
                final double ground = Campus.land().height(at.x() + reach * Math.sin(a), at.z() + reach * Math.cos(a));
                assertTrue(ground >= at.height() - 2, lab.name() + ": nothing under it lower than its foot");
                assertTrue(ground <= at.height() + LuxuryHut.PLINTH - 10, lab.name() + ": the slope within its plinth");
            }
        }
    }

    @Test
    void itsDoorToTheRoadClearOfIt() {
        for (final LabHut lab : LabHut.ALL) {
            final Place at = lab.place();
            final Vec2 road = lab.nearestRoad();
            assertEquals(Math.toDegrees(Math.atan2(road.x() - at.x(), road.z() - at.z())), at.yaw(), 1e-9, lab.name());
            for (final Vec2 v : Village.line("village road", Campus.HOUSED)) {
                assertTrue(Math.hypot(v.x() - at.x(), v.z() - at.z()) > lab.hut().reach() + 1_000, lab.name());
            }
        }
    }

    @Test
    void namedOnceFinishedAsNoDepartmentIs() {
        final Set<String> names = new HashSet<>();
        final Set<LuxuryHut.Finish> finishes = new HashSet<>();
        finishes.add(MainOffice.HUT.finish());
        Department.ALL.forEach(d -> {
            names.add(d.name());
            finishes.add(d.hut().finish());
        });
        for (final LabHut lab : LabHut.ALL) {
            assertTrue(names.add(lab.name()), lab.name() + ": named once");
            assertTrue(finishes.add(lab.hut().finish()), lab.name() + ": finished as no other is");
        }
        assertTrue(names.contains("Body shop"));
    }

    @Test
    void eachAPieceOfTheCampusByItsOwnKey() {
        final List<Course.Piece> pieces = Campus.pieces();
        for (int k = 0; k < LabHut.ALL.size(); k++) {
            final int key = Campus.LABS + k;
            assertEquals(1, pieces.stream().filter(p -> p.tint() == key).count(), LabHut.ALL.get(k).name());
        }
    }
}
