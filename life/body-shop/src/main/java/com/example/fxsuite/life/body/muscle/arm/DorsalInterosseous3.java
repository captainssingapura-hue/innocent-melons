package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The third dorsal interosseous: between the middle finger's and the ring finger's metacarpals, to the middle finger's
 * little-finger side.
 */
public record DorsalInterosseous3() implements Muscle {

    public static final DorsalInterosseous3 INSTANCE = new DorsalInterosseous3();

    @Override
    public String name() {
        return "third dorsal interosseous";
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
                s.at(ThirdMetacarpal.INSTANCE, -0.2, -4.4, 0.15, 0.8, 0.7, 0, 0, -1),
                s.at(ThirdMetacarpal.INSTANCE, -0.1, -7, 0.2, 0.9, 0.8, 0, 0, -1),
                s.at(ThirdMetacarpal.INSTANCE, 0.2, -10.2, 0.15, 0.4, 0.3, 0, 0, -1));
    }
}
