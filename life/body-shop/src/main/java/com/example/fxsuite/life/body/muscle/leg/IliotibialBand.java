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
 * The iliotibial band: the fascia down the side of the thigh, taking the tensor's and the gluteus maximus's pull, to
 * Gerdy's tubercle below the knee.
 */
public record IliotibialBand() implements Muscle {

    public static final IliotibialBand INSTANCE = new IliotibialBand();

    @Override
    public String name() {
        return "iliotibial band";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public Layer layer() {
        return Layer.TENDON;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 8.45, -10, 0.8, 3.4, 0.3, 1, 0, 0),
                s.at(Femur.INSTANCE, 8.85, -18, 0.6, 3.0, 0.3, 1, 0, 0),
                s.at(Femur.INSTANCE, 8.251, -28, 0.585, 2.6, 0.3, 1, 0, 0.1),
                s.at(Femur.INSTANCE, 5.253, -37, 0.571, 2.2, 0.3, 1, 0, 0.2),
                s.at(Tibia.INSTANCE, 2.856, -3.6, 1.357, 1.6, 0.3, 1, 0, 0.3));
    }
}
