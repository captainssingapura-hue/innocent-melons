package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The geniohyoid: above the mylohyoid, from the inside of the chin back to the hyoid.
 */
public record Geniohyoid() implements Muscle {

    public static final Geniohyoid INSTANCE = new Geniohyoid();

    @Override
    public String name() {
        return "geniohyoid";
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
                s.at(Mandible.INSTANCE, 0.4, -4.7, 7, 0.8, 0.6, 0, -1, 0),
                s.at(Hyoid.INSTANCE, 0.4, 5.1, 4.6, 0.8, 0.6, 0, -1, 0));
    }
}
