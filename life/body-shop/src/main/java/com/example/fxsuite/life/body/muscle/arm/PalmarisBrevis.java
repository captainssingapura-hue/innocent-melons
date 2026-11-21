package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The palmaris brevis: a thin sheet in the skin across the heel of the palm on the little finger's side.
 */
public record PalmarisBrevis() implements Muscle {

    public static final PalmarisBrevis INSTANCE = new PalmarisBrevis();

    @Override
    public String name() {
        return "palmaris brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Hamate.INSTANCE, -1, -3, 2.1, 1.4, 0.2, 0, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -2.771, -3.6, 1.704, 1.4, 0.2, -0.3, 0, 1));
    }
}
