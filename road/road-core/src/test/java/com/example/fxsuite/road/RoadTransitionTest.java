package com.example.fxsuite.road;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The road's profile an attribute at a time, each in its natural order; a transition of each, from its lower value to
 * its higher and never between equal ones, a function along its length, that length the engineers' for the road's
 * design.
 */
class RoadTransitionTest {

    private static final double CLOSE = 0.05;
    private static final Transitions TRANSITIONS = Transitions.INSTANCE;

    private final Taper taper = new Taper(new Width(0), new Width(3.5));
    private final RunOff runOff = new RunOff(new Crossfall(-2.5), new Crossfall(2.5));
    private final VerticalCurve curve = new VerticalCurve(new Grade(-2), new Grade(3));

    @Test
    void eachAttributeInItsNaturalOrder() {
        assertEquals(List.of(new Width(0), new Width(3), new Width(3.5)),
                List.of(new Width(3.5), new Width(0), new Width(3)).stream().sorted().toList());
        assertEquals(List.of(new Crossfall(-2.5), new Crossfall(0), new Crossfall(6)),
                List.of(new Crossfall(6), new Crossfall(-2.5), new Crossfall(0)).stream().sorted().toList());
        assertEquals(List.of(new Grade(-4), new Grade(1), new Grade(2)),
                List.of(new Grade(2), new Grade(-4), new Grade(1)).stream().sorted().toList());
        assertThrows(IllegalArgumentException.class, () -> new Width(-1));
    }

    @Test
    void fromItsLowerValueToItsHigherNeverBetweenEqualOnes() {
        assertThrows(IllegalArgumentException.class, () -> new Taper(new Width(3.5), new Width(3.5)));
        assertThrows(IllegalArgumentException.class, () -> new Taper(new Width(3.5), new Width(3)));
        assertThrows(IllegalArgumentException.class, () -> new RunOff(new Crossfall(2), new Crossfall(-2)));
        assertThrows(IllegalArgumentException.class, () -> new VerticalCurve(new Grade(1), new Grade(1)));
        // a narrowing is a widening laid the other way; between equal widths there is nothing to lay
        assertEquals(Optional.of(new Taper(new Width(3), new Width(3.5))),
                TRANSITIONS.between(new Width(3.5), new Width(3), Taper::new));
        assertEquals(Optional.empty(), TRANSITIONS.between(new Width(3.5), new Width(3.5), Taper::new));
        assertEquals(Optional.of(new RunOff(new Crossfall(-2), new Crossfall(2))),
                TRANSITIONS.between(new Crossfall(2), new Crossfall(-2), RunOff::new));
    }

    @Test
    void aFunctionAlongItsLength() {
        for (final RoadTransition<?> transition : List.of(taper, runOff, curve)) {
            rises(transition);
            assertThrows(IllegalArgumentException.class, () -> transition.at(1.5));
            assertThrows(IllegalArgumentException.class, () -> transition.at(-0.1));
        }
        // the taper's edge leaves and meets the widths along them; the run-off and the curve change evenly
        assertTrue(taper.at(0.05).metres() < 0.05 * 3.5 / 2);
        assertEquals(1.75, taper.at(0.5).metres(), 1e-9);
        assertEquals(0, runOff.at(0.5).percent(), 1e-9);
        assertEquals(1, curve.at(0.6).percent(), 1e-9);
    }

    @Test
    void itsLengthTheEngineersForTheRoadsDesign() {
        final Design street = new Design(50, 7);
        final Design highway = new Design(100, 7.3);
        // a lane added, 3.5 metres: 3.5 x 50 x 50 / 155 below 70 km/h; 3.5 x 100 / 1.6 above
        assertEquals(56.45, taper.length(street), CLOSE);
        assertEquals(218.75, taper.length(highway), CLOSE);
        // half of 7 metres turned 5 percent, its edge at 0.65 percent against the centre line at 50 km/h
        assertEquals(26.92, runOff.length(street), CLOSE);
        assertEquals(41.48, runOff.length(highway), CLOSE);
        // a crest's rate at 100 km/h is near 52 metres for each percent; never shorter than 0.6 metres a km/h
        assertEquals(258.0, curve.length(highway), 1.0);
        assertEquals(60, new VerticalCurve(new Grade(0), new Grade(0.1)).length(highway), CLOSE);
    }

    @Test
    void betweenTwoProfilesATransitionOfEachAttributeThatDiffers() {
        // a street onto a bridge's deck: narrowed, its camber run off level, rising onto it
        final RoadProfile street = new RoadProfile(new Width(7), new Crossfall(-2.5), new Grade(0));
        final RoadProfile deck = new RoadProfile(new Width(5), new Crossfall(0), new Grade(4));
        assertEquals(List.of(new Taper(new Width(5), new Width(7)), new RunOff(new Crossfall(-2.5), new Crossfall(0)),
                new VerticalCurve(new Grade(0), new Grade(4))), street.to(deck));
        assertEquals(street.to(deck), deck.to(street));
        final RoadProfile wider = new RoadProfile(new Width(10.5), new Crossfall(-2.5), new Grade(0));
        assertEquals(List.of(new Taper(new Width(7), new Width(10.5))), street.to(wider));
        assertEquals(List.of(), street.to(street));
    }

    private <A extends RoadProfileAttribute<A>> void rises(final RoadTransition<A> transition) {
        assertEquals(transition.lower(), transition.at(0));
        assertEquals(transition.higher(), transition.at(1));
        A last = transition.at(0);
        for (int step = 1; step <= 100; step++) {
            final A next = transition.at(step / 100.0);
            assertTrue(last.compareTo(next) <= 0, "rises all along");
            last = next;
        }
    }
}
