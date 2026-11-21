package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The popliteus: from the femur's outer condyle, down and in across the back of the knee, to the back of the tibia
 * above the soleal line; it unlocks the knee.
 */
public record Popliteus() implements Muscle {

    public static final Popliteus INSTANCE = new Popliteus();

    @Override
    public String name() {
        return "popliteus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 3.266, -41, -0.332, 1.0, 0.6, 0.5, 0, -1),
                s.at(Tibia.INSTANCE, 1, -4, -1.6, 2.4, 0.8, 0, 0, -1),
                s.at(Tibia.INSTANCE, -1, -6, -1.8, 3.0, 0.8, 0, 0, -1));
    }
}
