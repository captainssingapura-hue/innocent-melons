package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pectineus: from the pubis's upper ramus, down, out and back across the top of the thigh's inner side, to the line
 * below the lesser trochanter.
 */
public record Pectineus() implements Muscle {

    public static final Pectineus INSTANCE = new Pectineus();

    @Override
    public String name() {
        return "pectineus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 4.6, -2.6, 4.9, 2.6, 1.0, 0, 0, 1),
                s.at(Femur.INSTANCE, -0.94, -5.4, 2.35, 2.6, 1.4, -0.4, 0, 1),
                s.at(Femur.INSTANCE, 2.239, -7.6, 0.128, 1.4, 0.5, -1, 0, 0.3));
    }
}
