package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.thorax.NinthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The ninth levator costarum: from the transverse process of T8, down and out to the ninth rib beyond its tubercle.
 */
public record LevatorCostarum9() implements Muscle {

    public static final LevatorCostarum9 INSTANCE = new LevatorCostarum9();

    @Override
    public String name() {
        return "levator costarum, ninth";
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
                s.at(T8.INSTANCE, 3.25, 36.34, -9.28, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(NinthRib.INSTANCE, 2.27, 34.37, -7.38, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
