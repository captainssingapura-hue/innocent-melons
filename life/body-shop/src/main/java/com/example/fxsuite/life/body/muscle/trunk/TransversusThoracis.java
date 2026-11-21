package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The transversus thoracis: inside the front of the chest, from the back of the sternum's lower part, up and out to the
 * cartilages of the second to the sixth ribs.
 */
public record TransversusThoracis() implements Muscle {

    public static final TransversusThoracis INSTANCE = new TransversusThoracis();

    @Override
    public String name() {
        return "transversus thoracis";
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
                s.at(Sternum.INSTANCE, 1, 31, 7.35, 4.0, 0.3, 0, 0, -1),
                s.at(Sternum.INSTANCE, 6, 36, 7.35, 4.0, 0.3, 0, 0, -1));
    }
}
