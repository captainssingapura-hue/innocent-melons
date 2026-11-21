package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pyramidalis: a small triangle in front of the rectus, from the pubis up to the linea alba.
 */
public record Pyramidalis() implements Muscle {

    public static final Pyramidalis INSTANCE = new Pyramidalis();

    @Override
    public String name() {
        return "pyramidalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.ABDOMEN;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 1, -2.6, 6.95, 1.2, 0.5, 0, 0, 1),
                s.at(L5.INSTANCE, 0.6, 3.6, 7.2, 0.6, 0.4, 0, 0, 1));
    }
}
