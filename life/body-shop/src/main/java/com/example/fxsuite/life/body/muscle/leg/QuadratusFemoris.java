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
 * The quadratus femoris: a short flat block from the ischial tuberosity's outer edge, straight out to the crest between
 * the trochanters behind.
 */
public record QuadratusFemoris() implements Muscle {

    public static final QuadratusFemoris INSTANCE = new QuadratusFemoris();

    @Override
    public String name() {
        return "quadratus femoris";
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
                s.at(HipBone.INSTANCE, 6.4, -7.8, -2.3, 2.6, 1.0, 0, 0, -1),
                s.at(Femur.INSTANCE, 4.6, -5, -1.5, 2.6, 1.0, 0, 0, -1));
    }
}
