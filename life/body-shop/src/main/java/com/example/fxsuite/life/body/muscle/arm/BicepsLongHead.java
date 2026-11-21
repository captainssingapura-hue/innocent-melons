package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The biceps's long head: its tendon from above the glenoid, over the humerus's head and down its groove; its belly the
 * outer half of the front of the arm, to the radius's tuberosity.
 */
public record BicepsLongHead() implements Muscle {

    public static final BicepsLongHead INSTANCE = new BicepsLongHead();

    @Override
    public String name() {
        return "biceps brachii, long head";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 15.8, 46.1, -1.4, 0.5, 0.4, 0, 1, 0),
                s.at(Humerus.INSTANCE, 1.3, -1, 2.3, 0.7, 0.6, 0, 0, 1),
                s.at(Humerus.INSTANCE, 1.1, -6, 1.95, 1.4, 1.1, 0, 0, 1),
                s.at(Humerus.INSTANCE, 0.916, -14, 1.978, 2.9, 2.9, 0.2, 0, 1),
                s.at(Humerus.INSTANCE, 0.771, -21, 2.506, 2.6, 2.6, 0.1, 0, 1),
                s.at(Humerus.INSTANCE, 0.3, -26.5, 2.2, 1.2, 1.0, 0, 0, 1),
                s.at(Radius.INSTANCE, 0.95, -4.3, 0.95, 0.8, 0.5, 0, 0, 1));
    }
}
