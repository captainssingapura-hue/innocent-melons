package com.example.fxsuite.life.body;

/** How deep a part of the body is drawn ({@link Layers}): its bones; its muscles on its bones; its skin; not at all. */
public enum Layer {
    SKELETON("Skeleton"), MUSCLES("Muscles on the skeleton"), SKIN("Skin"), HIDDEN("Hidden");

    private final String label;

    Layer(final String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Whether its bones are drawn. */
    public boolean boned() {
        return this == SKELETON || this == MUSCLES;
    }

    @Override
    public String toString() {
        return label;
    }
}
