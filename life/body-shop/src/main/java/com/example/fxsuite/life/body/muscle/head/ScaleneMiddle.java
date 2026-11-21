package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The scalenus medius: from the transverse processes of the second to the seventh cervical vertebrae, down the side of
 * the neck to the first rib behind the subclavian groove.
 */
public record ScaleneMiddle() implements Muscle {

    public static final ScaleneMiddle INSTANCE = new ScaleneMiddle();

    @Override
    public String name() {
        return "scalenus medius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(C3.INSTANCE, 2.7, 57.6, -1, 1.2, 1.0, 1, 0, 0),
                s.at(C6.INSTANCE, 3.7, 53.8, -0.6, 1.8, 1.4, 1, 0, 0),
                s.at(FirstRib.INSTANCE, 5.914, 47.913, 0, 2.0, 0.6, 0.3, 1, 0));
    }
}
