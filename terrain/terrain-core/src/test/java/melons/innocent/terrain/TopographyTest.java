package melons.innocent.terrain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The ground of a topographic map: on every contour and spot height, as high as it; between two contours, never past
 * either, and going the one way; smooth across a contour, its slope the same both sides; level between two contours
 * as high as each other; a top nobody marked rising past its last contour, and a hollow sinking, by no more than half
 * the last rise; level inside a contour marked level; the map's edge as high as outside every contour; a hill drawn on
 * a slope between two contours rising from the lower; on a break line, as high as the line, and turning there.
 */
class TopographyTest {

    private static final double M = 100;
    private static final Extent EXTENT = new Extent(-2_000 * M, -2_000 * M, 2_000 * M, 2_000 * M);

    /** A loop round the middle, {@code metres} across, its spots every few degrees. */
    private Loop circle(final double metres) {
        final List<Spot> round = new ArrayList<>();
        for (int k = 0; k < 120; k++) {
            final double a = 2 * Math.PI * k / 120;
            round.add(new Spot(metres * M * Math.cos(a), metres * M * Math.sin(a)));
        }
        return new Loop(round);
    }

    private Contour contour(final double metres, final double radius) {
        return new Contour(metres * M, circle(radius));
    }

    /** A hill: 10 m, 20 m and 30 m contours, 1000, 600 and 200 m round its middle, marked 35 m at its top. */
    private final Topography hill = new Topography("a hill", EXTENT, 0,
            List.of(contour(10, 1_000), contour(20, 600), contour(30, 200)),
            List.of(new SpotHeight(new Spot(0, 0), 35 * M)), List.of());

    @Test
    void onEveryContourAndSpotHeightAsHighAsIt() {
        for (final Contour contour : hill.contours()) {
            for (final Spot spot : contour.loop().spots()) {
                assertEquals(contour.height(), hill.height(spot.x(), spot.z()), 0.1);
            }
        }
        assertEquals(35 * M, hill.height(0, 0), 0.1);
    }

    @Test
    void betweenTwoContoursNeverPastEitherGoingTheOneWay() {
        double before = Double.MAX_VALUE;
        for (double r = 600; r <= 990; r += 10) {
            final double h = hill.height(r * M * Math.cos(0.3), r * M * Math.sin(0.3));
            assertTrue(h >= 10 * M - 0.1 && h <= 20 * M + 0.1, "between 10 m and 20 m at " + r + " m: " + h);
            assertTrue(h <= before + 0.1, "falling outward at " + r + " m");
            before = h;
        }
    }

    @Test
    void smoothAcrossAContourItsSlopeTheSameBothSides() {
        final double step = 2 * M;
        final double at = 600 * M;
        final double inside = (hill.height(at - step, 0) - hill.height(at - 2 * step, 0)) / step;
        final double outside = (hill.height(at + 2 * step, 0) - hill.height(at + step, 0)) / step;
        assertTrue(inside < 0 && outside < 0, "falling outward both sides");
        assertEquals(1, inside / outside, 0.15, "the slope just inside as just outside: " + inside + ", " + outside);
    }

    @Test
    void levelBetweenTwoContoursAsHighAsEachOther() {
        final Topography terrace = new Topography("a terrace", EXTENT, 0,
                List.of(contour(10, 1_000), contour(10, 500), contour(20, 200)), List.of(), List.of());
        for (double r = 520; r <= 980; r += 40) {
            assertEquals(10 * M, terrace.height(r * M, 0), 0.1, "level at " + r + " m");
        }
        assertTrue(terrace.height(300 * M, 0) > 10 * M, "rising inside the 10 m contour within it");
    }

    @Test
    void aTopNobodyMarkedRisingByHalfTheLastRiseAHollowSinking() {
        final Topography knoll = new Topography("a knoll", EXTENT, 0,
                List.of(contour(10, 1_000), contour(20, 500)), List.of(), List.of());
        assertTrue(knoll.height(0, 0) > 20 * M && knoll.height(0, 0) <= 25 * M + 0.1, "" + knoll.height(0, 0));
        final Topography pit = new Topography("a pit", EXTENT, 0,
                List.of(contour(10, 1_000), contour(0, 500)), List.of(), List.of());
        assertTrue(pit.height(0, 0) < 0 && pit.height(0, 0) >= -5 * M - 0.1, "" + pit.height(0, 0));
    }

