package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The scalenus anterior: from the front of the transverse processes of the third to the sixth cervical vertebrae, down
 * behind the sternocleidomastoid to the first rib's scalene tubercle.
 */
public record ScaleneAnterior() implements Muscle {

    public static final ScaleneAnterior INSTANCE = new ScaleneAnterior();

    @Override
    public String name() {
        return "scalenus anterior";
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
                s.at(C4.INSTANCE, 2.353, 56.4, -0.024, 1.2, 1.0, 1, 0, 0.5),
                s.at(C6.INSTANCE, 3.063, 53.8, 1.132, 1.4, 1.2, 1, 0, 0.5),
                s.at(FirstRib.INSTANCE, 4.317, 47.124, 2.917, 1.4, 0.6, 0.3, 1, 0.3));
    }
}
