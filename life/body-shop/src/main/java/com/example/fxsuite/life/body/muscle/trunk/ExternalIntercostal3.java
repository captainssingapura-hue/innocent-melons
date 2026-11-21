package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the third space: from behind round to where the ribs become cartilage, between the third
 * and the fourth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal3() implements Muscle {

    public static final ExternalIntercostal3 INSTANCE = new ExternalIntercostal3();

    @Override
    public String name() {
        return "external intercostal, third space";
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
                s.at(ThirdRib.INSTANCE, 3.257, 46.98, -7.201, 1.84, 0.45, 0.369, 0.000, -0.929),
                s.at(ThirdRib.INSTANCE, 5.134, 46.83, -8.681, 1.87, 0.45, 0.469, 0.000, -0.883),
                s.at(ThirdRib.INSTANCE, 10.096, 42.08, -2.33, 2.24, 0.45, 0.950, 0.000, -0.313),
                s.at(ThirdRib.INSTANCE, 8.233, 37.04, 6.515, 2.58, 0.45, 0.831, 0.000, 0.556));
    }
}
