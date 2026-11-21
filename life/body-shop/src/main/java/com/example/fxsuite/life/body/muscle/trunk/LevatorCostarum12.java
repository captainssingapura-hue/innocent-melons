package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T11;
import com.example.fxsuite.life.body.bone.thorax.TwelfthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The twelfth levator costarum: from the transverse process of T11, down and out to the twelfth rib beyond its
 * tubercle.
 */
public record LevatorCostarum12() implements Muscle {

    public static final LevatorCostarum12 INSTANCE = new LevatorCostarum12();

    @Override
    public String name() {
        return "levator costarum, twelfth";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T11.INSTANCE, 2.57, 29.12, -7.84, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(TwelfthRib.INSTANCE, 1.97, 27.36, -5.56, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
