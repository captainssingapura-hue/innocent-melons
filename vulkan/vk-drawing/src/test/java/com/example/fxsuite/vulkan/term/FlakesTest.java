package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Flakes of metal in a paint. Seen close, a place either has a flake catching the sun or has not, and between them
 * all they come to one, on a flat face lying any way: so the glitter is neither brighter nor duller than the soft
 * shine it runs together into. Turned, other flakes catch the sun, and they still come to one. Far enough off for
 * a pixel to cover several, every place shows exactly one. The paint is as it is seen square on, and duller by
 * its flop at the very edge. And flakes that make no sense are refused.
 */
class FlakesTest {

    private static final int EACH_WAY = 500;
    private static final double STEP = 0.0137;

    private final Seen seen = new Seen();
    private final Flakes flakes = new Flakes(new Colour(1, 1, 1), 14, 0.5, 0.08, 0.35);

    @Test
    void seenCloseAPlaceHasAFlakeCatchingTheSunOrHasNotAndBetweenThemAllTheyComeToOne() {
        final double dark = 0.4;
        final double bright = 0.4 + 0.6 / flakes.share();
        // a level face, an upright one, and one lying askew to everything
        for (final Vec3[] face : new Vec3[][] {
                {new Vec3(1, 0, 0), new Vec3(0, 0, 1)}, {new Vec3(1, 0, 0), new Vec3(0, 1, 0)},
                {new Vec3(0.6, 0.8, 0), new Vec3(-0.48, 0.36, 0.8)}}) {
            final Reckoning caught = new Reckoning(flakes.caught(new Real(0.3)).node());
            double sum = 0;
            int lit = 0;
            for (int across = 0; across < EACH_WAY; across++) {
                for (int up = 0; up < EACH_WAY; up++) {
                    final Vec3 at = new Vec3(11.3, 3.3, -7.9).plus(face[0].times(across * STEP))
                            .plus(face[1].times(up * STEP));
                    final double here = caught.at(seen.sample(at, 0))[0];
                    assertTrue(Math.abs(here - dark) < 1e-9 || Math.abs(here - bright) < 1e-9, "one or the other");
                    sum += here;
                    lit += here > 1 ? 1 : 0;
                }
            }
            assertTrue(lit > 1000, "some places have a flake catching the sun: " + lit);
            assertEquals(1, sum / (EACH_WAY * EACH_WAY), 0.06, "between them all, one");
        }
    }

    @Test
    void turnedOtherFlakesCatchTheSunAndAWholeTurnBringsTheSameOnesRoundAgain() {
        final Reckoning first = new Reckoning(flakes.caught(new Real(0.1)).node());
        final Reckoning turned = new Reckoning(flakes.caught(new Real(0.6)).node());
        final Reckoning round = new Reckoning(flakes.caught(new Real(1.1)).node());
        int changed = 0;
        int lit = 0;
        for (int across = 0; across < EACH_WAY; across++) {
            for (int up = 0; up < EACH_WAY; up++) {
                final Sample at = seen.sample(new Vec3(across * STEP, 2, up * STEP), 0);
                lit += first.at(at)[0] > 1 ? 1 : 0;
                changed += first.at(at)[0] > 1 && turned.at(at)[0] < 1 ? 1 : 0;
                assertEquals(first.at(at)[0], round.at(at)[0], 1e-9, "a whole turn on, as it was");
            }
        }
        assertTrue(lit > 1000 && changed > lit / 2, "most that caught the sun no longer do: " + changed + " of " + lit);
    }

    @Test
    void farEnoughOffForAPixelToCoverSeveralEveryPlaceShowsExactlyOne() {
        final Reckoning caught = new Reckoning(flakes.caught(new Real(0.3)).node());
        for (int across = 0; across < EACH_WAY; across++) {
            assertEquals(1, caught.at(seen.sample(new Vec3(across * STEP, 5, 1), 0.16))[0], 1e-12);
        }
        final double part = caught.at(seen.sample(new Vec3(0.411, 5, 1), 0.1))[0];
        final double close = caught.at(seen.sample(new Vec3(0.411, 5, 1), 0))[0];
        assertTrue(Math.abs(part - 1) < Math.abs(close - 1), "part way off, part way to one");
    }

    @Test
    void thePaintIsAsItIsSeenSquareOnAndDullerByItsFlopAtTheVeryEdge() {
        assertEquals(1, flakes.dulled(new Real(1)).node().worked(new Sample())[0], 1e-12);
        assertEquals(0.5, flakes.dulled(new Real(0)).node().worked(new Sample())[0], 1e-12);
        assertEquals(1 - 0.5 * 0.25, flakes.dulled(new Real(0.5)).node().worked(new Sample())[0], 1e-12);
    }

    @Test
    void flakesThatMakeNoSenseAreRefused() {
        final Colour white = new Colour(1, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> new Flakes(white, 0, 0.5, 0.08, 0.35));
        assertThrows(IllegalArgumentException.class, () -> new Flakes(white, 14, 1.5, 0.08, 0.35));
        assertThrows(IllegalArgumentException.class, () -> new Flakes(white, 14, 0.5, 0, 0.35));
        assertThrows(IllegalArgumentException.class, () -> new Flakes(white, 14, 0.5, 0.08, 0));
    }
}
