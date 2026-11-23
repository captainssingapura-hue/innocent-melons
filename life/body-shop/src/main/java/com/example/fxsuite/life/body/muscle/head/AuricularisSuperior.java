package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The auricularis superior: a thin fan from the scalp's aponeurosis above the ear down to the top of the ear.
 */
public record AuricularisSuperior() implements Muscle {

    public static final AuricularisSuperior INSTANCE = new AuricularisSuperior();

    @Override
    public String name() {
        return "auricularis superior";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 6.502, 8.58, 0, 2.6, 0.2, 1, 0.2, 0),
                s.at(Skull.INSTANCE, 6.9, 4.2, -0.4, 1.0, 0.2, 1, 0, 0));
    }
}
