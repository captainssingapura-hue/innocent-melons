package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The three cuneiforms on the navicular: held.
 */
public record Cuneonavicular() implements Complex {

    public static final Cuneonavicular INSTANCE = new Cuneonavicular();

    @Override
    public String name() {
        return "cuneonavicular joint";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(NavicularMedial.INSTANCE, NavicularIntermediate.INSTANCE, NavicularLateral.INSTANCE);
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
