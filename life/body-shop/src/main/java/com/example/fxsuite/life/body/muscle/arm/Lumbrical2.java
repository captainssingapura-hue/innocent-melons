package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The second lumbrical: from the middle finger's tendon of the deep flexor, to the thumb's side of the middle finger's
 * extensor hood.
 */
public record Lumbrical2() implements Muscle {

    public static final Lumbrical2 INSTANCE = new Lumbrical2();

    @Override
    public String name() {
        return "second lumbrical";
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
                s.at(ThirdMetacarpal.INSTANCE, 0.6, -5, 1.05, 0.6, 0.5, 0, 0, 1),
                s.at(ThirdMetacarpal.INSTANCE, 1.357, -10, 0.656, 0.4, 0.3, 0.3, 0, 1));
    }
}
