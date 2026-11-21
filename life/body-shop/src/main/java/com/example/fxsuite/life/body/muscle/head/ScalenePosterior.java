package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The scalenus posterior: from the transverse processes of the fourth to the sixth cervical vertebrae, down to the
 * outer face of the second rib.
 */
public record ScalenePosterior() implements Muscle {

    public static final ScalenePosterior INSTANCE = new ScalenePosterior();

    @Override
    public String name() {
        return "scalenus posterior";
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
                s.at(C5.INSTANCE, 3.217, 55, -1.885, 1.0, 0.8, 1, 0, -0.3),
                s.at(C7.INSTANCE, 4.521, 52.6, -2.256, 1.4, 1.0, 1, 0, -0.3),
                s.at(SecondRib.INSTANCE, 7.517, 46.124, -1.917, 1.4, 0.6, 0.3, 1, -0.3));
    }
}
