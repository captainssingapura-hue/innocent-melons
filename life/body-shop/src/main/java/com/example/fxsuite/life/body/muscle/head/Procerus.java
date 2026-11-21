package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The procerus: from the root of the nose, up between the brows into their skin; it wrinkles the bridge of the nose.
 */
public record Procerus() implements Muscle {

    public static final Procerus INSTANCE = new Procerus();

    @Override
    public String name() {
        return "procerus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Maxilla.INSTANCE, 0.4, 5.8, 9.65, 0.8, 0.3, 0, 0, 1),
                s.at(Frontal.INSTANCE, 0.4, 7.8, 10.05, 0.8, 0.3, 0, 0, 1));
    }
}
