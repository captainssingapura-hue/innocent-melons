package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The coracobrachialis: from the coracoid, with the biceps's short head, down the inner side of the arm to the middle
 * of the humerus.
 */
public record Coracobrachialis() implements Muscle {

    public static final Coracobrachialis INSTANCE = new Coracobrachialis();

    @Override
    public String name() {
        return "coracobrachialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.UPPER_ARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 14.486, 44.2, 3.113, 0.8, 0.6, -0.3, 0, 1),
                s.at(Humerus.INSTANCE, -0.93, -5, 1.199, 1.6, 1.4, -1, 0, 0.3),
                s.at(Humerus.INSTANCE, -1.3, -12, 0.4, 1.2, 0.6, -1, 0, 0));
    }
}
