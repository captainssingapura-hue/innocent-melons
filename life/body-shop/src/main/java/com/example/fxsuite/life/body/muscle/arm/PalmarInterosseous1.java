package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The first palmar interosseous: on the palm's side of the index finger's metacarpal, to that finger's little-finger
 * side; it draws it in.
 */
public record PalmarInterosseous1() implements Muscle {

    public static final PalmarInterosseous1 INSTANCE = new PalmarInterosseous1();

    @Override
    public String name() {
        return "first palmar interosseous";
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
                s.at(SecondMetacarpal.INSTANCE, 1.4, -5.4, 0.3, 0.6, 0.6, 0, 0, 1),
                s.at(SecondMetacarpal.INSTANCE, 2, -9.6, 0.25, 0.3, 0.3, 0, 0, 1));
    }
}
