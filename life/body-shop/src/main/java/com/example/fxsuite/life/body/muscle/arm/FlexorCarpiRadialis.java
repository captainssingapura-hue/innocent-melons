package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor carpi radialis: from the humerus's inner epicondyle, down the front of the forearm, to the wrist on the
 * thumb's side.
 */
public record FlexorCarpiRadialis() implements Muscle {

    public static final FlexorCarpiRadialis INSTANCE = new FlexorCarpiRadialis();

    @Override
    public String name() {
        return "flexor carpi radialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -2.814, -29.8, 0.313, 0.8, 0.6, -0.3, 0, 1),
                s.at(Ulna.INSTANCE, -0.3, -6, 1.65, 2.2, 1.7, 0, 0, 1),
                s.at(Radius.INSTANCE, 0.9, -16, 1.35, 1.0, 0.7, 0, 0, 1),
                s.at(Radius.INSTANCE, 1.4, -23.6, 1.1, 0.5, 0.4, 0, 0, 1));
    }
}
