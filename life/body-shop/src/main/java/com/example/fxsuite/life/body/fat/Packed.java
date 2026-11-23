package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.muscle.MuscleSurface;

/**
 * One muscle being pressed into its place at rest ({@link Packing}): its points, moved as it is pressed, its tendon
 * staying where it is; how much of it there was, which it keeps, swelling where nothing presses on it.
 */
public final class Packed {

    /** How far through it a press reaches, cm, about as thick as a muscle is. */
    private static final double SLAB = 2;
    /** How far it swells or shrinks a round at most, cm; and at most this share of how big it is across. */
    private static final double SWELL = 0.3;
    private static final double SHARE = 0.1;
    /** How much of a muscle is too little to be pressed, cm3. */
    private static final double SMALL = 1;

    private final MuscleSurface.Surface surface;
    private final V3[] at;
    private final double volume;
    private final boolean[] pressed;

    /** {@code surface}, as it is drawn, to be pressed. */
    public Packed(final MuscleSurface.Surface surface) {
        this.surface = surface;
        final float[] p = surface.mesh().points();
        at = new V3[p.length / 3];
        for (int i = 0; i < at.length; i++) {
            at[i] = new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2]);
        }
        volume = volume();
        pressed = new boolean[at.length];
    }

    /** Where its points are now. */
    public V3[] at() {
        return at.clone();
    }

    /** Its triangles. */
    public int[] triangles() {
        return surface.mesh().triangles();
    }

    /** How deep it lies among the muscles about it, the outermost 1. */
    public int depth() {
        return surface.depth();
    }

    /** How far each of its points is tendon, which does not move. */
    public float[] tendon() {
        return surface.tendon();
    }

    /**
     * It pressed by {@code by}, each point's: each of its points moved as far as the furthest pressed of its points
     * within {@link #SLAB} of it, less as that is further off, so that it moves as a slab, the flesh over a point
     * pressed out going out with it, rather than folding over what pressed it; its tendon not moved.
     */
    public void press(final V3[] by) {
        final V3[] p = new V3[at.length];
        for (int i = 0; i < at.length; i++) {
            p[i] = V3.ZERO;
            for (int j = 0; j < at.length; j++) {
                if (by[j].length() > 0) {
                    final double d = at[i].minus(at[j]).length();
                    final V3 q = by[j].times(Math.max(0, 1 - d / SLAB));
                    p[i] = q.length() > p[i].length() ? q : p[i];
                }
            }
        }
        for (int i = 0; i < at.length; i++) {
            pressed[i] = p[i].length() > 1e-4;
            at[i] = at[i].plus(p[i].times(1 - surface.tendon()[i]));
        }
    }

    /** It swollen or shrunk where nothing presses on it, toward how much of it there was. */
    public void keep() {
        final V3[] out = Around.INSTANCE.normals(at, triangles());
        final double[] area = new double[at.length];
        final int[] t = triangles();
        for (int i = 0; i < t.length; i += 3) {
            final double a = at[t[i + 1]].minus(at[t[i]]).cross(at[t[i + 2]].minus(at[t[i]])).length() / 6;
            for (int k = 0; k < 3; k++) {
                area[t[i + k]] += a;
            }
        }
        double free = 0;
        for (int i = 0; i < at.length; i++) {
            free += pressed[i] ? 0 : area[i] * (1 - surface.tendon()[i]);
        }
        if (free <= 0) {
            return;
        }
        final double most = Math.min(SWELL, SHARE * Math.cbrt(volume));
        final double by = Math.max(-most, Math.min(most, (volume - volume()) / free));
        for (int i = 0; i < at.length; i++) {
            if (!pressed[i]) {
                at[i] = at[i].plus(out[i].times(by * (1 - surface.tendon()[i])));
            }
        }
    }

    /**
     * Whether {@code p} is inside it: the solid angle its surface fills, seen from there, over a whole sphere's, more
     * than half (van Oosterom and Strackee's angle of each triangle), true of a closed surface however it folds.
     */
    public boolean inside(final V3 p) {
        final int[] t = triangles();
        double sum = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = at[t[i]].minus(p);
            final V3 b = at[t[i + 1]].minus(p);
            final V3 c = at[t[i + 2]].minus(p);
            final double la = a.length();
            final double lb = b.length();
            final double lc = c.length();
            final double over = la * lb * lc + a.dot(b) * lc + b.dot(c) * la + c.dot(a) * lb;
            sum += 2 * Math.atan2(a.dot(b.cross(c)), over);
        }
        return sum / (4 * Math.PI) > 0.5;
    }

    /** Whether it is too small to be pressed, under {@link #SMALL}: as it is drawn, though others are pressed off it. */
    public boolean small() {
        return volume < SMALL;
    }

    /** How much of it there is, cm3. */
    public double volume() {
        final int[] t = triangles();
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            v += at[t[i]].dot(at[t[i + 1]].cross(at[t[i + 2]])) / 6;
        }
        return v;
    }

    /** How much of it there was, as it was drawn, cm3. */
    public double drawn() {
        return volume;
    }

    /** It as it is now, a muscle's surface. */
    public MuscleSurface.Surface surface() {
        final float[] p = new float[3 * at.length];
        for (int i = 0; i < at.length; i++) {
            p[3 * i] = (float) at[i].x();
            p[3 * i + 1] = (float) at[i].y();
            p[3 * i + 2] = (float) at[i].z();
        }
        return new MuscleSurface.Surface(new Mesher.Mesh(p, triangles()), surface.tendon(), surface.parts(),
                surface.shares(), surface.depth());
    }
}
