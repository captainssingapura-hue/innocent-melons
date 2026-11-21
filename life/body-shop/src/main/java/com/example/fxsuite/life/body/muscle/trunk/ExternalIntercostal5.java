package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FifthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the fifth space: from behind round to where the ribs become cartilage, between the fifth
 * and the sixth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal5() implements Muscle {

    public static final ExternalIntercostal5 INSTANCE = new ExternalIntercostal5();

    @Override
    public String name() {
        return "external intercostal, fifth space";
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
                s.at(FifthRib.INSTANCE, 3.108, 42.42, -8.267, 1.95, 0.45, 0.318, 0.000, -0.948),
                s.at(FifthRib.INSTANCE, 5.084, 42.22, -9.826, 1.99, 0.45, 0.425, 0.000, -0.905),
                s.at(FifthRib.INSTANCE, 11.846, 37.01, -2.762, 2.14, 0.45, 0.953, 0.000, -0.302),
                s.at(FifthRib.INSTANCE, 10.296, 31.46, 7.066, 2.30, 0.45, 0.861, 0.000, 0.508));
    }
}
