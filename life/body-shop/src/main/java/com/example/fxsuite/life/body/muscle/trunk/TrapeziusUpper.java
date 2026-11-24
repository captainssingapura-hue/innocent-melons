package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.muscle.Anchor;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleForm;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;

import java.util.List;

/**
 * The trapezius's upper part, a sheet spread from a line to a line ({@link MuscleForm.Spread}): from the medial third
 * of the occiput's superior nuchal line down the ligamentum nuchae to the seventh cervical spine, to the posterior
 * border of the clavicle's lateral third, out to the acromioclavicular joint. Its fibres run from each point along
 * the one line to as far along the other, the occipital ones down the side of the neck to the front of the clavicle's
 * third, the lowest from C7 nearly level out to the joint: so the sheet, its width up the neck where it arises and
 * along the clavicle where it ends, turns on itself as it goes over the shoulder. Its lower edge the middle part's
 * upper ({@link TrapeziusMiddle}), from C7.
 */
public record TrapeziusUpper() implements Muscle {

    public static final TrapeziusUpper INSTANCE = new TrapeziusUpper();

    @Override
    public String name() {
        return "trapezius, upper";
    }

    @Override
    public boolean sheet() {
        return true;
    }

    /**
     * Where it arises, over the neck, as thin as it is measured there by upright MRI (about 174 mm² a side, the
     * cervical levels together); its bulk lower, over the shoulder.
     */
    @Override
    public boolean keptAsDrawn(final int i) {
        return i == 0;
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public MuscleForm.Spread form() {
        return new MuscleForm.Spread(
                // along the medial third of the superior nuchal line in to the external occipital protuberance,
                // then down the ligamentum nuchae's back edge, half a centimetre behind C7's tip
                List.of(new Anchor(Skull.INSTANCE, new V3(3.8, 4.76, -8.1)),
                        new Anchor(Skull.INSTANCE, new V3(0.1, 3.96, -9.2)),
                        new Anchor(C7.INSTANCE, new V3(0.1, 54.35, -9.0))),
                // the clavicle's lateral third along its posterior border, out to the acromion's facet for it
                List.of(new Anchor(Clavicle.INSTANCE, new V3(11.2, 47.0, 3.85)),
                        new Anchor(Clavicle.INSTANCE, new V3(15.7, 47.8, 0.8)),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("clavicular facet"))),
                1.0, new V3(0, 0, -1),
                // halfway, its occipital fibres still down the back of the neck's side, its lowest over the
                // shoulder's top, before they turn forward to the clavicle
                List.of(new Anchor(C4.INSTANCE, new V3(6.0, 56.1, -4.7)),
                        new Anchor(C7.INSTANCE, new V3(8.0, 51.8, -3.7))));
    }

    @Override
    public List<Section> sections() {
        return form().sections();
    }
}
