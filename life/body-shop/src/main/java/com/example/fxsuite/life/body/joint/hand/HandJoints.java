package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The hand's joints, one hand's: the wrist, among the carpals, the metacarpals' bases, the fingers. */
public record HandJoints() implements StatelessFunctionalObject {

    public static final HandJoints INSTANCE = new HandJoints();

    public List<Complex> joints() {
        return List.of(Wrist.INSTANCE, Intercarpal.INSTANCE, FirstCarpometacarpal.INSTANCE,
                Carpometacarpal.INSTANCE, Intermetacarpal.INSTANCE, FirstMetacarpophalangeal.INSTANCE,
                SecondMetacarpophalangeal.INSTANCE, ThirdMetacarpophalangeal.INSTANCE,
                FourthMetacarpophalangeal.INSTANCE, FifthMetacarpophalangeal.INSTANCE,
                ThumbInterphalangeal.INSTANCE, IndexFingerProximalInterphalangeal.INSTANCE,
                IndexFingerDistalInterphalangeal.INSTANCE, MiddleFingerProximalInterphalangeal.INSTANCE,
                MiddleFingerDistalInterphalangeal.INSTANCE, RingFingerProximalInterphalangeal.INSTANCE,
                RingFingerDistalInterphalangeal.INSTANCE, LittleFingerProximalInterphalangeal.INSTANCE,
                LittleFingerDistalInterphalangeal.INSTANCE);
    }
}
