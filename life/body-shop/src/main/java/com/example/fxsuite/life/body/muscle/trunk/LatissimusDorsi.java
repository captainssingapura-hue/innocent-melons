package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The latissimus dorsi: from the spines of the lower back and the thoracolumbar fascia, the broadest muscle, up and out
 * round the side of the chest, twisting under the arm, to the floor of the humerus's groove; the back of the armpit
 * with the teres major.
 */
public record LatissimusDorsi() implements Muscle {

    public static final LatissimusDorsi INSTANCE = new LatissimusDorsi();

    @Override
    public String name() {
        return "latissimus dorsi";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(L1.INSTANCE, 2, 20, -10.25, 14.0, 0.3, 0, 0, -1),
                s.at(T10.INSTANCE, 8.856, 27, -10.121, 14.0, 1.0, 0.3, 0, -1),
                s.at(T8.INSTANCE, 11.76, 33, -6.72, 10.0, 1.6, 0.8, 0, -0.6),
                s.at(Scapula.INSTANCE, 13.152, 37.318, -3.376, 5.0, 2.0, 0.8, -0.3, -0.4),
                s.at(Humerus.INSTANCE, 0.792, -5.2, 0.943, 2.4, 0.4, -1, 0, 0.3));
    }
}
