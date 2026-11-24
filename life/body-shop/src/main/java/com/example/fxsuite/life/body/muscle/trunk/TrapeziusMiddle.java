package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.muscle.Anchor;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleForm;
import com.example.fxsuite.life.body.muscle.MuscleRegion;
import com.example.fxsuite.life.body.muscle.Section;

import java.util.List;

/**
 * The trapezius's middle part: from the spines of the seventh cervical to the third thoracic vertebra, out across the
 * top of the shoulder blade, the highest fibres to the acromion's medial margin from the acromioclavicular joint back,
 * the rest along the crest of the scapula's spine to its root. A sheet spread from the one line to the other
 * ({@link MuscleForm}), each line through all its points, lying just behind the spines' tips; its upper edge the upper
 * part's lower ({@link TrapeziusUpper}), from C7 to the acromion's facet for the clavicle, its lower edge the lower
 * part's upper ({@link TrapeziusLower}), between the third and fourth spines and at the crest's root, so that the
 * three meet. Its insertion held by the scapula's landmarks' names, so it goes where the scapula is drawn.
 */
public record TrapeziusMiddle() implements Muscle {

    public static final TrapeziusMiddle INSTANCE = new TrapeziusMiddle();

    @Override
    public String name() {
        return "trapezius, middle";
    }

    @Override
    public boolean sheet() {
        return true;
    }

    @Override
    public MuscleRegion region() {
        return MuscleRegion.BACK;
    }

    @Override
    public MuscleForm.Spread form() {
        return new MuscleForm.Spread(
                List.of(new Anchor(C7.INSTANCE, new V3(0.1, 54.35, -9.0)),
                        new Anchor(T1.INSTANCE, new V3(0.1, 51.8, -10.0)),
                        new Anchor(T3.INSTANCE, TrapeziusLower.INSTANCE.spines())),
                List.of(new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("clavicular facet")),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("acromion's base")),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("crest")),
                        new Anchor(Scapula.INSTANCE, Scapula.INSTANCE.at("crest's root"))),
                0.9, new V3(0, 0, -1),
                // its highest fibre over the shoulder's top as the upper part's lowest is, the same fibre, so the
                // seam between them is one line; its lowest, from T3 to the crest's root, straight, over its middle
                List.of(TrapeziusUpper.INSTANCE.overShoulder(), new Anchor(T3.INSTANCE, new V3(3.99, 44.94, -11.72))));
    }

    @Override
    public List<Section> sections() {
        return form().sections();
    }
}
