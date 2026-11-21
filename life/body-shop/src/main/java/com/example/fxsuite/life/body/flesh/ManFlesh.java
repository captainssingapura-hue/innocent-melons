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
 * A man's flesh, a model's build, as he is seen: 188 cm, about 100 cm round the chest, 79 round the waist and 96 round
 * the hips; broad in the shoulder. His torso one shape from the crotch to under the jaw ({@link Torso}); his pectorals
 * swelling either side of a groove down his sternum; his abs defined, a six-pack and the V down to the groin. His
 * limbs' tubes hung on their bones. In the drawn figure's centimetres, his figures over his height's share of its;
 * representative, set from memory, not any man's.
 */
public record ManFlesh() implements StatelessFunctionalObject {

    public static final ManFlesh INSTANCE = new ManFlesh();

    /** His limbs' tubes. */
    public List<Tube> tubes() {
        return LimbTubes.INSTANCE.tubes(new double[] {1.0, 1.04, 1.02, 1.0, 1.0},
                new double[] {1.0, 1.0, 1.0, 1.0, 1.05}, new double[] {1.05, 1.06, 1.08, 1.05, 1.0},
                new double[] {1.05, 1.05, 1.03, 1.0, 1.0});
    }

    /** His torso. */
    public Torso torso() {
        final TorsoFeatures t = TorsoFeatures.INSTANCE;
        final List<Torso.Feature> features = new ArrayList<>(t.abs());
        features.addAll(t.back(1.0, 2.2));
        features.add(t.patch("the pectorals", 8, t.at(XIPHOID, 2.5), 52, t.at(ARMPIT, -0.5), 1.2, 1.2, 0.9, true,
                true));
        features.add(t.line("the sternum's groove", 0, t.at(XIPHOID, 0), 0, t.at(ARMPIT, 3), 0.8, -0.35, false,
                true));
        return new Torso(List.of(
                new Torso.Key(t.at(CROTCH, 0), 8.5, 5.8, 6.0, -0.5),
                new Torso.Key(t.between(CROTCH, HIPS, 0.55), 13.2, 7.4, 10.4, -0.5),
                new Torso.Key(t.at(HIPS, 0), 13.8, 8.2, 10.8, -0.5),
                new Torso.Key(t.between(HIPS, CREST, 0.5), 12.9, 8.4, 9.8, 0.0),
                new Torso.Key(t.at(CREST, 0), 12.2, 8.4, 9.0, 0.5),
                new Torso.Key(t.at(NAVEL, 0), 11.8, 8.8, 8.2, 0.8),
                new Torso.Key(t.between(NAVEL, RIBS, 0.6), 11.7, 9.0, 8.2, 0.6),
                new Torso.Key(t.at(RIBS, 0), 13.0, 9.3, 8.8, 0.8),
                new Torso.Key(t.at(XIPHOID, 0), 14.6, 9.7, 9.8, 1.5),
                new Torso.Key(t.at(BUST, 0), 16.4, 10.4, 10.8, 1.6),
                new Torso.Key(t.at(ARMPIT, 0), 17.6, 10.2, 11.6, 1.5),
                new Torso.Key(t.at(SHOULDER, 0), 18.6, 9.0, 10.8, 0.5),
                new Torso.Key(t.at(SHOULDER, 2), 15.0, 7.0, 8.6, 0.0),
                new Torso.Key(t.at(NECK_BASE, 0), 7.0, 5.8, 6.4, 0.0),
                new Torso.Key(t.at(NECK, 0), 5.6, 5.2, 5.6, 0.5),
                new Torso.Key(t.at(JAW, 0), 5.6, 5.3, 5.8, 0.5)), features, 0.8);
    }
}
