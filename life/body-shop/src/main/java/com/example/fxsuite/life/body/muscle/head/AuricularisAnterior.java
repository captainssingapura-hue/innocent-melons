package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The auricularis anterior: from the scalp's aponeurosis before the ear, back to the front of the ear.
 */
public record AuricularisAnterior() implements Muscle {

    public static final AuricularisAnterior INSTANCE = new AuricularisAnterior();

    @Override
    public String name() {
        return "auricularis anterior";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Temporal.INSTANCE, 6.502, 4, 3.38, 1.0, 0.2, 1, 0, 0.2),
                s.at(Temporal.INSTANCE, 6.9, 2.8, 0.6, 0.8, 0.2, 1, 0, 0));
    }
}
