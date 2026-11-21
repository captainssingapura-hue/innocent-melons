package com.example.fxsuite.life.body.flesh;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

import static com.example.fxsuite.life.body.flesh.Landmark.CREST;
import static com.example.fxsuite.life.body.flesh.Landmark.CROTCH;
import static com.example.fxsuite.life.body.flesh.Landmark.HIPS;
import static com.example.fxsuite.life.body.flesh.Landmark.NAVEL;
import static com.example.fxsuite.life.body.flesh.Landmark.NECK_BASE;
import static com.example.fxsuite.life.body.flesh.Landmark.RIBS;
import static com.example.fxsuite.life.body.flesh.Landmark.XIPHOID;
import static com.example.fxsuite.life.body.flesh.Landmark.ARMPIT;

/**
 * How a torso's features are written down ({@link Torso.Feature}), and those any torso has. The abs: the groove down
 * the middle from below the navel to the xiphoid; the three across it, at the navel, and two fifths and three quarters
 * of the way up to the xiphoid; the grooves down either side of the rectus; the rectus swelling between them; the V
 * from each iliac crest down to the groin; the obliques over the crests: all a muscle's, as defined as the torso is.
 * Behind: the spine's groove, the shoulder blades, the buttocks.
 */
public record TorsoFeatures() implements StatelessFunctionalObject {

    public static final TorsoFeatures INSTANCE = new TorsoFeatures();

    /** At landmark {@code at}, {@code off} cm above it. */
    public Torso.Height at(final Landmark at, final double off) {
        return new Torso.Height(at, at, 0, off);
    }

    /** {@code along} of the way from {@code a} to {@code b}. */
    public Torso.Height between(final Landmark a, final Landmark b, final double along) {
        return new Torso.Height(a, b, along, 0);
    }

    /** A line from {@code from} degrees at {@code low} to {@code to} at {@code high}, falling off {@code soft} cm. */
    public Torso.Feature line(final String said, final double from, final Torso.Height low, final double to,
                              final Torso.Height high, final double soft, final double amount, final boolean both,
                              final boolean muscle) {
        return new Torso.Feature(said, Torso.Kind.LINE, from, low, to, high, soft, soft, amount, both, muscle);
    }

    /** A patch between those angles and heights, falling off {@code soft} cm round and {@code tall} up and down. */
    public Torso.Feature patch(final String said, final double from, final Torso.Height low, final double to,
                               final Torso.Height high, final double soft, final double tall, final double amount,
                               final boolean both, final boolean muscle) {
        return new Torso.Feature(said, Torso.Kind.PATCH, from, low, to, high, soft, tall, amount, both, muscle);
    }

    /**
     * A dome at {@code angle} and {@code at}, {@code wide} cm round either side, {@code below} cm down, {@code out} cm
     * out at its middle, on both sides.
     */
    public Torso.Feature dome(final String said, final double angle, final Torso.Height at, final double wide,
                              final double below, final double out) {
        return new Torso.Feature(said, Torso.Kind.DOME, angle, at, angle, at, wide, below, out, true, false);
    }

    /** The abs and the obliques, at their full definition. */
    public List<Torso.Feature> abs() {
        return List.of(
                line("the linea alba", 0, at(NAVEL, -7), 0, at(XIPHOID, 0), 0.7, -0.45, false, true),
                line("the lowest tendinous intersection", -38, between(NAVEL, XIPHOID, 0), 38,
                        between(NAVEL, XIPHOID, 0), 0.55, -0.35, false, true),
                line("the middle tendinous intersection", -38, between(NAVEL, XIPHOID, 0.4), 38,
                        between(NAVEL, XIPHOID, 0.4), 0.55, -0.35, false, true),
                line("the highest tendinous intersection", -36, between(NAVEL, XIPHOID, 0.75), 36,
                        between(NAVEL, XIPHOID, 0.75), 0.55, -0.35, false, true),
                line("the linea semilunaris", 42, at(NAVEL, -6), 40, at(XIPHOID, -1), 0.8, -0.3, true, true),
                patch("the rectus abdominis", -36, at(NAVEL, -6), 36, at(XIPHOID, -0.5), 1.5, 1.5, 0.35, false,
                        true),
                line("the inguinal V", 60, at(CREST, -2), 16, at(CROTCH, 4.5), 0.9, -0.45, true, true),
                patch("the obliques", 70, at(CREST, 0.5), 95, at(CREST, 4), 2.5, 2.0, 0.45, true, true));
    }

    /**
     * The back: the spine's groove, the shoulder blades; buttocks swelling {@code buttocks} cm; and the lats swelling
     * {@code lats} cm either side behind, from the lowest ribs up to the armpits, the back wide at the top and narrow
     * at the waist.
     */
    public List<Torso.Feature> back(final double buttocks, final double lats) {
        return List.of(
                patch("the lats", 85, at(RIBS, 2), 125, at(ARMPIT, -1), 3.0, 4.0, lats, true, false),
                line("the spine's groove", 180, at(CREST, 0), 180, at(NECK_BASE, -3), 1.0, -0.45, false, false),
                patch("the shoulder blades", 150, at(ARMPIT, 1), 150, at(ARMPIT, 5), 3.0, 3.0, 0.6, true, false),
                patch("the buttocks", 145, at(HIPS, -1), 145, at(HIPS, 1.5), 5.5, 4.0, buttocks, true, false));
    }
}
