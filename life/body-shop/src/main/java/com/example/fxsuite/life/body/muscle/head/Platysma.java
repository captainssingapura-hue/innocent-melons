package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The platysma: a thin sheet in the skin from the lower border of the mandible, down over the front and sides of the
 * neck, to the top of the chest over the clavicle.
 */
public record Platysma() implements Muscle {

    public static final Platysma INSTANCE = new Platysma();

    @Override
    public String name() {
        return "platysma";
    }

    @Override
    public boolean sheet() {
        return true;
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 3.549, -5.37, 6.319, 6.0, 0.2, 0.5, -0.3, 0.8),
                s.at(C5.INSTANCE, 4.947, 53.5, 4.915, 7.0, 0.2, 0.5, 0, 0.8),
                s.at(Clavicle.INSTANCE, 8, 46.571, 8.304, 8.0, 0.2, 0, 0.3, 1));
    }
}
