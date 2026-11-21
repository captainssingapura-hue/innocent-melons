package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SixthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the sixth space: from behind round to where the ribs become cartilage, between the sixth
 * and the seventh ribs, its fibres running down and forward.
 */
public record ExternalIntercostal6() implements Muscle {

    public static final ExternalIntercostal6 INSTANCE = new ExternalIntercostal6();

    @Override
    public String name() {
        return "external intercostal, sixth space";
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
                s.at(SixthRib.INSTANCE, 3.061, 40, -8.476, 1.94, 0.45, 0.307, 0.000, -0.952),
                s.at(SixthRib.INSTANCE, 5.135, 39.75, -10.046, 1.98, 0.45, 0.421, 0.000, -0.907),
                s.at(SixthRib.INSTANCE, 12.365, 34.44, -2.824, 2.05, 0.45, 0.955, 0.000, -0.295),
                s.at(SixthRib.INSTANCE, 10.982, 28.78, 6.903, 2.13, 0.45, 0.881, 0.000, 0.474));
    }
}
