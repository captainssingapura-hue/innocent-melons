package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A rib's line, on the right in the body's frame, from its measures ({@link Rib}) as Holcombe, Wang and Grotberg set
 * a rib in its place (J Anat 2017, 231:229): hung from its head straight down, its outer side out, its line from end to
 * end turned up ahead as far as said, swung out from the middle, then turned about itself, its outer side up. In its
 * own plane, from its head at the angle it leaves it at, round to where it is furthest out of its line, along it
 * there; then on round as a circle's arc to its end, which puts its curve where they found it, about 5 cm across round
 * the back, 15 cm along the side. Their own curves, spirals, are in a paper not read.
 */
public record RibLine() implements StatelessFunctionalObject {

    public static final RibLine INSTANCE = new RibLine();

    /** How many points round to where it is furthest out of its line, and from there to its end. */
    private static final int BACK = 20;
    private static final int ON = 30;

    /** {@code rib}'s line, from its head to the end of its bone, as female as said. */
    public List<V3> line(final Rib rib, final double female) {
        final V3[] f = frame(rib, female);
        final double length = rib.chord(female);
        final double x = rib.along();
        final double y = rib.peak(female);
        final double bow = y / Math.tan(Math.toRadians(rib.leaves()));
        final double r = ((1 - x) * (1 - x) + y * y) / (2 * y);
        final double sweep = Math.atan2(1 - x, r - y);
        final List<V3> line = new ArrayList<>();
        for (int k = 0; k <= BACK; k++) {
            final double t = (double) k / BACK;
            line.add(at(f, length, 2 * (1 - t) * t * bow + t * t * x, (2 * (1 - t) * t + t * t) * y));
        }
        for (int k = 1; k <= ON; k++) {
            final double a = sweep * k / ON;
            line.add(at(f, length, x + r * Math.sin(a), y - r + r * Math.cos(a)));
        }
        return line;
    }

    /** Where {@code rib}'s bone ends in front, as female as said. */
    public V3 end(final Rib rib, final double female) {
        final V3[] f = frame(rib, female);
        return f[0].plus(f[1].times(rib.chord(female)));
    }

    private V3 at(final V3[] f, final double length, final double u, final double v) {
        return f[0].plus(f[1].times(u * length)).plus(f[2].times(v * length));
    }

    /** {@code rib}'s head; its line from end to end; out of that line, the way it reaches out. */
    private V3[] frame(final Rib rib, final double female) {
        final double up = Math.toRadians(rib.pump(female));
        final double out = Math.toRadians(rib.swing());
        final double turn = Math.toRadians(rib.turn());
        final V3 along = new V3(Math.sin(up) * Math.sin(out), -Math.cos(up), Math.sin(up) * Math.cos(out));
        final V3 side = new V3(Math.cos(out), 0, -Math.sin(out));
        final V3 reach = side.times(Math.cos(turn)).plus(along.cross(side).times(Math.sin(turn)));
        return new V3[] {rib.head(), along, reach};
    }
}
