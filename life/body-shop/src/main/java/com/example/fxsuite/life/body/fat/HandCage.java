package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;
import com.example.fxsuite.life.body.bone.hand.Fingers;
import com.example.fxsuite.life.body.cage.Building;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 * The fat cage's right hand, going on from the wrist's ring ({@link FatCage}): the palm in rings across it, as many
 * points across its back as across its front, two in line with each finger, from the wrist to the knuckles; the
 * wrist's ring joined to the first; the thumb hung from the palm's side near the wrist, and each finger from its two
 * points across the back and the two across the front at the knuckles, each a tube along its bones as they hang
 * ({@link Fingers}), shut at its tip ({@link Limb}); between each finger and the next, its web. Each point on the
 * outside asked for, along a line in, among what is its own: the palm's, or one finger's, what lies nearer its bones
 * than any other finger's. Every face made to go round the way the wrist's do, as its neighbours across its edges do.
 */
final class HandCage {

    /** How far down the hand from the wrist the palm's rings are, cm, in the hand's own frame, the knuckles' last. */
    private final double[] rows = {-0.6, -2.6, -4.6, -6.6};
    /** How far out a finger's lines start, and the thumb's, cm. */
    private static final double FINGER = 1.3;
    private static final double THUMB = 2;

    private final FatTarget target;
    private final Rig.Frame hand;
    private final HandLines lines;

    HandCage(final FatTarget target) {
        this.target = target;
        this.hand = target.bind().frame(Part.RIGHT_HAND);
        this.lines = new HandLines();
    }

    /**
     * The hand hung from {@code wrist}, its points set out along {@code wristWays}, added to {@code b}; every point of
     * it put in {@code held}, wholly but the palm's first two rings, a little and more, the skin over the wrist free
     * to run on into the forearm's. Its faces made to go round the way the wrist's go ({@link Building#agree}).
     */
    void hang(final Building b, final int[] wrist, final V3[] wristWays, final java.util.Map<Integer, Double> held) {
        final int from = b.size();
        final IntPredicate hands = target.among(Part.RIGHT_HAND, p -> hand.local(p).y() > -10.5);
        final IntPredicate wrists = target.among(Part.RIGHT_FOREARM, p -> hand.local(p).y() < 4);
        final IntPredicate palm = i -> hands.test(i) || wrists.test(i);
        final int[][] rings = new int[rows.length + 1][];
        for (int r = 0; r < rings.length; r++) {
            rings[r] = palmRing(b, palm, r);
        }
        b.making(Part.RIGHT_HAND);
        new Zip(b).join(wrist, rings[0], hand.way(V3.Y.times(-1)), wristWays, palmWays());
        final int faces = b.faceCount();
        palm(b, rings);
        digits(b, rings);
        final int[] knuckles = rings[rings.length - 1];
        final int last = 2 * HandLines.ACROSS - 1;
        for (int i = 0; i < 3; i++) {
            b.face(knuckles[2 * i + 1], knuckles[2 * i + 2], knuckles[last - 2 * i - 2], knuckles[last - 2 * i - 1]);
        }
        b.agree(faces);
        for (int i = from; i < b.size(); i++) {
            held.put(i, 1.0);
        }
        for (final int p : rings[0]) {
            held.put(p, 0.3);
        }
        for (final int p : rings[1]) {
            held.put(p, 0.7);
        }
    }

    /** The palm's faces between its rings, but where the thumb goes out, by the wrist on the thumb's side. */
    private void palm(final Building b, final int[][] rings) {
        for (int r = 0; r + 1 < rings.length; r++) {
            for (int j = 0; j < 2 * HandLines.ACROSS; j++) {
                final int k = (j + 1) % (2 * HandLines.ACROSS);
                if (r != 1 || j != 2 * HandLines.ACROSS - 1) {
                    b.face(rings[r][j], rings[r][k], rings[r + 1][k], rings[r + 1][j]);
                }
            }
        }
    }

