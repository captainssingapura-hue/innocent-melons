package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The infraspinatus: on the back of the scapula below its spine, out to the back of the humerus's greater tubercle.
 */
public record Infraspinatus() implements Muscle {

    public static final Infraspinatus INSTANCE = new Infraspinatus();

    @Override
    public String name() {
        return "infraspinatus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 9.572, 39.6, -10.025, 6.0, 1.2, -0.3, 0, -1),
                s.at(Scapula.INSTANCE, 12.443, 41.6, -6.616, 4.0, 1.6, 0.2, 0, -1),
                s.at(Humerus.INSTANCE, 1.528, -0.6, -1.761, 1.4, 0.5, 0.3, 0, -1));
    }
}
