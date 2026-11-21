package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.flesh.Torso;
import com.example.fxsuite.life.body.flesh.TorsoFeatures;
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
import static com.example.fxsuite.life.body.cage.TorsoRing.ARMPITS;
import static com.example.fxsuite.life.body.cage.TorsoRing.BUST_RING;
import static com.example.fxsuite.life.body.cage.TorsoRing.FOLD;
import static com.example.fxsuite.life.body.cage.TorsoRing.UNDER_BUST;
import static com.example.fxsuite.life.body.cage.TorsoRing.UNDER_SHOULDERS;
import static com.example.fxsuite.life.body.cage.TorsoRing.XIPHOID_RING;

/**
 * A man's cage shaped ({@link Shaping}): his pectorals full over the bust and the armpits, their lower edge rising to
 * the armpit, creased; the groove down his sternum; his lats flaring behind under the arms, the back wide at the top;
 * his shoulder blades; his buttocks; his abs a six-pack and the V. Set from memory, not any man's.
 */
public record ManCage() implements StatelessFunctionalObject {

    public static final ManCage INSTANCE = new ManCage();

    public Shaping shaping() {
        final CageLines c = CageLines.INSTANCE;
        final List<Shaping.Push> pushes = new ArrayList<>(c.groin());
        pushes.addAll(c.buttocks(1.0));
        pushes.addAll(List.of(
                c.push(BUST_RING, 1, 0, 0, 0, 1.0), c.push(BUST_RING, 2, 0, 0, 0, 1.3),
                c.push(BUST_RING, 3, 0, 0, 0, 0.9), c.push(ARMPITS, 1, 0, 0, 0, 0.8),
                c.push(ARMPITS, 2, 0, 0, 0, 1.0), c.push(ARMPITS, 3, 0, 0, 0, 0.7),
                c.push(UNDER_BUST, 1, 0, 0, 0, 0.5), c.push(UNDER_BUST, 2, 0, 0.4, 0, 0.7),
                c.push(UNDER_BUST, 3, 0, 1.2, 0, 0.4), c.push(UNDER_BUST, 4, 0, 2.2, 0, 0),
                c.push(BUST_RING, 6, 0, 0, 0.8, 0), c.push(BUST_RING, 7, 0, 0, 1.4, 0),
                c.push(UNDER_BUST, 7, 0, 0, 1.0, 0), c.push(FOLD, 7, 0, 0, 0.6, 0),
                c.push(ARMPITS, 7, 0, 0, 1.2, 0), c.push(ARMPITS, 8, 0, 0, 0.6, 0),
                c.push(UNDER_SHOULDERS, 8, 0, 0, 0.5, 0)));
        final List<Shaping.Line> lines = new ArrayList<>(c.abs(true, 0.35, 1.0));
        lines.addAll(c.back());
        lines.add(new Shaping.Line("the pectoral's lower edge", c.round(UNDER_BUST, 1, 4), 0.8, 0.15, true, true));
        lines.add(new Shaping.Line("the sternum's groove", c.down(0, XIPHOID_RING, ARMPITS), 0.5, 0.3, true, false));
        return new Shaping(keys(), pushes, lines);
    }

    /** The torso's widths at its heights: side, ahead, behind, the middle ahead; in the drawn figure's cm. */
    public List<Torso.Key> keys() {
        final TorsoFeatures t = TorsoFeatures.INSTANCE;
        return List.of(
                new Torso.Key(t.at(CROTCH, 0), 14.2, 6.8, 9.6, -0.5),
                new Torso.Key(t.between(CROTCH, HIPS, 0.55), 15.0, 8.0, 11.8, -0.5),
                new Torso.Key(t.at(HIPS, 0), 15.4, 8.6, 11.8, -0.5),
                new Torso.Key(t.between(HIPS, CREST, 0.5), 14.2, 8.8, 10.4, 0.0),
                new Torso.Key(t.at(CREST, 0), 13.2, 9.0, 9.4, 0.5),
                new Torso.Key(t.at(NAVEL, 0), 12.8, 9.3, 8.8, 0.8),
                new Torso.Key(t.between(NAVEL, RIBS, 0.6), 12.7, 9.4, 8.8, 0.6),
                new Torso.Key(t.at(RIBS, 0), 13.6, 9.6, 9.2, 0.8),
                new Torso.Key(t.at(XIPHOID, 0), 14.8, 9.9, 9.9, 1.5),
                new Torso.Key(t.at(BUST, 0), 16.2, 10.4, 10.8, 1.6),
                new Torso.Key(t.at(ARMPIT, 0), 17.4, 10.2, 11.4, 1.5),
                new Torso.Key(t.at(SHOULDER, 0), 18.8, 9.0, 10.8, 0.5),
                new Torso.Key(t.at(SHOULDER, 2), 15.0, 7.0, 8.6, 0.0),
                new Torso.Key(t.at(NECK_BASE, 0), 7.0, 5.8, 6.4, 0.0),
                new Torso.Key(t.at(NECK, 0), 5.6, 5.2, 5.6, 0.5),
                new Torso.Key(t.at(JAW, 0), 5.6, 5.3, 5.8, 0.5));
    }
}
