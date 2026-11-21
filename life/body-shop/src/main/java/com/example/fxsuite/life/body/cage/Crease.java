package com.example.fxsuite.life.body.cage;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * A crease along a run of a cage's edges ({@link Cage}): each edge from one of its points to the next kept as sharp as
 * {@code sharpness} says as the cage is smoothed: 0 smoothed away at once; 1 kept sharp one step, then smoothed, a
 * soft fold; 2 two steps, tighter; between, between; so far, a muscle's, as defined as the body is.
 *
 * @param said      what it is
 * @param path      its points, one after another along it
 * @param sharpness how sharp it is
 * @param muscle    whether a muscle's, and so only as sharp as the body is defined
 */
public record Crease(String said, List<Integer> path, double sharpness, boolean muscle) implements Scalar {

    public Crease {
        path = List.copyOf(path);
    }

    /** How sharp it is on a body defined {@code definition}, 0 to 1. */
    public double sharpness(final double definition) {
        return muscle ? sharpness * definition : sharpness;
    }
}
