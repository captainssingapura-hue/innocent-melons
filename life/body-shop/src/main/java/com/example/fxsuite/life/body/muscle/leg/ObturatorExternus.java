package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The obturator externus: from the outside of the obturator membrane, back under the hip's neck to the trochanteric
 * fossa.
 */
public record ObturatorExternus() implements Muscle {

    public static final ObturatorExternus INSTANCE = new ObturatorExternus();

    @Override
    public String name() {
        return "obturator externus";
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
                s.at(HipBone.INSTANCE, 4.2, -5.776, 2.553, 3.0, 1.0, 0, -0.5, 1),
                s.at(Femur.INSTANCE, 4.4, -2.4, -1.3, 1.0, 0.6, 0, 0, -1));
    }
}
