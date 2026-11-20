package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The chest's bones: the sternum, the twelve ribs a side; and their cartilage. */
public record ThoraxBones() implements StatelessFunctionalObject {

    public static final ThoraxBones INSTANCE = new ThoraxBones();

    public List<Bone> bones() {
        return List.of(Sternum.INSTANCE, FirstRib.INSTANCE, SecondRib.INSTANCE, ThirdRib.INSTANCE,
                FourthRib.INSTANCE, FifthRib.INSTANCE, SixthRib.INSTANCE, SeventhRib.INSTANCE, EighthRib.INSTANCE,
                NinthRib.INSTANCE, TenthRib.INSTANCE, EleventhRib.INSTANCE, TwelfthRib.INSTANCE,
                CostalCartilages.INSTANCE);
    }
}
