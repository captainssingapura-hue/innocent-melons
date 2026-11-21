package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A body's skin, lofted: a surface swept along a part, ring after ring, each ring's shape a function of how far along
 * it is ({@code t}, 0 to 1) and how far round ({@code phi}, radians), its width and its depth read off profiles by how
 * far along (keyframes eased one into the next), its ends drawn in to a point. As the body model of the papers draws
 * its limbs (Papers/Body: the interactive full-body model).
 */
final class Loft {

    /** A point of a skin, by how far along it and how far round. */
    @FunctionalInterface
    interface Skin {
        V3 at(double t, double phi);
    }

    private Loft() {
    }

    /** {@code keys} ({@code {t, value}}, in order of {@code t}) read at {@code t}, eased between keys. */
    static double ip(final double[][] keys, final double t) {
        if (t <= keys[0][0]) {
            return keys[0][1];
        }
        for (int i = 0; i < keys.length - 1; i++) {
            final double[] a = keys[i];
            final double[] b = keys[i + 1];
            if (t <= b[0]) {
                double u = (t - a[0]) / (b[0] - a[0]);
                u = (1 - Math.cos(Math.PI * u)) / 2;
                return a[1] + (b[1] - a[1]) * u;
            }
        }
        return keys[keys.length - 1][1];
    }

    /** How full a skin is {@code t} along: 1 in its middle, falling to 0 at its ends, the steeper the higher {@code n}. */
    static double endF(final double t, final double n) {
        return Math.sqrt(Math.max(0, 1 - Math.pow(Math.abs(2 * t - 1), n)));
    }

    static double endF(final double t) {
        return endF(t, 12);
    }

    /** A bump at {@code c}, {@code w} wide: 1 at its peak. */
    static double prof(final double t, final double c, final double w) {
        final double u = (t - c) / w;
        return Math.exp(-u * u);
    }

    /** {@code c} raised to {@code p}, keeping its sign: a superellipse's way round, squarer below 1. */
    static double se(final double c, final double p) {
        return Math.signum(c) * Math.pow(Math.abs(c), p);
    }

    /** Smoothly from 0 to 1 over most of the way along: how much of a forearm's twist is felt so far down it. */
    static double sw(final double t) {
        final double x = Math.clamp((t - 0.05) / 0.95, 0, 1);
        return x * x * (3 - 2 * x);
    }

    /**
     * {@code skin} as a surface: {@code along} rings from one end to the other, each {@code round} points round; its
     * end rings, where it is drawn in to a point, its two ends.
     */
    static Surface of(final String name, final Paint paint, final int along, final int round, final Skin skin) {
        return of(name, paint, along, round, skin, true);
    }

    /** As {@link #of(String, Paint, int, int, Skin)}, seen smooth or, if not {@code smooth}, in flat panels round. */
    static Surface of(final String name, final Paint paint, final int along, final int round, final Skin skin,
                      final boolean smooth) {
        final List<List<V3>> rings = new ArrayList<>(along);
        for (int i = 1; i < along - 1; i++) {
            final double t = (double) i / (along - 1);
            final List<V3> ring = new ArrayList<>(round);
            for (int j = 0; j < round; j++) {
                ring.add(skin.at(t, 2 * Math.PI * j / round));
            }
            rings.add(ring);
        }
        return new Surface(name, paint, rings, skin.at(0, 0), skin.at(1, 0), smooth);
    }
}
