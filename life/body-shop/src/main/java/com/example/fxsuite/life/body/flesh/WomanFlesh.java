package com.example.fxsuite.life.body.flesh;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.fxsuite.life.body.flesh.Landmark.ARMPIT;
import static com.example.fxsuite.life.body.flesh.Landmark.BUST;
import static com.example.fxsuite.life.body.flesh.Landmark.CREST;
import static com.example.fxsuite.life.body.flesh.Landmark.CROTCH;
import static com.example.fxsuite.life.body.flesh.Landmark.HIPS;
import static com.example.fxsuite.life.body.flesh.Landmark.JAW;
import static com.example.fxsuite.life.body.flesh.Landmark.NAVEL;
import static com.example.fxsuite.life.body.flesh.Landmark.NECK;
import static com.example.fxsuite.life.body.flesh.Landmark.NECK_BASE;
import static com.example.fxsuite.life.body.flesh.Landmark.RIBS;
import static com.example.fxsuite.life.body.flesh.Landmark.SHOULDER;
import static com.example.fxsuite.life.body.flesh.Landmark.XIPHOID;

/**
 * A woman's flesh, a model's build, as she is seen: 178 cm, about 86 cm round the bust, 61 round the waist and 89 round
 * the hips. Her torso one shape from the crotch to under the jaw ({@link Torso}), widest at her hips, narrowest at her
 * waist; her breasts swelling either side ahead at the bust; her abs lightly defined, a line down the middle and soft
 * lines either side. Her limbs' tubes hung on their bones. In the drawn figure's centimetres, her figures over her
 * height's share of its; representative, set from memory, not any woman's.
 */
public record WomanFlesh() implements StatelessFunctionalObject {

    public static final WomanFlesh INSTANCE = new WomanFlesh();

    /** Her limbs' tubes. */
    public List<Tube> tubes() {
        return LimbTubes.INSTANCE.tubes(new double[] {1.12, 1.06, 1.0, 0.95, 0.95},
                new double[] {0.95, 1.0, 0.95, 0.97, 1.0}, new double[] {0.92, 0.9, 0.88, 0.88, 0.9},
                new double[] {0.9, 0.9, 0.88, 0.88, 0.9});
    }

    /** Her torso. */
    public Torso torso() {
        final TorsoFeatures t = TorsoFeatures.INSTANCE;
        final List<Torso.Feature> features = new ArrayList<>(t.abs());
        features.addAll(t.back(1.6, 0.5));
        features.add(t.dome("the breasts", 50, t.at(BUST, -1.5), 6.5, 6.0, 4.5));
        return new Torso(List.of(
                new Torso.Key(t.at(CROTCH, 0), 8.5, 5.5, 6.0, -0.5),
                new Torso.Key(t.between(CROTCH, HIPS, 0.55), 14.6, 7.0, 11.8, -0.5),
                new Torso.Key(t.at(HIPS, 0), 16.0, 8.0, 12.5, -0.5),
                new Torso.Key(t.between(HIPS, CREST, 0.5), 15.0, 8.0, 10.8, 0.0),
                new Torso.Key(t.at(CREST, 0), 13.0, 7.8, 8.8, 0.5),
                new Torso.Key(t.at(NAVEL, 0), 11.6, 7.9, 7.6, 0.8),
                new Torso.Key(t.between(NAVEL, RIBS, 0.6), 10.6, 8.1, 7.4, 0.6),
                new Torso.Key(t.at(RIBS, 0), 11.4, 8.3, 7.9, 0.8),
                new Torso.Key(t.at(XIPHOID, 0), 12.4, 8.5, 8.8, 1.5),
                new Torso.Key(t.at(BUST, 0), 12.8, 8.6, 9.4, 1.6),
                new Torso.Key(t.at(ARMPIT, 0), 13.8, 8.6, 9.8, 1.5),
                new Torso.Key(t.at(SHOULDER, 0), 16.5, 7.8, 9.6, 0.5),
                new Torso.Key(t.at(SHOULDER, 2), 13.0, 6.2, 7.6, 0.0),
                new Torso.Key(t.at(NECK_BASE, 0), 5.6, 4.8, 5.0, 0.0),
                new Torso.Key(t.at(NECK, 0), 4.6, 4.3, 4.4, 0.5),
                new Torso.Key(t.at(JAW, 0), 4.6, 4.4, 4.6, 0.5)), features, 0.25);
    }
}
