package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The occipitalis: the back belly of the occipitofrontalis, a thin sheet from the occiput's highest nuchal line up into
 * the scalp's aponeurosis.
 */
public record Occipitalis() implements Muscle {

    public static final Occipitalis INSTANCE = new Occipitalis();

    @Override
    public String name() {
        return "occipitalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Occipital.INSTANCE, 2.957, 6.6, -8.456, 3.0, 0.3, 0.3, 0, -1),
                s.at(Occipital.INSTANCE, 3.171, 9.361, -7.513, 3.0, 0.2, 0.3, 0.4, -0.9));
    }
}
