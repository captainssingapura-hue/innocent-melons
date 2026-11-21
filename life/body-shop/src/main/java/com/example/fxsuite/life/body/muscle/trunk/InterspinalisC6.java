package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the C6 and the C7: short, beside the ligament between their spines.
 */
public record InterspinalisC6() implements Muscle {

    public static final InterspinalisC6 INSTANCE = new InterspinalisC6();

    @Override
    public String name() {
        return "interspinalis, C6 to C7";
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
                s.at(C6.INSTANCE, 0.25, 54.86, -4.76, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(C7.INSTANCE, 0.25, 53.33, -6.19, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
