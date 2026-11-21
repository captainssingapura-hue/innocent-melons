package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The sternothyroid: under the sternohyoid, from the back of the manubrium up to the thyroid cartilage.
 */
public record Sternothyroid() implements Muscle {

    public static final Sternothyroid INSTANCE = new Sternothyroid();

    @Override
    public String name() {
        return "sternothyroid";
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
                s.at(Sternum.INSTANCE, 1, 45.6, 6.25, 1.2, 0.3, 0, 0, 1),
                s.at(C5.INSTANCE, 1.6, 53.6, 3.85, 1.0, 0.3, 0, 0, 1));
    }
}
