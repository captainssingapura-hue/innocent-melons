package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The semispinalis capitis: from the transverse processes of the upper thoracic and the articular processes of the
 * lower cervical vertebrae, up the back of the neck under the splenius, to the occiput between its nuchal lines.
 */
public record SemispinalisCapitis() implements Muscle {

    public static final SemispinalisCapitis INSTANCE = new SemispinalisCapitis();

    @Override
    public String name() {
        return "semispinalis capitis";
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
                s.at(T4.INSTANCE, 2.2, 45, -8.4, 1.6, 0.8, 0, 0, -1),
                s.at(C7.INSTANCE, 2, 52.6, -5.3, 2.2, 1.4, 0, 0, -1),
                s.at(C3.INSTANCE, 1.8, 57.2, -4.1, 2.4, 1.4, 0, 0, -1),
                s.at(Occipital.INSTANCE, 1.8, 3.6, -7.1, 2.4, 0.6, 0, 0, -1));
    }
}
