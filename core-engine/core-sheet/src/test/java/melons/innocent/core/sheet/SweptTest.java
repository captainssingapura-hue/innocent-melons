package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A sheet swept along a line says its height and its rise over any spot exactly: a strip, a spiral ramp passing over
 * itself, a vault and a dome, each held against what it is worked out to be by hand, and each one's rise against
 * the difference of its heights a little either side.
 */
class SweptTest {

    /** A spiral ramp: nine metres out, two turns to the right from due along, 3.6 m a turn, tilted across. */
    private final Swept ramp = new Swept(new Round(new Spot(0, 0, 0), 900, 0, 2, 360), new Banked(-200, 200, 0.04));
    /** A vault ten metres across and 2.2 m high, fourteen metres long, heading along. */
    private final Swept vault = new Swept(new Straight(new Spot(0, 0, 0), 0, 1_400), new Arched(500, 220));
    /** A dome six metres out and 2.5 m high: an arch swept round its rim, from the rim in to its middle. */
    private final Swept dome = new Swept(new Round(new Spot(0, 0, 0), 600, 0, 1, 0),
            new Arched(0, 600, 600, (600.0 * 600 + 250.0 * 250) / 500, 250));

    /** The one pass of {@code sheet} over a spot. */
    private Lift only(final Sheet sheet, final double across, final double along) {
        final List<Lift> lifts = sheet.over(across, along);
        assertEquals(1, lifts.size(), "passes over " + across + ", " + along);
        return lifts.getFirst();
    }

    /** How high {@code sheet}'s pass {@code layer} is over a spot. */
    private double high(final Sheet sheet, final int layer, final double across, final double along) {
        return sheet.over(across, along).stream().filter(lift -> lift.layer() == layer).findFirst().orElseThrow()
                .high();
    }

    /** That a pass's rise each way is the difference of its heights a little either side, over how far apart. */
    private void risesAsItsHeightsDiffer(final Sheet sheet, final int layer, final double across,
                                         final double along) {
        final double by = 1e-3;
        final Lift lift = sheet.over(across, along).stream().filter(pass -> pass.layer() == layer).findFirst()
                .orElseThrow();
        assertEquals((high(sheet, layer, across + by, along) - high(sheet, layer, across - by, along)) / (2 * by),
                lift.acrossRise(), 1e-7);
        assertEquals((high(sheet, layer, across, along + by) - high(sheet, layer, across, along - by)) / (2 * by),
                lift.alongRise(), 1e-7);
    }

    @Test
    void aStraightTiltedStripIsAsHighAsItsGradeAndItsTiltMakeItAndFacesAsBoth() {
        final Swept strip = new Swept(new Straight(new Spot(100, 50, 0), 90, 2_000, 0.08),
                new Banked(-300, 300, -0.025));
        // heading across: a spot 5 m on and a metre to its right, which is back along
        final Lift lift = only(strip, 600, -100);
        assertEquals(50 + 500 * 0.08 - 100 * 0.025, lift.high(), 1e-9);
        assertEquals(0.08, lift.acrossRise(), 1e-12);
        assertEquals(0.025, lift.alongRise(), 1e-12);
        assertEquals(200, lift.within(), 1e-9);
        assertEquals(Math.toDegrees(Math.atan(Math.hypot(0.08, 0.025))), lift.steep(), 1e-9);
        assertTrue(strip.over(600, 400).isEmpty(), "off its edge");
        assertTrue(strip.over(50, 0).isEmpty(), "before its start");
    }

    @Test
    void aSpiralRampPassesOverASpotOnceInEachTurnEachPassATurnsClimbHigher() {
        // a quarter of the way round, on the line and half a metre inside it
        final List<Lift> lifts = ramp.over(850, 0);
        assertEquals(2, lifts.size());
        assertEquals(90 + 50 * 0.04, lifts.get(0).high(), 1e-9);
        assertEquals(360, lifts.get(1).high() - lifts.get(0).high(), 1e-9);
        assertEquals(List.of(0, 1), lifts.stream().map(Lift::layer).toList());
        assertEquals(150, lifts.get(0).within(), 1e-9);
        // at its start it is passed three times: where it begins, a turn on, and at its end
        assertEquals(3, ramp.over(0, 900).size());
        assertTrue(ramp.over(0, 0).isEmpty(), "its middle is open");
    }

