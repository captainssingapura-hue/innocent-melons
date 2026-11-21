package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The abductor pollicis longus: from the backs of the ulna and the radius, coming out between the extensors low on the
 * forearm, over the radius's outer side to the thumb's base.
 */
public record AbductorPollicisLongus() implements Muscle {

    public static final AbductorPollicisLongus INSTANCE = new AbductorPollicisLongus();

    @Override
    public String name() {
        return "abductor pollicis longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Ulna.INSTANCE, 0, -9, -1.2, 1.2, 0.8, 0, 0, -1),
                s.at(Radius.INSTANCE, 2.017, -16, -0.285, 1.2, 0.8, 1, 0, -0.3),
                s.at(Radius.INSTANCE, 2.8, -23, 0.6, 0.5, 0.4, 1, 0, 0));
    }
}
