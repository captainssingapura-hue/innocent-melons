package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.thorax.EleventhRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The eleventh levator costarum: from the transverse process of T10, down and out to the eleventh rib beyond its
 * tubercle.
 */
public record LevatorCostarum11() implements Muscle {

    public static final LevatorCostarum11 INSTANCE = new LevatorCostarum11();

    @Override
    public String name() {
        return "levator costarum, eleventh";
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
                s.at(T10.INSTANCE, 2.75, 31.51, -8.49, 0.80, 0.50, 0.000, 0.000, -1.000),
                s.at(EleventhRib.INSTANCE, 1.98, 29.67, -6.3, 1.20, 0.50, 0.000, 0.000, -1.000));
    }
}
