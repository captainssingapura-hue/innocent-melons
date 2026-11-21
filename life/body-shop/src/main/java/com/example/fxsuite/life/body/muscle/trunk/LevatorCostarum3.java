package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The third levator costarum: from the transverse process of T2, down and out to the third rib beyond its tubercle.
 */
public record LevatorCostarum3() implements Muscle {

    public static final LevatorCostarum3 INSTANCE = new LevatorCostarum3();

    @Override
    public String name() {
        return "levator costarum, third";
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
                s.at(T2.INSTANCE, 3.86, 50.41, -6.87, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(ThirdRib.INSTANCE, 2.45, 47.99, -6.13, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
