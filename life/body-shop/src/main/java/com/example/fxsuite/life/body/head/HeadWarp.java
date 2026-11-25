package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;

/**
 * How a reference head is made a man's size region by region ({@link HeadSize}), not by one scale each way: about its
 * right eye's middle, {@code eye}, which stays where it was up and ahead; up from it the vault made as high as a
 * man's, down from it the face, the two scales blended over {@link #BLEND} either side; all of it as long; across,
 * the vault as broad as it is long, its breadth unmeasured, and the face below the cheekbones as broad as they are,
 * blended between by height. The jaw is not sized: MakeHuman's narrows to the chin with no angle to it, which no
 * scale makes a man's. The levels where the regions meet are about the eye once it is sized, so that the map is undone
 * by finding first how high and how far ahead a point was, then how far across.
 *
 * @param eye    the right eye's middle, as the head was read
 * @param levels where the regions meet
 * @param scales how many times as big each is made
 */
public record HeadWarp(V3 eye, Levels levels, Scales scales) {

    /** How far either side of the eye's level the vault's and the face's scales are blended, cm. */
    private static final double BLEND = 1.5;

    /**
     * Where, about the eye once it is sized, the regions meet, cm: the brow, above which the vault's breadth; the
     * cheekbones, below which theirs.
     */
    public record Levels(double brow, double cheek) {
    }

    /** How many times as big: the vault up from the eye, the face down from it, ahead, across the cheekbones. */
    public record Scales(double vault, double face, double ahead, double cheek) {
    }

    /** Where {@code p}, as the head was read, is once it is sized. */
    public V3 apply(final V3 p) {
        final double up = up(p.y() - eye.y());
        return new V3(p.x() * across(up), eye.y() + up, eye.z() + (p.z() - eye.z()) * scales.ahead());
    }

    /** Where {@code q}, as the head is sized, was as it was read. */
    public V3 back(final V3 q) {
        final double up = q.y() - eye.y();
        return new V3(q.x() / across(up), eye.y() + down(up), eye.z() + (q.z() - eye.z()) / scales.ahead());
    }

    /** How many times as big an eyeball is made: as the face round it is, on the whole. */
    public double round() {
        return Math.cbrt(scales.cheek() * scales.face() * scales.ahead());
    }

    /**
     * How high above the eye a point {@code d} above it as read is once sized: the face's scale below the blend, the
     * vault's above it, between them the scale changing evenly, so that the head bends without a crease.
     */
    private double up(final double d) {
        final double a = scales.face();
        final double b = scales.vault();
        final double low = -BLEND * (3 * a + b) / 4;
        final double high = BLEND * (a + 3 * b) / 4;
        if (d <= -BLEND) {
            return low + a * (d + BLEND);
        }
        if (d >= BLEND) {
            return high + b * (d - BLEND);
        }
        final double u = d + BLEND;
        return low + a * u + (b - a) * u * u / (4 * BLEND);
    }

    /** How high above the eye a point {@code e} above it once sized was as read: {@link #up} undone. */
    private double down(final double e) {
        final double a = scales.face();
        final double b = scales.vault();
        final double low = -BLEND * (3 * a + b) / 4;
        final double high = BLEND * (a + 3 * b) / 4;
        if (e <= low) {
            return -BLEND + (e - low) / a;
        }
        if (e >= high) {
            return BLEND + (e - high) / b;
        }
        final double c = (b - a) / (4 * BLEND);
        final double r = e - low;
        final double u = Math.abs(c) < 1e-9 ? r / a : (-a + Math.sqrt(a * a + 4 * c * r)) / (2 * c);
        return u - BLEND;
    }

    /**
     * How many times as broad a point {@code up} above the eye, once sized, is made: as the cheekbones below them, as
     * the vault is long above the brow, easing from the one to the other between.
     */
    private double across(final double up) {
        final double t = Math.max(0, Math.min(1, (up - levels.cheek()) / (levels.brow() - levels.cheek())));
        return scales.cheek() + (scales.ahead() - scales.cheek()) * t * t * (3 - 2 * t);
    }
}
