package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * Across the foot behind its middle: the talus on the navicular, the heel bone on the cuboid; held, in this foot.
 */
public record TransverseTarsal() implements Complex {

    public static final TransverseTarsal INSTANCE = new TransverseTarsal();

    @Override
    public String name() {
        return "transverse tarsal joint";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Talonavicular.INSTANCE, Calcaneocuboid.INSTANCE);
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
