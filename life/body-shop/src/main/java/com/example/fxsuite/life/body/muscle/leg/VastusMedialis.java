package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The vastus medialis: from the femur's inner lip, down the inside of the thigh, full and low above the knee, to the
 * kneecap's inner edge.
 */
public record VastusMedialis() implements Muscle {

    public static final VastusMedialis INSTANCE = new VastusMedialis();

    @Override
    public String name() {
        return "vastus medialis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 2.679, -12, -0.544, 2.0, 1.0, -1, 0, 0.3),
                s.at(Femur.INSTANCE, 0.688, -24, 1.172, 4.7, 2.6, -0.9, 0, 0.4),
                s.at(Femur.INSTANCE, -0.721, -32, 1.321, 6.5, 3.9, -0.7, 0, 0.7),
                s.at(Femur.INSTANCE, -1.109, -37.4, 3.076, 3.0, 1.2, -0.5, 0, 0.9));
    }
}
