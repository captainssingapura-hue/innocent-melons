package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T5: from the transverse process of T6, up and in to the root of T5's spine.
 */
public record RotatorT5() implements Muscle {

    public static final RotatorT5 INSTANCE = new RotatorT5();

    @Override
    public String name() {
        return "rotator, T5";
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
                s.at(T6.INSTANCE, 1.8, 40.95, -8.11, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T5.INSTANCE, 0.9, 43.8, -8.23, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
