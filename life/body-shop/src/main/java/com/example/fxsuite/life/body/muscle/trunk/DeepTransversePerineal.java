package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The deep transverse perineal: across the back of the urogenital triangle, from the ischiopubic ramus to the middle.
 */
public record DeepTransversePerineal() implements Muscle {

    public static final DeepTransversePerineal INSTANCE = new DeepTransversePerineal();

    @Override
    public String name() {
        return "deep transverse perineal";
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
                s.at(HipBone.INSTANCE, 3.8, -8.15, 1.6, 1.0, 0.5, 0, -1, 0),
                s.at(HipBone.INSTANCE, 0.2, -8.15, 0.6, 1.0, 0.5, 0, -1, 0));
    }
}
