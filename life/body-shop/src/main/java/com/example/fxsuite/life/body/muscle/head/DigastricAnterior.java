package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The digastric's anterior belly: from the inside of the mandible beside the chin, back to its intermediate tendon held
 * to the hyoid.
 */
public record DigastricAnterior() implements Muscle {

    public static final DigastricAnterior INSTANCE = new DigastricAnterior();

    @Override
    public String name() {
        return "digastric, anterior belly";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 0.9, -5.306, 7.141, 0.9, 0.6, 0, -1, 0.2),
                s.at(Hyoid.INSTANCE, 1.4, 4.796, 3.561, 0.5, 0.4, 0, -1, 0.2));
    }
}
