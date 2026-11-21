package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The psoas major: from the sides of the lumbar vertebrae, down beside the spine and over the pelvis's brim, joining
 * the iliacus, to the lesser trochanter.
 */
public record Psoas() implements Muscle {

    public static final Psoas INSTANCE = new Psoas();

    @Override
    public String name() {
        return "psoas major";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(T12.INSTANCE, 2.105, 26, -1.495, 1.6, 1.4, 0.5, 0, 0.5),
                s.at(L3.INSTANCE, 3.139, 17, -1.661, 3.0, 3.0, 0.5, 0, 0.5),
                s.at(L5.INSTANCE, 4.838, 10, -0.898, 3.4, 3.2, 0.3, 0, 0.8),
                s.at(HipBone.INSTANCE, 7, 2, 2.4, 2.6, 2.0, 0, 0, 1),
                s.at(Femur.INSTANCE, 1.623, -4.4, 1.043, 2.0, 1.2, -0.4, 0, 1),
                s.at(Femur.INSTANCE, 2.7, -6.4, -0.4, 1.0, 0.6, -1, 0, 0));
    }
}
