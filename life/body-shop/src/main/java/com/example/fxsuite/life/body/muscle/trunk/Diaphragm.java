package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The diaphragm, a half of it: from the crus on the upper lumbar vertebrae, up over its dome under the lungs and the
 * heart, down to the lower ribs' insides and the xiphoid; the floor of the chest.
 */
public record Diaphragm() implements Muscle {

    public static final Diaphragm INSTANCE = new Diaphragm();

    @Override
    public String name() {
        return "diaphragm";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.CHEST;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(L1.INSTANCE, 1.6, 22, -0.3, 2.0, 1.0, 0, 0, 1),
                s.at(T10.INSTANCE, 5, 29.808, -4.943, 10.0, 0.4, 0, 1, -0.3),
                s.at(Sternum.INSTANCE, 5, 33.8, 0.5, 12.0, 0.4, 0, 1, 0),
                s.at(Sternum.INSTANCE, 5, 29.808, 6.443, 10.0, 0.4, 0, 1, 0.3),
                s.at(Sternum.INSTANCE, 3, 28.543, 8.408, 4.0, 0.4, 0, 0.3, 1));
    }
}
