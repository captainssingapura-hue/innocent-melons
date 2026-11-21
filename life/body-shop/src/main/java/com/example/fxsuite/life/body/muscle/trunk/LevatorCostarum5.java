package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.thorax.FifthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fifth levator costarum: from the transverse process of T4, down and out to the fifth rib beyond its tubercle.
 */
public record LevatorCostarum5() implements Muscle {

    public static final LevatorCostarum5 INSTANCE = new LevatorCostarum5();

    @Override
    public String name() {
        return "levator costarum, fifth";
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
                s.at(T4.INSTANCE, 3.64, 46.05, -8.42, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(FifthRib.INSTANCE, 2.4, 43.67, -7.34, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
