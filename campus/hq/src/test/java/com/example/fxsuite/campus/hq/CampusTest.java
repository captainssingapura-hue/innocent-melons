package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.bench.kit.Played;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.worlds.Meadow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The campus: laid on the meadow bare of its village's huts, all else of the meadow as it is; its main office and its
 * departments standing on it, each a piece by its own key; laid as a world to play.
 */
class CampusTest {

    /** Whether {@code p} is one of the village's huts: a building drawn by one of their keys. */
    private static boolean hut(final Course.Piece p) {
        return p.kind() == Course.Kind.BUILDING && p.tint() >= Village.KEYS
                && p.tint() < Village.KEYS + Village.VILLAGE.size();
    }

    @Test
    void onTheMeadowBareOfItsHutsAllElseAsItIs() {
        final List<Course.Piece> housed = Meadow.pieces(true);
        final List<Course.Piece> campus = Campus.pieces();
        assertEquals(Village.VILLAGE.size(), housed.stream().filter(CampusTest::hut).count(), "the meadow's huts");
        assertEquals(0, campus.stream().filter(CampusTest::hut).count(), "none of them on the campus");
        assertEquals(housed.size() - Village.VILLAGE.size() + 1 + Department.ALL.size() + LabHut.ALL.size(), campus.size(),
                "all else of the meadow, its main office, its departments and its labs' libraries, nothing more");
        assertEquals(1, campus.stream().filter(c -> c.tint() == Village.KEYS + Village.CHURCH).count(), "its church");
        assertEquals(1, campus.stream().filter(c -> c.tint() == Campus.MAIN).count(), "its main office, once");
    }

    @Test
    void itLaysAsAWorldToPlay() {
        final Course course = new Course(new Played(new CampusWorld(), Played.Guests.of(List.of())));
        assertTrue(course.pieces().stream().anyMatch(p -> p.tint() == Campus.MAIN), "its main office");
        assertTrue(course.pieces().stream().noneMatch(CampusTest::hut), "none of the village's huts");
        assertEquals("north", CampusBench.compass(0));
        assertEquals("east", CampusBench.compass(90));
        assertEquals("south-west", CampusBench.compass(-135));
    }
}
