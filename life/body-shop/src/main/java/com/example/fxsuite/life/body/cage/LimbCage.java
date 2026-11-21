package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Sculptor;
import com.example.fxsuite.life.body.flesh.Tube;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A limb's cage, hung from a loop of the torso's points where it goes out: its tubes' rings ({@link Tube}), each
 * sculpted over its bones as the tube's are, each with as many points as the loop, each at the same angle round its
 * bone as the loop's point it runs down from; four-sided faces between one ring and the next, the first ring's and the
 * loop's; its end shut by one face. On the right, as it is held to be bound.
 */
record LimbCage() implements StatelessFunctionalObject {

    static final LimbCage INSTANCE = new LimbCage();

    /**
     * A tube of a limb, its rings from the {@code from}th on, each ring {@code grow} times as thick as the tube's, its
     * middle moved in toward the body as far as {@code in} says for it, cm (none, past its end).
     */
    record Piece(Tube tube, int from, double grow, double... in) {

        V3 middle(final V3 m, final int i) {
            return i < in.length ? m.minus(new V3(in[i], 0, 0)) : m;
        }
    }

    /**
     * The limb through {@code pieces}' rings hung from {@code loop}, round it the way the torso's faces go along it,
     * held as {@code bind} holds it: each ring's points evenly round it, the first at the angle round the loop's middle
     * of its first point, and on round the way the loop goes.
     */
    void hang(final Building b, final int[] loop, final List<Piece> pieces, final Rig bind, final double female) {
        final Rig.Frame top = bind.frame(pieces.getFirst().tube().part());
        final int n = loop.length;
        V3 mid = V3.ZERO;
        for (final int p : loop) {
            mid = mid.plus(top.local(b.point(p)).times(1.0 / n));
        }
        final double[] round = new double[n];
        double turned = 0;
        for (int k = 0; k < n; k++) {
            final V3 u = top.local(b.point(loop[k])).minus(mid);
            round[k] = Math.toDegrees(Math.atan2(u.x(), u.z()));
        }
        for (int k = 0; k < n; k++) {
            turned += ((round[(k + 1) % n] - round[k]) % 360 + 540) % 360 - 180;
        }
        final double[] angle = new double[n];
        for (int k = 0; k < n; k++) {
            angle[k] = round[0] + Math.signum(turned) * 360.0 * k / n;
        }
        int[] above = loop.clone();
        for (final Piece piece : pieces) {
            final Sculptor.Outline o = Sculptor.INSTANCE.outline(piece.tube(), female);
            final Rig.Frame f = bind.frame(piece.tube().part());
            for (int i = piece.from(); i < o.middles().size(); i++) {
                final int[] ring = new int[n];
                final V3 m = piece.middle(o.middles().get(i), i);
                for (int k = 0; k < n; k++) {
                    ring[k] = b.add(f.place(at(m, o.middles().get(i), o.ends().get(i), angle[k], piece.grow())),
                            piece.tube().part().said() + ", " + piece.tube().rings().get(i).said() + ", "
                                    + Math.round(((angle[k] % 360) + 360) % 360) + "°");
                }
                for (int k = 0; k < n; k++) {
                    final int next = (k + 1) % n;
                    b.face(ring[k], ring[next], above[next], above[k]);
                }
                above = ring;
            }
        }
        b.face(reversed(above));
    }

    /**
     * Where a ring round {@code middle} through {@code ends} is, {@code degrees} round, between its spokes, smooth: its
     * middle moved to {@code moved}, {@code grow} times as far from it.
     */
    private V3 at(final V3 moved, final V3 middle, final V3[] ends, final double degrees, final double grow) {
        final int n = ends.length;
        final double[] phi = new double[n];
        final double[] rho = new double[n];
        for (int s = 0; s < n; s++) {
            final V3 d = ends[s].minus(middle);
            phi[s] = Math.toDegrees(Math.atan2(d.x(), d.z()));
            rho[s] = Math.hypot(d.x(), d.z());
        }
        int s = 0;
        double best = Double.MAX_VALUE;
        for (int q = 0; q < n; q++) {
            final double ahead = (degrees - phi[q] + 720) % 360;
            if (ahead < best) {
                best = ahead;
                s = q;
            }
        }
        final int t = (s + 1) % n;
        final double span = (phi[t] - phi[s] + 720) % 360;
        final double u = best / span;
        final double m1 = (rho[t] - rho[(s + n - 1) % n]) / 2;
        final double m2 = (rho[(t + 1) % n] - rho[s]) / 2;
        final double u2 = u * u;
        final double u3 = u2 * u;
        final double r = (2 * u3 - 3 * u2 + 1) * rho[s] + (u3 - 2 * u2 + u) * m1 + (-2 * u3 + 3 * u2) * rho[t]
                + (u3 - u2) * m2;
        final double a = Math.toRadians(degrees);
        return moved.plus(new V3(Math.sin(a) * r * grow, 0, Math.cos(a) * r * grow));
    }

    private int[] reversed(final int[] a) {
        final int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = a[a.length - 1 - i];
        }
        return r;
    }
}
