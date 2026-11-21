package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the first space: from the angles round to the front, between the first and the second
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal1() implements Muscle {

    public static final InternalIntercostal1 INSTANCE = new InternalIntercostal1();

    @Override
    public String name() {
        return "internal intercostal, first space";
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
                s.at(FirstRib.INSTANCE, 4.366, 51.18, -6.209, 1.66, 0.40, 0.518, 0.000, -0.855),
                s.at(FirstRib.INSTANCE, 5.831, 49.22, -4.266, 1.92, 0.40, 0.743, 0.000, -0.670),
                s.at(FirstRib.INSTANCE, 5.429, 44.26, 2.7, 2.81, 0.40, 0.954, 0.000, 0.299),
                s.at(FirstRib.INSTANCE, 3.93, 42, 5.618, 3.14, 0.40, 0.648, 0.000, 0.762));
    }
}
