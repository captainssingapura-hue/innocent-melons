package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T6: from the transverse process of T7, up and in to the root of T6's spine.
 */
public record RotatorT6() implements Muscle {

    public static final RotatorT6 INSTANCE = new RotatorT6();

    @Override
    public String name() {
        return "rotator, T6";
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
                s.at(T7.INSTANCE, 1.8, 38.55, -8.19, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T6.INSTANCE, 0.9, 41.36, -8.59, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