    /** The thumb, hung from the palm by the wrist on its side; the fingers, from the knuckles; each to its tip. */
    private void digits(final Building b, final int[][] rings) {
        final int last = 2 * HandLines.ACROSS - 1;
        digit(b, Digit.THUMB, new int[] {rings[1][last], rings[1][0], rings[2][0], rings[2][last]}, THUMB);
        final int[] knuckles = rings[rings.length - 1];
        for (int i = 0; i < 4; i++) {
            digit(b, Digit.values()[i + 1], new int[] {knuckles[2 * i], knuckles[2 * i + 1],
                knuckles[last - 2 * i - 1], knuckles[last - 2 * i]}, FINGER);
        }
    }

    /**
     * Digit {@code d} hung from {@code loop}, its lines starting {@code out} cm out, among what is its own; closed
     * over its tip, every point of its last ring joined to one on the outside straight out from the end of its bone.
     */
    private void digit(final Building b, final Digit d, final int[] loop, final double out) {
        final IntPredicate own = target.among(Part.RIGHT_HAND, p -> lines.nearest(hand.local(p)) == d);
        final int[] ring = new Limb(target).hang(b, loop, own, rings(d, out), false, Double.NaN);
        final V3[] end = Fingers.INSTANCE.way(d, d.phalanges() - 1);
        final V3 tip = end[0].plus(end[1].times(d.length(d.phalanges() - 1)));
        final V3 way = hand.way(end[1]).unit();
        final int at = b.add(target.onto(hand.place(tip).plus(way.times(out)), way.times(-1), own, 2 * out)
                .map(FatTarget.Hit::at).orElse(hand.place(tip).plus(way.times(0.3))), "right " + d.said() + ", tip");
        for (int k = 0; k < ring.length; k++) {
            b.face(ring[k], ring[(k + 1) % ring.length], at);
        }
    }

    /** Which way out each point of the palm's first ring was set along: from its middle, to its back or its front. */
    private V3[] palmWays() {
        final int n = HandLines.ACROSS;
        final V3[] ways = new V3[2 * n];
        for (int j = 0; j < n; j++) {
            final double x = lines.across(j, rows[0], false);
            ways[j] = hand.way(new V3(x, 0, -1.4)).unit();
            ways[2 * n - 1 - j] = hand.way(new V3(x, 0, 1.6)).unit();
        }
        return ways;
    }

    /**
     * The palm's ring {@code r}: across its back from the thumb's side to the little finger's, then back across its
     * front; each point on the outside along a line square to the palm, from behind or before; the last ring at the
     * knuckles, each finger's two points where its metacarpal's head is.
     */
    private int[] palmRing(final Building b, final IntPredicate palm, final int r) {
        final int n = HandLines.ACROSS;
        final int[] ring = new int[2 * n];
        for (int j = 0; j < n; j++) {
            final boolean knuckles = r == rows.length;
            final double x = lines.across(j, knuckles ? lines.knuckle(j) : rows[r], knuckles);
            final double y = knuckles ? lines.knuckle(j) : rows[r];
            ring[j] = b.add(onto(new V3(x, y, -5), 1, palm), "right hand, back " + r + ", " + j);
            ring[2 * n - 1 - j] = b.add(onto(new V3(x, y, 6), -1, palm), "right hand, palm " + r + ", " + j);
        }
        return ring;
    }

    /** The outside on the line from {@code local}, in the hand's frame, along its {@code z} as {@code sign} says. */
    private V3 onto(final V3 local, final int sign, final IntPredicate counts) {
        final V3 from = hand.place(local);
        final V3 way = hand.way(V3.Z.times(sign));
        return target.onto(from, way, counts, 11).map(FatTarget.Hit::at)
                .orElse(hand.place(new V3(local.x(), local.y(), -sign * 1.2)));
    }

    /** {@code d}'s rings along its bones, their lines starting {@code out} cm out. */
    private List<Limb.Ring> rings(final Digit d, final double out) {
        final List<Limb.Ring> rings = new ArrayList<>();
        for (final V3[] at : lines.stations(d)) {
            final V3 way = hand.way(at[1]).unit();
            final V3 palm = hand.way(V3.Z);
            final V3 w = palm.minus(way.times(palm.dot(way))).unit();
            rings.add(new Limb.Ring(hand.place(at[0]), way.cross(w).unit(), w, Double.POSITIVE_INFINITY, out,
                    "right " + d.said(), Part.RIGHT_HAND));
        }
        return rings;
    }
}
