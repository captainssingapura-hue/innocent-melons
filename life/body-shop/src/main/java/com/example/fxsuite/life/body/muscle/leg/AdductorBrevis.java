package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The adductor brevis: from the pubis's body and lower ramus, under the longus, to the upper part of the femur's rough
 * line.
 */
public record AdductorBrevis() implements Muscle {

    public static final AdductorBrevis INSTANCE = new AdductorBrevis();

    @Override
    public String name() {
        return "adductor brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 3.2, -6, 4.1, 2.0, 1.0, 0, 0, 1),
                s.at(Femur.INSTANCE, -1.034, -11, 0.97, 3.4, 1.6, -1, 0, 0.3),
                s.at(Femur.INSTANCE, 2.85, -14, -0.8, 1.6, 0.5, -1, 0, 0));
    }
}
