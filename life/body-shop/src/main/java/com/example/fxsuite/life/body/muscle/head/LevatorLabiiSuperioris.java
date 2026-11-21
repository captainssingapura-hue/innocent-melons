package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The levator labii superioris: from under the orbit's rim, down into the upper lip; it lifts it.
 */
public record LevatorLabiiSuperioris() implements Muscle {

    public static final LevatorLabiiSuperioris INSTANCE = new LevatorLabiiSuperioris();

    @Override
    public String name() {
        return "levator labii superioris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Maxilla.INSTANCE, 3, 3.6, 9.2, 1.6, 0.4, 0, 0, 1),
                s.at(Maxilla.INSTANCE, 2.4, 0.4, 10.4, 1.4, 0.4, 0, 0, 1));
    }
}
