package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fourth dorsal interosseous: between the ring finger's and the little finger's metacarpals, to the ring finger's
 * little-finger side.
 */
public record DorsalInterosseous4() implements Muscle {

    public static final DorsalInterosseous4 INSTANCE = new DorsalInterosseous4();

    @Override
    public String name() {
        return "fourth dorsal interosseous";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(FourthMetacarpal.INSTANCE, -1.6, -4.4, 0.15, 0.8, 0.7, 0, 0, -1),
                s.at(FourthMetacarpal.INSTANCE, -1.5, -7, 0.2, 0.9, 0.8, 0, 0, -1),
                s.at(FourthMetacarpal.INSTANCE, -1.2, -9.8, 0.15, 0.4, 0.3, 0, 0, -1));
    }
}
