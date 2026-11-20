package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The fingers' metacarpals' bases on the carpals: held, in this hand.
 */
public record Carpometacarpal() implements Complex {

    public static final Carpometacarpal INSTANCE = new Carpometacarpal();

    @Override
    public String name() {
        return "carpometacarpal joints of the fingers";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(SecondCarpometacarpal.INSTANCE, ThirdCarpometacarpal.INSTANCE, FourthCarpometacarpal.INSTANCE,
                FifthCarpometacarpal.INSTANCE);
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
