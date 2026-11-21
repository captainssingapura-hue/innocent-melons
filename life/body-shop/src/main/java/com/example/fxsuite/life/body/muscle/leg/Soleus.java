package com.example.fxsuite.life.body.muscle.leg;

import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The soleus: from the backs of the fibula's head and the tibia's upper shaft, broad and flat under the gastrocnemius,
 * showing either side of it, into the Achilles tendon.
 */
public record Soleus() implements Muscle {

    public static final Soleus INSTANCE = new Soleus();

    @Override
    public String name() {
        return "soleus";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.LOWER_LEG;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Tibia.INSTANCE, 1.8, -5, -1.7, 4.0, 1.0, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.4, -14, -1.75, 7.7, 2.9, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.2, -24, -1.9, 6.5, 2.4, 0, 0, -1),
                s.at(Tibia.INSTANCE, 0.2, -31, -2.2, 3.0, 1.0, 0, 0, -1));
    }
}
