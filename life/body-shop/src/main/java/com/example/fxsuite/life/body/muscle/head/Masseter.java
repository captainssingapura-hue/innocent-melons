package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The masseter: from the zygomatic arch, down the side of the jaw, thick, to the outer face of the mandible's angle; it
 * closes the jaw.
 */
public record Masseter() implements Muscle {

    public static final Masseter INSTANCE = new Masseter();

    @Override
    public String name() {
        return "masseter";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Zygomatic.INSTANCE, 5.408, 2.2, 3.922, 2.4, 0.8, 1, 0, 0.2),
                s.at(Mandible.INSTANCE, 5.4, -1, 2, 3.0, 1.2, 1, 0, 0),
                s.at(Mandible.INSTANCE, 5.106, -3.4, 0.459, 2.6, 0.6, 1, 0, -0.2));
    }
}
