package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The serratus posterior inferior: a thin sheet from the spines of the lowest thoracic and the upper lumbar vertebrae,
 * up and out to the last four ribs.
 */
public record SerratusPosteriorInferior() implements Muscle {

    public static final SerratusPosteriorInferior INSTANCE = new SerratusPosteriorInferior();

    @Override
    public String name() {
        return "serratus posterior inferior";
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
                s.at(T12.INSTANCE, 0.8, 25, -9.7, 5.0, 0.2, 0, 0, -1),
                s.at(TenthRib.INSTANCE, 9.555, 27, -8.311, 6.0, 0.2, 0.5, 0, -1));
    }
}
