package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The buccinator: the cheek's wall, from the maxilla and the mandible behind the teeth, forward to the corner of the
 * mouth.
 */
public record Buccinator() implements Muscle {

    public static final Buccinator INSTANCE = new Buccinator();

    @Override
    public String name() {
        return "buccinator";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 4.208, -0.6, 4.543, 2.4, 0.4, 1, 0, 0.3),
                s.at(Mandible.INSTANCE, 3.421, -1.6, 7.511, 2.2, 0.4, 1, 0, 0.5),
                s.at(Mandible.INSTANCE, 2.88, -1.6, 9.04, 1.0, 0.4, 0.6, 0, 0.8));
    }
}
