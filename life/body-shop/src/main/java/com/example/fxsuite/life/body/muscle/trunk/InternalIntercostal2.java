package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the second space: from the angles round to the front, between the second and the third
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal2() implements Muscle {

    public static final InternalIntercostal2 INSTANCE = new InternalIntercostal2();

    @Override
    public String name() {
        return "internal intercostal, second space";
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
                s.at(SecondRib.INSTANCE, 4.286, 49.17, -7.024, 1.83, 0.40, 0.471, 0.000, -0.882),
                s.at(SecondRib.INSTANCE, 6.642, 47.05, -5.055, 2.00, 0.40, 0.739, 0.000, -0.674),
                s.at(SecondRib.INSTANCE, 7.497, 41.53, 3.047, 2.73, 0.40, 0.965, 0.000, 0.264),
                s.at(SecondRib.INSTANCE, 5.567, 39, 7.231, 2.95, 0.40, 0.666, 0.000, 0.746));
    }
}
