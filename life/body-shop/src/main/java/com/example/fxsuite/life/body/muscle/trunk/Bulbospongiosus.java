package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The bulbospongiosus: from the perineal body forward round the bulb of the erectile body.
 */
public record Bulbospongiosus() implements Muscle {

    public static final Bulbospongiosus INSTANCE = new Bulbospongiosus();

    @Override
    public String name() {
        return "bulbospongiosus";
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
                s.at(HipBone.INSTANCE, 0.3, -9.2, -0.6, 1.0, 0.8, 0, -1, 0),
                s.at(HipBone.INSTANCE, 0.6, -9.042, 3.021, 1.0, 0.8, 0, -1, 0.5));
    }
}
