package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The adductor pollicis: across the palm from the third metacarpal to the thumb's base, the web between the thumb and
 * the index finger.
 */
public record AdductorPollicis() implements Muscle {

    public static final AdductorPollicis INSTANCE = new AdductorPollicis();

    @Override
    public String name() {
        return "adductor pollicis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(ThirdMetacarpal.INSTANCE, 0.4, -6, 0.5, 2.0, 0.8, 0, 0, 1),
                s.at(FirstMetacarpal.INSTANCE, 3.2, -6.4, 1.6, 1.6, 0.8, 0, 0, 1),
                s.at(ThumbProximalPhalanx.INSTANCE, 3.9, -7.6, 2.35, 0.8, 0.5, 0, 0, 1));
    }
}
