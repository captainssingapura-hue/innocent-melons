package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external anal sphincter: a ring round the end of the bowel, held behind to the coccyx and in front to the
 * perineal body.
 */
public record ExternalAnalSphincter() implements Muscle {

    public static final ExternalAnalSphincter INSTANCE = new ExternalAnalSphincter();

    @Override
    public String name() {
        return "external anal sphincter";
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
    public boolean ring() {
        return true;
    }

    @Override
    public boolean paired() {
        return false;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 0.9, -8.6, -3.4, 0.6, 0.6, 1, 0, 0),
                s.at(HipBone.INSTANCE, 0.638, -8.6, -2.762, 0.6, 0.6, 0.7, 0, 0.7),
                s.at(HipBone.INSTANCE, 0, -8.6, -2.5, 0.6, 0.6, 0, 0, 1),
                s.at(HipBone.INSTANCE, -0.638, -8.6, -2.762, 0.6, 0.6, -0.7, 0, 0.7),
                s.at(HipBone.INSTANCE, -0.9, -8.6, -3.4, 0.6, 0.6, -1, 0, 0),
                s.at(HipBone.INSTANCE, -0.638, -8.6, -4.038, 0.6, 0.6, -0.7, 0, -0.7),
                s.at(HipBone.INSTANCE, 0, -8.6, -4.3, 0.6, 0.6, 0, 0, -1),
                s.at(HipBone.INSTANCE, 0.638, -8.6, -4.038, 0.6, 0.6, 0.7, 0, -0.7));
    }
}
