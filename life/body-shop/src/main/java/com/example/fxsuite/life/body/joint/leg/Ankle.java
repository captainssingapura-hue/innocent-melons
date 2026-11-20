package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The ankle: the talus turning in the mortise of the tibia and the fibula, the toes down as far as 50 degrees and up
 * 20.
 */
public record Ankle() implements Complex {

    public static final Ankle INSTANCE = new Ankle();

    @Override
    public String name() {
        return "ankle";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Tibiotalar.INSTANCE, Fibulotalar.INSTANCE);
    }

    @Override
    public List<Movement> movements() {
        return List.of(Movement.FLEXION);
    }

    @Override
    public Range range(final Movement m) {
        return m == Movement.FLEXION ? new Range(-20, 50) : Range.NONE;
    }

    /** Each of its articulations flexing as far as it is flexed, or its share. */
    @Override
    public Map<Movement, Double> turns(final Joint<?, ?> joint, final Map<Movement, Double> moved) {
        final double flexed = moved.getOrDefault(Movement.FLEXION, 0.0);
        if (joint == Tibiotalar.INSTANCE) {
            return Map.of(Movement.FLEXION, flexed * 1);
        }
        if (joint == Fibulotalar.INSTANCE) {
            return Map.of(Movement.FLEXION, flexed * 1);
        }
        return Map.of();
    }
}
