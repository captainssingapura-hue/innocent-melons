package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semispinalis thoracis: from the lower thoracic transverse processes, up across several levels to the spines of
 * the upper thoracic.
 */
public record SemispinalisThoracis() implements Muscle {

    public static final SemispinalisThoracis INSTANCE = new SemispinalisThoracis();

    @Override
    public String name() {
        return "semispinalis thoracis";
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
                s.at(T10.INSTANCE, 2.8, 31, -7.3, 1.2, 0.6, 0, 0, -1),
                s.at(T6.INSTANCE, 1.6, 41, -8.65, 1.4, 0.7, 0, 0, -1),
                s.at(T2.INSTANCE, 0.6, 49, -9.35, 1.0, 0.5, 0, 0, -1));
    }
}
