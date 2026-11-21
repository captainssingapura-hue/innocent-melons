package com.example.fxsuite.vulkan.view;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A bench's way of looking, moved by the mouse as the engine's own benches are: dragged, it turns round the point it
 * looks at and never past straight up or down; carried, what is seen follows the mouse; scrolled, it comes nearer or
 * goes further in proportion, within its limits.
 */
class OrbitTest {

    private final Vec3 lookedAt = new Vec3(10, 20, 30);
    private final Orbit orbit = new Orbit(lookedAt, -35, 22, 900);

    @Test
    void draggedItTurnsFourTenthsOfADegreeForEachUnitAndStaysAsFarFromThePoint() {
        final Orbit dragged = orbit.dragged(10, 5);
        assertEquals(-31, dragged.yaw(), 1e-9);
        assertEquals(24, dragged.pitch(), 1e-9);
        assertEquals(lookedAt, dragged.round());
        assertEquals(900, dragged.camera(40, 1).eye().minus(lookedAt).length(), 1e-9);
    }

    @Test
    void draggedHoweverFarItLooksNoSteeperThanItMay() {
        assertEquals(89, orbit.dragged(0, 10_000).pitch());
        assertEquals(-89, orbit.dragged(0, -10_000).pitch());
    }

    @Test
    void carriedWhatWasLookedAtFollowsTheMouseAcrossAndDown() {
        final Camera right = orbit.carried(50, 0).camera(40, 1);
        final Camera down = orbit.carried(0, 50).camera(40, 1);
        final Vec3 across = right.seen(1).clip(right.from(lookedAt)).shown();
        final Vec3 lower = down.seen(1).clip(down.from(lookedAt)).shown();
        assertTrue(across.x() > 0.01 && Math.abs(across.y()) < 1e-9, "to the right of the middle: " + across);
        assertTrue(lower.y() > 0.01 && Math.abs(lower.x()) < 1e-9, "below the middle: " + lower);
    }

    @Test
    void carriedThePointMovesAsFarAsTheMouseAtNineMetresAndTwiceAsFarFromTwiceAsFarOff() {
        assertEquals(50, orbit.carried(50, 0).round().minus(lookedAt).length(), 1e-9);
        assertEquals(100, orbit.backed(2).carried(50, 0).round().minus(lookedAt).length(), 1e-9);
        assertEquals(new Vec3(10, 70, 30), new Orbit(lookedAt, 0, 0, 900).carried(0, 50).round());
    }

    @Test
    void scrolledItComesNearerOrGoesFurtherInProportionAndStopsAtItsLimits() {
        final double nearer = orbit.scrolled(1, 100, 5000).distance();
        final double further = orbit.scrolled(-1, 100, 5000).distance();
        assertEquals(900 / Math.pow(1.0015, 40), nearer, 1e-9);
        assertEquals(900 * 900, nearer * further, 1e-6, "a notch back undoes a notch forward");
        assertEquals(100, orbit.scrolled(1000, 100, 5000).distance());
        assertEquals(5000, orbit.scrolled(-1000, 100, 5000).distance());
        assertEquals(lookedAt, orbit.scrolled(3, 100, 5000).round());
    }
}
