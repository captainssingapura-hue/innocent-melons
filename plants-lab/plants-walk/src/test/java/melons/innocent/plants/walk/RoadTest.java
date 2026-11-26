package melons.innocent.plants.walk;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A road that wanders. Its wandering keeps one drift for each section and takes another for the next; no step goes
 * further sideways than it may; and its volatility is not one figure but wanders itself, by degrees, through calm
 * stretches and twisting ones. Given the same chances it is the same road, and given none of anybody's choosing it
 * is another each time. The road begins straight behind its start, is smooth, never steeper than its steepest step,
 * and says the same of a place however much further it has been made since; it is made on ahead as it is reached
 * and lets go of what is far behind, so it cannot be asked about where it was long ago.
 */
class RoadTest {

    @Test
    void itsWanderingKeepsADriftForEachSectionAndItsVolatilityWandersToo() {
        final Wandering wandering = new Wandering(new Random(7));
        final Set<Double> drifts = new HashSet<>();
        final List<Double> volatilities = new ArrayList<>();
        double before = 0;
        for (int step = 0; step < 4_000; step++) {
            final double strayed = wandering.stepped();
            assertTrue(Math.abs(strayed) <= Wandering.STEEPEST * Wandering.STEP + 1e-9, "no step too steep");
            if (step % Wandering.SECTION != 0) {
                assertEquals(before, wandering.drift(), "one drift for the whole of a section");
            }
            before = wandering.drift();
            drifts.add(before);
            if (step > 0) {
                final double changed = wandering.volatility() / volatilities.getLast();
                assertTrue(changed > 0.3 && changed < 3.4, "the volatility moves by degrees: " + changed);
            }
            volatilities.add(wandering.volatility());
        }
        assertEquals(4_000 / Wandering.SECTION, drifts.size(), "a drift of its own for every section");
        final double calmest = volatilities.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
        final double wildest = volatilities.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        assertTrue(wildest > 8 * calmest, "calm stretches and twisting ones: " + calmest + " to " + wildest);
        assertEquals(new Wandering(new Random(7)).stepped(), new Wandering(new Random(7)).stepped(),
                "the same chances, the same road");
        assertNotEquals(new Wandering().stepped(), new Wandering().stepped(), "nobody's chances, another road");
    }

    @Test
    void theRoadIsSmoothSaysTheSameOfAPlaceAsItIsMadeOnAndGoesOnBothWaysTheSameWhenComeBackTo() {
        final Road road = new Road(new Wandering(new Random(11)));
        assertEquals(0, road.across(-20_000), "straight behind where the travelling begins");
        assertEquals(0, road.across(0));
        assertTrue(road.has(120_000) && road.has(-120_000) && !road.has(130_000) && !road.has(-130_000),
                "to hand some way each way, and no more");
        final double there = road.across(35_000);
        final double leaning = road.slope(35_000);
        double last = road.across(0);
        double turned = 0;
        for (double along = 50; along <= 40_000; along += 50) {
            final double here = road.across(along);
            assertTrue(Math.abs(here - last) <= Wandering.STEEPEST * 50 * 1.5 + 1e-6, "never steeper than its step");
            turned = Math.max(turned, Math.abs(road.slope(along) - road.slope(along - 50)));
            last = here;
        }
        assertTrue(turned < 0.2, "it turns nowhere sharply: " + turned);
        assertTrue(road.reached(100_000), "made on as it is reached");
        assertFalse(road.reached(100_000), "and not again till it is reached further");
        assertTrue(road.has(220_000) && road.has(-20_000) && !road.has(-30_000),
                "to hand each way of where it was reached, and what is far from it not");
        assertThrows(IllegalArgumentException.class, () -> road.across(-30_000));
        assertTrue(road.reached(-500_000), "made on back the other way, past where it began");
        assertTrue(road.has(-500_000) && road.across(-450_000) != 0, "and wandering there too");
        road.reached(100_000);
        assertEquals(there, road.across(35_000), 1e-9, "the same road when it is come back to");
        final Road again = new Road(new Wandering(new Random(11)));
        again.reached(20_000);
        assertEquals(there, again.across(35_000), 1e-9, "the same of a place however far it has been made since");
        assertEquals(leaning, again.slope(35_000), 1e-9);
        assertEquals(0, road.off(road.across(120_000), 120_000), 1e-9, "its middle is no way off it");
        assertTrue(road.off(road.across(120_000) + 500, 120_000) <= 500, "a spot beside it, no further than across");
    }
}
