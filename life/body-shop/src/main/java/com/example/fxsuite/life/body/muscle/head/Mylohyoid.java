package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The mylohyoid: the floor of the mouth, from the inside of the mandible's body, down and back to the hyoid; with the
 * digastric, the underside of the jaw.
 */
public record Mylohyoid() implements Muscle {

    public static final Mylohyoid INSTANCE = new Mylohyoid();

    @Override
    public String name() {
        return "mylohyoid";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 2.6, -4.404, 5.361, 3.0, 0.4, 0, -1, 0.2),
                s.at(Hyoid.INSTANCE, 1, 5.186, 3.726, 2.0, 0.4, 0, -1, 0.4));
    }
}
