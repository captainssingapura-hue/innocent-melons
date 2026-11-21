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
 * The obturator internus: from the inside of the obturator membrane and the bone round it, out through the lesser
 * sciatic notch, turning there, to the greater trochanter's inner face.
 */
public record ObturatorInternus() implements Muscle {

    public static final ObturatorInternus INSTANCE = new ObturatorInternus();

    @Override
    public String name() {
        return "obturator internus";
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
                s.at(HipBone.INSTANCE, 3.8, -5.4, 2, 3.0, 0.8, -1, 0, 0),
                s.at(HipBone.INSTANCE, 5.4, -4.4, -3.4, 1.2, 0.8, 0, 0, -1),
                s.at(Femur.INSTANCE, 4.714, -1.6, -1.113, 0.8, 0.6, 0.3, 0, -1));
    }
}
