package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The bones round a region to be simulated soft, as walls the muscles in it are kept out of: how far outside the
 * nearest bone each point of a grid over the region is, cm, less than nothing inside one, found once from the bones'
 * own fields ({@link PartBones#shape}) as the body is bound, and read between the grid's points by weighing the eight
 * about it. A point far from every bone reads as far as the grid's reach.
 */
public final class BoneWalls implements Walls {

    /** How far beyond the region the grid reaches, cm. */
    private static final double BEYOND = 1.0;
    /** How far a bone is counted, cm: further, and it is as far as this. */
    private static final double REACH = 3.0;

    private final double[] lo = new double[3];
    private final int[] n = new int[3];
    private final double cell;
    private final float[] far;

    /** The bones of the body {@code rig} holds, round {@code region}, on a grid {@code cell} apart. */
    public BoneWalls(final SoftRegion region, final Rig rig, final double cell) {
        this.cell = cell;
        final double[] l = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] h = {region.hi().x(), region.hi().y(), region.hi().z()};
        for (int c = 0; c < 3; c++) {
            lo[c] = l[c] - BEYOND;
            n[c] = (int) Math.ceil((h[c] - l[c] + 2 * BEYOND) / cell) + 1;
        }
        final List<Placed> near = near(rig);
        far = new float[n[0] * n[1] * n[2]];
        IntStream.range(0, n[2]).parallel().forEach(k -> {
            for (int j = 0; j < n[1]; j++) {
                for (int i = 0; i < n[0]; i++) {
                    far[(k * n[1] + j) * n[0] + i] = (float) nearest(near,
                            new V3(lo[0] + i * cell, lo[1] + j * cell, lo[2] + k * cell));
                }
            }
        });
    }

    /** A part's bones, in its own frame, and where the part is. */
    private record Placed(Field.Shape shape, Rig.Frame frame) {
    }

    /** Every part's bones with the part where {@code rig} holds it. */
    private List<Placed> near(final Rig rig) {
        final List<Placed> all = new ArrayList<>();
        for (final Part p : Part.values()) {
            all.add(new Placed(PartBones.INSTANCE.shape(p.side() < 0 ? p.other() : p), rig.frame(p)));
        }
        return all;
    }

    /** How far {@code v} is outside the nearest bone, no further than the reach. */
    private double nearest(final List<Placed> near, final V3 v) {
        double d = REACH;
        for (final Placed p : near) {
            final V3 local = p.frame().local(v);
            final double[] box = p.shape().box();
            if (local.x() < box[0] - REACH || local.y() < box[1] - REACH || local.z() < box[2] - REACH
                    || local.x() > box[3] + REACH || local.y() > box[4] + REACH || local.z() > box[5] + REACH) {
                continue;
            }
            d = Math.min(d, p.shape().at(local.x(), local.y(), local.z()) * p.frame().scale().x());
        }
        return d;
    }

    /** How far outside the nearest bone {@code x}, {@code y}, {@code z} is, cm; less than nothing inside one. */
    @Override
    public double distance(final double x, final double y, final double z) {
        final double u = Math.clamp((x - lo[0]) / cell, 0, n[0] - 1.0001);
        final double v = Math.clamp((y - lo[1]) / cell, 0, n[1] - 1.0001);
        final double w = Math.clamp((z - lo[2]) / cell, 0, n[2] - 1.0001);
        final int i = (int) u;
        final int j = (int) v;
        final int k = (int) w;
        final double a = u - i;
        final double b = v - j;
        final double c = w - k;
        double d = 0;
        for (int corner = 0; corner < 8; corner++) {
            final int di = corner & 1;
            final int dj = corner >> 1 & 1;
            final int dk = corner >> 2 & 1;
            d += far[((k + dk) * n[1] + j + dj) * n[0] + i + di] * (di == 1 ? a : 1 - a) * (dj == 1 ? b : 1 - b)
                    * (dk == 1 ? c : 1 - c);
        }
        return d;
    }

    /** The way out of the nearest bone at {@code x}, {@code y}, {@code z}, one long; none where it is level. */
    @Override
    public V3 out(final double x, final double y, final double z) {
        final double e = cell / 2;
        final V3 g = new V3(distance(x + e, y, z) - distance(x - e, y, z),
                distance(x, y + e, z) - distance(x, y - e, z),
                distance(x, y, z + e) - distance(x, y, z - e));
        final double length = g.length();
        return length < 1e-12 ? V3.ZERO : g.times(1 / length);
    }
}
