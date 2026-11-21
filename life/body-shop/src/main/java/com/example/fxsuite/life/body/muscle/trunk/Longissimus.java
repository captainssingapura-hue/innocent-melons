package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The longissimus: the erector spinae's middle column, the thickest, from the sacrum up beside the spine to the
 * transverse processes of the thoracic and cervical vertebrae; it stands out in the small of the back.
 */
public record Longissimus() implements Muscle {

    public static final Longissimus INSTANCE = new Longissimus();

    @Override
    public String name() {
        return "longissimus";
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
                s.at(Sacrum.INSTANCE, 3.2, 4, -8.7, 2.0, 1.4, 0, 0, -1),
                s.at(L3.INSTANCE, 3.4, 18, -6.9, 2.6, 3.0, 0, 0, -1),
                s.at(T12.INSTANCE, 3.4, 26, -7.5, 2.6, 2.2, 0, 0, -1),
                s.at(T8.INSTANCE, 3.4, 35, -9.1, 2.0, 1.4, 0, 0, -1),
                s.at(T4.INSTANCE, 3, 44, -9.4, 1.4, 0.8, 0, 0, -1),
                s.at(C7.INSTANCE, 3.2, 52, -5.15, 0.9, 0.5, 0, 0, -1));
    }
}
