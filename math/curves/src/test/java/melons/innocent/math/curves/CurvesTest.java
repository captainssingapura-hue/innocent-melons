package melons.innocent.math.curves;

import com.example.fxsuite.math.existences.Curves;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Curves worked out: through what they are given, one value for each x over their span and none outside it, held at
 * their ends when asked; each the kind it says; a fold straight between its knots, a monotone one smooth through
 * them and never past them, a level stretch kept level; equal by what they are given.
 */
class CurvesTest {

    private static final double CLOSE = 1e-9;

    private final Knots bed = new Knots(List.of(new Knot(0, 10), new Knot(100, 4), new Knot(300, 3),
            new Knot(400, 3), new Knot(500, -2)));

    @Test
    void throughWhatTheyAreGivenOverTheirSpanAndNoneOutside() {
        final Line line = new Line(new Knot(-10, 0), new Knot(10, 5));
        assertEquals(2.5, line.at(0), CLOSE);
        assertEquals(0.25, line.slope(3), CLOSE);
        assertThrows(IllegalArgumentException.class, () -> line.at(10.5));
        assertEquals(5, line.held(1_000), CLOSE);
        assertEquals(0, line.held(-1_000), CLOSE);
        for (final CurveImpl<?> curve : List.of(new Fold(bed), new Monotone(bed))) {
            for (final Knot knot : bed.listed()) {
                assertEquals(knot.y(), curve.at(knot.x()), CLOSE, curve.kind().title() + " through " + knot);
            }
            assertThrows(IllegalArgumentException.class, () -> curve.at(-1));
        }
        assertThrows(IllegalArgumentException.class, () -> new Knots(List.of(new Knot(0, 0))));
        assertThrows(IllegalArgumentException.class, () -> new Knots(List.of(new Knot(0, 0), new Knot(0, 1))));
        assertThrows(IllegalArgumentException.class, () -> new Line(new Knot(1, 0), new Knot(1, 1)));
    }

    @Test
    void eachTheKindItSays() {
        assertEquals(new Curves.Linear(), new Line(new Knot(0, 0), new Knot(1, 1)).kind());
        assertEquals(new Curves.Folded(), new Fold(bed).kind());
        assertEquals(new Curves.MonotoneCubic(), new Monotone(bed).kind());
        assertTrue(Curves.TREE.has(new Monotone(bed).kind()));
    }

    @Test
    void aFoldStraightBetweenItsKnotsAMonotoneOneSmoothAndNeverPastThem() {
        final Fold fold = new Fold(bed);
        assertEquals(7, fold.at(50), CLOSE);
        assertEquals(-0.06, fold.slope(50), CLOSE);
        final Monotone smooth = new Monotone(bed);
        double last = smooth.at(0);
        for (double x = 1; x <= 500; x++) {
            final double next = smooth.at(x);
            assertTrue(next <= last + CLOSE, "falls or stays level all along, at " + x);
            last = next;
        }
        // level between its two knots at 3, it stays level there
        assertEquals(3, smooth.at(350), CLOSE);
        // no kink where two pieces meet: its slope the same either side of a knot
        assertEquals(smooth.slope(100 - 1e-3), smooth.slope(100 + 1e-3), 1e-3);
    }

    @Test
    void equalByWhatTheyAreGiven() {
        assertEquals(new Monotone(bed), new Monotone(new Knots(List.copyOf(bed.listed()))));
        assertEquals(new Line(new Knot(0, 1), new Knot(2, 3)), new Line(new Knot(0, 1), new Knot(2, 3)));
    }
}
