package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.Coccyx;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pubococcygeus: the levator ani's middle part, from the back of the pubis, back along the pelvic floor to the
 * coccyx.
 */
public record Pubococcygeus() implements Muscle {

    public static final Pubococcygeus INSTANCE = new Pubococcygeus();

    @Override
    public String name() {
        return "pubococcygeus";
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
                s.at(HipBone.INSTANCE, 1.8, -4.2, 4.6, 1.4, 0.4, 0, -1, 0),
                s.at(HipBone.INSTANCE, 1.6, -6.2, -1, 2.4, 0.4, 0, -1, 0),
                s.at(Coccyx.INSTANCE, 0.4, -0.608, -5.943, 1.0, 0.4, 0, -1, -0.3));
    }
}
