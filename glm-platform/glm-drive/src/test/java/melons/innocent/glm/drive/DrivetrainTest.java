package melons.innocent.glm.drive;

import com.example.fxsuite.avatar.sound.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A drivetrain driven: pulling away and changing up by itself, flat out; switched to manual and taken out of first
 * into neutral, revving free and coasting, then into first again; and its cruise control holding its speed, let go at
 * a touch of the brake.
 */
class DrivetrainTest {

    private static final double TICK = 1.0 / 60;

    @Test
    void pullingAwayAndChangingUpByItselfFlatOut() {
        final Drivetrain driven = new Drivetrain(Vehicle.SALOON);
        assertEquals("1", driven.gear());
        run(driven, new Driver(1), 12);
        assertTrue(driven.kmh() > 80, "under way: " + driven.kmh());
        assertTrue(Integer.parseInt(driven.gear()) >= 3, "changed up by itself: " + driven.gear());
    }

    @Test
    void manualOutOfFirstIntoNeutralRevvingFreeThenFirstAgain() {
        final Drivetrain driven = new Drivetrain(Vehicle.SALOON);
        driven.step(new Driver(0, 0, false, false, true, false, false, false), TICK);
        assertTrue(!driven.automatic(), "switched to manual");
        driven.step(new Driver(0, 0, false, true, false, false, false, false), TICK);
        assertEquals("N", driven.gear());
        run(driven, new Driver(1), 1.5);
        assertTrue(driven.rpm() > 4000, "revving free: " + driven.rpm());
        assertEquals(0, driven.kmh(), 1e-9, "going nowhere");
        driven.step(new Driver(0, 0, true, false, false, false, false, false), TICK);
        assertEquals("1", driven.gear());
    }

    @Test
    void itsCruiseControlHoldingItsSpeedLetGoAtTheBrake() {
        final Drivetrain driven = new Drivetrain(Vehicle.SALOON);
        run(driven, new Driver(1), 8);
        driven.step(new Driver(0, 0, false, false, false, true, false, false), TICK);
        final double set = driven.cruise();
        assertTrue(set > 20, "set at its speed: " + set);
        run(driven, new Driver(), 30);
        assertEquals(set, driven.kmh(), 4, "held there");
        driven.step(new Driver(0, 0.5, false, false, false, false, false, false), TICK);
        assertTrue(driven.cruise() < 0, "let go at the brake");
    }

    private void run(final Drivetrain driven, final Driver driver, final double seconds) {
        for (double t = 0; t < seconds; t += TICK) {
            driven.step(driver, TICK);
        }
    }
}
