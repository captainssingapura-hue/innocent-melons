package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The orbicularis oculi: a ring round the eye, over the rim of the orbit and in the lids; it shuts the eye.
 */
public record OrbicularisOculi() implements Muscle {

    public static final OrbicularisOculi INSTANCE = new OrbicularisOculi();

    @Override
    public String name() {
        return "orbicularis oculi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public boolean ring() {
        return true;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Frontal.INSTANCE, 5.51, 5.4, 7.68, 1.2, 0.3, 0.6, 0, 0.8),
                s.at(Frontal.INSTANCE, 4.839, 6.739, 8.478, 1.2, 0.3, 0.4, 0.4, 0.8),
                s.at(Frontal.INSTANCE, 3.3, 7.339, 9.263, 1.2, 0.3, 0, 0.4, 0.9),
                s.at(Frontal.INSTANCE, 1.745, 6.755, 9.464, 1.2, 0.3, -0.3, 0.3, 0.9),
                s.at(Maxilla.INSTANCE, 1.061, 5.4, 9.263, 1.2, 0.3, -0.4, 0, 0.9),
                s.at(Maxilla.INSTANCE, 1.731, 4.046, 9.261, 1.2, 0.3, -0.2, -0.3, 0.9),
                s.at(Zygomatic.INSTANCE, 3.3, 3.461, 9.063, 1.2, 0.3, 0, -0.4, 0.9),
                s.at(Zygomatic.INSTANCE, 4.836, 4.048, 8.473, 1.2, 0.3, 0.4, -0.3, 0.8));
    }
}
