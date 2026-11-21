package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The third palmar interosseous: on the palm's side of the little finger's metacarpal, to that finger's thumb side.
 */
public record PalmarInterosseous3() implements Muscle {

    public static final PalmarInterosseous3 INSTANCE = new PalmarInterosseous3();

    @Override
    public String name() {
        return "third palmar interosseous";
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
                s.at(FifthMetacarpal.INSTANCE, -1.6, -5.4, 0.3, 0.6, 0.6, 0, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -1.8, -9, 0.25, 0.3, 0.3, 0, 0, 1));
    }
}
