package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The adductor longus: from the front of the pubis by its tubercle, out and down the inside of the thigh, to the middle
 * of the femur's rough line behind.
 */
public record AdductorLongus() implements Muscle {

    public static final AdductorLongus INSTANCE = new AdductorLongus();

    @Override
    public String name() {
        return "adductor longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 2.4, -4.4, 5.6, 1.4, 0.8, 0, 0, 1),
                s.at(Femur.INSTANCE, -3.208, -10, 2.216, 4.6, 2.2, -0.5, 0, 1),
                s.at(Femur.INSTANCE, 1.126, -18, 0.387, 3.6, 1.4, -1, 0, 0.5),
                s.at(Femur.INSTANCE, 2.445, -22, -0.849, 1.6, 0.5, -1, 0, 0.2));
    }
}
