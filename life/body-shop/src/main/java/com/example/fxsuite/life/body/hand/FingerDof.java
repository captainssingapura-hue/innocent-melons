package com.example.fxsuite.life.body.hand;

/**
 * A way a finger's joint turns, and how far each way, degrees: a knuckle bending toward the palm and spreading from the
 * middle finger; a finger's middle and end joints bending; the thumb's base bending in toward the palm and swinging
 * across it, its knuckle and its end bending. Bending is toward the palm as it grows, spreading toward the thumb.
 */
public enum FingerDof {
    KNUCKLE_BEND("Knuckle bent", -20, 90),
    KNUCKLE_SPREAD("Knuckle spread", -20, 20),
    MIDDLE_BEND("Middle joint bent", 0, 110),
    END_BEND("End joint bent", -5, 80),
    THUMB_BEND("Thumb's base bent in", -15, 50),
    THUMB_SWING("Thumb swung across", -10, 60),
    THUMB_KNUCKLE("Thumb's knuckle bent", -10, 60),
    THUMB_END("Thumb's end bent", -15, 80);

    private final String label;
    private final double min;
    private final double max;

    FingerDof(final String label, final double min, final double max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    /** What it is called. */
    public String label() {
        return label;
    }

    /** How far it turns the least way. */
    public double min() {
        return min;
    }

    /** How far it turns the most way. */
    public double max() {
        return max;
    }

    /** {@code v}, no further than it turns. */
    public double clamp(final double v) {
        return Math.clamp(v, min, max);
    }

    /** Whether it bends toward the palm, rather than spreading or swinging across it. */
    public boolean bends() {
        return this != KNUCKLE_SPREAD && this != THUMB_SWING;
    }
}
