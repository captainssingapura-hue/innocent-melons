package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gluteus minimus: under the medius, from the ilium's outer face lower down, to the front of the greater
 * trochanter.
 */
public record GluteusMinimus() implements Muscle {

    public static final GluteusMinimus INSTANCE = new GluteusMinimus();

    @Override
    public String name() {
        return "gluteus minimus";
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
                s.at(HipBone.INSTANCE, 12.4, 6, 0, 7.0, 1.2, 1, 0, 0),
                s.at(HipBone.INSTANCE, 13.012, 1.6, 0.882, 4.0, 1.2, 1, 0, 0.2),
                s.at(Femur.INSTANCE, 5.913, -1.6, 0.914, 1.6, 0.6, 1, 0, 0.3));
    }
}
