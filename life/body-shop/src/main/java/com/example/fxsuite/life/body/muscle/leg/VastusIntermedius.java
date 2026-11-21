package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The vastus intermedius: on the front of the femur's shaft, under the rectus femoris, to the kneecap.
 */
public record VastusIntermedius() implements Muscle {

    public static final VastusIntermedius INSTANCE = new VastusIntermedius();

    @Override
    public String name() {
        return "vastus intermedius";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 4.4, -8, 1.2, 3.0, 1.2, 0, 0, 1),
                s.at(Femur.INSTANCE, 3, -20, 1.6, 5.6, 2.8, 0, 0, 1),
                s.at(Femur.INSTANCE, 1.6, -31, 2.15, 4.2, 1.7, 0, 0, 1),
                s.at(Femur.INSTANCE, 0.2, -36.6, 3.2, 2.0, 0.4, 0, 0, 1));
    }
}
