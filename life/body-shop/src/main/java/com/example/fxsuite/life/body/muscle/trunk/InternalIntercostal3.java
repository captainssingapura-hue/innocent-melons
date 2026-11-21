package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the third space: from the angles round to the front, between the third and the fourth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal3() implements Muscle {

    public static final InternalIntercostal3 INSTANCE = new InternalIntercostal3();

    @Override
    public String name() {
        return "internal intercostal, third space";
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
                s.at(ThirdRib.INSTANCE, 4.174, 47, -7.799, 1.88, 0.40, 0.429, 0.000, -0.903),
                s.at(ThirdRib.INSTANCE, 7.224, 44.73, -5.724, 1.97, 0.40, 0.732, 0.000, -0.681),
                s.at(ThirdRib.INSTANCE, 9.056, 38.68, 3.291, 2.49, 0.40, 0.969, 0.000, 0.245),
                s.at(ThirdRib.INSTANCE, 6.852, 35.9, 8.235, 2.66, 0.40, 0.688, 0.000, 0.726));
    }
}