    @Test
    void onABreakLineAsHighAsTheLineTurningThere() {
        // a valley down the hill's side: a thalweg from its 20 m contour out to its 10 m one, 4 m under the ground
        final BreakLine valley = new BreakLine("a gully", BreakLine.Kind.THALWEG,
                List.of(new Spot(0, 600 * M), new Spot(0, 800 * M), new Spot(0, 1_000 * M)),
                List.of(20 * M, 11 * M, 10 * M));
        final Topography gullied = new Topography("a gullied hill", EXTENT, 0, hill.contours(), hill.spotHeights(),
                List.of(valley));
        assertEquals(11 * M, gullied.height(0, 800 * M), 0.1);
        final double left = gullied.height(-30 * M, 800 * M);
        final double right = gullied.height(30 * M, 800 * M);
        assertTrue(left > 11 * M + 10 && right > 11 * M + 10, "rising both sides: " + left + ", " + right);
        assertTrue(gullied.height(-300 * M, 800 * M) <= hill.height(-300 * M, 800 * M) + 10,
                "the gully's own, scarcely felt so far off");
    }

    @Test
    void seenAtAScaleAnEvenSlopeStaysAGrooveShallows() {
        final Ground slope = new Ground() {
            public Extent extent() {
                return EXTENT;
            }

            public double height(final double x, final double z) {
                return 0.1 * x + 0.05 * z;
            }
        };
        assertEquals(slope.height(1_000, 2_000), new Seen(slope, 50 * M).height(1_000, 2_000), 1e-6);
        final Ground groove = new Ground() {
            public Extent extent() {
                return EXTENT;
            }

            public double height(final double x, final double z) {
                return Math.abs(x) < 10 * M ? -300 : 0;
            }
        };
        assertEquals(-300, groove.height(0, 0));
        assertTrue(new Seen(groove, 50 * M).height(0, 0) > -150, "a groove 20 m wide, seen at 50 m, shallowed");
        assertEquals(-300, new Seen(groove, 0).height(0, 0));
    }

    @Test
    void levelInsideALevelContourTheEdgeAsHighAsTheOutsideAHillOnASlopeRisingFromTheLower() {
        final Topography floor = new Topography("a floor", EXTENT, 100 * M,
                List.of(contour(10, 1_000), new Contour(0, circle(500), true)), List.of(), List.of());
        assertEquals(0, floor.height(0, 0), 0.1, "level at its middle");
        assertEquals(0, floor.height(300 * M, 100 * M), 0.1, "and all over it");
        assertEquals(100 * M, floor.height(2_000 * M, 0), 0.1, "the map's edge as high as outside every contour");
        assertTrue(floor.height(1_500 * M, 0) > 10 * M, "rising from the outermost contour to the edge");
        // a valley: the 25 m contour round the 10 m one; a knoll drawn between them, its 30 m loop 1.1 km out
        final List<Spot> knoll = new ArrayList<>();
        for (int k = 0; k < 36; k++) {
            final double a = 2 * Math.PI * k / 36;
            knoll.add(new Spot((1_100 + 100 * Math.cos(a)) * M, 100 * M * Math.sin(a)));
        }
        final Topography slope = new Topography("a slope", EXTENT, 40 * M,
                List.of(contour(25, 1_600), contour(10, 700), new Contour(30 * M, new Loop(knoll))),
                List.of(new SpotHeight(new Spot(1_100 * M, 0), 34 * M)), List.of());
        final double below = slope.height(850 * M, 0);
        assertTrue(below > 10 * M && below < 25 * M, "on the slope below the knoll, between 10 m and 25 m: " + below);
        assertTrue(slope.height(950 * M, 0) > below, "rising toward the knoll");
        assertEquals(34 * M, slope.height(1_100 * M, 0), 0.1);
    }

    @Test
    void writtenAndReadBackTheSame() {
        final Topography read = TopographyText.INSTANCE.parse(TopographyText.INSTANCE.text(hill));
        assertEquals(TopographyText.INSTANCE.text(hill), TopographyText.INSTANCE.text(read));
        assertEquals(hill.height(123 * M, 456 * M), read.height(123 * M, 456 * M), 0.5);
    }
}
