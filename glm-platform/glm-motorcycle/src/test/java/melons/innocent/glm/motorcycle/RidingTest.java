package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Where the motorcycle's rider meets it, by its own measures, whoever rides it: the seat as high as a standard bike's;
 * each peg under the rider's knee, ahead of where they sit and as far under it as the leg bends easily, high enough
 * to lean on; the grips ahead of the seat and over it, as far apart as the reference bike's bars; each pedal ahead of
 * its peg, where the toe reaches it; and each side's the same as the other's, across the middle.
 */
class RidingTest {

    private final Motorcycle bike = new Motorcycle();
    private final BikeFigures figures = bike.figures();
    private final Riding riding = bike.riding();

    @Test
    void itsSeatAsHighAsAStandardBikesAndItsPegsUnderTheKnee() {
        final Vec3 seat = riding.seat();
        within(75, 82, over(seat), "the seat's height");
        for (final int side : new int[] {-1, 1}) {
            final Vec3 peg = riding.peg(side);
            within(30, 38, over(peg), "the peg's height");
            within(5, 20, peg.x() - seat.x(), "how far ahead of the seat the peg is");
            within(40, 50, seat.y() - peg.y(), "how far under the seat the peg is");
        }
    }

    @Test
    void itsGripsAheadOfTheSeatOverItAndAsFarApartAsItsBars() {
        final Vec3 seat = riding.seat();
        for (final int side : new int[] {-1, 1}) {
            final Vec3 grip = riding.grip(side);
            within(35, 55, grip.x() - seat.x(), "how far ahead of the seat the grip is");
            within(18, 30, grip.y() - seat.y(), "how far over the seat the grip is");
        }
        within(60, 90, riding.grip(1).z() - riding.grip(-1).z(), "how far apart the grips are");
    }

    @Test
    void eachPedalAheadOfItsPegWhereTheToeReachesIt() {
        final Vec3 left = riding.peg(-1);
        final Vec3 right = riding.peg(1);
        within(10, 18, riding.gearLever().x() - left.x(), "how far ahead of the peg the gear lever is");
        within(2, 8, riding.gearLever().y() - left.y(), "how far over the peg the gear lever's toe goes");
        within(10, 18, riding.brakePedal().x() - right.x(), "how far ahead of the peg the brake pedal is");
        within(0, 5, riding.brakePedal().y() - right.y(), "how far over the peg the brake pedal is");
        assertTrue(riding.gearLever().z() < figures.centre() && riding.brakePedal().z() > figures.centre(),
                "the gear lever on the left, the brake pedal on the right");
    }

    @Test
    void eachSideTheSameAsTheOthers() {
        for (final Vec3[] pair : new Vec3[][] {{riding.peg(-1), riding.peg(1)}, {riding.grip(-1), riding.grip(1)}}) {
            assertEquals(pair[0].x(), pair[1].x(), 1e-9);
            assertEquals(pair[0].y(), pair[1].y(), 1e-9);
            assertEquals(figures.centre() - pair[0].z(), pair[1].z() - figures.centre(), 1e-9);
        }
        assertEquals(figures.centre(), riding.seat().z(), 1e-9);
    }

    @Test
    void itsPegsLetItLeanFarBeforeTheyTouchTheGround() {
        final double out = riding.peg(1).z() - figures.centre();
        final double lean = Math.toDegrees(Math.atan2(over(riding.peg(1)) - 2.2, out + 4.5));
        assertTrue(lean > 35, "it leans " + lean + " degrees before its pegs' ends touch");
    }

    private double over(final Vec3 p) {
        return p.y() - figures.ground();
    }

    private void within(final double low, final double high, final double was, final String what) {
        assertTrue(was >= low && was <= high, what + " " + was + ", not within " + low + " and " + high);
    }
}
