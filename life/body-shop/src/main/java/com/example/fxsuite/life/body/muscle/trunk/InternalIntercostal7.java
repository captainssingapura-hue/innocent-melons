package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SeventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the seventh space: from the angles round to the front, between the seventh and the eighth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal7() implements Muscle {

    public static final InternalIntercostal7 INSTANCE = new InternalIntercostal7();

    @Override
    public String name() {
        return "internal intercostal, seventh space";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(SeventhRib.INSTANCE, 3.908, 37.36, -9.103, 2.00, 0.40, 0.360, 0.000, -0.933),
                s.at(SeventhRib.INSTANCE, 8.442, 34.95, -6.675, 1.97, 0.40, 0.740, 0.000, -0.673),
                s.at(SeventhRib.INSTANCE, 12.283, 28.13, 3.215, 1.97, 0.40, 0.984, 0.000, 0.177),
                s.at(SeventhRib.INSTANCE, 9.999, 25, 8.302, 2.05, 0.40, 0.807, 0.000, 0.590));
    }
}
