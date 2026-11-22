package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The auricularis posterior: from the mastoid, forward to the back of the ear.
 */
public record AuricularisPosterior() implements Muscle {

    public static final AuricularisPosterior INSTANCE = new AuricularisPosterior();

    @Override
    public String name() {
        return "auricularis posterior";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 4.856, 1.8, -2.957, 0.6, 0.3, 1, 0, -0.3),
                s.at(Skull.INSTANCE, 6.45, 2.4, -1.4, 0.6, 0.3, 1, 0, 0));
    }
}
