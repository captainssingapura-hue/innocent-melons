package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.SeventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external intercostal of the seventh space: from behind round to where the ribs become cartilage, between the
 * seventh and the eighth ribs, its fibres running down and forward.
 */
public record ExternalIntercostal7() implements Muscle {

    public static final ExternalIntercostal7 INSTANCE = new ExternalIntercostal7();

    @Override
    public String name() {
        return "external intercostal, seventh space";
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
                s.at(SeventhRib.INSTANCE, 2.963, 37.58, -8.475, 1.94, 0.45, 0.298, 0.000, -0.955),
                s.at(SeventhRib.INSTANCE, 5.115, 37.27, -10.026, 1.99, 0.45, 0.421, 0.000, -0.907),
                s.at(SeventhRib.INSTANCE, 12.674, 31.96, -2.776, 1.96, 0.45, 0.958, 0.000, -0.286),
                s.at(SeventhRib.INSTANCE, 11.457, 26.28, 6.453, 2.01, 0.45, 0.903, 0.000, 0.430));
    }
}
