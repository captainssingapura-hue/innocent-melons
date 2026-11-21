package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The abductor hallucis: along the inner edge of the sole, from the heel bone to the base of the great toe; the arch's
 * inner fullness.
 */
public record AbductorHallucis() implements Muscle {

    public static final AbductorHallucis INSTANCE = new AbductorHallucis();

    @Override
    public String name() {
        return "abductor hallucis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOOT;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Calcaneus.INSTANCE, -1.225, -4.628, -1.6, 1.6, 1.2, -1, -0.3, 0),
                s.at(Navicular.INSTANCE, -2.225, -4.028, 4.4, 2.0, 1.2, -1, -0.3, 0),
                s.at(FirstMetatarsal.INSTANCE, -2.713, -5.114, 10, 1.0, 0.6, -1, -0.3, 0));
    }
}
