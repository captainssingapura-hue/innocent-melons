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

/**
 * A woman's cage shaped ({@link Shaping}): her breasts, each ahead and a little out over the bust, fuller below its
 * point than above, sloping up to the armpit, the fold under it creased and curving up at either end; her buttocks
 * full; her abs a line down the middle, their edges and the V soft. Set from memory, not any woman's.
 */
public record WomanCage() implements StatelessFunctionalObject {

    public static final WomanCage INSTANCE = new WomanCage();

    public Shaping shaping() {
        final CageLines c = CageLines.INSTANCE;
        final List<Shaping.Push> pushes = new ArrayList<>(c.groin());
        pushes.addAll(c.buttocks(1.8));
        pushes.addAll(List.of(
                c.push(BUST_RING, 1, 0, 0, 0, 1.6), c.push(BUST_RING, 2, 0, 0, 0.3, 3.2),
                c.push(BUST_RING, 3, 0, -0.5, 0.6, 4.0), c.push(BUST_RING, 4, 0, 0, 0.8, 2.2),
                c.push(UNDER_BUST, 1, 0, 0, 0, 1.2), c.push(UNDER_BUST, 2, 0, 0, 0, 2.6),
                c.push(UNDER_BUST, 3, 0, -0.3, 0.3, 3.0), c.push(UNDER_BUST, 4, 0, 0, 0.5, 1.6),
                c.push(ARMPITS, 1, 0, 0, 0, 0.8), c.push(ARMPITS, 2, 0, 0, 0, 1.6),
                c.push(ARMPITS, 3, 0, 0, 0, 1.6), c.push(FOLD, 1, 0, 1.0, 0, 0),
                c.push(FOLD, 4, 0, 1.5, 0, 0)));
        final List<Shaping.Line> lines = new ArrayList<>(c.abs(false, 0.35, 1.0));
        lines.addAll(c.back());
        lines.add(new Shaping.Line("the fold under the breast", c.round(FOLD, 1, 4), 1.2, 0, false, true));
        return new Shaping(keys(), pushes, lines);
    }

    /** The torso's widths at its heights: side, ahead, behind, the middle ahead; in the drawn figure's cm. */
    public List<Torso.Key> keys() {
        final TorsoFeatures t = TorsoFeatures.INSTANCE;
        return List.of(
                new Torso.Key(t.at(CROTCH, 0), 15.0, 6.6, 10.0, -0.5),
                new Torso.Key(t.between(CROTCH, HIPS, 0.55), 15.6, 7.6, 12.0, -0.5),
                new Torso.Key(t.at(HIPS, 0), 16.0, 8.2, 12.0, -0.5),
                new Torso.Key(t.between(HIPS, CREST, 0.5), 14.6, 8.0, 10.6, 0.0),
                new Torso.Key(t.at(CREST, 0), 12.6, 7.8, 8.6, 0.5),
                new Torso.Key(t.at(NAVEL, 0), 11.2, 7.8, 7.4, 0.8),
                new Torso.Key(t.between(NAVEL, RIBS, 0.6), 10.4, 7.8, 7.2, 0.6),
                new Torso.Key(t.at(RIBS, 0), 11.2, 8.1, 7.7, 0.8),
                new Torso.Key(t.at(XIPHOID, 0), 12.0, 8.3, 8.4, 1.5),
                new Torso.Key(t.at(BUST, 0), 12.6, 8.4, 9.0, 1.6),
                new Torso.Key(t.at(ARMPIT, 0), 13.6, 8.4, 9.4, 1.5),
                new Torso.Key(t.at(SHOULDER, 0), 15.8, 7.6, 9.2, 0.5),
                new Torso.Key(t.at(SHOULDER, 2), 12.6, 6.0, 7.4, 0.0),
                new Torso.Key(t.at(NECK_BASE, 0), 5.4, 4.6, 4.8, 0.0),
                new Torso.Key(t.at(NECK, 0), 4.4, 4.1, 4.2, 0.5),
                new Torso.Key(t.at(JAW, 0), 4.4, 4.2, 4.4, 0.5));
    }
}
