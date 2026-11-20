package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.joint.Complex;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Movement;
import com.example.fxsuite.life.body.joint.Range;

import java.util.List;
import java.util.Map;

/**
 * The elbow and the forearm, as one: the forearm bending forward on the humerus as far as 145 degrees; twisting, the
 * radius turning across the ulna at both its ends, pronating it as far as 170 degrees from the palm forward and
 * supinating it 10. The radius hangs from the ulna; its head under the capitulum, listed last, turns with both, the
 * twist undone and then the bend, as it is walked from the radius, so that its ring closes however the arm is posed.
 */
public record Elbow() implements Complex {

    public static final Elbow INSTANCE = new Elbow();

    @Override
    public String name() {
        return "elbow and forearm";
    }

    @Override
    public List<Joint<?, ?>> articulations() {
        return List.of(Humeroulnar.INSTANCE, ProximalRadioulnar.INSTANCE, DistalRadioulnar.INSTANCE,
                Humeroradial.INSTANCE);
    }

    @Override
    public List<Movement> movements() {
        return List.of(Movement.FLEXION, Movement.TWIST);
    }

    @Override
    public Range range(final Movement m) {
        return switch (m) {
            case FLEXION -> new Range(0, 145);
            case TWIST -> new Range(-10, 170);
            case SIDE_BEND -> Range.NONE;
        };
    }

    @Override
    public Map<Movement, Double> turns(final Joint<?, ?> joint, final Map<Movement, Double> moved) {
        final double bent = moved.getOrDefault(Movement.FLEXION, 0.0);
        final double twisted = moved.getOrDefault(Movement.TWIST, 0.0);
        if (joint == Humeroulnar.INSTANCE) {
            return Map.of(Movement.FLEXION, bent);
        }
        if (joint == Humeroradial.INSTANCE) {
            return Map.of(Movement.FLEXION, -bent, Movement.TWIST, -twisted);
        }
        if (joint == ProximalRadioulnar.INSTANCE || joint == DistalRadioulnar.INSTANCE) {
            return Map.of(Movement.TWIST, twisted);
        }
        return Map.of();
    }
}
