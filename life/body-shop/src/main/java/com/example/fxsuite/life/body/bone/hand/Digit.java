package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.V3;

/**
 * Each finger's ray, thumb first, in the hand's frame as on the right side (x toward the thumb, y up the arm, z out of
 * the palm): where its metacarpal's base and head are; how thick its bones are, as against the index's; how far each of
 * its joints is bent toward the palm, as a hand hangs at rest; its phalanges' lengths, the thumb's two, the rest three.
 */
public enum Digit {
    THUMB("thumb", new V3(2.55, -3.05, 0.95), new V3(4.0, -6.8, 2.3), 1.2, 8, 3.0, 2.2),
    INDEX("index finger", new V3(1.1, -3.95, 0.05), new V3(2.4, -9.6, 0.25), 1.0, 14, 3.9, 2.2, 1.6),
    MIDDLE("middle finger", new V3(0.0, -3.95, -0.05), new V3(0.75, -9.85, -0.05), 1.05, 14, 4.4, 2.7, 1.7),
    RING("ring finger", new V3(-1.0, -3.9, 0.0), new V3(-0.85, -9.4, 0.15), 0.95, 15, 4.1, 2.6, 1.7),
    LITTLE("little finger", new V3(-1.9, -3.7, 0.1), new V3(-2.4, -8.7, 0.4), 0.85, 16, 3.3, 1.8, 1.5);

    private final String said;
    private final V3 base;
    private final V3 head;
    private final double thick;
    private final double curl;
    private final double[] phalanges;

    Digit(final String said, final V3 base, final V3 head, final double thick, final double curl,
          final double... phalanges) {
        this.said = said;
        this.base = base;
        this.head = head;
        this.thick = thick;
        this.curl = curl;
        this.phalanges = phalanges;
    }

    /** What it is called. */
    public String said() {
        return said;
    }

    public V3 base() {
        return base;
    }

    public V3 head() {
        return head;
    }

    public double thick() {
        return thick;
    }

    /** How far each joint is bent toward the palm, degrees. */
    public double curl() {
        return curl;
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
