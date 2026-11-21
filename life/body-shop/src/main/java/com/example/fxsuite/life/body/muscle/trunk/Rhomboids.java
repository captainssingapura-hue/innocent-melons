package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rhomboids, major and minor: from the spines of the seventh cervical and the upper thoracic vertebrae, under the
 * trapezius, down and out to the scapula's inner border.
 */
public record Rhomboids() implements Muscle {

    public static final Rhomboids INSTANCE = new Rhomboids();

    @Override
    public String name() {
        return "rhomboids";
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
                s.at(T4.INSTANCE, 1, 44, -11.2, 9.0, 0.8, 0, 0, -1),
                s.at(Scapula.INSTANCE, 7.459, 40.5, -10.306, 10.0, 0.6, -0.2, 0, -1));
    }
}
