package com.example.fxsuite.life.body.head;

/** What is put on a head's blank, as a sculptor puts it on clay: its eyes, its mouth, its nose, its ears. */
public enum Feature {
    RIGHT_EYE("the right eye"), LEFT_EYE("the left eye"), MOUTH("the mouth"), NOSE("the nose"),
    RIGHT_EAR("the right ear"), LEFT_EAR("the left ear");

    private final String said;

    Feature(final String said) {
        this.said = said;
    }

    @Override
    public String toString() {
        return said;
    }
}
