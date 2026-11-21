package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The splenius capitis: from the spines of the lowest cervical and highest thoracic vertebrae, up and out under the
 * trapezius, to the mastoid and the occiput beside it.
 */
public record SpleniusCapitis() implements Muscle {

    public static final SpleniusCapitis INSTANCE = new SpleniusCapitis();

    @Override
    public String name() {
        return "splenius capitis";
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
                s.at(T1.INSTANCE, 0.8, 51, -9.1, 2.0, 0.6, 0, 0, -1),
                s.at(C4.INSTANCE, 2.428, 56.6, -4.425, 3.0, 1.2, 0.3, 0, -1),
                s.at(Occipital.INSTANCE, 4.241, 4, -5.146, 2.2, 0.6, 0.5, 0, -0.8));
    }
}
