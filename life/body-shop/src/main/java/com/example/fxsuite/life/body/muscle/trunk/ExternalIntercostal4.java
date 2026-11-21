package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FourthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the fourth space: from behind round to where the ribs become cartilage, between the
 * fourth and the fifth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal4() implements Muscle {

    public static final ExternalIntercostal4 INSTANCE = new ExternalIntercostal4();

    @Override
    public String name() {
        return "external intercostal, fourth space";
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
                s.at(FourthRib.INSTANCE, 3.164, 44.76, -7.828, 1.85, 0.45, 0.338, 0.000, -0.941),
                s.at(FourthRib.INSTANCE, 5.081, 44.59, -9.358, 1.88, 0.45, 0.440, 0.000, -0.898),
                s.at(FourthRib.INSTANCE, 11.096, 39.56, -2.591, 2.12, 0.45, 0.951, 0.000, -0.308),
                s.at(FourthRib.INSTANCE, 9.38, 34.21, 6.94, 2.36, 0.45, 0.845, 0.000, 0.535));
    }
}
