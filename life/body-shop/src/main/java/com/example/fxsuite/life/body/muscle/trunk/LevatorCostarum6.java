package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.bone.thorax.SixthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The sixth levator costarum: from the transverse process of T5, down and out to the sixth rib beyond its tubercle.
 */
public record LevatorCostarum6() implements Muscle {

    public static final LevatorCostarum6 INSTANCE = new LevatorCostarum6();

    @Override
    public String name() {
        return "levator costarum, sixth";
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
                s.at(T5.INSTANCE, 3.54, 43.76, -8.98, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(SixthRib.INSTANCE, 2.39, 41.34, -7.67, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
