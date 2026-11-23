package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The multifidus: deepest of the long back muscles bar the rotatores, in the groove beside the spines from the sacrum
 * to the axis, each bundle across two to four levels; thickest in the small of the back.
 */
public record Multifidus() implements Muscle {

    public static final Multifidus INSTANCE = new Multifidus();

    @Override
    public String name() {
        return "multifidus";
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
                s.at(Sacrum.INSTANCE, 1.8, 4, -7.2, 2.4, 1.6, 0, 0, -1),
                s.at(L5.INSTANCE, 1.8, 13, -6.2, 2.2, 1.6, 0, 0, -1),
                s.at(L3.INSTANCE, 1.8, 18.5, -6.1, 2.0, 1.4, 0, 0, -1),
                s.at(L1.INSTANCE, 1.6, 24, -6.5, 1.6, 1.0, 0, 0, -1),
                s.at(T10.INSTANCE, 1.4, 32, -7.2, 1.2, 0.8, 0, 0, -1),
                s.at(T6.INSTANCE, 1.3, 41, -7.9, 1.0, 0.6, 0, 0, -1),
                s.at(T2.INSTANCE, 1.3, 49, -7.1, 1.0, 0.6, 0, 0, -1),
                s.at(C6.INSTANCE, 1.2, 54.2, -4.3, 1.6, 0.95, 0, 0, -1),
                s.at(C4.INSTANCE, 1.1, 56.6, -4.0, 1.5, 0.9, 0, 0, -1),
                s.at(Axis.INSTANCE, 1, 58.4, -3.75, 0.8, 0.5, 0, 0, -1));
    }
}
