package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FourthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the fourth space: from the angles round to the front, between the fourth and the fifth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal4() implements Muscle {

    public static final InternalIntercostal4 INSTANCE = new InternalIntercostal4();

    @Override
    public String name() {
        return "internal intercostal, fourth space";
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
                s.at(FourthRib.INSTANCE, 4.081, 44.74, -8.456, 1.90, 0.40, 0.396, 0.000, -0.918),
                s.at(FourthRib.INSTANCE, 7.664, 42.38, -6.223, 1.95, 0.40, 0.728, 0.000, -0.686),
                s.at(FourthRib.INSTANCE, 10.266, 35.95, 3.473, 2.30, 0.40, 0.972, 0.000, 0.234),
                s.at(FourthRib.INSTANCE, 7.948, 33, 8.849, 2.40, 0.40, 0.711, 0.000, 0.703));
    }
}
