package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The interspinalis between the T12 and the L1: short, beside the ligament between their spines.
 */
public record InterspinalisT12() implements Muscle {

    public static final InterspinalisT12 INSTANCE = new InterspinalisT12();

    @Override
    public String name() {
        return "interspinalis, T12 to L1";
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
                s.at(T12.INSTANCE, 0.25, 25.98, -7.77, 0.60, 0.40, 1.000, 0.000, 0.000),
                s.at(L1.INSTANCE, 0.25, 23.95, -7.4, 0.60, 0.40, 1.000, 0.000, 0.000));
    }
}
