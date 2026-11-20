package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The wrist: the scaphoid and the lunate under the radius's foot, turning together about the capitate's head, the
 * carpals beyond held to them; toward the palm as far as 80 degrees and back 70, toward the thumb 20 and the little
 * finger 35.
 */
public record Wrist() implements Complex {

    public static final Wrist INSTANCE = new Wrist();

    @Override
    public String name() {
        return "wrist";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Radioscaphoid.INSTANCE, Radiolunate.INSTANCE);
    }

    @Override
    public List<Movement> movements() {
        return List.of(Movement.FLEXION, Movement.SIDE_BEND);
    }

    @Override
    public Range range(final Movement m) {
        return switch (m) {
            case FLEXION -> new Range(-70, 80);
            case SIDE_BEND -> new Range(-35, 20);
            case TWIST -> Range.NONE;
        };
    }

    /** Both its articulations turning as it is moved. */
    @Override
    public Map<Movement, Double> turns(final Joint<?, ?> joint, final Map<Movement, Double> moved) {
        return articulations().contains(joint) ? moved : Map.of();
    }
}
