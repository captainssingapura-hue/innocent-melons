package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The deltoid's middle: from the acromion, over the top and outside of the shoulder, its rounded cap, to the deltoid
 * tuberosity.
 */
public record DeltoidMiddle() implements Muscle {

    public static final DeltoidMiddle INSTANCE = new DeltoidMiddle();

    @Override
    public String name() {
        return "deltoid, middle";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 18.414, 47.536, -1, 3.0, 1.0, 0.4, 1, 0),
                s.at(Humerus.INSTANCE, 2.55, -1.6, 0.2, 5.0, 2.9, 1, 0, 0),
                s.at(Humerus.INSTANCE, 2.4, -7, 0.3, 3.8, 2.4, 1, 0, 0),
                s.at(Humerus.INSTANCE, 1.7, -12, 0.3, 1.2, 0.6, 1, 0, 0));
    }
}
