package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The supraspinatus: in the hollow of the scapula above its spine, out under the acromion, to the top of the humerus's
 * greater tubercle.
 */
public record Supraspinatus() implements Muscle {

    public static final Supraspinatus INSTANCE = new Supraspinatus();

    @Override
    public String name() {
        return "supraspinatus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.SHOULDER;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scapula.INSTANCE, 9.8, 46.13, -7.999, 3.0, 1.4, 0, 1, -0.3),
                s.at(Scapula.INSTANCE, 14.6, 45.8, -3.6, 2.0, 1.2, 0, 1, 0),
                s.at(Humerus.INSTANCE, 1.4, 1.55, 0.2, 1.2, 0.5, 0, 1, 0));
    }
}
