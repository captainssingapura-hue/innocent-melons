package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The triceps's lateral head: from the back of the humerus high on its outer side, the outer bulk of the back of the
 * arm, into the triceps's tendon.
 */
public record TricepsLateralHead() implements Muscle {

    public static final TricepsLateralHead INSTANCE = new TricepsLateralHead();

    @Override
    public String name() {
        return "triceps brachii, lateral head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 1.314, -4, -1.136, 1.6, 1.0, 0.4, 0, -1),
                s.at(Humerus.INSTANCE, 1.341, -12, -2.082, 3.5, 2.5, 0.5, 0, -1),
                s.at(Humerus.INSTANCE, 0.804, -20, -2.219, 3.2, 2.0, 0.2, 0, -1),
                s.at(Humerus.INSTANCE, 0.3, -26, -2.2, 3.0, 1.0, 0, 0, -1),
                s.at(Ulna.INSTANCE, -0.5, 1.728, -2.361, 1.6, 0.5, 0, 0.3, -1));
    }
}
