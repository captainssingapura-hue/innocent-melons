package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The anconeus: a small triangle from the back of the humerus's outer epicondyle to the side of the olecranon and the
 * ulna below it.
 */
public record Anconeus() implements Muscle {

    public static final Anconeus INSTANCE = new Anconeus();

    @Override
    public String name() {
        return "anconeus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, 2.288, -30.2, -0.776, 0.8, 0.5, 0.5, 0, -1),
                s.at(Ulna.INSTANCE, 0.485, -1.5, -1.617, 2.0, 0.8, 0.3, 0, -1),
                s.at(Ulna.INSTANCE, -0.4, -4.5, -1.35, 1.0, 0.5, 0, 0, -1));
    }
}
