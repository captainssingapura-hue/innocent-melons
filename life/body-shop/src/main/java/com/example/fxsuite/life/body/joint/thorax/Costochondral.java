package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * Each rib's bone on its cartilage, one side's cartilages as one: held fast.
 */
public record Costochondral() implements Complex {

    public static final Costochondral INSTANCE = new Costochondral();

    @Override
    public String name() {
        return "costochondral joints";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(FirstCostochondral.INSTANCE, SecondCostochondral.INSTANCE, ThirdCostochondral.INSTANCE,
                FourthCostochondral.INSTANCE, FifthCostochondral.INSTANCE, SixthCostochondral.INSTANCE,
                SeventhCostochondral.INSTANCE, EighthCostochondral.INSTANCE, NinthCostochondral.INSTANCE,
                TenthCostochondral.INSTANCE, EleventhCostochondral.INSTANCE, TwelfthCostochondral.INSTANCE);
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
