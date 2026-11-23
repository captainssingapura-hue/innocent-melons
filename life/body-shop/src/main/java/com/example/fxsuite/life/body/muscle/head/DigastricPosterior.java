package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The digastric's posterior belly: from the mastoid notch, down and forward to its intermediate tendon at the hyoid.
 */
public record DigastricPosterior() implements Muscle {

    public static final DigastricPosterior INSTANCE = new DigastricPosterior();

    @Override
    public String name() {
        return "digastric, posterior belly";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.NECK;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Skull.INSTANCE, 4.05, -1.4, -1.4, 0.9, 0.7, 1, 0, 0),
                s.at(Hyoid.INSTANCE, 1.8, 4.8, 3, 0.5, 0.4, 1, 0, 0));
    }
}
