package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * How a rib is drawn, any rib, on the right in the body's frame: from its head at its vertebra's body, past its
 * tubercle on the transverse process, round its angle, out and down round the side to the front, round at its neck,
 * flat beyond, its flat face out (up, for the first); and its cartilage on from its end. A woman's narrower.
 */
public record Ribs() implements StatelessFunctionalObject {

    public static final Ribs INSTANCE = new Ribs();

    /** {@code rib}, as narrow and as light as said. */
    public void draw(final Sketch s, final Rib rib, final double narrow, final double light) {
        final List<V3> path = path(rib, narrow);
        final boolean first = rib == Rib.FIRST;
        final double r = (first ? 0.95 : rib.floating() ? 0.62 : 0.72) * light;
        s.blend(0.35);
        for (int k = 0; k + 1 < path.size(); k++) {
            final V3 a = path.get(k);
            final V3 b = path.get(k + 1);
            final double t = (double) k / (path.size() - 1);
            final double flat = t < 0.12 ? 1 : first ? 0.45 : 0.42;
            final V3 mid = a.plus(b).times(0.5);
            final V3 out = first ? V3.Y : v(mid.x(), 0, mid.z() - 1.0).unit();
            final double rr = r * (t < 0.12 ? 0.75 : 1.0 - 0.15 * t);
            s.flat(a, b, rr, rr, out, flat);
        }
        s.ball(path.getFirst(), r * 0.72);
    }

    /** {@code rib}'s cartilage, from its bone's end to what it reaches, the lower ones dipping before they turn up. */
    public void cartilage(final Sketch s, final Rib rib, final double narrow) {
        final V3 a = end(rib, narrow);
        final V3 b = rib.cartilage();
        final double r = rib == Rib.FIRST ? 0.75 : rib.floating() ? 0.4 : 0.55;
        final boolean lower = rib.ordinal() >= Rib.FIFTH.ordinal() && !rib.floating();
        final V3 mid = lower
                ? v((a.x() + b.x()) * 0.5, Math.min(a.y(), b.y()) - 0.6 - 0.15 * rib.ordinal(),
                        Math.max(a.z(), b.z()) + 0.35)
                : a.plus(b).times(0.5).plus(v(0, 0, 0.25));
        s.blend(0.45).tube(r, r * 0.9, a, mid, b);
    }

    /** {@code rib}'s line, from its head to the end of its bone, smooth through its points. */
    private List<V3> path(final Rib rib, final double narrow) {
        final Level l = rib.vertebra();
        final V3 head = l.at(l.wide() / 2 + 0.25, l.high() * 0.35, -l.deep() * 0.2);
        final V3 tip = l.transverseTip();
        final V3 end = end(rib, narrow);
        final List<V3> keys = new ArrayList<>();
        keys.add(head);
        keys.add(rib.floating() ? v(tip.x() * 0.9, tip.y() - 0.4, tip.z() + 0.3) : tip.plus(v(0.15, -0.2, 0.25)));
        keys.add(v(tip.x() + 1.6 + 0.16 * rib.ordinal(), tip.y() - 0.8, tip.z() - 0.25));
        keys.add(v(rib.reach() * narrow, (tip.y() + end.y()) / 2 - 0.5, rib.widestAhead()));
        keys.add(end);
        return Sketch.smooth(keys, 28);
    }

    private V3 end(final Rib rib, final double narrow) {
        return v(rib.end().x() * narrow, rib.end().y(), rib.end().z());
    }
}
