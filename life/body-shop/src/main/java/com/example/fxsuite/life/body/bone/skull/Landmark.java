package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.V3;

/**
 * A landmark of the skull, as craniometry names it, and where it is on the skull as drawn ({@link Cranium}) or its
 * jaw ({@link Mandible}), in the head's frame, cm: down the middle, or on the right, its fellow on the left the mirror.
 * Those where the skull meets what moves on it held where they are: the foramen magnum's front and back, the jaw's
 * condyle.
 */
public enum Landmark {
    NASION("nasion", 0, 5.89, 10.01),
    GLABELLA("glabella", 0, 7.03, 10.32),
    BREGMA("bregma, where the frontal's, the parietals' and the occipital's chords are as a man's", 0, 14.85, 2.18),
    LAMBDA("lambda, so too", 0, 9.49, -8.60),
    OPISTHOCRANION("opisthocranion, above the protuberance", 0, 7.26, -8.89),
    BASION("basion", 0, 0.26, -0.09, true),
    OPISTHION("opisthion", 0, 0.26, -3.97, true),
    PROSTHION("prosthion", 0, -0.62, 9.11),
    NASOSPINALE("nasospinale", 0, 1.15, 10.02),
    EURYON("euryon", 6.89, 7.25, 0.10),
    ZYGION("zygion, the arch's middle", 6.65, 2.55, 2.8),
    ASTERION("asterion", 5.62, 3.5, -4.5),
    DACRYON("dacryon, the orbit's inner edge", 1.84, 4.75, 8.55),
    ECTOCONCHION("ectoconchion, the orbit's outer edge", 4.82, 4.75, 8.55),
    ORBIT_TOP("the orbit's top edge", 3.3, 6.12, 8.55),
    ORBIT_FOOT("the orbit's lower edge", 3.3, 3.36, 8.55),
    ALARE("alare, the nasal opening's widest", 1.05, 2.9, 9.6),
    ECTOMOLARE("ectomolare, the upper arch's outside at the molars", 3.79, 0.15, 3.8),
    GONION("gonion, the jaw's angle", 4.62, -5.14, 0.98),
    GNATHION("gnathion, the chin's lowest", 0, -6.22, 7.92),
    CONDYLION("condylion, the condyle's outside", 6.06, 1.0, -0.15, true),
    INFRADENTALE("infradentale, the lower arch's front", 0, -2.2, 8.78);

    private final String said;
    private final V3 drawn;
    private final boolean held;

    Landmark(final String said, final double x, final double y, final double z) {
        this(said, x, y, z, false);
    }

    Landmark(final String said, final double x, final double y, final double z, final boolean held) {
        this.said = said;
        this.drawn = new V3(x, y, z);
        this.held = held;
    }

    /** Where it is as drawn, on the right if it is one of a pair. */
    public V3 drawn() {
        return drawn;
    }

    /** Whether it is held where it is drawn: where the skull meets what moves on it. */
    public boolean held() {
        return held;
    }

    /** Whether it is down the middle, not one of a pair. */
    public boolean middle() {
        return drawn.x() == 0;
    }

    @Override
    public String toString() {
        return said;
    }
}
