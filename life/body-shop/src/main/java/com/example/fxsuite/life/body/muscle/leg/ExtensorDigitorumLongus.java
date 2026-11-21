package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor digitorum longus: from the tibia's outer condyle and the front of the fibula, down the shin beside the
 * tibialis anterior, its tendons over the ankle fanning across the top of the foot to the toes.
 */
public record ExtensorDigitorumLongus() implements Muscle {

    public static final ExtensorDigitorumLongus INSTANCE = new ExtensorDigitorumLongus();

    @Override
    public String name() {
        return "extensor digitorum longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 2.44, -6, 0.12, 1.6, 1.2, 0.6, 0, 0.8),
                s.at(Tibia.INSTANCE, 2.98, -18, 0.44, 1.8, 1.4, 0.6, 0, 0.8),
                s.at(Tibia.INSTANCE, 2.678, -32, 1.326, 1.0, 0.6, 0.4, 0, 0.9),
                s.at(Tibia.INSTANCE, 1.6, -40.5, 2.2, 1.4, 0.4, 0, 0, 1),
                s.at(ThirdMetatarsal.INSTANCE, 0.8, -0.147, 3.571, 2.6, 0.3, 0, 1, 0.2),
                s.at(ThirdMetatarsal.INSTANCE, 0.8, -2.12, 6.964, 3.6, 0.25, 0, 1, 0.3),
                s.at(ThirdMetatarsal.INSTANCE, 0.6, -3.8, 11.39, 4.4, 0.2, 0, 1, 0.1));
    }
}
