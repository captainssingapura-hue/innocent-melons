package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The serratus posterior superior: a thin sheet from the spines of the lowest cervical and the upper thoracic
 * vertebrae, down and out to the second to the fifth ribs.
 */
public record SerratusPosteriorSuperior() implements Muscle {

    public static final SerratusPosteriorSuperior INSTANCE = new SerratusPosteriorSuperior();

    @Override
    public String name() {
        return "serratus posterior superior";
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
                s.at(C7.INSTANCE, 0.8, 53, -8.5, 4.0, 0.2, 0, 0, -1),
                s.at(ThirdRib.INSTANCE, 6.971, 43.6, -9.504, 5.0, 0.2, 0.3, 0, -1));
    }
}
