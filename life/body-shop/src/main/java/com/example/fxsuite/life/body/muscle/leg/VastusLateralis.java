package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The vastus lateralis: from the femur under the greater trochanter and down its outer lip, the outer bulk of the
 * thigh, to the kneecap's outer edge.
 */
public record VastusLateralis() implements Muscle {

    public static final VastusLateralis INSTANCE = new VastusLateralis();

    @Override
    public String name() {
        return "vastus lateralis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.THIGH;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 5.4, -5, 0, 2.5, 1.2, 1, 0, 0),
                s.at(Femur.INSTANCE, 5.536, -14, 0.211, 8.1, 4.1, 1, 0, 0.3),
                s.at(Femur.INSTANCE, 4.627, -25, 0.567, 8.1, 4.1, 0.9, 0, 0.4),
                s.at(Femur.INSTANCE, 3.445, -33, 1.845, 5.4, 2.7, 0.7, 0, 0.7),
                s.at(Femur.INSTANCE, 1.878, -37.4, 3.526, 2.5, 0.6, 0.4, 0, 0.9));
    }
}
