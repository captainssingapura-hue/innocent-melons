package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.head.Crossings;

/**
 * Muscles as walls round a region, as the bones are: those held round it, and those of its layers already come to rest,
 * so a layer is laid on them as on bone, however deep it began in them. Each muscle added marked on a grid over the
 * region, which of its points are inside it ({@link Crossings}); and how far each point is from the edge of all marked,
 * outside or in, found afresh ({@link Chamfer}), and kept as {@link SampledWalls}.
 */
public final class MuscleWalls implements Walls {

    /** How far beyond the region the grid reaches, cm. */
    private static final double BEYOND = 1.0;
    /** How far any point is, at first, in steps of the grid: further than any can be. */
    private static final float FAR = 1e6f;

    private final SampledWalls grid;
    private final boolean[] inside;

    /** None yet, on a grid {@code cell} apart over {@code region}. */
    public MuscleWalls(final SoftRegion region, final double cell) {
        grid = new SampledWalls(region, cell, BEYOND);
        final int[] n = grid.counts();
        inside = new boolean[n[0] * n[1] * n[2]];
        for (int i = 0; i < inside.length; i++) {
            grid.set(i, FAR * cell);
        }
    }

    /** {@code surface}, a muscle as it lies now, made a wall too. */
    public void add(final Mesher.Mesh surface) {
        final boolean[] in = Crossings.INSTANCE.inside(surface, grid.lowest(), grid.cell(), grid.counts());
        boolean any = false;
        for (int i = 0; i < in.length; i++) {
            any |= in[i] && !inside[i];
            inside[i] |= in[i];
        }
        if (any) {
            measured();
        }
    }

    /** How far each point is from the edge of all marked: outside, from the nearest inside, and inside, the other way. */
    private void measured() {
        final boolean[] outside = new boolean[inside.length];
        for (int i = 0; i < outside.length; i++) {
            outside[i] = !inside[i];
        }
        final float[] fromIn = Chamfer.INSTANCE.from(inside, grid.counts());
        final float[] fromOut = Chamfer.INSTANCE.from(outside, grid.counts());
        final double cell = grid.cell();
        for (int i = 0; i < inside.length; i++) {
            grid.set(i, inside[i] ? -(fromOut[i] - 0.5) * cell : (fromIn[i] - 0.5) * cell);
        }
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
