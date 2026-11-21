package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gluteus maximus: from the back of the ilium, the sacrum and the coccyx, thick, down and out across the buttock,
 * behind the greater trochanter, to the femur's gluteal tuberosity and the iliotibial band; the buttock's bulk.
 */
public record GluteusMaximus() implements Muscle {

    public static final GluteusMaximus INSTANCE = new GluteusMaximus();

    @Override
    public String name() {
        return "gluteus maximus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sacrum.INSTANCE, 2.4, 6, -8.2, 8.0, 1.2, 0, 0, -1),
                s.at(HipBone.INSTANCE, 6.363, 0.5, -7.854, 11.0, 3.6, 0.25, 0, -1),
                s.at(HipBone.INSTANCE, 9.881, -5, -5.281, 8.0, 2.6, 0.7, 0, -0.7),
                s.at(Femur.INSTANCE, 4.817, -9, -1.485, 3.4, 0.8, 1, 0, -0.3));
    }
}
