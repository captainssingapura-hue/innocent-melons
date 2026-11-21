package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T2: from the transverse process of T3, up and in to the root of T2's spine.
 */
public record RotatorT2() implements Muscle {

    public static final RotatorT2 INSTANCE = new RotatorT2();

    @Override
    public String name() {
        return "rotator, T2";
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
                s.at(T3.INSTANCE, 1.8, 47.79, -6.53, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T2.INSTANCE, 0.9, 50.34, -6.07, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
