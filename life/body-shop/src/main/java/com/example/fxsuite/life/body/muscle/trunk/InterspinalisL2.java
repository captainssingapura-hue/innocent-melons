package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L2;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the L2 and the L3: short, beside the ligament between their spines.
 */
public record InterspinalisL2() implements Muscle {

    public static final InterspinalisL2 INSTANCE = new InterspinalisL2();

    @Override
    public String name() {
        return "interspinalis, L2 to L3";
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
                s.at(L2.INSTANCE, 0.25, 21.27, -6.81, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(L3.INSTANCE, 0.25, 18.5, -6.5, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
