package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T4: from the transverse process of T5, up and in to the root of T4's spine.
 */
public record RotatorT4() implements Muscle {

    public static final RotatorT4 INSTANCE = new RotatorT4();

    @Override
    public String name() {
        return "rotator, T4";
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
                s.at(T5.INSTANCE, 1.8, 43.36, -7.8, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T4.INSTANCE, 0.9, 46.05, -7.64, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
