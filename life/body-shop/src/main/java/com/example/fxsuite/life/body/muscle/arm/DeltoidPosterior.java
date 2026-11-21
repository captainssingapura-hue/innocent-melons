package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The deltoid's back: from the spine of the scapula, over the back of the shoulder, to the deltoid tuberosity.
 */
public record DeltoidPosterior() implements Muscle {

    public static final DeltoidPosterior INSTANCE = new DeltoidPosterior();

    @Override
    public String name() {
        return "deltoid, back";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 15.4, 47.021, -6.242, 4.0, 0.8, 0, 0.5, -1),
                s.at(Humerus.INSTANCE, 1.726, -1.8, -2.355, 4.3, 2.6, 0.3, 0, -1),
                s.at(Humerus.INSTANCE, 1.83, -7, -1.34, 3.1, 1.9, 0.6, 0, -0.8),
                s.at(Humerus.INSTANCE, 1.606, -12, -0.241, 1.2, 0.6, 1, 0, -0.2));
    }
}
