package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.bench.kit.Played;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Course;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The campus as it starts: every hut of the meadow's village an office, numbered once each; each furnished, its desk
 * and chair within its walls, at its back, clear of its door; laid on the meadow, its offices standing where the huts
 * stood and the huts themselves gone, all else of the meadow as it was.
 */
class CampusTest {

    @Test
    void everyHutAnOfficeNumberedOnceEach() {
        assertEquals(Village.VILLAGE.size(), Campus.OFFICES.size());
        for (int k = 0; k < Campus.OFFICES.size(); k++) {
            final Office o = Campus.OFFICES.get(k);
            assertEquals(k + 1, o.number());
            assertEquals("Office " + (k + 1), o.name());
            assertEquals(Village.VILLAGE.get(k).place(), o.place(), o.name() + " where its hut stands");
        }
    }

    @Test
    void eachFurnishedWithinItsWallsAtItsBackClearOfItsDoor() {
        for (final Office o : Campus.OFFICES) {
            final double inside = o.inside();
            for (final Solid s : o.furniture()) {
                for (final Point p : s.corners()) {
                    assertTrue(Math.hypot(p.across(), p.along()) <= inside - Office.CLEAR + 1e-6,
                            o.name() + ", " + s.name() + ": within its walls");
                    assertTrue(p.up() >= Office.FLOOR - 1e-9, o.name() + ", " + s.name() + ": on its floor");
                }
            }
            // its desk at its back; its chair before it, still behind the hut's middle, its door ahead well clear
            assertTrue(o.deskBack() < 0, o.name());
            assertTrue(o.chairAt() + Office.CHAIR / 2 < inside / 2, o.name() + ": its chair well back from its door");
            assertTrue(o.solids().size() > o.furniture().size(), o.name() + ": the hut as well as its furniture");
        }
    }

    @Test
    void laidOnTheMeadowItsOfficesWhereTheHutsStoodAllElseAsItWas() {
        final List<Course.Piece> meadow = com.example.fxsuite.avatar.worlds.Meadow.pieces();
        final List<Course.Piece> campus = Campus.pieces();
        final long huts = meadow.stream().filter(Campus::hut).count();
        assertEquals(Village.VILLAGE.size(), huts, "the meadow's huts, each a piece");
        assertEquals(0, campus.stream().filter(Campus::hut).count(), "none of them left on the campus");
        assertEquals(meadow.size() + 1, campus.size(), "an office in each one's place, and its main office, nothing else");
        assertEquals(1, campus.stream().filter(c -> c.tint() == Campus.MAIN).count(), "its main office, once");
        for (final Office o : Campus.OFFICES) {
            final Course.Piece p = campus.stream().filter(c -> c.tint() == Campus.key(o)).findFirst().orElseThrow();
            assertEquals(Course.Kind.BUILDING, p.kind());
            assertEquals(o.place().x(), p.block().x(), 1e-9, o.name());
            assertEquals(o.place().z(), p.block().z(), 1e-9, o.name());
        }
    }

    @Test
    void itLaysAsAWorldToPlay() {
        final Course course = new Course(new Played(new CampusWorld(), Played.Guests.of(List.of())));
        assertTrue(course.pieces().stream().anyMatch(p -> p.tint() == Campus.KEYS), "its first office");
        assertEquals("north", CampusBench.compass(0));
        assertEquals("east", CampusBench.compass(90));
        assertEquals("south-west", CampusBench.compass(-135));
    }
}
