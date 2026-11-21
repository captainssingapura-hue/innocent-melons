package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The first lumbrical: from the index finger's tendon of the deep flexor in the palm, to the thumb's side of the index
 * finger's extensor hood.
 */
public record Lumbrical1() implements Muscle {

    public static final Lumbrical1 INSTANCE = new Lumbrical1();

    @Override
    public String name() {
        return "first lumbrical";
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
                s.at(SecondMetacarpal.INSTANCE, 1.8, -5, 1.05, 0.6, 0.5, 0, 0, 1),
                s.at(SecondMetacarpal.INSTANCE, 2.757, -9.6, 0.756, 0.4, 0.3, 0.3, 0, 1));
    }
}
