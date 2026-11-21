package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The abductor pollicis brevis: the outer of the ball of the thumb, from the scaphoid and the trapezium to the outer
 * side of the thumb's first phalanx.
 */
public record AbductorPollicisBrevis() implements Muscle {

    public static final AbductorPollicisBrevis INSTANCE = new AbductorPollicisBrevis();

    @Override
    public String name() {
        return "abductor pollicis brevis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Scaphoid.INSTANCE, 2.114, -2, 1.713, 1.0, 0.6, 0.3, 0, 1),
                s.at(FirstMetacarpal.INSTANCE, 3.414, -4.8, 2.936, 1.8, 1.0, 0.4, 0, 1),
                s.at(ThumbProximalPhalanx.INSTANCE, 4.711, -7.6, 3.621, 0.6, 0.4, 0.5, 0, 1));
    }
}
