package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The coccygeus: behind the levator ani, from the ischial spine in to the edges of the coccyx and the sacrum's foot.
 */
public record Coccygeus() implements Muscle {

    public static final Coccygeus INSTANCE = new Coccygeus();

    @Override
    public String name() {
        return "coccygeus";
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
                s.at(HipBone.INSTANCE, 5.6, -2.561, -4.528, 1.0, 0.5, 0, -1, -0.3),
                s.at(Sacrum.INSTANCE, 1.6, 1.239, -6.928, 2.0, 0.5, 0, -1, -0.3));
    }
}
