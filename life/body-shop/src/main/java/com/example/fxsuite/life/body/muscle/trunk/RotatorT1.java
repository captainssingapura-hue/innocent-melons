package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rotator of T1: from the transverse process of T2, up and in to the root of T1's spine.
 */
public record RotatorT1() implements Muscle {

    public static final RotatorT1 INSTANCE = new RotatorT1();

    @Override
    public String name() {
        return "rotator, T1";
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
                s.at(T2.INSTANCE, 1.8, 49.87, -5.71, 0.70, 0.50, 0.000, 0.000, -1.000),
                s.at(T1.INSTANCE, 0.9, 52.26, -5.27, 0.70, 0.50, 0.000, 0.000, -1.000));
    }
}
