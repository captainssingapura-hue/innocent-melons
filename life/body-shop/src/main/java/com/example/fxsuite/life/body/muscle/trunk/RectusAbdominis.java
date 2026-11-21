package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus abdominis: from the pubic crest, up the front of the belly either side of the linea alba, widening, to the
 * fifth, sixth and seventh ribs' cartilages and the xiphoid.
 */
public record RectusAbdominis() implements Muscle {

    public static final RectusAbdominis INSTANCE = new RectusAbdominis();

    @Override
    public String name() {
        return "rectus abdominis";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.ABDOMEN;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(HipBone.INSTANCE, 1.6, -2.4, 6.5, 2.6, 1.0, 0, 0, 1),
                s.at(L5.INSTANCE, 3, 6, 7.1, 5.4, 1.0, 0, 0, 1),
                s.at(L3.INSTANCE, 3.6, 16, 7.8, 6.4, 1.2, 0, 0, 1),
                s.at(T12.INSTANCE, 4, 24, 8.5, 7.0, 1.0, 0, 0, 1),
                s.at(Sternum.INSTANCE, 4.6, 29.922, 9.208, 7.4, 0.8, 0, 0.2, 1));
    }
}
