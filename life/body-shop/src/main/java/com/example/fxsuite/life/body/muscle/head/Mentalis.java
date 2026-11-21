package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The mentalis: from the mandible below the lower teeth, down into the skin of the chin; it puckers it.
 */
public record Mentalis() implements Muscle {

    public static final Mentalis INSTANCE = new Mentalis();

    @Override
    public String name() {
        return "mentalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 0.6, -3.6, 8.9, 0.8, 0.6, 0, 0, 1),
                s.at(Mandible.INSTANCE, 0.6, -5.114, 9.513, 0.8, 0.6, 0, -0.3, 1));
    }
}
