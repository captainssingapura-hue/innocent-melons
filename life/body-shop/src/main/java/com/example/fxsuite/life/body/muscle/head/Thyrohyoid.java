package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The thyrohyoid: from the thyroid cartilage up to the hyoid, the sternothyroid's continuation.
 */
public record Thyrohyoid() implements Muscle {

    public static final Thyrohyoid INSTANCE = new Thyrohyoid();

    @Override
    public String name() {
        return "thyrohyoid";
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
                s.at(C5.INSTANCE, 1.6, 53.8, 4.05, 1.0, 0.3, 0, 0, 1),
                s.at(Hyoid.INSTANCE, 1.6, 4.6, 4.25, 1.0, 0.3, 0, 0, 1));
    }
}
