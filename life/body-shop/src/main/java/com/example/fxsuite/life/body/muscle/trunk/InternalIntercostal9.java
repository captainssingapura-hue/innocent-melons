package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.NinthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the ninth space: from the angles round to the front, between the ninth and the tenth
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal9() implements Muscle {

    public static final InternalIntercostal9 INSTANCE = new InternalIntercostal9();

    @Override
    public String name() {
        return "internal intercostal, ninth space";
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
                s.at(NinthRib.INSTANCE, 3.491, 32.45, -8.442, 1.98, 0.40, 0.347, 0.000, -0.938),
                s.at(NinthRib.INSTANCE, 8.29, 30.21, -6.327, 1.83, 0.40, 0.749, 0.000, -0.663),
                s.at(NinthRib.INSTANCE, 12.421, 23.9, 2.014, 1.63, 0.40, 0.997, 0.000, 0.082),
                s.at(NinthRib.INSTANCE, 10.549, 21, 5.985, 1.82, 0.40, 0.904, 0.000, 0.427));
    }
}