    @Test
    void aSpiralRampClimbsFasterNearerItsMiddle() {
        final Lift inner = ramp.over(0, -700).getFirst();
        final Lift outer = ramp.over(0, -1_100).getFirst();
        // going on round is toward less across there; the same climb a turn over a shorter way round
        assertEquals(-360 / (2 * Math.PI * 700), inner.acrossRise(), 1e-12);
        assertEquals(-360 / (2 * Math.PI * 1_100), outer.acrossRise(), 1e-12);
        assertEquals(0.04, inner.alongRise(), 1e-12);
    }

    @Test
    void everySheetRisesAsItsHeightsDifferALittleEitherSide() {
        risesAsItsHeightsDiffer(ramp, 0, 640, -520);
        risesAsItsHeightsDiffer(ramp, 1, -300, 950);
        risesAsItsHeightsDiffer(vault, 0, 310, 700);
        risesAsItsHeightsDiffer(dome, 0, 220, -330);
        risesAsItsHeightsDiffer(new Warped(800, 600, 150), 0, 120, -210);
    }

    @Test
    void aRampIsOneSurfaceAcrossWhereATurnEndsAndTheNextBegins() {
        // either side of where it started, a turn up: the same height to within what a hair's breadth climbs
        final double before = ramp.over(-1e-4, 900).get(0).high();
        final double after = ramp.over(1e-4, 900).get(1).high();
        assertEquals(360, (before + after) / 2, 1e-6);
        assertEquals(2e-4 * 360 / (2 * Math.PI * 900), after - before, 1e-9);
    }

    @Test
    void aVaultIsHighestAlongItsMiddleAndFallsAwayEachSideAlike() {
        assertEquals(220, only(vault, 0, 700).high(), 1e-9);
        assertEquals(0, only(vault, 0, 700).steep(), 1e-9);
        assertEquals(0, only(vault, 500, 700).high(), 1e-9);
        assertEquals(only(vault, 300, 200).high(), only(vault, -300, 1_200).high(), 1e-9);
        assertEquals(-only(vault, 300, 200).acrossRise(), only(vault, -300, 200).acrossRise(), 1e-12);
        assertEquals(0, only(vault, 300, 200).alongRise(), 1e-12);
    }

    @Test
    void aDomeIsARingWithNoEndsHighestAndLevelAtItsMiddle() {
        final Lift top = only(dome, 0, 0);
        assertEquals(250, top.high(), 1e-9);
        assertEquals(0, top.steep(), 1e-9);
        assertEquals(600, top.within(), 1e-9);
        assertEquals(0, only(dome, 0, 600).high(), 1e-9);
        // the same all the way round, and no edge where the ring closes
        assertEquals(only(dome, 300, 0).high(), only(dome, -180, 240).high(), 1e-9);
        assertEquals(300, only(dome, 1e-7, 300).within(), 1e-6);
        assertEquals(300, only(dome, -1e-7, 300).within(), 1e-6);
    }

    @Test
    void everySpotASheetIsDrawnByIsOnIt() {
        for (final Sheet sheet : List.of(ramp, vault, dome, new Warped(800, 600, 150))) {
            for (final double far : new double[] {0.03, 0.37, 0.71, 0.96}) {
                for (final double over : new double[] {0.1, 0.5, 0.9}) {
                    final Spot at = sheet.at(far, over);
                    assertTrue(sheet.over(at.across(), at.along()).stream()
                            .anyMatch(lift -> Math.abs(lift.high() - at.up()) < 1e-6), sheet + " at " + at);
                    assertTrue(at.length() <= sheet.reaches() + 1e-6);
                }
            }
        }
    }
}
