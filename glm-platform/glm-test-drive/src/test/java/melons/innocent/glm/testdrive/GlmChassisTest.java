package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Lift;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.model.Proportions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The GLM car as the avatar engine knows it: its wheels where the car's are, its track and its wheelbase; the car as
 * made carried to where it is driven, its front axle over its own wheels' and its tyres on the ground; and driven
 * flat out on level ground, from nought to 100 km/h in about six seconds, straight ahead.
 */
class GlmChassisTest {

    private final Proportions suv = new Proportions();
    private final GlmChassis glm = new GlmChassis(suv);

    @Test
    void itsWheelsWhereTheCarsAre() {
        final Course.Chassis chassis = glm.chassis();
        assertEquals(suv.halfTrack(), chassis.wheelOut(), 1e-9, "its track");
        assertEquals(suv.wheelbase(), chassis.wheelbase(), 1e-9, "its wheelbase");
        assertEquals(suv.tyre(), chassis.wheelRadius(), 1e-9, "its tyres");
    }

    @Test
    void carriedItsFrontAxleOverItsOwnAndOnTheGround() {
        final Place at = new Place(500, 800, glm.ride() + 40, 90);
        final Mat4 m = glm.carried(at, 0, 0);
        final Vec3 axle = new Vec3(m.at(0, 3), m.at(1, 3), m.at(2, 3));
        assertEquals(0, axle.minus(new Vec3(500 + glm.front(), 40, 800)).length(), 1e-9,
                "headed along +x, the front axle ahead of the middle, its tyres on the ground 40 cm up");
    }

    @Test
    void drivenFlatOutFromNoughtToAHundredInAboutSixSeconds() {
        Lift lift = new Lift(-1, glm.chassis(), new Place(0, 0, glm.ride(), 0), 0, 0, 0, true, true, 0, 0, 0, 0);
        final Controls flatOut = new Controls(1, 0, 0, 0, true, false, 0, List.of());
        double seconds = 0;
        while (lift.ahead() < 100 / 0.036 && seconds < 20) {
            lift = lift.step(flatOut, Clock.STEP, Obstacles.NONE, List.of());
            seconds += Clock.STEP;
        }
        assertTrue(seconds > 5 && seconds < 7.5, "0-100 km/h in " + seconds + " s");
        assertEquals(0, lift.place().x(), 1e-6, "straight ahead");
        assertTrue(lift.place().z() > 50 * 100, "a good way along: " + lift.place().z());
    }
}
