package com.example.fxsuite.life.body;

/**
 * How a humanoid walks and runs: every joint through a stride, as a person's turn, the same for a human and a robot.
 * A stride is a cycle from the right heel striking the ground (0) round through the left's (one half) and back. Each
 * leg's hip, knee and ankle follow a person's (the familiar curves of gait laboratories, for walking and for running,
 * smoothed), its toes bending as its heel lifts; the pelvis turning, dropping and tipping with them, the chest turning
 * back against it, the head kept looking ahead; the arms swinging against the legs. The faster, the further each
 * swings; past a brisk walk, running ({@link #run}). Standing, still but for its breathing. Not put on the ground:
 * that is the rig's, and any flight is its walker's ({@link Walker}).
 */
public final class Gait {

    /** Walking, a person's, at about 1.4 m/s: the thigh from straight down (forward), the knee bent, the ankle up. */
    static final Curve WALK_THIGH = new Curve(0, 23, 10, 21, 20, 12, 30, 1, 40, -9, 50, -20, 55, -22, 62, -14, 70, 3,
            75, 10, 80, 17, 87, 24, 93, 24);
    static final Curve WALK_KNEE = new Curve(0, 5, 8, 15, 15, 19, 25, 12, 35, 6, 42, 5, 50, 10, 57, 25, 62, 40, 68,
            58, 73, 63, 80, 54, 87, 32, 93, 10);
    static final Curve WALK_ANKLE_UP = new Curve(0, 0, 7, -6, 12, -2, 20, 4, 30, 8, 42, 10, 50, 4, 56, -6, 62, -18,
            67, -10, 72, 2, 80, 7, 88, 4, 95, 0);
    /** Running, at about 5 m/s: each foot down under a third of the stride, then in the air; two flights a stride. */
    static final Curve RUN_THIGH = new Curve(0, 30, 12, 18, 25, -15, 30, -26, 35, -28, 45, -16, 58, 8, 72, 35, 85, 42);
    static final Curve RUN_KNEE = new Curve(0, 20, 10, 40, 25, 26, 32, 24, 50, 75, 62, 105, 72, 100, 82, 70, 92, 30);
    static final Curve RUN_ANKLE_UP = new Curve(0, 5, 10, 18, 28, -22, 35, -25, 45, -12, 60, 2, 75, 6);

    /** How much of a stride each foot is down, walking and running. */
    static final double WALK_STANCE = 0.62;
    static final double RUN_STANCE = 0.30;
    /** Between these speeds, cm/s, a walk turns to a run. */
    static final double RUN_FROM = 210;
    static final double RUN_BY = 300;
    /** Speeds a walk's and a run's curves are a person's at; under this, a step is fading in from standing. */
    static final double WALK_AT = 140;
    static final double RUN_AT = 500;
    static final double STEPPING = 50;

    private Gait() {
    }

    /** How much it is running, from 0 walking to 1, at {@code speed} cm/s. */
    public static double run(final double speed) {
        return smooth(RUN_FROM, RUN_BY, speed);
    }

    /** How much of a stride each foot is down at {@code speed}. */
    public static double stance(final double speed) {
        return WALK_STANCE + (RUN_STANCE - WALK_STANCE) * run(speed);
    }

    /** How much of a stride neither foot is down, in all, at {@code speed}: twice what is short of half each. */
    public static double flight(final double speed) {
        return Math.max(0, 2 * (0.5 - stance(speed)));
    }

    /** One leg at {@code c} of its own stride (0 its heel striking): its thigh, knee, ankle up, toes, degrees. */
    static double[] leg(final double c, final double speed) {
        final double w = run(speed);
        final double m = Math.clamp(speed / STEPPING, 0, 1);
        final double s = Math.clamp(speed / WALK_AT, 0.45, 1.25);
        final double r = Math.clamp(1 + 0.2 * (speed - RUN_AT) / (760 - RUN_AT), 0.8, 1.2);
        final double thigh = m * mix(WALK_THIGH.at(c) * s, RUN_THIGH.at(c) * r, w);
        final double knee = m * mix(WALK_KNEE.at(c) * (0.6 + 0.4 * s), RUN_KNEE.at(c) * r, w);
        final double up = m * mix(WALK_ANKLE_UP.at(c) * s, RUN_ANKLE_UP.at(c), w);
        // the toes flat on the ground while the heel lifts off it: bent up as far as the foot is tipped down
        final double pitch = thigh - knee + up;
        final double pushing = mix(window(c, 0.38, 0.66), window(c, 0.14, 0.33), w);
        // swinging through, held a little up, clear of the ground
        final double swinging = m * 12 * mix(window(c, 0.70, 0.94), window(c, 0.40, 0.90), w);
        final double toes = Math.clamp(-pitch, 0, 55) * pushing + swinging;
        return new double[] {thigh, knee, up, toes};
    }

