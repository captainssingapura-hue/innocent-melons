package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The hand's twenty-seven bones a side: its carpals, its metacarpals and its fingers' phalanges. */
public record HandBones() implements StatelessFunctionalObject {

    public static final HandBones INSTANCE = new HandBones();

    public List<Bone> bones() {
        return List.of(Scaphoid.INSTANCE, Lunate.INSTANCE, Triquetrum.INSTANCE, Pisiform.INSTANCE,
                Trapezium.INSTANCE, Trapezoid.INSTANCE, Capitate.INSTANCE, Hamate.INSTANCE,
                FirstMetacarpal.INSTANCE, SecondMetacarpal.INSTANCE, ThirdMetacarpal.INSTANCE,
                FourthMetacarpal.INSTANCE, FifthMetacarpal.INSTANCE, ThumbProximalPhalanx.INSTANCE,
                ThumbDistalPhalanx.INSTANCE, IndexFingerProximalPhalanx.INSTANCE, IndexFingerMiddlePhalanx.INSTANCE,
                IndexFingerDistalPhalanx.INSTANCE, MiddleFingerProximalPhalanx.INSTANCE,
                MiddleFingerMiddlePhalanx.INSTANCE, MiddleFingerDistalPhalanx.INSTANCE,
                RingFingerProximalPhalanx.INSTANCE, RingFingerMiddlePhalanx.INSTANCE,
                RingFingerDistalPhalanx.INSTANCE, LittleFingerProximalPhalanx.INSTANCE,
                LittleFingerMiddlePhalanx.INSTANCE, LittleFingerDistalPhalanx.INSTANCE);
    }
}
