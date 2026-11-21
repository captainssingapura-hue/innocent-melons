package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The plantaris: a small belly from above the femur's outer condyle, and a long thin tendon down between the
 * gastrocnemius and the soleus to the heel.
 */
public record Plantaris() implements Muscle {

    public static final Plantaris INSTANCE = new Plantaris();

    @Override
    public String name() {
        return "plantaris";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public Layer layer() {
        return Layer.DEEP;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Femur.INSTANCE, 2.8, -36.4, -2.2, 0.6, 0.4, 0, 0, -1),
                s.at(Femur.INSTANCE, 2.2, -38.8, -2.6, 1.0, 0.8, 0, 0, -1),
                s.at(Tibia.INSTANCE, 1, -6, -3.25, 0.4, 0.3, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0, -36, -3.1, 0.3, 0.2, 0, 0, -1),
                s.at(Calcaneus.INSTANCE, -0.2, -3, -5.3, 0.3, 0.2, 0, 0, -1));
    }
}
