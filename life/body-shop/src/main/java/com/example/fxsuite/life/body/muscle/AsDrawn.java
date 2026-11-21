package com.example.fxsuite.life.body.muscle;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/** A body with every muscle as its sections are written ({@link Muscularity}). */
public record AsDrawn() implements Muscularity, StatelessFunctionalObject {

    public static final AsDrawn INSTANCE = new AsDrawn();

    @Override
    public double volume(final Muscle m, final double asDrawn) {
        return asDrawn;
    }
}
