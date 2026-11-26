package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.muscle.Sections;

import java.util.List;

/**
 * The external oblique: from the outer faces of the lower eight ribs, down and forward over the flank, to the iliac
 * crest and into the sheath of the rectus; its fleshy part over the front and the side of the lower ribs and the
 * flank, facing forward and out, its front edge at the rectus's side, so that nothing of the ribs under the pectoral
 * nor of the belly above the hip is bare; its lowest along the inguinal ligament, from the hip bone's front to the
 * pubis, beside the rectus.
 */
public record ExternalOblique() implements Muscle {

    public static final ExternalOblique INSTANCE = new ExternalOblique();

    @Override
    public String name() {
        return "external oblique";
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.ABDOMEN;
    }

    @Override
    public List<Section> sections() {
        final Sections s = Sections.INSTANCE;
        return List.of(
                s.at(Sternum.INSTANCE, 11.5, 29, 4.5, 11.0, 0.8, 0.75, 0, 0.66),
                s.at(L3.INSTANCE, 10.0, 17, 5.0, 12.0, 1.2, 0.8, 0, 0.6),
                s.at(HipBone.INSTANCE, 10.0, 8.6, 4.5, 11.0, 1.0, 0.8, 0, 0.6),
                s.at(HipBone.INSTANCE, 6.5, 1.5, 6.8, 7.0, 0.6, 0.4, 0.2, 0.9));
    }
}
