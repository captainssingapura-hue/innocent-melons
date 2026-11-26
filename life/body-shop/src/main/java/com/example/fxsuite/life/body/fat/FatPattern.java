package com.example.fxsuite.life.body.fat;

/**
 * Whose fat a body's follows, part by part, as Störchle and others measured it by ultrasound at 216 sites on ten young
 * men, 1.826 m tall on average (Sci Rep 2018, 8:16268, tables 1, 3 and 5, the fat alone, the fibres in it left out),
 * mm: the head, the neck, the trunk before and behind, the buttocks, the upper arms, the forearms, the hands, the
 * thighs, the shins, the feet; and over all 216 sites.
 */
public enum FatPattern {

    /** The ten on average. */
    AVERAGE(1.03, 1.30, 3.99, 3.17, 10.72, 2.38, 0.99, 0.29, 4.87, 2.44, 0.36, 3.17),
    /** The leanest of them, P1: 1.816 m, 66.0 kg, BMI 20.0, near a male model's (Ralph-Nearman and others 2024: 62
     * professional models 1.870 m, 76.6 kg, BMI 21.8, 10.8 % fat). */
    LEAN(0.48, 0.43, 1.55, 2.00, 6.49, 0.81, 0.50, 0.22, 2.05, 1.24, 0.18, 1.54);

    private final double head;
    private final double neck;
    private final double front;
    private final double back;
    private final double buttocks;
    private final double upperArm;
    private final double forearm;
    private final double hand;
    private final double thigh;
    private final double shin;
    private final double foot;
    private final double mean;

    FatPattern(final double head, final double neck, final double front, final double back, final double buttocks,
               final double upperArm, final double forearm, final double hand, final double thigh, final double shin,
               final double foot, final double mean) {
        this.head = head;
        this.neck = neck;
        this.front = front;
        this.back = back;
        this.buttocks = buttocks;
        this.upperArm = upperArm;
        this.forearm = forearm;
        this.hand = hand;
        this.thigh = thigh;
        this.shin = shin;
        this.foot = foot;
        this.mean = mean;
    }

    public double head() {
        return head;
    }

    public double neck() {
        return neck;
    }

    /** The trunk before, on average over it. */
    public double front() {
        return front;
    }

    /** The trunk behind. */
    public double back() {
        return back;
    }

    public double buttocks() {
        return buttocks;
    }

    public double upperArm() {
        return upperArm;
    }

    public double forearm() {
        return forearm;
    }

    public double hand() {
        return hand;
    }

    /** The thigh, on average round it. */
    public double thigh() {
        return thigh;
    }

    public double shin() {
        return shin;
    }

    public double foot() {
        return foot;
    }

    /** Over all 216 sites. */
    public double mean() {
        return mean;
    }
}
