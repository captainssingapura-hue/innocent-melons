package com.example.fxsuite.qingming.world;

import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Demos;
import com.example.fxsuite.avatar.sim.Play;
import com.example.fxsuite.qingming.land.Reach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reach as a demo: told to the engine and found by its name; laid, its land as it lies, its river, the bridge's
 * deck standing in it, the avatar on the street; its cruiser and its boxer parked there, each to ride.
 */
class QingmingTest {

    @Test
    void toldAndFoundByItsName() {
        Qingming.tell();
        assertSame(Qingming.DEMO, Demos.named("QINGMING"));
    }

    @Test
    void laidItsLandItsRiverTheDeckTheAvatarOnTheStreet() {
        Demos.register(Qingming.DEMO);
        final Course course = new Course(Qingming.DEMO);
        assertFalse(course.land().level(), "its land as it lies");
        assertFalse(course.grid());
        assertEquals(1, course.rivers().size(), "the Bian");
        assertFalse(course.pieces().isEmpty(), "the bridge's deck");
        assertEquals(Reach.START, course.start());
        assertTrue(Math.abs(course.land().height(Reach.START.x(), Reach.START.z())) < 100, "the street level");
    }

    @Test
    void itsCruiserAndItsBoxerParkedOnTheStreetEachToRide() {
        Demos.register(Qingming.DEMO);
        final Play play = Play.start(Qingming.DEMO, true);
        assertEquals(Reach.BIKES.size(), play.lifts().stream().filter(l -> l.craft() instanceof Course.Chassis c
                && c.twoWheels()).count(), "each one to ride");
        assertTrue(play.lifts().stream().anyMatch(l -> l.craft() == Course.CRUISER), "the cruiser");
        assertTrue(play.lifts().stream().anyMatch(l -> l.craft() == Course.BOXER), "the boxer");
    }
}
