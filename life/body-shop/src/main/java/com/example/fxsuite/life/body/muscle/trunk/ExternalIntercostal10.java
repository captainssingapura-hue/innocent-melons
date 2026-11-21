package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the tenth space: from behind round to where the ribs become cartilage, between the tenth
 * and the eleventh ribs, its fibres running down and forward.
 */
public record ExternalIntercostal10() implements Muscle {

    public static final ExternalIntercostal10 INSTANCE = new ExternalIntercostal10();

    @Override
    public String name() {
        return "external intercostal, tenth space";
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
                s.at(TenthRib.INSTANCE, 2.281, 30.37, -7.313, 2.14, 0.45, 0.264, 0.000, -0.964),
                s.at(TenthRib.INSTANCE, 4.81, 29.89, -8.688, 2.12, 0.45, 0.445, 0.000, -0.896),
                s.at(TenthRib.INSTANCE, 11.909, 25.77, -3.471, 1.41, 0.45, 0.936, 0.000, -0.351),
                s.at(TenthRib.INSTANCE, 11.895, 21.39, 1.697, 3.44, 0.45, 0.998, 0.000, 0.058));
    }
}
