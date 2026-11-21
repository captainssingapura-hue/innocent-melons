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
 * The iliococcygeus: the levator ani's back part, from the tendinous arch on the obturator fascia, in and back to the
 * coccyx.
 */
public record Iliococcygeus() implements Muscle {

    public static final Iliococcygeus INSTANCE = new Iliococcygeus();

    @Override
    public String name() {
        return "iliococcygeus";
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
                s.at(HipBone.INSTANCE, 4.6, -2.85, 1, 1.0, 0.3, 0, -1, 0),
                s.at(HipBone.INSTANCE, 3, -3.85, -3, 3.0, 0.3, 0, -1, 0),
                s.at(Coccyx.INSTANCE, 0.4, -0.256, -5.757, 1.0, 0.3, 0, -1, -0.3));
    }
}
