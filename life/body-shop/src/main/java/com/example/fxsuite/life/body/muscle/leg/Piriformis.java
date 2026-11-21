package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The piriformis: from the front of the sacrum, out through the greater sciatic notch, to the top of the greater
 * trochanter.
 */
public record Piriformis() implements Muscle {

    public static final Piriformis INSTANCE = new Piriformis();

    @Override
    public String name() {
        return "piriformis";
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
                s.at(Sacrum.INSTANCE, 3.6, 5, -6.5, 2.2, 1.0, 0, 0, -1),
                s.at(HipBone.INSTANCE, 8.882, 1.6, -5.012, 2.4, 1.2, 0.2, 0, -1),
                s.at(Femur.INSTANCE, 5.514, -0.8, -1.313, 1.0, 0.6, 0.3, 0, -1));
    }
}
