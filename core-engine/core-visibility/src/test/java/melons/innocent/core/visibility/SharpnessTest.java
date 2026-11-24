package melons.innocent.core.visibility;

import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Spot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How sharply an observer could see a thing. Standing still it sees everything wholly sharp, and going fast it
 * still sees what is straight ahead so, while a thing close beside it is a blur and one further off less of one;
 * the faster, the less sharp, smoothly and never by a jump; a thing it is within is wholly sharp, and a large thing
 * is never less sharp than its sharpest part; turning blurs nothing; an eye that gathers for no time is never
 * blurred, and one that gathers longer is blurred sooner. The finest angle told apart is the eye's own and the
 * smear together.
 */
class SharpnessTest {

    /** A hundred and fifty kilometres an hour, in centimetres a second. */
    private static final double FAST = 150 * 100_000.0 / 3_600;

    private final Sharpness sharpness = new Sharpness();
    private final Spot here = new Spot(0, 170, 0);
    private final Motion fast = new Motion(here, new Spot(0, 0, FAST), 0);

    @Test
    void standingStillAllIsSharpAndGoingFastWhatIsAheadStillIsWhileWhatIsBesideIsABlur() {
        final Seen beside = new Seen(here.plus(new Spot(1_000, 0, 0)));
        final Seen further = new Seen(here.plus(new Spot(6_000, 0, 0)));
        final Seen ahead = new Seen(here.plus(new Spot(0, 0, 30_000)));
        assertEquals(1, sharpness.of(new Motion(here), beside), 1e-12, "still, wholly sharp");
        assertEquals(1, sharpness.of(fast, ahead), 1e-12, "dead ahead, as sharp as standing still");
        assertTrue(sharpness.of(fast, beside) < 0.01, "ten metres aside, a blur: " + sharpness.of(fast, beside));
        assertTrue(sharpness.of(fast, further) > 4 * sharpness.of(fast, beside), "sixty metres aside, less of one");
        assertEquals(Math.toDegrees(FAST / 1_000), sharpness.sweeps(fast, beside), 1e-6);
        assertEquals(1.0 / 60 + sharpness.sweeps(fast, beside) / 120, sharpness.told(fast, beside), 1e-9,
                "the eye's own finest angle and the smear together");
        assertEquals(sharpness.told(fast, beside), new Eye().finest() / sharpness.of(fast, beside), 1e-9);
    }

    @Test
    void theFasterTheLessSharpSmoothlyAndNeverByAJump() {
        final Seen aside = new Seen(here.plus(new Spot(2_000, 0, 4_000)));
        double last = 1;
        for (int kmh = 1; kmh <= 150; kmh++) {
            final double now = sharpness.of(new Motion(here, new Spot(0, 0, kmh * FAST / 150), 0), aside);
            assertTrue(now < last, "less sharp at " + kmh + " km an hour than a kilometre an hour slower");
            assertTrue(last - now < 0.2, "and by no jump: " + (last - now));
            last = now;
        }
    }

    @Test
    void aThingItIsWithinIsWhollySharpAndALargeThingAsSharpAsItsSharpestPart() {
        assertEquals(1, sharpness.of(fast, new Seen(here.plus(new Spot(500, 0, 0)), 1_800)), 1e-12, "within it");
        final Spot middle = here.plus(new Spot(2_500, 0, 5_000));
        final double whole = sharpness.of(fast, new Seen(middle, 1_800));
        for (int part = 0; part < 360; part += 15) {
            final Spot rim = middle.plus(new Spot(1_800 * Math.cos(Math.toRadians(part)), 0,
                    1_800 * Math.sin(Math.toRadians(part))));
            assertTrue(whole >= sharpness.of(fast, new Seen(rim)) - 1e-12, "no part of it sharper than the whole");
        }
        assertTrue(whole > sharpness.of(fast, new Seen(middle)), "and sharper than its middle taken alone");
    }

    @Test
    void overallItSeesAllStandingStillHalfAtTheSpeedItsEyeIsHalvedAtAndLessBeyondSmoothly() {
        assertEquals(1, sharpness.overall(new Motion(here)), 1e-12, "standing still, all of it");
        assertEquals(0.5, sharpness.overall(fast), 1e-9, "at a hundred and fifty kilometres an hour, half");
        assertEquals(2.0 / 3, sharpness.overall(new Motion(here, new Spot(0, 0, FAST / 2), 0)), 1e-9);
        assertEquals(sharpness.overall(fast), sharpness.overall(new Motion(here, new Spot(FAST, 0, 0), 45)), 1e-12,
                "whichever way it goes, and turning or not");
        double last = 1;
        for (int kmh = 1; kmh <= 300; kmh++) {
            final double now = sharpness.overall(new Motion(here, new Spot(0, 0, kmh * FAST / 150), 0));
            assertTrue(now < last && last - now < 0.01, "less at " + kmh + " km an hour, and by no jump");
            last = now;
        }
        assertEquals(1, new Sharpness(new Eye(1.0 / 60, 0, Double.POSITIVE_INFINITY)).overall(fast), 1e-12,
                "an eye that going fast costs nothing");
    }

    @Test
    void turningBlursNothingAndTheLongerAnEyeGathersTheSoonerItIsBlurred() {
        final Seen beside = new Seen(here.plus(new Spot(3_000, 0, 0)));
        assertEquals(sharpness.of(fast, beside), sharpness.of(new Motion(here, fast.going(), 90), beside), 1e-12,
                "turning as well, no less sharp");
        assertEquals(1, new Sharpness(new Eye(1.0 / 60, 0, 1)).of(fast, beside), 1e-12, "gathering for no time");
        assertTrue(new Sharpness(new Eye(1.0 / 60, 1.0 / 30, 1)).of(fast, beside) < sharpness.of(fast, beside));
        assertThrows(IllegalArgumentException.class, () -> new Eye(0, 0.01, 1));
        assertThrows(IllegalArgumentException.class, () -> new Seen(here, -1));
    }
}
