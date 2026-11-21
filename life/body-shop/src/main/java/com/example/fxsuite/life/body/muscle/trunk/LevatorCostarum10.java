package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The tenth levator costarum: from the transverse process of T9, down and out to the tenth rib beyond its tubercle.
 */
public record LevatorCostarum10() implements Muscle {

    public static final LevatorCostarum10 INSTANCE = new LevatorCostarum10();

    @Override
    public String name() {
        return "levator costarum, tenth";
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
                s.at(T9.INSTANCE, 3, 33.91, -8.95, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(TenthRib.INSTANCE, 2.22, 32.08, -6.94, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
