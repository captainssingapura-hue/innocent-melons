package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The risorius: from the fascia over the masseter, forward to the corner of the mouth; it draws it back.
 */
public record Risorius() implements Muscle {

    public static final Risorius INSTANCE = new Risorius();

    @Override
    public String name() {
        return "risorius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 5.704, -0.6, 4.971, 0.8, 0.2, 1, 0, 0.3),
                s.at(Mandible.INSTANCE, 2.571, -1.4, 10.104, 0.6, 0.2, 0.3, 0, 1));
    }
}
