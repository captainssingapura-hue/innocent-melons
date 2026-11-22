package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * How a rib is drawn, any rib, on the right in the body's frame: along its line ({@link RibLine}), from its head at
 * its vertebra's body round to the front, round at its neck, flat beyond, its flat face out, square to it, half as thick
 * as it is tall (up, for the first), the floating two less flat; and its cartilage on from its end. A woman's as her
 * ribs are measured.
 */
public record Ribs() implements StatelessFunctionalObject {

    public static final Ribs INSTANCE = new Ribs();

    /** {@code rib}, as female and as light as said. */
    public void draw(final Sketch s, final Rib rib, final double female, final double light) {
        final List<V3> path = RibLine.INSTANCE.line(rib, female);
        final boolean first = rib == Rib.FIRST;
        final double r = (first ? 0.95 : rib.floating() ? 0.62 : 0.72) * light;
        s.blend(0.35);
        for (int k = 0; k + 1 < path.size(); k++) {
            final V3 a = path.get(k);
            final V3 b = path.get(k + 1);
            final double t = (double) k / (path.size() - 1);
            final double flat = t < 0.12 ? 1 : first ? 0.45 : rib.floating() ? 0.55 : 0.5;
            final V3 mid = a.plus(b).times(0.5);
            final V3 way = b.minus(a).unit();
            final V3 round = v(mid.x(), 0, mid.z() - 1.0).unit();
            final V3 out = first ? V3.Y : round.minus(way.times(round.dot(way))).unit();
            final double rr = r * (t < 0.12 ? 0.75 : 1.0 - 0.15 * t);
            s.flat(a, b, rr, rr, out, flat);
        }
        s.ball(path.getFirst(), r * 0.72);
    }

    /** {@code rib}'s cartilage, from its bone's end to what it reaches, the lower ones dipping before they turn up. */
    public void cartilage(final Sketch s, final Rib rib, final double female) {
        final V3 a = RibLine.INSTANCE.end(rib, female);
        final V3 b = rib.cartilage();
        final double r = rib == Rib.FIRST ? 0.75 : rib.floating() ? 0.4 : 0.55;
        final boolean lower = rib.ordinal() >= Rib.FIFTH.ordinal() && !rib.floating();
        final V3 mid = lower
                ? v((a.x() + b.x()) * 0.5, Math.min(a.y(), b.y()) - 0.6 - 0.15 * rib.ordinal(),
                        Math.max(a.z(), b.z()) + 0.35)
                : a.plus(b).times(0.5).plus(v(0, 0, 0.25));
        s.blend(0.45).tube(r, r * 0.9, a, mid, b);
    }
}
