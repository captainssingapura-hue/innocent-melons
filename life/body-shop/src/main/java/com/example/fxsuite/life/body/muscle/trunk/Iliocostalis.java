package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The iliocostalis: the erector spinae's outer column, from the sacrum and the iliac crest up over the angles of the
 * ribs to the lower cervical transverse processes.
 */
public record Iliocostalis() implements Muscle {

    public static final Iliocostalis INSTANCE = new Iliocostalis();

    @Override
    public String name() {
        return "iliocostalis";
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
                s.at(Sacrum.INSTANCE, 5.4, 6, -8.1, 2.6, 1.4, 0, 0, -1),
                s.at(L3.INSTANCE, 5.6, 18, -5.7, 3.0, 2.6, 0, 0, -1),
                s.at(T10.INSTANCE, 6.4, 30, -8, 2.2, 1.2, 0, 0, -1),
                s.at(T4.INSTANCE, 6, 44, -8.5, 1.4, 0.6, 0, 0, -1),
                s.at(C7.INSTANCE, 4.4, 52, -4.2, 0.8, 0.4, 0, 0, -1));
    }
}
