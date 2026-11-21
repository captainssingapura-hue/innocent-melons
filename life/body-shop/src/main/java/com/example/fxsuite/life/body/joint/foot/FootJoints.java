package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The foot's joints, one foot's twenty: under the talus, across the tarsus, the metatarsals' bases, the toes. */
public record FootJoints() implements StatelessFunctionalObject {

    public static final FootJoints INSTANCE = new FootJoints();

    public List<Complex> joints() {
        return List.of(Subtalar.INSTANCE, TransverseTarsal.INSTANCE, Cuneonavicular.INSTANCE,
                Intercuneiform.INSTANCE, Tarsometatarsal.INSTANCE, Intermetatarsal.INSTANCE,
                FirstMetatarsophalangeal.INSTANCE, SecondMetatarsophalangeal.INSTANCE,
                ThirdMetatarsophalangeal.INSTANCE, FourthMetatarsophalangeal.INSTANCE,
                FifthMetatarsophalangeal.INSTANCE, GreatToeInterphalangeal.INSTANCE,
                SecondToeProximalInterphalangeal.INSTANCE, SecondToeDistalInterphalangeal.INSTANCE,
                ThirdToeProximalInterphalangeal.INSTANCE, ThirdToeDistalInterphalangeal.INSTANCE,
                FourthToeProximalInterphalangeal.INSTANCE, FourthToeDistalInterphalangeal.INSTANCE,
                LittleToeProximalInterphalangeal.INSTANCE, LittleToeDistalInterphalangeal.INSTANCE);
    }
}
