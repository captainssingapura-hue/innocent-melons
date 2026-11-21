package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The first dorsal interosseous: between the thumb's and the index finger's metacarpals on the back of the hand,
 * bulging as the thumb is drawn in.
 */
public record FirstDorsalInterosseous() implements Muscle {

    public static final FirstDorsalInterosseous INSTANCE = new FirstDorsalInterosseous();

    @Override
    public String name() {
        return "first dorsal interosseous";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(FirstMetacarpal.INSTANCE, 2.256, -4.2, 0.779, 1.2, 1.0, 0.3, 0, -1),
                s.at(SecondMetacarpal.INSTANCE, 1.863, -6, 0.268, 1.6, 1.2, 1, 0, -0.5),
                s.at(SecondMetacarpal.INSTANCE, 2.4, -9, 0.1, 0.6, 0.4, 1, 0, 0));
    }
}
