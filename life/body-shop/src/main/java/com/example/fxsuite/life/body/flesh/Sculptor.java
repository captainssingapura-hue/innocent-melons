package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Moved;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Puppet;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A tube's surface ({@link Tube}), sculpted over its part's bones: each ring's spokes reaching so far from its middle,
 * or so far beyond the bone that way, found by looking out along the spoke for where the bones' surface is (the last of
 * it within reach), and never less than a little beyond it; a smooth closed curve round each ring through its spokes'
 * ends, and a smooth curve from ring to ring (both Catmull-Rom's); each end rounded shut. Its triangles about as far
 * apart as the detail asked says for the part ({@link Detail}), facing out. In the part's frame as on the right, as its
 * bones are, a forearm's twisted as far as the puppet's are at rest.
 */
public record Sculptor() implements StatelessFunctionalObject {

    public static final Sculptor INSTANCE = new Sculptor();

    /** How far beyond a bone a free spoke's flesh reaches at least, cm. */
    private static final double COVER = 0.4;
    /** How far out a spoke looks for its bone's surface, and in what steps, cm. */
    private static final double FAR = 15;
    private static final double STEP = 0.05;

    /** A tube's rings as sculpted: each one's middle, and where its spokes end, round from straight ahead. */
    public record Outline(List<V3> middles, List<V3[]> ends) {
    }

    /** Rows round a tube, each a closed curve of as many points, and each row's middle. */
    public record Rows(List<V3[]> rows, List<V3> middles) {
    }

    /** {@code tube}'s surface, as finely as {@code detail} says, over bones as far a woman's as {@code female} says. */
    public Mesher.Mesh mesh(final Tube tube, final Detail detail, final double female) {
        final Outline o = outline(tube, female);
        final Rows rows = rows(o, detail.cell(tube.part()));
        final List<V3> m = o.middles();
        return surface(rows.rows(), m.getFirst(), m.get(1), m.getLast(), m.get(m.size() - 2));
    }

    /**
     * {@code tube}'s rings, sculpted over its bones as far a woman's as {@code female} says: their middles and their
     * spokes' ends, in its part's frame as on the right, a forearm's twisted as far as the puppet's are at rest.
     */
    public Outline outline(final Tube tube, final double female) {
        final Field.Shape bones = bones(tube, female);
        final List<V3[]> ends = new ArrayList<>();
        final List<V3> middles = new ArrayList<>();
        for (final Ring r : tube.rings()) {
            final V3 m = twisted(r.middle(), r);
            middles.add(m);
            ends.add(ends(r, m, bones));
        }
        return new Outline(middles, ends);
    }

    /**
     * Rows round {@code o}'s rings and between them, smooth round each and from each to the next, about {@code cell}
     * apart both ways; each row's middle with it.
     */
    public Rows rows(final Outline o, final double cell) {
        double reach = 0;
        for (int i = 0; i < o.ends().size(); i++) {
            reach = Math.max(reach, radius(o.ends().get(i), o.middles().get(i)));
        }
        final int around = (int) Math.max(12, Math.min(160, Math.round(2 * Math.PI * reach / cell)));
        final List<V3[]> rings = new ArrayList<>();
        o.ends().forEach(e -> rings.add(round(e, around)));
        final int[] steps = new int[o.middles().size()];
        final List<V3[]> middles = new ArrayList<>();
        for (int i = 0; i < steps.length; i++) {
            middles.add(new V3[] {o.middles().get(i)});
            if (i + 1 < steps.length) {
                final double apart = o.middles().get(i).minus(o.middles().get(i + 1)).length();
                steps[i] = (int) Math.max(1, Math.min(40, Math.ceil(apart / cell)));
            }
        }
        final List<V3> along = new ArrayList<>();
        lengthwise(middles, steps).forEach(m -> along.add(m[0]));
        return new Rows(lengthwise(rings, steps), along);
    }

    /** The bones {@code tube} is laid over: its part's, and those of the parts it covers on both sides, at rest. */
    private Field.Shape bones(final Tube tube, final double female) {
        final List<Field.Element> all = new ArrayList<>();
        all.add(new Field.Element(tube.said(), PartBones.INSTANCE.shape(tube.part(), female), Field.Op.ADD, 0));
        final Rig rest = new Rig(Proportions.DRAWN, Pose.REST.unturned(), false);
        for (final Part c : tube.covers()) {
            for (final Part side : c.side() == 0 ? List.of(c) : List.of(c, c.other())) {
                final V3 at = rest.frame(side).at().minus(rest.frame(tube.part()).at());
                all.add(new Field.Element(side.said(), new Moved(PartBones.INSTANCE.shape(c, female), Rot.NONE, at,
                        side.side() < 0, 0), Field.Op.ADD, 0));
            }
        }
        return Field.Compound.of(all);
    }

    /** Where {@code r}'s spokes end, round from straight ahead, its middle {@code m}. */
    private V3[] ends(final Ring r, final V3 m, final Field.Shape bones) {
        final V3[] out = new V3[r.spokes().size()];
        for (int k = 0; k < out.length; k++) {
            final Spoke s = r.spokes().get(k);
            final double a = Math.toRadians(s.angle());
            final V3 way = twisted(r.middle().plus(new V3(Math.sin(a), 0, Math.cos(a))), r).minus(m).unit();
            final double bone = boneOut(bones, m, way);
            final double reach = s.held() == Spoke.Held.ON_BONE ? bone + s.reach() : Math.max(s.reach(), bone + COVER);
            out[k] = m.plus(way.times(reach));
        }
        return out;
    }

    /** How far out from {@code from} along {@code way} the bones' surface last is within reach; none, if no bone. */
    private double boneOut(final Field.Shape bones, final V3 from, final V3 way) {
        double last = 0;
        for (double t = 0; t <= FAR; t += STEP) {
            final V3 p = from.plus(way.times(t));
            if (bones.at(p.x(), p.y(), p.z()) < 0) {
                last = t;
            }
        }
        return last;
    }

    /** {@code p} twisted as far of the puppet's forearm twist as {@code r} takes. */
    private V3 twisted(final V3 p, final Ring r) {
        return r.twist() == 0 ? p : Rig.twisted(p, Puppet.TWIST, r.twist());
    }

    /** A closed smooth curve through {@code ends}, {@code n} points round it. */
    private V3[] round(final V3[] ends, final int n) {
        final int k = ends.length;
        final V3[] out = new V3[n];
        for (int s = 0; s < n; s++) {
            final double t = (double) s * k / n;
            final int i = (int) Math.floor(t);
            out[s] = smooth(ends[(i - 1 + k) % k], ends[i % k], ends[(i + 1) % k], ends[(i + 2) % k], t - i);
        }
        return out;
    }

    /** Rows between the rings', smooth from ring to ring, {@code steps[i]} from ring {@code i} to the next. */
    private List<V3[]> lengthwise(final List<V3[]> rings, final int[] stepsFrom) {
        final List<V3[]> out = new ArrayList<>();
        final int n = rings.size();
        final int around = rings.getFirst().length;
        for (int i = 0; i + 1 < n; i++) {
            final V3[] a = rings.get(i);
            final V3[] b = rings.get(i + 1);
            final int steps = stepsFrom[i];
            for (int s = 0; s < steps; s++) {
                final V3[] row = new V3[around];
                for (int j = 0; j < around; j++) {
                    final V3 before = i > 0 ? rings.get(i - 1)[j] : a[j].times(2).minus(b[j]);
                    final V3 after = i + 2 < n ? rings.get(i + 2)[j] : b[j].times(2).minus(a[j]);
                    row[j] = smooth(before, a[j], b[j], after, (double) s / steps);
                }
                out.add(row);
            }
        }
        out.add(rings.getLast());
        return out;
    }

    /** The surface through {@code rows}, top to bottom, each end rounded shut beyond its middle. */
    private Mesher.Mesh surface(final List<V3[]> rows, final V3 top, final V3 belowTop, final V3 bottom,
                                final V3 aboveBottom) {
        final List<V3[]> all = new ArrayList<>();
        all.add(cap(rows.getFirst(), top, top.minus(belowTop).unit()));
        all.addAll(rows);
        all.add(cap(rows.getLast(), bottom, bottom.minus(aboveBottom).unit()));
        final int around = rows.getFirst().length;
        final List<V3> points = new ArrayList<>();
        all.forEach(r -> points.addAll(List.of(r)));
        final V3 topPole = top.plus(top.minus(belowTop).unit().times(0.6 * radius(rows.getFirst(), top)));
        final V3 bottomPole = bottom.plus(bottom.minus(aboveBottom).unit().times(0.6 * radius(rows.getLast(), bottom)));
        points.add(topPole);
        points.add(bottomPole);
        final List<Integer> t = new ArrayList<>();
        final int last = all.size() - 1;
        for (int j = 0; j < around; j++) {
            final int k = (j + 1) % around;
            t.addAll(List.of(j, k, points.size() - 2));
            t.addAll(List.of(last * around + j, points.size() - 1, last * around + k));
            for (int i = 0; i < last; i++) {
                final int a = i * around + j;
                final int b = (i + 1) * around + j;
                final int c = i * around + k;
                final int d = (i + 1) * around + k;
                t.addAll(List.of(a, b, c, b, d, c));
            }
        }
        return mesh(points, t);
    }

    /** A row closing {@code row} in toward its middle {@code m}, a little beyond it along {@code way}. */
    private V3[] cap(final V3[] row, final V3 m, final V3 way) {
        final double r = radius(row, m);
        final V3[] out = new V3[row.length];
        for (int j = 0; j < row.length; j++) {
            out[j] = m.plus(row[j].minus(m).times(0.65)).plus(way.times(0.45 * r));
        }
        return out;
    }

    /** How far {@code row} is from {@code m}, on the whole. */
    private double radius(final V3[] row, final V3 m) {
        double sum = 0;
        for (final V3 p : row) {
            sum += p.minus(m).length();
        }
        return sum / row.length;
    }

    /** {@code points} and {@code triangles} a surface, turned to face out if its rings were given bottom first. */
    private Mesher.Mesh mesh(final List<V3> points, final List<Integer> triangles) {
        final float[] p = new float[points.size() * 3];
        for (int i = 0; i < points.size(); i++) {
            p[3 * i] = (float) points.get(i).x();
            p[3 * i + 1] = (float) points.get(i).y();
            p[3 * i + 2] = (float) points.get(i).z();
        }
        final int[] t = triangles.stream().mapToInt(Integer::intValue).toArray();
        double volume = 0;
        for (int i = 0; i < t.length; i += 3) {
            volume += points.get(t[i]).dot(points.get(t[i + 1]).cross(points.get(t[i + 2])));
        }
        if (volume < 0) {
            for (int i = 0; i < t.length; i += 3) {
                final int b = t[i + 1];
                t[i + 1] = t[i + 2];
                t[i + 2] = b;
            }
        }
        return new Mesher.Mesh(p, t);
    }

    /** Catmull-Rom's curve from {@code b} to {@code c}, {@code a} before and {@code d} after, {@code t} of the way. */
    private V3 smooth(final V3 a, final V3 b, final V3 c, final V3 d, final double t) {
        final double t2 = t * t;
        final double t3 = t2 * t;
        return a.times(-0.5 * t3 + t2 - 0.5 * t).plus(b.times(1.5 * t3 - 2.5 * t2 + 1))
                .plus(c.times(-1.5 * t3 + 2 * t2 + 0.5 * t)).plus(d.times(0.5 * t3 - 0.5 * t2));
    }
}
