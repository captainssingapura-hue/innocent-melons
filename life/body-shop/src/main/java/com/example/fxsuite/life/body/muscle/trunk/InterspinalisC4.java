package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the C4 and the C5: short, beside the ligament between their spines.
 */
public record InterspinalisC4() implements Muscle {

    public static final InterspinalisC4 INSTANCE = new InterspinalisC4();

    @Override
    public String name() {
        return "interspinalis, C4 to C5";
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
                s.at(C4.INSTANCE, 0.25, 56.57, -3.86, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(C5.INSTANCE, 0.25, 55.81, -4.21, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
