package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.V3;

/**
 * Each toe's ray, in the foot's frame as on the right side (the big toe's side in): where its metatarsal's base and
 * head are and how thick it is there and along its shaft; its phalanges' lengths, the big toe two, the rest three; how
 * thick its phalanges are.
 */
public enum Toe {
    GREAT("great toe", new V3(-1.9, -3.4, 7.4), 1.0, 0.66, new V3(-2.5, -4.6, 12.7), 1.05, 0.72, 2.8, 2.0),
    SECOND("second toe", new V3(-0.5, -2.7, 7.25), 0.62, 0.4, new V3(-0.6, -4.8, 13.5), 0.58, 0.44, 2.4, 1.1, 0.85),
    THIRD("third toe", new V3(0.7, -2.9, 7.3), 0.6, 0.38, new V3(0.7, -4.9, 13.1), 0.55, 0.42, 2.15, 0.95, 0.75),
    FOURTH("fourth toe", new V3(1.65, -3.4, 7.0), 0.58, 0.37, new V3(1.9, -4.9, 12.4), 0.53, 0.4, 1.95, 0.85, 0.7),
    LITTLE("little toe", new V3(2.45, -4.0, 6.4), 0.62, 0.38, new V3(3.2, -5.0, 11.4), 0.52, 0.38, 1.7, 0.65, 0.65);

    private final String said;
    private final V3 base;
    private final double baseR;
    private final double shaftR;
    private final V3 head;
    private final double headR;
    private final double phalanxR;
    private final double[] phalanges;

    Toe(final String said, final V3 base, final double baseR, final double shaftR, final V3 head, final double headR,
        final double phalanxR, final double... phalanges) {
        this.said = said;
        this.base = base;
        this.baseR = baseR;
        this.shaftR = shaftR;
        this.head = head;
        this.headR = headR;
        this.phalanxR = phalanxR;
        this.phalanges = phalanges;
    }

    /** What it is called. */
    public String said() {
        return said;
    }

    public V3 base() {
        return base;
    }

    public double baseR() {
        return baseR;
    }

    public double shaftR() {
        return shaftR;
    }

    public V3 head() {
        return head;
    }

    public double headR() {
        return headR;
    }

    public double phalanxR() {
        return phalanxR;
    }

    /** How many phalanges it has. */
    public int phalanges() {
        return phalanges.length;
    }

    /** How long its {@code q}th phalanx is, from its root. */
    public double length(final int q) {
        return phalanges[q];
    }
}
