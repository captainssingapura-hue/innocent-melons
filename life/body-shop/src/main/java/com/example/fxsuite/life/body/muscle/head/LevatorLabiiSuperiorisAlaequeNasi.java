package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The levator labii superioris alaeque nasi: from the maxilla's frontal process, down beside the nose to its wing and
 * the upper lip.
 */
public record LevatorLabiiSuperiorisAlaequeNasi() implements Muscle {

    public static final LevatorLabiiSuperiorisAlaequeNasi INSTANCE = new LevatorLabiiSuperiorisAlaequeNasi();

    @Override
    public String name() {
        return "levator labii superioris alaeque nasi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 1.2, 5, 9.45, 0.7, 0.3, 0, 0, 1),
                s.at(Skull.INSTANCE, 1.6, 2.6, 10.05, 0.7, 0.3, 0, 0, 1),
                s.at(Skull.INSTANCE, 1.8, 0.4, 10.45, 0.7, 0.3, 0, 0, 1));
    }
}
