package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.EighthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the eighth space: from behind round to where the ribs become cartilage, between the
 * eighth and the ninth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal8() implements Muscle {

    public static final ExternalIntercostal8 INSTANCE = new ExternalIntercostal8();

    @Override
    public String name() {
        return "external intercostal, eighth space";
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
                s.at(EighthRib.INSTANCE, 2.795, 35.18, -8.265, 1.93, 0.45, 0.289, 0.000, -0.957),
                s.at(EighthRib.INSTANCE, 5.015, 34.8, -9.786, 1.99, 0.45, 0.422, 0.000, -0.907),
                s.at(EighthRib.INSTANCE, 12.684, 29.61, -2.776, 1.81, 0.45, 0.958, 0.000, -0.286),
                s.at(EighthRib.INSTANCE, 11.741, 24.06, 5.667, 1.82, 0.45, 0.929, 0.000, 0.370));
    }
}
