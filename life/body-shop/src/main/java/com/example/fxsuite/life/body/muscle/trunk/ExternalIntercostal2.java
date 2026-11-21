package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the second space: from behind round to where the ribs become cartilage, between the
 * second and the third ribs, its fibres running down and forward.
 */
public record ExternalIntercostal2() implements Muscle {

    public static final ExternalIntercostal2 INSTANCE = new ExternalIntercostal2();

    @Override
    public String name() {
        return "external intercostal, second space";
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
                s.at(SecondRib.INSTANCE, 3.348, 49.13, -6.445, 1.80, 0.45, 0.410, 0.000, -0.912),
                s.at(SecondRib.INSTANCE, 5.226, 48.98, -7.876, 1.81, 0.45, 0.507, 0.000, -0.862),
                s.at(SecondRib.INSTANCE, 8.736, 44.62, -1.889, 2.45, 0.45, 0.950, 0.000, -0.314),
                s.at(SecondRib.INSTANCE, 6.826, 40.04, 5.83, 2.84, 0.45, 0.816, 0.000, 0.578));
    }
}
