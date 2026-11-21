package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The nasalis: from the maxilla beside the nose, up over the side of the nose's ridge; it flares and narrows the
 * nostrils.
 */
public record Nasalis() implements Muscle {

    public static final Nasalis INSTANCE = new Nasalis();

    @Override
    public String name() {
        return "nasalis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Maxilla.INSTANCE, 1.571, 2.2, 9.504, 1.0, 0.2, 0.3, 0, 1),
                s.at(Maxilla.INSTANCE, 0.6, 4, 10.3, 1.0, 0.2, 0, 0, 1));
    }
}
