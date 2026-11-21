package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The first levator costarum: from the transverse process of C7, down and out to the first rib beyond its tubercle.
 */
public record LevatorCostarum1() implements Muscle {

    public static final LevatorCostarum1 INSTANCE = new LevatorCostarum1();

    @Override
    public String name() {
        return "levator costarum, first";
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
                s.at(C7.INSTANCE, 3.4, 53.6, -4.07, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(FirstRib.INSTANCE, 2.49, 51.93, -4.56, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
