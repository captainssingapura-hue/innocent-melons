package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The temporalis: a fan on the side of the skull, from the temporal fossa, gathering down under the zygomatic arch to
 * the mandible's coronoid.
 */
public record Temporalis() implements Muscle {

    public static final Temporalis INSTANCE = new Temporalis();

    @Override
    public String name() {
        return "temporalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 6.313, 8.914, 0, 8.0, 0.6, 1, 0.3, 0),
                s.at(Skull.INSTANCE, 6.1, 5.2, 1.6, 4.5, 1.0, 1, 0, 0),
                s.at(Mandible.INSTANCE, 4.706, 2, 2.541, 1.4, 0.6, 1, 0, 0.2));
    }
}
