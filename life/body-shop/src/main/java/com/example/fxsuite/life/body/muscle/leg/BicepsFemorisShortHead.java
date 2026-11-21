package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Fibula;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The biceps femoris's short head: from the femur's rough line low on its outer side, under the long head, to the head
 * of the fibula.
 */
public record BicepsFemorisShortHead() implements Muscle {

    public static final BicepsFemorisShortHead INSTANCE = new BicepsFemorisShortHead();

    @Override
    public String name() {
        return "biceps femoris, short head";
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
                s.at(Femur.INSTANCE, 3.176, -22, -1.953, 1.6, 1.0, 0.5, 0, -1),
                s.at(Femur.INSTANCE, 3.442, -32, -2.084, 2.4, 1.6, 0.5, 0, -1),
                s.at(Fibula.INSTANCE, 2.961, -4.2, -1.528, 0.8, 0.5, 1, 0, -0.3));
    }
}
