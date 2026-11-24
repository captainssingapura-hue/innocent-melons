package melons.innocent.core.motion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An observer's motion, detected. Going steadily it comes to say that speed, in centimetres a second and in
 * kilometres an hour, and where the observer will be shortly; its speed is evened out, coming most of the way in a
 * quarter of a second and not at once, and at once where it is not evened; told the same steps it says the same.
 * Turning, it says how fast, the short way round. Set down elsewhere it is standing still there. And a thing beside
 * a fast observer sweeps across its view while one far ahead hardly moves, nearer things faster, and turning sweeps
 * everything alike.
 */
class MovingTest {

    private static final double STEP = 1.0 / 120;
    /** A hundred and fifty kilometres an hour, in centimetres a second. */
    private static final double FAST = 150 * 100_000.0 / 3_600;

    @Test
    void goingSteadilyItComesToSayThatSpeedAndWhereItWillBe() {
        final Moving moving = flown(new Moving(new Spot(0, 1_000, 0), 0), 480);
        final Motion motion = moving.motion();
        assertEquals(FAST, motion.speed(), 1e-3);
        assertEquals(150, motion.hourly(), 1e-4);
        assertEquals(0, motion.turning(), 1e-9);
        assertEquals(480 * STEP * FAST, motion.at().along(), 1e-6);
        final Spot shortly = motion.ahead(2);
        assertEquals(motion.at().along() + 2 * FAST, shortly.along(), 1e-2, "two seconds on, going on as it is");
        assertEquals(1_000, shortly.up(), 1e-6);
        assertEquals(0, new Motion(new Spot(5, 5, 5)).speed(), "one never told of a step stands still");
    }

    @Test
    void itsSpeedIsEvenedOutAndTheSameStepsSayTheSame() {
        final double after = flown(new Moving(new Spot(0, 0, 0), 0), 30).motion().speed();
        assertEquals(FAST * (1 - Math.exp(-1)), after, 1, "a quarter of a second in, most of the way to its speed");
        assertEquals(FAST, flown(new Moving(new Spot(0, 0, 0), 0, 0), 1).motion().speed(), 1e-6,
                "not evened, it says a step's speed at once");
        assertEquals(flown(new Moving(new Spot(0, 0, 0), 0), 77).motion(),
                flown(new Moving(new Spot(0, 0, 0), 0), 77).motion(), "the same steps, the same motion");
        assertThrows(IllegalArgumentException.class, () -> new Moving(new Spot(0, 0, 0), 0).stepped(
                new Spot(1, 0, 0), 0, 0));
    }

    @Test
    void turningItSaysHowFastTheShortWayRoundAndSetDownElsewhereItStandsStill() {
        final Moving moving = new Moving(new Spot(0, 0, 0), 350);
        double facing = 350;
        for (int step = 0; step < 480; step++) {
            facing = (facing + 90 * STEP) % 360;
            moving.stepped(new Spot(0, 0, 0), facing, STEP);
        }
        assertEquals(90, moving.motion().turning(), 1e-3, "ninety degrees a second, through nought and on");
        assertEquals(0, moving.motion().speed(), 1e-9);
        final Moving cut = flown(new Moving(new Spot(0, 0, 0), 0), 240);
        cut.placed(new Spot(90_000, 500, -3_000), 180);
        assertEquals(0, cut.motion().speed(), "set down, not flown there");
        assertEquals(90_000, cut.motion().at().across());
    }

    @Test
    void aThingBesideAFastObserverSweepsPastAndOneFarAheadHardlyMoves() {
        final Motion motion = flown(new Moving(new Spot(0, 0, 0), 0), 480).motion();
        final Spot here = motion.at();
        final double beside = motion.sweeps(here.plus(new Spot(1_000, 0, 0)));
        final double ahead = motion.sweeps(here.plus(new Spot(0, 0, 30_000)));
        final double aheadAside = motion.sweeps(here.plus(new Spot(1_000, 0, 30_000)));
        assertEquals(Math.toDegrees(FAST / 1_000), beside, 1e-3, "ten metres to one side: its speed over that");
        assertTrue(beside > 200, "which is hundreds of degrees a second: " + beside);
        assertEquals(0, ahead, 1e-6, "dead ahead, not at all");
        assertTrue(aheadAside < 0.3, "three hundred metres ahead and ten aside, hardly: " + aheadAside);
        assertTrue(motion.sweeps(here.plus(new Spot(2_000, 0, 0))) < beside, "twice as far off, slower");
        final Motion turned = new Motion(here, motion.going(), 30);
        assertEquals(30, turned.sweeps(here.plus(new Spot(0, 0, 30_000))), 1e-6, "turning sweeps what is ahead too");
        assertTrue(Double.isFinite(motion.sweeps(here)), "at the eye itself, still a number");
    }

    /** {@code moving} after so many steps along the world at a hundred and fifty kilometres an hour. */
    private Moving flown(final Moving moving, final int steps) {
        final Spot from = moving.motion().at();
        for (int step = 1; step <= steps; step++) {
            moving.stepped(from.plus(new Spot(0, 0, step * STEP * FAST)), 0, STEP);
        }
        return moving;
    }
}
