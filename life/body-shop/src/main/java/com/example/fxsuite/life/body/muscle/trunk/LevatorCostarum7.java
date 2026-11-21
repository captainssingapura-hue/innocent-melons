package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.bone.thorax.SeventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The seventh levator costarum: from the transverse process of T6, down and out to the seventh rib beyond its tubercle.
 */
public record LevatorCostarum7() implements Muscle {

    public static final LevatorCostarum7 INSTANCE = new LevatorCostarum7();

    @Override
    public String name() {
        return "levator costarum, seventh";
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
                s.at(T6.INSTANCE, 3.5, 41.29, -9.3, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(SeventhRib.INSTANCE, 2.37, 39.01, -7.77, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
