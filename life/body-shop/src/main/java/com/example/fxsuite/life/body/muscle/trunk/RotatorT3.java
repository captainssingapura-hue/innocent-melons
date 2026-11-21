package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T3: from the transverse process of T4, up and in to the root of T3's spine.
 */
public record RotatorT3() implements Muscle {

    public static final RotatorT3 INSTANCE = new RotatorT3();

    @Override
    public String name() {
        return "rotator, T3";
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
                s.at(T4.INSTANCE, 1.8, 45.6, -7.23, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T3.INSTANCE, 0.9, 48.25, -6.91, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
