package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How a humanoid marches, a drill's way ({@link Move}), through a stride from the right heel striking the ground (0) to
 * the left's (one half) and back; standing upright, the head still and looking ahead, the pelvis hardly turning, the
 * chest not turning back against it; not put on the ground, which is its walker's ({@link Walker}).
 *
 * <p>The quick march, 齐步: the legs a brisk walk's, a firm heel strike; each arm swung forward bent, its forearm
 * turned in toward the middle, the fist level with the lowest button some 25 cm before the body; and back straight,
 * the wrist some 30 cm behind the seam of the trousers; evenly, against the legs.</p>
 *
 * <p>The parade step, 正步: each leg kicked out straight before it, the knee locked, the toes pressed down so that
 * the sole is level, some 25 cm off the ground, then put down flat, the whole sole at once, the body going on over it;
 * the trunk upright, leaning a little forward. Each arm snapped forward bent, its forearm level, the wrist some 10 cm
 * above the lowest button and 10 cm before the chest, as the other leg is kicked out, held there; and back straight,
 * the wrist some 30 cm behind the seam; held there.</p>
 */
public record March() implements StatelessFunctionalObject {

    public static final March INSTANCE = new March();

    /** The parade step's thigh, forward, through the stride, per cent. */
    private Curve paradeThigh() {
        return new Curve(0, 26, 10, 16, 20, 6, 30, -6, 40, -16, 50, -23, 55, -26, 62,
                -19, 70, -3, 76, 22, 82, 44, 88, 52, 93, 46, 97, 35);
    }

    /** Its knee, bent. */
    private Curve paradeKnee() {
        return new Curve(0, 0, 10, 2, 20, 3, 30, 2, 40, 2, 50, 5, 55, 12, 60, 30, 65, 42,
                70, 38, 75, 18, 80, 4, 86, 0, 93, 0, 97, 0);
    }

    /** Its ankle, up. */
    private Curve paradeUp() {
        return new Curve(0, -26, 10, -16, 20, -6, 30, 6, 40, 16, 48, 21, 55, 0, 60, -10,
                66, -12, 72, -20, 78, -34, 84, -48, 88, -52, 93, -44, 97, -33);
    }

    /**
     * Its pose at {@code c} of a stride of {@code move}, {@code time} seconds on; its arms at rest if
     * {@code holding}, for what it holds to take.
     */
    public Pose at(final Move move, final double c, final double time, final boolean holding) {
        final boolean parade = move == Move.PARADE_STEP;
        final double breath = Math.sin(time * 1.8);
        final double turn = Math.cos(2 * Math.PI * c);
        final double tilt = parade ? 3 : 1.5;
        final double drop = (parade ? 1.5 : 2.5) * Math.sin(2 * Math.PI * c + 0.63);
        final double pelvisTurn = (parade ? 2 : 3) * turn;
        Pose p = Pose.REST.with(Part.PELVIS, Dof.TILT, tilt).with(Part.PELVIS, Dof.PELVIS_LIST, drop)
                .with(Part.PELVIS, Dof.PELVIS_TURN, pelvisTurn);
        for (final Part trunk : new Part[] {Part.ABDOMEN, Part.CHEST}) {
            p = p.with(trunk, Dof.SPINE_BEND, 0.3 * breath).with(trunk, Dof.SPINE_TWIST, -pelvisTurn / 2)
                    .with(trunk, Dof.SPINE_SIDE, -drop * 0.4);
        }
        for (final Part n : new Part[] {Part.NECK, Part.HEAD}) {
            p = p.with(n, Dof.NECK_BEND, -tilt * 0.5 + 1.5).with(n, Dof.NECK_SIDE, drop * 0.3);
        }
        p = leg(p, Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, Part.RIGHT_TOES, leg(move, c), tilt);
        p = leg(p, Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT, Part.LEFT_TOES, leg(move, c - 0.5), tilt);
        return holding ? p : arms(p, parade, c);
    }

    /** A leg at {@code c} of a stride: its thigh forward, its knee bent, its ankle up, its toes up. */
    private double[] leg(final Move move, final double c) {
        if (move != Move.PARADE_STEP) {
            return Gait.leg(c, 150);
        }
        final double thigh = paradeThigh().at(c);
        final double knee = paradeKnee().at(c);
        final double up = paradeUp().at(c);
        // the toes flat on the ground while the heel lifts off it, as far as the foot is tipped down
        final double t = c - Math.floor(c);
        final double pushing = Gait.smooth(0.40, 0.46, t) * (1 - Gait.smooth(0.58, 0.64, t));
        return new double[] {thigh, knee, up, Math.clamp(-(thigh - knee + up), 0, 55) * pushing};
    }

    private Pose leg(final Pose p, final Part thigh, final Part shin, final Part foot, final Part toes,
                     final double[] l, final double tilt) {
        return p.with(thigh, Dof.HIP_FORWARD, l[0] - tilt).with(shin, Dof.KNEE, l[1]).with(foot, Dof.ANKLE, -l[2])
                .with(toes, Dof.TOES, l[3]);
    }

    /**
     * The arms swung against the legs: the right forward as the left leg goes out, at one half of the stride; evenly in
     * the quick march, snapped and held in the parade step.
     */
    private Pose arms(final Pose pose, final boolean parade, final double c) {
        Pose p = pose;
        for (final int side : new int[] {1, -1}) {
            final double t = c - Math.floor(c) - (side > 0 ? 0 : 0.5);
            final double u = t - Math.floor(t);
            final double f = parade ? Gait.smooth(0.12, 0.25, u) * (1 - Gait.smooth(0.62, 0.75, u))
                    : 0.5 - 0.5 * Math.cos(2 * Math.PI * u);
            final Part upper = side > 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
            final Part fore = side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM;
            final double[] ahead = parade ? new double[] {20, 2, -62, 95} : new double[] {15, 2, -58, 70};
            final double[] behind = parade ? new double[] {-32, 6, 0, 5} : new double[] {-28, 6, 0, 8};
            p = p.with(upper, Dof.RAISE_FORWARD, Gait.mix(behind[0], ahead[0], f))
                    .with(upper, Dof.RAISE_SIDEWAYS, Gait.mix(behind[1], ahead[1], f))
                    .with(upper, Dof.ARM_ROTATION, Gait.mix(behind[2], ahead[2], f))
                    .with(fore, Dof.ELBOW, Gait.mix(behind[3], ahead[3], f));
        }
        return p;
    }
}
