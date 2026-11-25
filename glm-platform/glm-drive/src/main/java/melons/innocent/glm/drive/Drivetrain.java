package melons.innocent.glm.drive;

import com.example.fxsuite.avatar.sound.Driving;
import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Revs;
import com.example.fxsuite.avatar.sound.Vehicle;

/**
 * A vehicle's drivetrain, driven: its engine, its gearbox and its speed, stepped a moment at a time as the driver
 * drives it. In gear, as the engines' own driving goes: pulling away on a slipping clutch, revving up through each
 * gear, the revs dropping at each change up and blipped at each change down, holding back off the throttle. The
 * gearbox changes by itself, or at the driver's word, Shift switching between them. In manual, changing down out of
 * first takes it out of gear: in neutral it revs free on its throttle and coasts, slowing; changing up, or the throttle
 * in automatic, takes first again. Its cruise control, set at the speed it goes, works the throttle to hold that
 * speed, the driver's foot over it if pressed harder; raised or lowered five at a time, let go at a touch of the
 * brake.
 */
public final class Drivetrain {

    /** In neutral, it slows this hard rolling, and this hard more with the brake full on, in m/s each second. */
    private static final double ROLLING = 0.25;
    private static final double BRAKING = 8;
    /** The slowest the cruise control holds, a step of its speed, and how hard it works the throttle. */
    private static final double SLOWEST = 20;
    private static final double STEP = 5;
    private static final double PULL = 0.08;
    private static final double GATHER = 0.02;
    private static final double GATHERED = 20;

    private final Vehicle vehicle;
    private Driving driving;
    private boolean neutral;
    private boolean auto = true;
    private Revs revs;
    private double coasting;
    private double cruise = -1;
    private double gathered;
    private double opened;

    /** {@code vehicle}, stopped in first, idling, its gearbox changing by itself. */
    public Drivetrain(final Vehicle vehicle) {
        this.vehicle = vehicle;
        this.driving = Driving.stopped(vehicle);
        this.revs = Revs.idling(vehicle.engine());
    }

    /** {@code seconds} on, driven as {@code driver} drives. */
    public void step(final Driver driver, final double seconds) {
        if (driver.switched()) {
            auto = !auto;
        }
        cruised(driver, seconds);
        final double throttle = cruise > 0 ? Math.max(driver.throttle(), held()) : driver.throttle();
        opened = throttle;
        boolean engaged = false;
        if (neutral) {
            if (driver.up() || auto && throttle > 0.1) {
                neutral = false;
                engaged = true;
                driving = new Driving(coasting, 1, revs.rpm(), 0, 0, 0, 0, 0, Driving.Clutch.OUT, 0);
            } else {
                revs = revs.step(vehicle.engine(), throttle, seconds);
                coasting = Math.max(0, coasting - (ROLLING + BRAKING * driver.brake()) * seconds);
                return;
            }
        }
        final int lever = engaged ? 0 : driver.up() ? 1 : driver.down() ? -1 : 0;
        if (lever < 0 && !auto && driving.gear() == 1 && driving.shifting() <= 0) {
            neutral = true;
            revs = new Revs(driving.rpm(), 0, 0);
            coasting = driving.speed();
            return;
        }
        driving = driving.step(vehicle, throttle, driver.brake(), lever, auto, seconds);
    }

    /** The cruise control: set, let go, raised or lowered as the driver asks; its throttle gathered. */
    private void cruised(final Driver driver, final double seconds) {
        if (driver.cruise()) {
            cruise = cruise > 0 || kmh() < SLOWEST ? -1 : kmh();
            gathered = 0;
        }
        if (cruise > 0) {
            if (driver.brake() > 0.05) {
                cruise = -1;
            } else {
                cruise = Math.max(SLOWEST, cruise + (driver.faster() ? STEP : 0) - (driver.slower() ? STEP : 0));
                gathered = Math.clamp(gathered + (cruise - kmh()) * seconds, -GATHERED, GATHERED);
            }
        }
    }

    /** The throttle the cruise control asks for, to hold its speed. */
    private double held() {
        return Math.clamp(PULL * (cruise - kmh()) + GATHER * gathered + 0.15, 0, 1);
    }

    /** How fast the engine turns. */
    public double rpm() {
        return neutral ? revs.rpm() : driving.rpm();
    }

    /** How fast it goes, in kilometres an hour. */
    public double kmh() {
        return (neutral ? coasting : driving.speed()) * 3.6;
    }

    /** The gear as a dashboard shows it: N in neutral, else the gear it is in or going into. */
    public String gear() {
        return neutral ? "N" : driving.shown();
    }

    /** Whether the gearbox changes by itself. */
    public boolean automatic() {
        return auto;
    }

    /** The speed the cruise control holds, in kilometres an hour; or none, below nought, when it is not set. */
    public double cruise() {
        return cruise;
    }

    /** The throttle the cylinders get, for its sound: none while it changes gear or the limiter cuts. */
    public double firing() {
        return neutral ? opened : driving.firing();
    }

    /** How hard the engine works, for its sound: none in neutral or out of gear. */
    public double load() {
        return neutral ? 0 : driving.load();
    }

    /** The throttle the driver, or the cruise control, opened it to. */
    public double throttle() {
        return opened;
    }

    /** Its engine. */
    public Engine engine() {
        return vehicle.engine();
    }

    /** Its vehicle. */
    public Vehicle vehicle() {
        return vehicle;
    }
}
