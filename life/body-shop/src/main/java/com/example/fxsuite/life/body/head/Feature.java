package com.example.fxsuite.life.body.head;

/** What is put on a head's blank, as a sculptor puts it on clay: its eyes, its mouth, its nose, its ears. */
public enum Feature {
    RIGHT_EYE("the right eye", false), LEFT_EYE("the left eye", true), MOUTH("the mouth", false),
    NOSE("the nose", false), RIGHT_EAR("the right ear", false), LEFT_EAR("the left ear", true);

    private final String said;
    private final boolean left;

    Feature(final String said, final boolean left) {
        this.said = said;
        this.left = left;
    }

    /** Whether it is on the left, the right's seen in a mirror. */
    public boolean left() {
        return left;
    }

    @Override
    public String toString() {
        return said;
    }
}
