package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.worlds.Meadow;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The main shared departments: each named once, each in a hut of luxury of its own, no two finished alike; standing in
 * a ring round the main office, each its door to it, on level ground, clear of one another, of the main office and
 * of the roads; each a piece of the campus by its own key.
 */
class DepartmentTest {

    @Test
    void eachNamedOnceEachFinishedOtherwise() {
        final Set<String> names = new HashSet<>();
        final Set<LuxuryHut.Finish> finishes = new HashSet<>();
        for (int k = 0; k < Department.ALL.size(); k++) {
            final Department d = Department.ALL.get(k);
            assertEquals(k + 1, d.number());
            assertTrue(names.add(d.name()), d.name() + ": named once");
            assertTrue(finishes.add(d.hut().finish()), d.name() + ": finished as no other is");
            assertTrue(d.hut().interior() instanceof LuxuryHut.Team, d.name() + ": a team's room");
        }
        assertTrue(finishes.add(MainOffice.HUT.finish()), "the main office finished as none of them is");
        assertTrue(names.containsAll(List.of("Bench UI Substrate", "Generic Object Interaction")));
    }

    @Test
    void inARingRoundTheMainOfficeEachItsDoorToIt() {
        for (final Department d : Department.ALL) {
            final Place at = d.place();
            assertEquals(Department.RING, Math.hypot(at.x() - MainOffice.EAST, at.z() - MainOffice.NORTH), 1e-6,
                    d.name());
            final double toMain = Math.toDegrees(Math.atan2(MainOffice.EAST - at.x(), MainOffice.NORTH - at.z()));
            assertEquals(toMain, at.yaw(), 1e-9, d.name() + ": its door to the main office");
        }
    }

    @Test
    void onLevelGroundClearOfOneAnotherTheMainOfficeAndTheRoads() {
        final List<Department> all = Department.ALL;
        for (int i = 0; i < all.size(); i++) {
            final Department d = all.get(i);
            final Place at = d.place();
            final double reach = d.hut().reach();
            for (int j = i + 1; j < all.size(); j++) {
                final Place other = all.get(j).place();
                assertTrue(Math.hypot(other.x() - at.x(), other.z() - at.z()) > reach + all.get(j).hut().reach() + 300,
                        d.name() + " and " + all.get(j).name() + ": 3 m clear");
            }
            final Place main = MainOffice.place();
            assertTrue(Math.hypot(main.x() - at.x(), main.z() - at.z()) > reach + MainOffice.HUT.reach() + 1_500,
                    d.name() + ": 15 m of court between it and the main office");
            for (final String road : List.of("village road")) {
                for (final Vec2 v : Village.line(road, Campus.HOUSED)) {
                    assertTrue(Math.hypot(v.x() - at.x(), v.z() - at.z()) > reach + 1_000, d.name() + ", " + road);
                }
            }
            for (int k = 0; k < 16; k++) {
                final double a = 2 * Math.PI * k / 16;
                assertEquals(at.height(), Meadow.land(Campus.HOUSED).height(at.x() + reach * Math.sin(a),
                                at.z() + reach * Math.cos(a)),
                        5, d.name() + ": level under it, all round");
            }
        }
    }

    @Test
    void eachAPieceOfTheCampusByItsOwnKey() {
        final List<Course.Piece> campus = Campus.pieces();
        for (final Department d : Department.ALL) {
            final List<Course.Piece> its = campus.stream().filter(p -> p.tint() == Campus.key(d)).toList();
            assertEquals(1, its.size(), d.name());
            assertEquals(d.place().x(), its.getFirst().block().x(), 1e-9, d.name());
        }
    }
}
