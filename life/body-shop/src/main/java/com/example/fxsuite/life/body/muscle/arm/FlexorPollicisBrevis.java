package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The flexor pollicis brevis: the inner of the ball of the thumb, from the trapezium and the flexor retinaculum to the
 * thumb's first phalanx.
 */
public record FlexorPollicisBrevis() implements Muscle {

    public static final FlexorPollicisBrevis INSTANCE = new FlexorPollicisBrevis();

    @Override
    public String name() {
        return "flexor pollicis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Trapezium.INSTANCE, 1.6, -2.6, 1.5, 0.9, 0.6, 0, 0, 1),
                s.at(FirstMetacarpal.INSTANCE, 2.512, -5.4, 2.559, 1.4, 0.9, 0.2, 0, 1),
                s.at(ThumbProximalPhalanx.INSTANCE, 3.943, -7.8, 3.208, 0.6, 0.4, 0.3, 0, 1));
    }
}
