package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The cuneiforms side by side, and the cuboid beside the lateral: held.
 */
public record Intercuneiform() implements Complex {

    public static final Intercuneiform INSTANCE = new Intercuneiform();

    @Override
    public String name() {
        return "intercuneiform and cuneocuboid joints";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(MedialIntermediate.INSTANCE, IntermediateLateral.INSTANCE, Cuneocuboid.INSTANCE);
    }

    @Override
    public List<Movement> movements() {
        return List.of();
    }

    @Override
    public Range range(final Movement m) {
        return Range.NONE;
    }

    @Override
    public Map<Movement, Double> turns(final Joint<?, ?> joint, final Map<Movement, Double> moved) {
        return Map.of();
    }
}
