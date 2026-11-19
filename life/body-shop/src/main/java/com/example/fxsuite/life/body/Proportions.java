package com.example.fxsuite.life.body;

import java.util.Locale;

/**
 * How a body is made, in the handover's few numbers: its height; its build, how broad and deep for its height; how
 * long its legs (its hip joints' height) and its arms (shoulder to fingertip), and how far apart its shoulder joints
 * and its hip joints, as shares of its height. The interactive model's figure, {@link #DRAWN}, is drawn in its centimetres;
 * any other is it scaled ({@link #scale}, {@link #legScale} and so on).
 *
 * @param stature   how tall, cm
 * @param build     how broad and deep for its height: 1 the drawn figure's
 * @param legs      how high its hip joints, as a share of its height
 * @param arms      how long its arms, shoulder joint to fingertip
 * @param shoulders how far apart its shoulder joints
 * @param hips      how far apart its hip joints
 */
public record Proportions(double stature, double build, double legs, double arms, double shoulders, double hips) {

    /**
     * The interactive model's figure, every part's figures drawn to: 166.5 cm, its legs 42 and 42 cm and its foot;
     * its arms 31, 25 and 17 cm; its shoulder joints 36 cm apart, its hip joints 17. Long in the leg.
     */
    public static final Proportions DRAWN = new Proportions(166.5, 1, 90.6 / 166.5, 73 / 166.5, 36 / 166.5,
            17 / 166.5);

    /**
     * The avatar as the handover sizes it: 1.70 m; its thigh 0.245 and its shin 0.246 of its height, its ankle 0.039
     * over the ground, so its hip joints 0.53 up; its arms, its shoulders and its hips the drawn figure's.
     */
    public static final Proportions PAPERS = new Proportions(170, 1, 0.53, DRAWN.arms, DRAWN.shoulders, DRAWN.hips);

    public Proportions {
        check(stature >= 100 && stature <= 230, "from 1 m to 2.3 m tall");
        check(build >= 0.7 && build <= 1.6, "built from 0.7 to 1.6 times as broad as the papers' figure");
        check(legs >= 0.45 && legs <= 0.62, "its hip joints from 0.45 to 0.62 of its height up");
        check(arms >= 0.36 && arms <= 0.52, "its arms from 0.36 to 0.52 of its height long");
        check(shoulders >= 0.17 && shoulders <= 0.27, "its shoulder joints from 0.17 to 0.27 of its height apart");
        check(hips >= 0.07 && hips <= 0.14, "its hip joints from 0.07 to 0.14 of its height apart");
    }

    private static void check(final boolean ok, final String what) {
        if (!ok) {
            throw new IllegalArgumentException("a body " + what);
        }
    }

    /** How much bigger than the drawn figure, all over. */
    public double scale() {
        return stature / DRAWN.stature;
    }

    /** How much longer its legs are, for its height, than the drawn figure's. */
    public double legScale() {
        return legs / DRAWN.legs;
    }

    /** How much longer its arms. */
    public double armScale() {
        return arms / DRAWN.arms;
    }

    /** How much further apart its shoulders. */
    public double shoulderScale() {
        return shoulders / DRAWN.shoulders;
    }

    /** How much further apart its hips. */
    public double hipScale() {
        return hips / DRAWN.hips;
    }

    /** How heavy, kg: the handover's 70 kg at 1.70 m, as much more as it is bigger and broader. */
    public double mass() {
        final double k = stature / 170;
        return 70 * k * k * k * build * build;
    }

    /** Itself, in a line. */
    public String said() {
        return String.format(Locale.ROOT, "%.0f cm tall, built %.2f, %.0f kg; hip joints %.0f cm up, arms %.0f cm long,"
                + " shoulder joints %.0f cm apart, hip joints %.0f cm", stature, build, mass(), legs * stature,
                arms * stature, shoulders * stature, hips * stature);
    }
}
