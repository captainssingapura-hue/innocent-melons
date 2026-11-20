package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The carpals one on the next, across the wrist and between its rows: held, in this hand.
 */
public record Intercarpal() implements Complex {

    public static final Intercarpal INSTANCE = new Intercarpal();

    @Override
    public String name() {
        return "intercarpal joints";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Scapholunate.INSTANCE, Lunotriquetral.INSTANCE, Pisotriquetral.INSTANCE,
                Scaphotrapezial.INSTANCE, Scaphotrapezoid.INSTANCE, Scaphocapitate.INSTANCE, Lunocapitate.INSTANCE,
                Triquetrohamate.INSTANCE, Trapeziotrapezoid.INSTANCE, Trapezocapitate.INSTANCE, Capitohamate.INSTANCE);
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
