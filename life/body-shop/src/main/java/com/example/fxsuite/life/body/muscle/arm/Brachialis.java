package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The brachialis: from the front of the humerus's lower half, under the biceps, showing either side of it, across the
 * front of the elbow to the ulna's tuberosity.
 */
public record Brachialis() implements Muscle {

    public static final Brachialis INSTANCE = new Brachialis();

    @Override
    public String name() {
        return "brachialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 0.5, -13, 1.1, 2.4, 1.0, 0, 0, 1),
                s.at(Humerus.INSTANCE, 0.2, -21, 1, 5.0, 2.2, 0, 0, 1),
                s.at(Humerus.INSTANCE, 0, -27, 1.15, 4.6, 1.9, 0, 0, 1),
                s.at(Ulna.INSTANCE, -0.6, -2.4, 1.3, 1.4, 0.6, 0, 0, 1));
    }
}
