package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the C3 and the C4: short, beside the ligament between their spines.
 */
public record InterspinalisC3() implements Muscle {

    public static final InterspinalisC3 INSTANCE = new InterspinalisC3();

    @Override
    public String name() {
        return "interspinalis, C3 to C4";
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
                s.at(C3.INSTANCE, 0.25, 57.38, -3.78, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(C4.INSTANCE, 0.25, 56.57, -3.86, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
