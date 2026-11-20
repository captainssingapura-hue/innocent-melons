package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The metatarsals' bases side by side, the second's to the fifth's: held.
 */
public record Intermetatarsal() implements Complex {

    public static final Intermetatarsal INSTANCE = new Intermetatarsal();

    @Override
    public String name() {
        return "intermetatarsal joints";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(SecondThirdIntermetatarsal.INSTANCE, ThirdFourthIntermetatarsal.INSTANCE,
                FourthFifthIntermetatarsal.INSTANCE);
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
