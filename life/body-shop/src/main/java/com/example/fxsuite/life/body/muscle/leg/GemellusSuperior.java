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
 * The gemellus superior: from the ischial spine, out above the obturator internus's tendon to the greater trochanter.
 */
public record GemellusSuperior() implements Muscle {

    public static final GemellusSuperior INSTANCE = new GemellusSuperior();

    @Override
    public String name() {
        return "gemellus superior";
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
                s.at(HipBone.INSTANCE, 5.4, -3, -4.5, 0.8, 0.6, 0, 0, -1),
                s.at(Femur.INSTANCE, 4.914, -1.4, -1.313, 0.8, 0.6, 0.3, 0, -1));
    }
}
