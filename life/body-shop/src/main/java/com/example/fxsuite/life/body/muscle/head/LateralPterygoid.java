package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The lateral pterygoid: from the outer face of the sphenoid's lateral pterygoid plate, back and out to the neck of the
 * mandible's condyle; it opens the jaw and draws it forward.
 */
public record LateralPterygoid() implements Muscle {

    public static final LateralPterygoid INSTANCE = new LateralPterygoid();

    @Override
    public String name() {
        return "lateral pterygoid";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HEAD;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 1.9, -0.6, 3, 1.4, 1.0, 1, 0, 0),
                s.at(Mandible.INSTANCE, 5, 0.6, 0.4, 1.0, 0.8, 1, 0, 0));
    }
}
