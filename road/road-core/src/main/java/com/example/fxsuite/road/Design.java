package com.example.fxsuite.road;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * What a road is designed for, as far as its transitions' lengths need it: the speed it is driven at, in kilometres an
 * hour, and how wide its carriageway is, in metres.
 *
 * @param speed the design speed, km/h
 * @param width the carriageway's width, m
 */
public record Design(double speed, double width) implements Scalar {

    public Design {
        if (!(speed > 0) || !Double.isFinite(speed)) {
            throw new IllegalArgumentException("a road is designed for a speed above nothing, not " + speed);
        }
        if (!(width > 0) || !Double.isFinite(width)) {
            throw new IllegalArgumentException("a carriageway has a width, not " + width);
        }
    }
}
