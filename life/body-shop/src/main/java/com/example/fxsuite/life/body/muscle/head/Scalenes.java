package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The scalenes: from the transverse processes of the neck's vertebrae, down its side behind the sternocleidomastoid, to
 * the first ribs.
 */
public record Scalenes() implements Muscle {

    public static final Scalenes INSTANCE = new Scalenes();

    @Override
    public String name() {
        return "scalenes";
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
                s.at(C4.INSTANCE, 2.9, 56.4, -1, 1.6, 1.0, 1, 0, 0),
                s.at(C7.INSTANCE, 4.13, 52.5, -0.801, 2.4, 1.4, 1, 0, 0.3),
                s.at(FirstRib.INSTANCE, 5.248, 48.346, 1.349, 2.6, 0.6, 0.6, 1, 0.2));
    }
}
