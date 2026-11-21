package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semispinalis cervicis: from the upper thoracic transverse processes, up to the spines of the cervical vertebrae
 * from the axis down.
 */
public record SemispinalisCervicis() implements Muscle {

    public static final SemispinalisCervicis INSTANCE = new SemispinalisCervicis();

    @Override
    public String name() {
        return "semispinalis cervicis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T5.INSTANCE, 2.6, 43, -7.3, 1.2, 0.6, 0, 0, -1),
                s.at(C7.INSTANCE, 1.4, 52.4, -5.6, 1.4, 0.8, 0, 0, -1),
                s.at(Axis.INSTANCE, 0.6, 58.4, -4.3, 1.0, 0.6, 0, 0, -1));
    }
}
