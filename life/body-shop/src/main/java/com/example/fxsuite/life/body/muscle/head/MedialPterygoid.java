package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The medial pterygoid: from the inner face of the lateral pterygoid plate, down, out and back to the inside of the
 * mandible's angle; it closes the jaw.
 */
public record MedialPterygoid() implements Muscle {

    public static final MedialPterygoid INSTANCE = new MedialPterygoid();

    @Override
    public String name() {
        return "medial pterygoid";
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
                s.at(Sphenoid.INSTANCE, 2.5, -1.4, 3, 1.6, 1.0, -1, 0, 0),
                s.at(Mandible.INSTANCE, 4.9, -3.4, -0.6, 1.6, 1.0, -1, 0, 0));
    }
}
