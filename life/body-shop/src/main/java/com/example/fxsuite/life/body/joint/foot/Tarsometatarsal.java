package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The metatarsals' bases on the cuneiforms and the cuboid: held.
 */
public record Tarsometatarsal() implements Complex {

    public static final Tarsometatarsal INSTANCE = new Tarsometatarsal();

    @Override
    public String name() {
        return "tarsometatarsal joints";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(FirstTarsometatarsal.INSTANCE, SecondTarsometatarsal.INSTANCE, ThirdTarsometatarsal.INSTANCE,
                FourthTarsometatarsal.INSTANCE, FifthTarsometatarsal.INSTANCE);
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
