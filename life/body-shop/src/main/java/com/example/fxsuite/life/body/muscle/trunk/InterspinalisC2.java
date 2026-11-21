package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the axis and the C3: short, beside the ligament between their spines.
 */
public record InterspinalisC2() implements Muscle {

    public static final InterspinalisC2 INSTANCE = new InterspinalisC2();

    @Override
    public String name() {
        return "interspinalis, axis to C3";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Axis.INSTANCE, 0.25, 58.58, -3.88, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(C3.INSTANCE, 0.25, 57.38, -3.78, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
