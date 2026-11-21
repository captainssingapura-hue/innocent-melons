package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The orbicularis oris: a ring round the mouth, in the lips; it closes and purses them.
 */
public record OrbicularisOris() implements Muscle {

    public static final OrbicularisOris INSTANCE = new OrbicularisOris();

    @Override
    public String name() {
        return "orbicularis oris";
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
    public boolean paired() {
        return false;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Maxilla.INSTANCE, 2.807, -1.6, 9.368, 0.9, 0.5, 0.4, 0, 1),
                s.at(Maxilla.INSTANCE, 1.952, -0.648, 9.959, 0.9, 0.5, 0.2, 0.2, 1),
                s.at(Maxilla.INSTANCE, 0, -0.372, 10.361, 0.9, 0.5, 0, 0.3, 1),
                s.at(Maxilla.INSTANCE, -1.952, -0.648, 9.959, 0.9, 0.5, -0.2, 0.2, 1),
                s.at(Maxilla.INSTANCE, -2.807, -1.6, 9.368, 0.9, 0.5, -0.4, 0, 1),
                s.at(Mandible.INSTANCE, -1.952, -2.552, 9.959, 0.9, 0.5, -0.2, -0.2, 1),
                s.at(Mandible.INSTANCE, 0, -2.828, 10.261, 0.9, 0.5, 0, -0.3, 1),
                s.at(Mandible.INSTANCE, 1.952, -2.552, 9.959, 0.9, 0.5, 0.2, -0.2, 1));
    }
}
