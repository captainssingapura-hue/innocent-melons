package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external oblique: from the outer faces of the lower eight ribs, down and forward over the flank, to the iliac
 * crest and into the sheath of the rectus.
 */
public record ExternalOblique() implements Muscle {

    public static final ExternalOblique INSTANCE = new ExternalOblique();

    @Override
    public String name() {
        return "external oblique";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.ABDOMEN;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 13.608, 29, 1.922, 12.0, 0.8, 1, 0, 0.2),
                s.at(L3.INSTANCE, 10.75, 17, 3.74, 12.0, 1.4, 1, 0, 0.4),
                s.at(HipBone.INSTANCE, 12.052, 8.6, 2.956, 8.0, 1.2, 0.9, 0, 0.4));
    }
}
