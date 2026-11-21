package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor pollicis longus: on the front of the radius, its tendon through the wrist to the thumb's tip.
 */
public record FlexorPollicisLongus() implements Muscle {

    public static final FlexorPollicisLongus INSTANCE = new FlexorPollicisLongus();

    @Override
    public String name() {
        return "flexor pollicis longus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.FOREARM;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Radius.INSTANCE, 1.8, -8, 0.6, 1.4, 0.8, 0, 0, 1),
                s.at(Radius.INSTANCE, 1.6, -18, 0.7, 1.0, 0.6, 0, 0, 1),
                s.at(Radius.INSTANCE, 1.2, -23.6, 0.65, 0.5, 0.3, 0, 0, 1));
    }
}
