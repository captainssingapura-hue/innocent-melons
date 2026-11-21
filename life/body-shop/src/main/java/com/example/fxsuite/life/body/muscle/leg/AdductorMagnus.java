package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The adductor magnus: from the pubis's lower ramus and the ischial tuberosity, the great mass at the back of the
 * thigh's inner side, down the femur's rough line to its adductor tubercle.
 */
public record AdductorMagnus() implements Muscle {

    public static final AdductorMagnus INSTANCE = new AdductorMagnus();

    @Override
    public String name() {
        return "adductor magnus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 5.095, -9.2, 0.495, 3.5, 1.4, -0.6, 0, -0.6),
                s.at(Femur.INSTANCE, -0.249, -16, -1.925, 6.5, 4.7, -1, 0, -0.3),
                s.at(Femur.INSTANCE, -0.533, -28, -1.567, 4.7, 3.4, -1, 0, -0.2),
                s.at(Femur.INSTANCE, -2.45, -36, -1.2, 1.8, 1.3, -1, 0, 0),
                s.at(Femur.INSTANCE, -3.05, -38, -0.9, 0.8, 0.5, -1, 0, 0));
    }
}
