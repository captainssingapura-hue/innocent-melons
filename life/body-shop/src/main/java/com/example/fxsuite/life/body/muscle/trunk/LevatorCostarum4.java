package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.bone.thorax.FourthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The fourth levator costarum: from the transverse process of T3, down and out to the fourth rib beyond its tubercle.
 */
public record LevatorCostarum4() implements Muscle {

    public static final LevatorCostarum4 INSTANCE = new LevatorCostarum4();

    @Override
    public String name() {
        return "levator costarum, fourth";
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
                s.at(T3.INSTANCE, 3.76, 48.28, -7.7, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(FourthRib.INSTANCE, 2.43, 45.85, -6.8, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
