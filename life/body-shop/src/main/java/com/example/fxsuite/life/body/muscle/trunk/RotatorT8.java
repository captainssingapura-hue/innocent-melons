package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T8: from the transverse process of T9, up and in to the root of T8's spine.
 */
public record RotatorT8() implements Muscle {

    public static final RotatorT8 INSTANCE = new RotatorT8();

    @Override
    public String name() {
        return "rotator, T8";
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
                s.at(T9.INSTANCE, 1.8, 33.74, -7.77, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T8.INSTANCE, 0.9, 36.47, -8.63, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
