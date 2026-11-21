package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The depressor labii inferioris: from the mandible beside the chin, up into the lower lip.
 */
public record DepressorLabiiInferioris() implements Muscle {

    public static final DepressorLabiiInferioris INSTANCE = new DepressorLabiiInferioris();

    @Override
    public String name() {
        return "depressor labii inferioris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Mandible.INSTANCE, 1.8, -4.957, 8.456, 1.2, 0.3, 0, -0.3, 1),
                s.at(Mandible.INSTANCE, 1.2, -2.6, 10.05, 1.2, 0.3, 0, 0, 1));
    }
}
