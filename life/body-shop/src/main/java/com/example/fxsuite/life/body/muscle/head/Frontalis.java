package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The frontalis: the front belly of the occipitofrontalis, a thin sheet over the forehead, from the scalp's aponeurosis
 * down to the skin of the brows.
 */
public record Frontalis() implements Muscle {

    public static final Frontalis INSTANCE = new Frontalis();

    @Override
    public String name() {
        return "frontalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Frontal.INSTANCE, 2.771, 13.512, 5.482, 5.0, 0.3, 0.2, 0.6, 0.8),
                s.at(Frontal.INSTANCE, 2.761, 9, 8.804, 5.0, 0.4, 0.2, 0, 1),
                s.at(Frontal.INSTANCE, 2.8, 6.4, 9.65, 4.6, 0.3, 0, 0, 1));
    }
}
