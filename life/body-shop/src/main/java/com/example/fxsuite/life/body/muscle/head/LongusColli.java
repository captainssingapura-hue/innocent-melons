package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The longus colli: on the fronts of the vertebral bodies from the atlas down to the third thoracic; it bends the neck
 * forward.
 */
public record LongusColli() implements Muscle {

    public static final LongusColli INSTANCE = new LongusColli();

    @Override
    public String name() {
        return "longus colli";
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
                s.at(Axis.INSTANCE, 0.8, 58.8, 0.1, 1.0, 0.6, 0, 0, 1),
                s.at(C5.INSTANCE, 1, 55.4, -1, 1.4, 0.8, 0, 0, 1),
                s.at(T1.INSTANCE, 0.8, 51.2, -2.5, 1.2, 0.6, 0, 0, 1));
    }
}
