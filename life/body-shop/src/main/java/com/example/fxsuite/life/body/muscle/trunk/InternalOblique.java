package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal oblique: under the external, from the iliac crest and the thoracolumbar fascia, up and forward to the
 * lower ribs and the rectus's sheath.
 */
public record InternalOblique() implements Muscle {

    public static final InternalOblique INSTANCE = new InternalOblique();

    @Override
    public String name() {
        return "internal oblique";
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
                s.at(HipBone.INSTANCE, 11.617, 8, 0.885, 7.0, 0.8, 1, 0, 0.3),
                s.at(L3.INSTANCE, 10.136, 16, 2.814, 10.0, 1.0, 1, 0, 0.4),
                s.at(Sternum.INSTANCE, 11.926, 27, 3.878, 9.0, 0.6, 0.9, 0, 0.4));
    }
}
