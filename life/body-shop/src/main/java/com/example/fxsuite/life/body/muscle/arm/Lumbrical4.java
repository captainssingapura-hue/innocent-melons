package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fourth lumbrical: from the tendons of the deep flexor to the ring and little fingers, to the thumb's side of the
 * little finger's extensor hood.
 */
public record Lumbrical4() implements Muscle {

    public static final Lumbrical4 INSTANCE = new Lumbrical4();

    @Override
    public String name() {
        return "fourth lumbrical";
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
                s.at(FifthMetacarpal.INSTANCE, -1.8, -5, 1.05, 0.6, 0.5, 0, 0, 1),
                s.at(FifthMetacarpal.INSTANCE, -1.643, -9.2, 0.756, 0.4, 0.3, 0.3, 0, 1));
    }
}
