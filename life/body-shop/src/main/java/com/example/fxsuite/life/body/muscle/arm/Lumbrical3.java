package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The third lumbrical: from the tendons of the deep flexor to the middle and ring fingers, to the thumb's side of the
 * ring finger's extensor hood.
 */
public record Lumbrical3() implements Muscle {

    public static final Lumbrical3 INSTANCE = new Lumbrical3();

    @Override
    public String name() {
        return "third lumbrical";
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
                s.at(FourthMetacarpal.INSTANCE, -0.8, -5, 1.05, 0.6, 0.5, 0, 0, 1),
                s.at(FourthMetacarpal.INSTANCE, -0.243, -9.8, 0.656, 0.4, 0.3, 0.3, 0, 1));
    }
}
