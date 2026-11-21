package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The second dorsal interosseous: between the index finger's and the middle finger's metacarpals on the back of the
 * hand, to the middle finger's thumb side.
 */
public record DorsalInterosseous2() implements Muscle {

    public static final DorsalInterosseous2 INSTANCE = new DorsalInterosseous2();

    @Override
    public String name() {
        return "second dorsal interosseous";
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
                s.at(SecondMetacarpal.INSTANCE, 1.4, -4.2, 0.15, 0.8, 0.7, 0, 0, -1),
                s.at(ThirdMetacarpal.INSTANCE, 1.1, -7, 0.2, 0.9, 0.8, 0, 0, -1),
                s.at(ThirdMetacarpal.INSTANCE, 1.3, -10, 0.15, 0.4, 0.3, 0, 0, -1));
    }
}
