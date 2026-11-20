package com.example.fxsuite.life.body.joint;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.HashMap;
import java.util.Map;

/**
 * How far each joint has moved, each way it moves, on each side it is, degrees: none, at rest. Made afresh for every
 * change, never changed; no joint moved further than it may.
 *
 * @param degrees how far, by joint, side and way
 */
public record Posture(Map<Key, Double> degrees) implements Scalar {

    /** Every joint at rest. */
    public static final Posture REST = new Posture(Map.of());

    /** A joint, on a side, moving a way. */
    public record Key(Complex joint, Side side, Movement movement) {
    }

    public Posture {
        degrees = Map.copyOf(degrees);
    }

    /** How far {@code joint} on {@code side} has moved {@code m}. */
    public double degrees(final Complex joint, final Side side, final Movement m) {
        return degrees.getOrDefault(new Key(joint, side, m), 0.0);
    }

    /** This, but {@code joint} on {@code side} moved {@code d} degrees {@code m}, which it must be able to. */
    public Posture with(final Complex joint, final Side side, final Movement m, final double d) {
        if (!joint.range(m).holds(d)) {
            throw new IllegalArgumentException(joint.name() + " moved " + d + " degrees " + m.said() + ", past "
                    + joint.range(m));
        }
        final Map<Key, Double> more = new HashMap<>(degrees);
        more.put(new Key(joint, side, m), d);
        return new Posture(more);
    }
}
