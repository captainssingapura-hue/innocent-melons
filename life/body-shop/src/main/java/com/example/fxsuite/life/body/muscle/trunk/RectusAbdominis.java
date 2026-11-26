package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The rectus abdominis: from the pubic crest, up the front of the belly either side of the linea alba, widening, to the
 * fifth, sixth and seventh ribs' cartilages and the xiphoid, its top as high as the fifth's, under the pectoral. Above
 * the navel crossed by its three tendinous intersections, where it is thin and held down, {@link #HELD} deep, at the
 * navel, a third of the way up to the xiphoid and two thirds, the highest below the costal margin, so that it is full
 * where it meets the pectoral; between them its bellies, {@link #BELLY} deep, as a lean man's show; below the navel
 * smooth.
 */
public record RectusAbdominis() implements Muscle {

    public static final RectusAbdominis INSTANCE = new RectusAbdominis();

    /** How deep it is at an intersection, and between them, cm. */
    private static final double HELD = 0.6;
    private static final double BELLY = 1.3;

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
                s.at(L3.INSTANCE, 3.4, 12, 7.5, 6.0, HELD, 0, 0, 1),
                s.at(L3.INSTANCE, 3.7, 15.5, 7.8, 6.6, BELLY, 0, 0, 1),
                s.at(L3.INSTANCE, 4.0, 19, 8.1, 7.1, HELD, 0, 0, 1),
                s.at(T12.INSTANCE, 4.2, 21.5, 8.3, 7.5, BELLY, 0, 0, 1),
                s.at(T12.INSTANCE, 4.4, 24, 8.5, 8.0, 1.0, 0, 0, 1),
                s.at(Sternum.INSTANCE, 5.2, 31.5, 9.0, 9.0, 0.8, 0, 0.25, 1));
    }
}
