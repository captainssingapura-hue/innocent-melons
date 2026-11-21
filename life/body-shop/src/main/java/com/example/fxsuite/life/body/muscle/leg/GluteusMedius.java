package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The gluteus medius: from the outer face of the ilium under its crest, fanning down to the top of the greater
 * trochanter; the side of the hip above the buttock.
 */
public record GluteusMedius() implements Muscle {

    public static final GluteusMedius INSTANCE = new GluteusMedius();

    @Override
    public String name() {
        return "gluteus medius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 13.417, 9.685, -1.6, 9.0, 0.8, 1, 0.3, 0),
                s.at(HipBone.INSTANCE, 13.356, 5, -1.676, 10.3, 2.5, 1, 0, -0.1),
                s.at(HipBone.INSTANCE, 13.85, 0.5, -1, 5.8, 2.1, 1, 0, 0),
                s.at(Femur.INSTANCE, 6, -1.2, -0.8, 2.4, 0.8, 1, 0, 0));
    }
}
