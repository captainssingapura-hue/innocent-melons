package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor pollicis longus: from the back of the ulna, its tendon round the radius's back tubercle and across the
 * wrist to the thumb's tip.
 */
public record ExtensorPollicisLongus() implements Muscle {

    public static final ExtensorPollicisLongus INSTANCE = new ExtensorPollicisLongus();

    @Override
    public String name() {
        return "extensor pollicis longus";
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
                s.at(Ulna.INSTANCE, -0.6, -12, -1.5, 0.9, 0.6, 0, 0, -1),
                s.at(Radius.INSTANCE, 1.4, -23.2, -1.2, 0.5, 0.4, 0, 0, -1),
                s.at(FirstMetacarpal.INSTANCE, 2.933, -3.6, 0.734, 0.4, 0.3, 0.5, 0, -1),
                s.at(ThumbProximalPhalanx.INSTANCE, 4.133, -7.6, 2.534, 0.4, 0.3, 0.5, 0, -1));
    }
}
