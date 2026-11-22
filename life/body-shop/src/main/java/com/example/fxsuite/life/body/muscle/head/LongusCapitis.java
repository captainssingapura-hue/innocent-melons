package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The longus capitis: from the transverse processes of the third to the sixth cervical vertebrae, up to the base of the
 * occiput; it bends the head forward.
 */
public record LongusCapitis() implements Muscle {

    public static final LongusCapitis INSTANCE = new LongusCapitis();

    @Override
    public String name() {
        return "longus capitis";
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
                s.at(C6.INSTANCE, 2.2, 54.2, -0.45, 1.0, 0.5, 0, 0, 1),
                s.at(C3.INSTANCE, 1.6, 57.4, 0, 1.4, 0.8, 0, 0, 1),
                s.at(Skull.INSTANCE, 0.9, -0.4, 1.1, 1.0, 0.6, 0, 0, 1));
    }
}
