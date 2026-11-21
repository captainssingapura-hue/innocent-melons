package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The opponens pollicis: under the abductor, from the trapezium along the thumb's metacarpal; it turns the thumb to
 * meet the fingers.
 */
public record OpponensPollicis() implements Muscle {

    public static final OpponensPollicis INSTANCE = new OpponensPollicis();

    @Override
    public String name() {
        return "opponens pollicis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HAND;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Trapezium.INSTANCE, 1.788, -2.4, 1.188, 1.0, 0.6, 0.6, 0, 0.6),
                s.at(FirstMetacarpal.INSTANCE, 3.317, -5, 2.117, 1.4, 0.8, 0.6, 0, 0.6));
    }
}
