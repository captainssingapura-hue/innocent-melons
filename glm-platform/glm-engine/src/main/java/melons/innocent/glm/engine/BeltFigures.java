package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * How a twin's final drive runs, on the bike's right, at the crankcase's back end, in the block's frame: after the
 * reference bike's, a toothed belt of 178 teeth, 24 mm wide, from the output pulley of 34 teeth on the countershaft
 * back round the rear pulley of 80 on the back wheel; at 11 mm pitch, an estimate, which puts the back wheel a
 * swingarm's length behind the gearbox and leaves the front wheel room before the engine at the bike's wheelbase.
 * The back wheel as far behind the countershaft as the belt's length puts it, as high as its tyre is round, 150/80
 * on 16 inches; the crank as high over the ground as the bike is likely to carry it, until its frame says, an
 * estimate. As the back wheel rolls the belt runs with it, the rear pulley turning with the wheel and the output
 * pulley 80 to 34 times as fast, the same way.
 *
 * @param figures the engine's figures
 */
public record BeltFigures(EngineFigures figures) {

    /** How far one tooth is from the next; how many the belt has, and each pulley; how wide the belt is. */
    public static final double PITCH = 1.1;
    public static final int TEETH = 178;
    public static final int FRONT = 34;
    public static final int REAR = 80;
    public static final double WIDE = 2.4;
    /** How far round the back tyre is, 150/80 on 16 inches; how high the crank stands over the ground. */
    public static final double TYRE = (16 * 2.54 + 2 * 0.8 * 15) / 2;
    public static final double CRANK = 28;
    /** How far out its plane is past the crankcase's back end. */
    private static final double OUT = 2;

    /** Where its plane is, along the crank. */
    public double plane() {
        return figures.length() + OUT;
    }

    /** How far round the output pulley is to the belt's line, and the rear pulley. */
    public double front() {
        return round(FRONT);
    }

    public double rear() {
        return round(REAR);
    }

    /** Where the countershaft is, across the crank. */
    public Spot countershaft() {
        return new GearboxFigures(figures).countershaft();
    }

    /** Where the back axle is, across the crank: behind the countershaft, as far as the belt's length puts it. */
    public Spot axle() {
        final Spot from = countershaft();
        final double up = TYRE - CRANK - from.v();
        double near = rear() + front();
        double far = TEETH * PITCH / 2;
        for (int k = 0; k < 60; k++) {
            final double mid = (near + far) / 2;
            if (belt(new Spot(from.u() - Math.sqrt(mid * mid - up * up), from.v() + up)).length() < TEETH * PITCH) {
                near = mid;
            } else {
                far = mid;
            }
        }
        final double apart = (near + far) / 2;
        return new Spot(from.u() - Math.sqrt(apart * apart - up * up), from.v() + up);
    }

    /** The belt's line, round the output pulley and the rear one. */
    Belt belt() {
        return belt(axle());
    }

    /** How far the back wheel has turned, in radians, the bike having rolled {@code rolled}; forward, toward +x. */
    public double wheel(final double rolled) {
        return rolled / TYRE;
    }

    private Belt belt(final Spot axle) {
        return new Belt(List.of(new Belt.Wheel(countershaft(), front()), new Belt.Wheel(axle, rear())));
    }

    /** How far round a pulley of {@code teeth} is to the belt's line, the pitch its chord. */
    private double round(final int teeth) {
        return PITCH / (2 * Math.sin(Math.PI / teeth));
    }
}
