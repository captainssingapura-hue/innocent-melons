package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The triceps's medial head: from the back of the humerus's lower shaft, under the other two, into the triceps's
 * tendon.
 */
public record TricepsMedialHead() implements Muscle {

    public static final TricepsMedialHead INSTANCE = new TricepsMedialHead();

    @Override
    public String name() {
        return "triceps brachii, medial head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Humerus.INSTANCE, -0.856, -12, -1.321, 1.6, 1.0, -0.3, 0, -1),
                s.at(Humerus.INSTANCE, -0.6, -22, -1.25, 3.6, 1.7, 0, 0, -1),
                s.at(Humerus.INSTANCE, -0.4, -28, -1.3, 3.4, 1.2, 0, 0, -1),
                s.at(Ulna.INSTANCE, -0.75, 1.528, -1.761, 1.6, 0.5, 0, 0.3, -1));
    }
}
