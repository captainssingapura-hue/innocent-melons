package com.example.fxsuite.life.body.flesh;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * One way out from a ring's middle, and how far the flesh reaches that way: so far from the middle, as over muscle; or
 * so far out beyond the bone, found where the bone's surface is that way, as over a kneecap, a shin's crest, an elbow.
 * Its angle from straight ahead, toward the outside of the limb: 0 ahead, 90 out, 180 behind, 270 in.
 *
 * @param said  which way, in words
 * @param angle its angle, degrees
 * @param held  whether it is held from the middle or from the bone
 * @param reach how far, cm: from the middle, or beyond the bone
 */
public record Spoke(String said, double angle, Held held, double reach) implements Scalar {

    /** How a spoke's reach is held. */
    public enum Held {
        /** So far from the ring's middle, but never less than a little beyond the bone. */
        FREE,
        /** So far out beyond the bone's surface. */
        ON_BONE
    }
}
