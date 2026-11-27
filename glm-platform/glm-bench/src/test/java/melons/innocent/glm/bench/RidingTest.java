package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.motorcycle.BikeFigures;
import melons.innocent.glm.motorcycle.Motorcycle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The motorcycle leaned on the bench: over to its right as the lean is more, its tyres' crowns staying on the ground;
 * leaned as far as 35 degrees either way, as bikes of its kind lean some 32, nothing but its tyres touching; leaned
 * farther, what reaches lowest on that side touching.
 */
class RidingTest {

    private final Riding riding = new Riding();
    private final BikeFigures figures = new Motorcycle().figures();

    @Test
    void leanedToItsRightAsTheLeanIsMoreItsCrownsOnTheGround() {
        final Mat4 m = riding.leaned(30);
        final Vec3 seat = moved(m, new Vec3(0, 80, figures.centre()));
        assertTrue(seat.z() > figures.centre(), "its top over to its right, toward +z");
        assertEquals(80 * Math.cos(Math.toRadians(30)), seat.y(), 4, "lowered as far as it is leaned");
        final double crown = BikeFigures.REAR.wide() / 2;
        final double out = crown * Math.sin(Math.toRadians(30));
        final Vec3 under = moved(m, new Vec3(0, crown - crown * Math.cos(Math.toRadians(30)),
                figures.centre() + out));
        assertEquals(0, under.y(), 1e-9, "the back tyre's crown, where it then meets the ground, on it");
    }

    @Test
    void leanedAsFarAs35DegreesEitherWayNothingButItsTyresTouchingFartherSomethingDoes() {
        final Assembled built = riding.assembled();
        for (final double lean : List.of(0.0, -35.0, 35.0)) {
            assertEquals(List.of(), riding.touching(built, lean), "leaned " + lean);
        }
        assertTrue(!riding.touching(built, -Riding.MOST).isEmpty() && !riding.touching(built, Riding.MOST).isEmpty());
    }

    private Vec3 moved(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
    }
}
