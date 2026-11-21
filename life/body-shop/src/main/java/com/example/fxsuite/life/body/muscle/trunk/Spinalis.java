package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The spinalis: the erector spinae's inner column, from the spines of the upper lumbar and lowest thoracic vertebrae up
 * the spines of the thoracic.
 */
public record Spinalis() implements Muscle {

    public static final Spinalis INSTANCE = new Spinalis();

    @Override
    public String name() {
        return "spinalis";
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
                s.at(T12.INSTANCE, 1.2, 25, -9.2, 1.0, 0.8, 0, 0, -1),
                s.at(T8.INSTANCE, 1.2, 35, -10.5, 1.4, 1.0, 0, 0, -1),
                s.at(T4.INSTANCE, 1.2, 44.5, -11.1, 1.0, 0.6, 0, 0, -1));
    }
}
