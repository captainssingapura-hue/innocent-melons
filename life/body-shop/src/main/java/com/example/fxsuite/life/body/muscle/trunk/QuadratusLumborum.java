package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.thorax.TwelfthRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The quadratus lumborum: from the back of the iliac crest, up beside the lumbar spine to its transverse processes and
 * the twelfth rib; it bends the trunk sideways.
 */
public record QuadratusLumborum() implements Muscle {

    public static final QuadratusLumborum INSTANCE = new QuadratusLumborum();

    @Override
    public String name() {
        return "quadratus lumborum";
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
                s.at(HipBone.INSTANCE, 7.6, 11.6, -3.9, 3.0, 1.4, 0, 0, -1),
                s.at(L3.INSTANCE, 4.6, 19, -3.9, 3.0, 1.4, 0, 0, -1),
                s.at(TwelfthRib.INSTANCE, 5, 24.6, -4.5, 2.4, 1.0, 0, 0, -1));
    }
}
