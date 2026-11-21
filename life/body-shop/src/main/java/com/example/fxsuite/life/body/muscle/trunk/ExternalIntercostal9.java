package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.NinthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the ninth space: from behind round to where the ribs become cartilage, between the ninth
 * and the tenth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal9() implements Muscle {

    public static final ExternalIntercostal9 INSTANCE = new ExternalIntercostal9();

    @Override
    public String name() {
        return "external intercostal, ninth space";
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
                s.at(NinthRib.INSTANCE, 2.617, 32.81, -7.874, 1.92, 0.45, 0.282, 0.000, -0.959),
                s.at(NinthRib.INSTANCE, 4.904, 32.37, -9.337, 1.98, 0.45, 0.428, 0.000, -0.904),
                s.at(NinthRib.INSTANCE, 12.475, 27.44, -2.923, 1.69, 0.45, 0.954, 0.000, -0.300),
                s.at(NinthRib.INSTANCE, 11.874, 22.19, 4.526, 1.72, 0.45, 0.959, 0.000, 0.284));
    }
}
