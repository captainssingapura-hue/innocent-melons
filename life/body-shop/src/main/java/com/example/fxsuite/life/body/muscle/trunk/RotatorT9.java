package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T9: from the transverse process of T10, up and in to the root of T9's spine.
 */
public record RotatorT9() implements Muscle {

    public static final RotatorT9 INSTANCE = new RotatorT9();

    @Override
    public String name() {
        return "rotator, T9";
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
                s.at(T10.INSTANCE, 1.8, 31.38, -7.31, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T9.INSTANCE, 0.9, 34.05, -8.34, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
