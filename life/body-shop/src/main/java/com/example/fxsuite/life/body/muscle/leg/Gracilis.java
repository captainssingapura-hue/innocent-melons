package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gracilis: from the pubis by the symphysis, a thin strap straight down the inside of the thigh, round behind the
 * knee's inner side, to the tibia's upper inner face.
 */
public record Gracilis() implements Muscle {

    public static final Gracilis INSTANCE = new Gracilis();

    @Override
    public String name() {
        return "gracilis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 2.039, -6.8, 4.928, 2.2, 0.5, -1, 0, 0.3),
                s.at(Femur.INSTANCE, -4.5, -14, 0.8, 2.9, 1.0, -1, 0, 0),
                s.at(Femur.INSTANCE, -4.55, -28, -0.6, 2.0, 0.9, -1, 0, 0),
                s.at(Femur.INSTANCE, -4.5, -38, -1.6, 0.7, 0.6, -1, 0, 0),
                s.at(Tibia.INSTANCE, -2.68, -6.4, 0.31, 0.8, 0.3, -0.8, 0, 0.6));
    }
}
