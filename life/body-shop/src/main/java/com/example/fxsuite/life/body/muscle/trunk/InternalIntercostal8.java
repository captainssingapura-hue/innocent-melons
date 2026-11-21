package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.EighthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the eighth space: from the angles round to the front, between the eighth and the ninth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal8() implements Muscle {

    public static final InternalIntercostal8 INSTANCE = new InternalIntercostal8();

    @Override
    public String name() {
        return "internal intercostal, eighth space";
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
                s.at(EighthRib.INSTANCE, 3.71, 34.89, -8.863, 1.99, 0.40, 0.352, 0.000, -0.936),
                s.at(EighthRib.INSTANCE, 8.421, 32.53, -6.537, 1.90, 0.40, 0.745, 0.000, -0.667),
                s.at(EighthRib.INSTANCE, 12.452, 25.86, 2.762, 1.77, 0.40, 0.990, 0.000, 0.140),
                s.at(EighthRib.INSTANCE, 10.32, 22.8, 7.335, 1.88, 0.40, 0.852, 0.000, 0.523));
    }
}
