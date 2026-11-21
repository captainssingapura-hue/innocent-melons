package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.Pisiform;
import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The hypothenar muscles: the heel of the palm on the little finger's side, from the pisiform and the hamate to the
 * little finger's base.
 */
public record HypothenarMuscles() implements Muscle {

    public static final HypothenarMuscles INSTANCE = new HypothenarMuscles();

    @Override
    public String name() {
        return "hypothenar muscles";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Pisiform.INSTANCE, -1.414, -1.8, 1.036, 1.4, 1.0, -0.4, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -2.34, -6, 0.35, 1.6, 1.4, -0.4, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -2.607, -8.8, 0.768, 0.8, 0.5, -0.4, 0, 1));
    }
}
