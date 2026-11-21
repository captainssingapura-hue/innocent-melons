package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the first space: from behind round to where the ribs become cartilage, between the first
 * and the second ribs, its fibres running down and forward.
 */
public record ExternalIntercostal1() implements Muscle {

    public static final ExternalIntercostal1 INSTANCE = new ExternalIntercostal1();

    @Override
    public String name() {
        return "external intercostal, first space";
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
                s.at(FirstRib.INSTANCE, 3.407, 51.13, -5.64, 1.66, 0.45, 0.457, 0.000, -0.890),
                s.at(FirstRib.INSTANCE, 5.296, 50.98, -7.042, 1.65, 0.45, 0.550, 0.000, -0.835),
                s.at(FirstRib.INSTANCE, 6.876, 47.04, -1.211, 2.56, 0.45, 0.952, 0.000, -0.306),
                s.at(FirstRib.INSTANCE, 5, 42.93, 4.755, 2.94, 0.45, 0.799, 0.000, 0.601));
    }
}
