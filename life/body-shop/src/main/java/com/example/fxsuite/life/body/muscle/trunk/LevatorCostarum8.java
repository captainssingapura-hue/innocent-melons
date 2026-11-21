package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.bone.thorax.EighthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The eighth levator costarum: from the transverse process of T7, down and out to the eighth rib beyond its tubercle.
 */
public record LevatorCostarum8() implements Muscle {

    public static final LevatorCostarum8 INSTANCE = new LevatorCostarum8();

    @Override
    public String name() {
        return "levator costarum, eighth";
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
                s.at(T7.INSTANCE, 3.43, 38.81, -9.39, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(EighthRib.INSTANCE, 2.33, 36.68, -7.68, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
