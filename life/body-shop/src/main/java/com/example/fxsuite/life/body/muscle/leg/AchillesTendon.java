package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Layer;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The Achilles tendon: the calf's muscles gathered into one cord, narrowing down behind the ankle, to the back of the
 * heel bone.
 */
public record AchillesTendon() implements Muscle {

    public static final AchillesTendon INSTANCE = new AchillesTendon();

    @Override
    public String name() {
        return "Achilles tendon";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public Layer layer() {
        return Layer.TENDON;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 0.2, -21, -3.65, 2.4, 0.5, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.3, -30, -3.1, 1.8, 0.6, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.3, -37, -3.25, 1.4, 0.7, 0, 0, -1),
                s.at(Calcaneus.INSTANCE, 0.3, -3, -5.25, 1.8, 0.7, 0, 0, -1));
    }
}
