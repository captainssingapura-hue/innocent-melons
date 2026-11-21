package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SixthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the sixth space: from the angles round to the front, between the sixth and the seventh
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal6() implements Muscle {

    public static final InternalIntercostal6 INSTANCE = new InternalIntercostal6();

    @Override
    public String name() {
        return "internal intercostal, sixth space";
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
                s.at(SixthRib.INSTANCE, 4.006, 39.85, -9.124, 1.99, 0.40, 0.368, 0.000, -0.930),
                s.at(SixthRib.INSTANCE, 8.303, 37.42, -6.714, 2.00, 0.40, 0.733, 0.000, -0.681),
                s.at(SixthRib.INSTANCE, 11.854, 30.62, 3.43, 2.11, 0.40, 0.980, 0.000, 0.201),
                s.at(SixthRib.INSTANCE, 9.516, 27.5, 8.882, 2.16, 0.40, 0.770, 0.000, 0.638));
    }
}
