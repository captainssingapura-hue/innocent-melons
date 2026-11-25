package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Vehicle;

/**
 * An automatic gearbox between the test engine's speed and the engine's revs, so the engine is heard as the car goes:
 * in the gear the speed and the throttle want, changing up later the harder the throttle is pressed and down as the
 * revs fall away; its clutch slipping at a walking pace, the engine held over its idle by the throttle; as the
 * vehicle {@code vehicle}'s gears and wheels turn it. Reversing, as in first.
 */
final class AutoBox {

    /** Below this speed, in metres a second, its clutch slips. */
    private static final double SLIPPING = 4;

    private final Vehicle vehicle;
    private int gear = 1;
    private double rpm;

    /** {@code vehicle}'s, in first, idling. */
    AutoBox(final Vehicle vehicle) {
        this.vehicle = vehicle;
        this.rpm = vehicle.engine().idle();
    }

    /** At {@code speed} metres a second, the throttle {@code throttle} open, nought to one. */
    void step(final double speed, final double throttle) {
        final Engine engine = vehicle.engine();
        final double going = Math.abs(speed);
        final double up = engine.redline() * (0.45 + 0.4 * throttle);
        final double down = engine.redline() * (0.25 + 0.15 * throttle);
        double at = vehicle.rpm(gear, going);
        while (gear < vehicle.count() && at > up) {
            gear++;
            at = vehicle.rpm(gear, going);
        }
        while (gear > 1 && at < down) {
            gear--;
            at = vehicle.rpm(gear, going);
        }
        final double slip = going < SLIPPING ? throttle * (engine.redline() * 0.35 - engine.idle()) : 0;
        rpm = Math.min(engine.redline(), Math.max(at, engine.idle() + slip));
    }

    /** How fast the engine turns. */
    double rpm() {
        return rpm;
    }

    /** The gear it is in, 1 the first. */
    int gear() {
        return gear;
    }
}
