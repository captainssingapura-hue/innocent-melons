package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The erector spinae: from the sacrum and the iliac crest, a thick column either side of the spine, up the small of the
 * back where it stands out, thinning up the chest to the neck.
 */
public record ErectorSpinae() implements Muscle {

    public static final ErectorSpinae INSTANCE = new ErectorSpinae();

    @Override
    public String name() {
        return "erector spinae";
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
                s.at(Sacrum.INSTANCE, 2.6, 4, -8.5, 3.0, 1.4, 0, 0, -1),
                s.at(L3.INSTANCE, 3.2, 18, -6.2, 5.0, 3.6, 0, 0, -1),
                s.at(T12.INSTANCE, 3.2, 26, -6.9, 4.4, 2.6, 0, 0, -1),
                s.at(T8.INSTANCE, 3, 35, -9, 3.4, 1.6, 0, 0, -1),
                s.at(T4.INSTANCE, 2.6, 44, -9.5, 2.4, 1.0, 0, 0, -1),
                s.at(C7.INSTANCE, 2, 52, -6.2, 1.6, 0.8, 0, 0, -1),
                s.at(C4.INSTANCE, 1.6, 57, -4.3, 1.2, 0.6, 0, 0, -1));
    }
}
