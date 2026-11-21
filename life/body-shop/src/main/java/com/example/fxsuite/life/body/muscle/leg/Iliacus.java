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
 * The iliacus: from the hollow inside the ilium's wing, down over the front of the hip joint, round to the lesser
 * trochanter; with the psoas, the hip's strongest flexor.
 */
public record Iliacus() implements Muscle {

    public static final Iliacus INSTANCE = new Iliacus();

    @Override
    public String name() {
        return "iliacus";
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
                s.at(HipBone.INSTANCE, 9.9, 8.6, 0.2, 6.0, 1.0, -0.6, 0, 0.8),
                s.at(HipBone.INSTANCE, 8.6, 2, 2.6, 4.0, 2.0, 0, 0, 1),
                s.at(Femur.INSTANCE, 1.823, -4.5, 0.843, 2.2, 1.2, -0.4, 0, 1),
                s.at(Femur.INSTANCE, 2.7, -6.4, -0.4, 1.0, 0.6, -1, 0, 0));
    }
}
