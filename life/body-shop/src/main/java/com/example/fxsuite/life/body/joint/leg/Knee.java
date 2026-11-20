package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The knee: the tibia bending back under the femur, as far as 140 degrees, the kneecap riding down the femur's groove
 * three quarters as far.
 */
public record Knee() implements Complex {

    public static final Knee INSTANCE = new Knee();

    @Override
    public String name() {
        return "knee";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Tibiofemoral.INSTANCE, Patellofemoral.INSTANCE);
    }

    @Override
    public List<Movement> movements() {
        return List.of(Movement.FLEXION);
    }

    @Override
    public Range range(final Movement m) {
        return m == Movement.FLEXION ? new Range(0, 140) : Range.NONE;
    }

    /** Each of its articulations flexing as far as it is flexed, or its share. */
    @Override
    public Map<Movement, Double> turns(final Joint<?, ?> joint, final Map<Movement, Double> moved) {
        final double flexed = moved.getOrDefault(Movement.FLEXION, 0.0);
        if (joint == Tibiofemoral.INSTANCE) {
            return Map.of(Movement.FLEXION, flexed * 1);
        }
        if (joint == Patellofemoral.INSTANCE) {
            return Map.of(Movement.FLEXION, flexed * 0.75);
        }
        return Map.of();
    }
}
