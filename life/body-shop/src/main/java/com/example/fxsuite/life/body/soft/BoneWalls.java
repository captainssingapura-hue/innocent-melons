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
 * own fields ({@link PartBones#shape}) as the body is bound, and read between the grid's points ({@link SampledWalls}).
 * A point far from every bone reads as far as the grid's reach.
 */
public final class BoneWalls implements Walls {

    /** How far beyond the region the grid reaches, cm. */
    private static final double BEYOND = 1.0;
    /** How far a bone is counted, cm: further, and it is as far as this. */
    private static final double REACH = 3.0;

    private final SampledWalls grid;

    /** The bones of the body {@code rig} holds, round {@code region}, on a grid {@code cell} apart. */
    public BoneWalls(final SoftRegion region, final Rig rig, final double cell) {
        grid = new SampledWalls(region, cell, BEYOND);
        final int[] n = grid.counts();
        final List<Placed> near = near(rig);
        IntStream.range(0, n[0] * n[1] * n[2]).parallel().forEach(i -> grid.set(i, nearest(near, grid.point(i))));
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

    @Override
    public double distance(final double x, final double y, final double z) {
        return grid.distance(x, y, z);
    }

    @Override
    public V3 out(final double x, final double y, final double z) {
        return grid.out(x, y, z);
    }
}
