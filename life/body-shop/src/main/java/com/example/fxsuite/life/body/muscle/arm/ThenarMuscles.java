package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The thenar muscles, the abductor and the short flexor of the thumb: the ball of the thumb, from the wrist's bones on
 * the thumb's side to the base of its first phalanx.
 */
public record ThenarMuscles() implements Muscle {

    public static final ThenarMuscles INSTANCE = new ThenarMuscles();

    @Override
    public String name() {
        return "thenar muscles";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Trapezium.INSTANCE, 1.922, -2.4, 1.408, 1.6, 0.8, 0.2, 0, 1),
                s.at(FirstMetacarpal.INSTANCE, 2.703, -4.8, 2.257, 2.4, 1.6, 0.4, 0, 1),
                s.at(ThumbProximalPhalanx.INSTANCE, 4.266, -7.6, 3.332, 1.0, 0.6, 0.5, 0, 1));
    }
}
