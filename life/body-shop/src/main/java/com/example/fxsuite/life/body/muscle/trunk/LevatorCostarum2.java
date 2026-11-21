package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The second levator costarum: from the transverse process of T1, down and out to the second rib beyond its tubercle.
 */
public record LevatorCostarum2() implements Muscle {

    public static final LevatorCostarum2 INSTANCE = new LevatorCostarum2();

    @Override
    public String name() {
        return "levator costarum, second";
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
                s.at(T1.INSTANCE, 3.9, 52.33, -6.08, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(SecondRib.INSTANCE, 2.48, 50.03, -5.34, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
