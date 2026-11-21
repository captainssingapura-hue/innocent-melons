package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The internal intercostal of the tenth space: from the angles round to the front, between the tenth and the eleventh
 * ribs, its fibres running down and back.
 */
public record InternalIntercostal10() implements Muscle {

    public static final InternalIntercostal10 INSTANCE = new InternalIntercostal10();

    @Override
    public String name() {
        return "internal intercostal, tenth space";
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
                s.at(TenthRib.INSTANCE, 3.162, 29.94, -7.812, 2.20, 0.40, 0.338, 0.000, -0.941),
                s.at(TenthRib.INSTANCE, 8.041, 28.09, -6.167, 1.59, 0.40, 0.747, 0.000, -0.665),
                s.at(TenthRib.INSTANCE, 12.101, 22.81, 0.124, 2.46, 0.40, 0.997, 0.000, -0.072),
                s.at(TenthRib.INSTANCE, 10.753, 20.4, 2.718, 4.18, 0.40, 0.987, 0.000, 0.158));
    }
}
