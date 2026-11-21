package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T11;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T10: from the transverse process of T11, up and in to the root of T10's spine.
 */
public record RotatorT10() implements Muscle {

    public static final RotatorT10 INSTANCE = new RotatorT10();

    @Override
    public String name() {
        return "rotator, T10";
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
                s.at(T11.INSTANCE, 1.8, 29, -6.67, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T10.INSTANCE, 0.9, 31.68, -7.9, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
