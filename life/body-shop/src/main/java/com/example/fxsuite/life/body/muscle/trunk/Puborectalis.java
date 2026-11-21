package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The puborectalis: the levator ani's sling, from the pubis back round behind the end of the bowel to its fellow.
 */
public record Puborectalis() implements Muscle {

    public static final Puborectalis INSTANCE = new Puborectalis();

    @Override
    public String name() {
        return "puborectalis";
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
                s.at(HipBone.INSTANCE, 1.6, -4.5, 4.2, 1.0, 0.6, 0, -1, 0),
                s.at(HipBone.INSTANCE, 1.4, -7.1, -2.2, 1.2, 0.6, 0, -1, 0),
                s.at(HipBone.INSTANCE, 0.2, -7.513, -3.514, 1.0, 0.6, 0, -1, -0.3));
    }
}
