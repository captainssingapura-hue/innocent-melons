package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The superficial transverse perineal: from the ischial tuberosity in to the perineal body.
 */
public record SuperficialTransversePerineal() implements Muscle {

    public static final SuperficialTransversePerineal INSTANCE = new SuperficialTransversePerineal();

    @Override
    public String name() {
        return "superficial transverse perineal";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 5, -9, -1.4, 0.6, 0.4, 0, -1, 0),
                s.at(HipBone.INSTANCE, 0.2, -9.2, -0.8, 0.6, 0.4, 0, -1, 0));
    }
}
