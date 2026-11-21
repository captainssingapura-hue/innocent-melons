package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The stylohyoid: from the temporal bone's styloid process, down and forward to the hyoid, beside the digastric.
 */
public record Stylohyoid() implements Muscle {

    public static final Stylohyoid INSTANCE = new Stylohyoid();

    @Override
    public String name() {
        return "stylohyoid";
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
                s.at(Temporal.INSTANCE, 4, -1.8, 0.4, 0.4, 0.4, 1, 0, 0),
                s.at(Hyoid.INSTANCE, 2, 5, 3, 0.4, 0.4, 1, 0, 0));
    }
}
