package com.example.fxsuite.life.body.head;

import java.util.List;

/** A piece put on a head's blank and adjusted as one: its nose, its mouth, its eyes, its ears, each pair together. */
public enum Piece {
    NOSE("its nose", Feature.NOSE), MOUTH("its mouth", Feature.MOUTH),
    EYES("its eyes", Feature.RIGHT_EYE, Feature.LEFT_EYE), EARS("its ears", Feature.RIGHT_EAR, Feature.LEFT_EAR);

    private final String said;
    private final List<Feature> features;

    Piece(final String said, final Feature... features) {
        this.said = said;
        this.features = List.of(features);
    }

    /** Its features, the right first. */
    public List<Feature> features() {
        return features;
    }

    @Override
    public String toString() {
        return said;
    }
}
