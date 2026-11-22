package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The longissimus capitis: the erector's longissimus continued up the neck, from the upper thoracic transverse
 * processes to the back of the mastoid.
 */
public record LongissimusCapitis() implements Muscle {

    public static final LongissimusCapitis INSTANCE = new LongissimusCapitis();

    @Override
    public String name() {
        return "longissimus capitis";
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
                s.at(T4.INSTANCE, 3.4, 46, -7.1, 1.0, 0.6, 0, 0, -1),
                s.at(C5.INSTANCE, 3.114, 55, -2.913, 1.0, 0.6, 0.3, 0, -1),
                s.at(Skull.INSTANCE, 4.466, -0.8, -1.732, 1.0, 0.6, 0.5, 0, -1));
    }
}
