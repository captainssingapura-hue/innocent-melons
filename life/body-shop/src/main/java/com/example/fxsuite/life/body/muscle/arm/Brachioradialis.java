package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The brachioradialis: from the humerus's outer ridge above the elbow, the top of the forearm's outer side, its tendon
 * down to the radius's styloid.
 */
public record Brachioradialis() implements Muscle {

    public static final Brachioradialis INSTANCE = new Brachioradialis();

    @Override
    public String name() {
        return "brachioradialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 1.9, -22, -0.2, 1.0, 0.6, 1, 0, 0),
                s.at(Humerus.INSTANCE, 2.19, -27, 0.327, 2.2, 1.9, 1, 0, 0.3),
                s.at(Radius.INSTANCE, 2.04, -5, 0.38, 2.9, 2.4, 0.8, 0, 0.6),
                s.at(Radius.INSTANCE, 2.352, -14, 0.556, 1.7, 1.2, 0.9, 0, 0.4),
                s.at(Radius.INSTANCE, 2.8, -23.4, 0.4, 0.8, 0.4, 1, 0, 0));
    }
}
