package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The tensor fasciae latae: from the iliac crest by its anterior superior spine, a short belly down the front of the
 * hip's side, into the iliotibial band.
 */
public record TensorFasciaeLatae() implements Muscle {

    public static final TensorFasciaeLatae INSTANCE = new TensorFasciaeLatae();

    @Override
    public String name() {
        return "tensor fasciae latae";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 12.442, 6.6, 4.621, 1.6, 0.8, 1, 0, 0.5),
                s.at(HipBone.INSTANCE, 13.057, 2, 3.303, 2.8, 1.6, 1, 0, 0.4),
                s.at(Femur.INSTANCE, 7.225, -6, 2.028, 3.0, 1.2, 1, 0, 0.3),
                s.at(Femur.INSTANCE, 8.404, -10, 1.161, 3.0, 0.4, 1, 0, 0.2));
    }
}
