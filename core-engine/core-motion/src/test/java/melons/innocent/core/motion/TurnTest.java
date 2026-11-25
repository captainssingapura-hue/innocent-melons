package melons.innocent.core.motion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A turning about any line. Not turned, a way is as it was. A quarter turn about the upright takes what lay along to
 * lie across, and about the way along takes what lay across to stand up, each as a screw goes in looking along the
 * line. One turning after another is one turning, and a turning and its undoing are none. A way keeps its length
 * however it is turned, and a line of no length is no line to turn about.
 */
class TurnTest {

    private static final double QUARTER = Math.PI / 2;

    @Test
    void aQuarterTurnAboutALineTakesAWayRoundAsAScrewGoesIn() {
        final Spot along = new Spot(0, 0, 1);
        same(along, new Turn().turned(along));
        same(new Spot(1, 0, 0), new Turn(new Spot(0, 1, 0), QUARTER).turned(along));
        same(new Spot(0, 1, 0), new Turn(new Spot(0, 0, 1), QUARTER).turned(new Spot(1, 0, 0)));
        same(new Spot(0, 0, 3), new Turn(new Spot(5, 0, 0), QUARTER).turned(new Spot(0, 3, 0)));
    }

    @Test
    void oneTurningAfterAnotherIsOneAndATurningWithItsUndoingIsNone() {
        final Turn first = new Turn(new Spot(0, 1, 0), QUARTER);
        final Turn second = new Turn(new Spot(1, 0, 0), QUARTER);
        final Spot way = new Spot(2, -3, 5);
        same(second.turned(first.turned(way)), first.then(second).turned(way));
        same(way, first.then(first.back()).turned(way));
        same(way, first.back().turned(first.turned(way)));
        assertEquals(way.length(), first.then(second).turned(way).length(), 1e-12);
    }

    @Test
    void aSpotSaysHowMuchOfItLiesAlongAnotherAndItsWayOneLong() {
        assertEquals(11, new Spot(1, 2, 3).dot(new Spot(4, -1, 3)), 1e-12);
        same(new Spot(0.6, 0, 0.8), new Spot(3, 0, 4).unit());
        assertThrows(IllegalArgumentException.class, () -> new Spot(0, 0, 0).unit());
        assertThrows(IllegalArgumentException.class, () -> new Turn(new Spot(0, 0, 0), 1));
    }

    private void same(final Spot wanted, final Spot found) {
        assertEquals(wanted.across(), found.across(), 1e-12);
        assertEquals(wanted.up(), found.up(), 1e-12);
        assertEquals(wanted.along(), found.along(), 1e-12);
    }
}
