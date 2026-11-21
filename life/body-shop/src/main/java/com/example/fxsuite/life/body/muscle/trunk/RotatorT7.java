package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T7: from the transverse process of T8, up and in to the root of T7's spine.
 */
public record RotatorT7() implements Muscle {

    public static final RotatorT7 INSTANCE = new RotatorT7();

    @Override
    public String name() {
        return "rotator, T7";
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
                s.at(T8.INSTANCE, 1.8, 36.13, -8.09, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T7.INSTANCE, 0.9, 38.91, -8.7, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
