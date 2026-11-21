package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The triceps's long head: from below the glenoid, down the back and inner side of the arm, into the triceps's tendon
 * on the olecranon.
 */
public record TricepsLongHead() implements Muscle {

    public static final TricepsLongHead INSTANCE = new TricepsLongHead();

    @Override
    public String name() {
        return "triceps brachii, long head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 15.4, 42.2, -2, 1.2, 0.8, 0, 0, -1),
                s.at(Humerus.INSTANCE, -0.641, -6, -2.003, 2.8, 2.5, -0.3, 0, -1),
                s.at(Humerus.INSTANCE, -0.506, -14, -2.429, 3.2, 3.0, -0.2, 0, -1),
                s.at(Humerus.INSTANCE, -0.5, -22, -2.1, 3.0, 2.2, 0, 0, -1),
                s.at(Humerus.INSTANCE, -0.3, -27, -2, 3.2, 1.0, 0, 0, -1),
                s.at(Ulna.INSTANCE, -0.75, 1.914, -2.113, 1.8, 0.6, 0, 0.3, -1));
    }
}
