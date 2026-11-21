package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The deltoid's front: from the outer third of the clavicle, over the front of the shoulder, to the humerus's deltoid
 * tuberosity.
 */
public record DeltoidAnterior() implements Muscle {

    public static final DeltoidAnterior INSTANCE = new DeltoidAnterior();

    @Override
    public String name() {
        return "deltoid, front";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Clavicle.INSTANCE, 13.122, 46.565, 4.086, 3.2, 0.8, 0.2, 0.6, 0.8),
                s.at(Humerus.INSTANCE, 1.483, -1.6, 2.411, 4.3, 2.9, 0.3, 0, 1),
                s.at(Humerus.INSTANCE, 1.64, -7, 1.42, 3.1, 2.2, 0.6, 0, 0.8),
                s.at(Humerus.INSTANCE, 1.606, -12, 0.741, 1.2, 0.6, 1, 0, 0.2));
    }
}
