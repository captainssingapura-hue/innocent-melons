package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FifthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the fifth space: from the angles round to the front, between the fifth and the sixth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal5() implements Muscle {

    public static final InternalIntercostal5 INSTANCE = new InternalIntercostal5();

    @Override
    public String name() {
        return "internal intercostal, fifth space";
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
                s.at(FifthRib.INSTANCE, 4.035, 42.34, -8.915, 2.01, 0.40, 0.377, 0.000, -0.926),
                s.at(FifthRib.INSTANCE, 8.024, 39.93, -6.563, 2.05, 0.40, 0.728, 0.000, -0.686),
                s.at(FifthRib.INSTANCE, 11.185, 33.26, 3.516, 2.26, 0.40, 0.976, 0.000, 0.220),
                s.at(FifthRib.INSTANCE, 8.832, 30.2, 9.065, 2.33, 0.40, 0.738, 0.000, 0.674));
    }
}