    /**
     * Its pose at {@code c} of a stride at {@code speed} cm/s, {@code time} seconds on (for its breathing); its arms
     * swinging, unless {@code holding} something in both hands, when they are left at rest for the hold to take.
     */
    public static Pose at(final double c, final double speed, final double time, final boolean holding) {
        final double w = run(speed);
        final double m = Math.clamp(speed / STEPPING, 0, 1);
        final double s = Math.clamp(speed / WALK_AT, 0.45, 1.25);
        final double turn = Math.cos(2 * Math.PI * c);
        final double breath = Math.sin(time * 1.8);
        Pose p = Pose.REST;

        // the trunk: leaning into a run, its pelvis tipped, dropped on the side swinging through, turned with the leg
        // swinging forward; the chest turned back against it, less with both hands on something
        final double tilt = m * (mix(1 + 1.2 * Math.sin(4 * Math.PI * c), 5 + 1.5 * Math.sin(4 * Math.PI * c), w));
        final double bend = m * mix(1.5, 6, w) + 0.6 * breath;
        final double drop = m * mix(4 * s, 3, w) * Math.sin(2 * Math.PI * c + 0.63);
        final double pelvisTurn = m * mix(4 * s, 3, w) * turn;
        final double chestBack = m * mix(3 * s, 7, w) * (holding ? 0.3 : 1);
        p = p.with(Part.PELVIS, Dof.TILT, tilt).with(Part.PELVIS, Dof.PELVIS_LIST, drop)
                .with(Part.PELVIS, Dof.PELVIS_TURN, pelvisTurn);
        for (final Part trunk : new Part[] {Part.ABDOMEN, Part.CHEST}) {
            p = p.with(trunk, Dof.SPINE_BEND, bend / 2).with(trunk, Dof.SPINE_TWIST, -(pelvisTurn + chestBack * turn) / 2)
                    .with(trunk, Dof.SPINE_SIDE, -drop * 0.4);
        }
        // the head looking ahead and level, whatever the trunk does
        for (final Part n : new Part[] {Part.NECK, Part.HEAD}) {
            p = p.with(n, Dof.NECK_TURN, chestBack * turn / 2).with(n, Dof.NECK_BEND, -(tilt + bend) * 0.4)
                    .with(n, Dof.NECK_SIDE, drop * 0.3);
        }

        // the legs: the right's stride from its strike, the left's half a stride on; the hip from the pelvis tipped
        p = leg(p, Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, Part.RIGHT_TOES, leg(c, speed), tilt);
        p = leg(p, Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT, Part.LEFT_TOES, leg(c - 0.5, speed), tilt);

        // the arms swinging against the legs: the right forward as the left leg is; the elbows bent more running
        if (!holding) {
            final double swing = m * mix(14 * s, 38, w);
            final double ahead = m * mix(2, 8, w);
            final double out = mix(8, 13, w);
            for (final int side : new int[] {1, -1}) {
                final Part upper = side > 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
                final Part fore = side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM;
                final double forward = -side * turn;
                final double elbow = mix(14 + 12 * s * m, 82, w) + m * mix(10, 18, w)
                        * Math.max(0, forward);
                p = p.with(upper, Dof.RAISE_FORWARD, ahead + swing * forward + 0.4 * breath)
                        .with(upper, Dof.RAISE_SIDEWAYS, out).with(fore, Dof.ELBOW, elbow);
            }
        }
        return p;
    }

    private static Pose leg(final Pose p, final Part thigh, final Part shin, final Part foot, final Part toes,
                            final double[] l, final double tilt) {
        // the thigh's turn from the pelvis, which is tipped already; the ankle's toes-down from the shin
        return p.with(thigh, Dof.HIP_FORWARD, l[0] - tilt).with(shin, Dof.KNEE, l[1]).with(foot, Dof.ANKLE, -l[2])
                .with(toes, Dof.TOES, l[3]);
    }

    /** 1 from {@code from} to {@code to} of a stride, 0 well outside, smoothly between. */
    private static double window(final double c, final double from, final double to) {
        final double t = c - Math.floor(c);
        return smooth(from - 0.03, from + 0.02, t) * (1 - smooth(to - 0.02, to + 0.03, t));
    }

    static double mix(final double a, final double b, final double w) {
        return a + (b - a) * w;
    }

    static double smooth(final double from, final double to, final double v) {
        final double t = Math.clamp((v - from) / (to - from), 0, 1);
        return t * t * (3 - 2 * t);
    }
}
