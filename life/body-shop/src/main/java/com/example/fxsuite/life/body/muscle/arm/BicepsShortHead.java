package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The biceps's short head: from the coracoid, down the inner half of the front of the arm, joining the long head, to
 * the radius's tuberosity.
 */
public record BicepsShortHead() implements Muscle {

    public static final BicepsShortHead INSTANCE = new BicepsShortHead();

    @Override
    public String name() {
        return "biceps brachii, short head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 14.6, 44.4, 3.55, 0.7, 0.5, 0, 0, 1),
                s.at(Humerus.INSTANCE, -0.628, -5, 2.325, 1.7, 1.2, -0.3, 0, 1),
                s.at(Humerus.INSTANCE, -0.871, -14, 1.906, 2.9, 2.6, -0.1, 0, 1),
                s.at(Humerus.INSTANCE, -0.6, -21, 2.4, 2.6, 2.4, 0, 0, 1),
                s.at(Humerus.INSTANCE, 0, -26.5, 2.2, 1.2, 1.0, 0, 0, 1),
                s.at(Radius.INSTANCE, 0.95, -4.3, 0.95, 0.8, 0.5, 0, 0, 1));
    }
}
