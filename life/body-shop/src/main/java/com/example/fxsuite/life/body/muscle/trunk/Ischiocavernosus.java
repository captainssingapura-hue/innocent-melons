package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The ischiocavernosus: along the ischiopubic ramus, from the ischial tuberosity forward round the crus of the erectile
 * body.
 */
public record Ischiocavernosus() implements Muscle {

    public static final Ischiocavernosus INSTANCE = new Ischiocavernosus();

    @Override
    public String name() {
        return "ischiocavernosus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.HIP;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 4.2, -9.1, 0.6, 0.8, 0.6, 0, -1, 0),
                s.at(HipBone.INSTANCE, 1.4, -7.732, 4.266, 0.8, 0.6, 0, -1, 0.5));
    }
}
