package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L4;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the L4 and the L5: short, beside the ligament between their spines.
 */
public record InterspinalisL4() implements Muscle {

    public static final InterspinalisL4 INSTANCE = new InterspinalisL4();

    @Override
    public String name() {
        return "interspinalis, L4 to L5";
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
                s.at(L4.INSTANCE, 0.25, 15.72, -6.49, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(L5.INSTANCE, 0.25, 12.94, -6.89, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
