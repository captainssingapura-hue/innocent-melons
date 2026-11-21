package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The extensor indicis: from the back of the ulna low down, its tendon over the back of the wrist beside the extensor
 * digitorum's to the index finger.
 */
public record ExtensorIndicis() implements Muscle {

    public static final ExtensorIndicis INSTANCE = new ExtensorIndicis();

    @Override
    public String name() {
        return "extensor indicis";
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
                s.at(Ulna.INSTANCE, -0.4, -16, -1.5, 0.8, 0.6, 0, 0, -1),
                s.at(Radius.INSTANCE, 0.8, -23.6, -1.25, 0.4, 0.3, 0, 0, -1),
                s.at(SecondMetacarpal.INSTANCE, 1.8, -6, -0.65, 0.4, 0.3, 0, 0, -1));
    }
}
