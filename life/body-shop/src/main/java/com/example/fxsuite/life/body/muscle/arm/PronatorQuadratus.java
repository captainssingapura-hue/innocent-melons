package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The pronator quadratus: a square across the front of the forearm's lowest quarter, from the ulna to the radius.
 */
public record PronatorQuadratus() implements Muscle {

    public static final PronatorQuadratus INSTANCE = new PronatorQuadratus();

    @Override
    public String name() {
        return "pronator quadratus";
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
                s.at(Ulna.INSTANCE, -1.6, -21.5, 0.3, 2.6, 0.6, 0, 0, 1),
                s.at(Radius.INSTANCE, 1.6, -21.5, 0.5, 2.6, 0.6, 0, 0, 1));
    }
}
