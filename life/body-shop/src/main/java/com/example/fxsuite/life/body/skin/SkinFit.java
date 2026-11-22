package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Subdivision;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.fat.Wrap;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Spread;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.stream.IntStream;

/**
 * A body's skin found over its muscles and bones at once, the fat under it as how thick it is, not a layer of its own
 * ({@link com.example.fxsuite.life.body.fat.FatMap}): wrapped round where the skin's outside would be, the fat and
 * the skin as thick as they are over what is beneath, its cage smoothed twice ({@link Wrap}); then drawn taut, so
 * many times as its tension says, each point moved half way toward its neighbours' middle, then a third of the way
 * back to lying as far out as the fat and the skin are thick, and never nearer than the skin and {@link #KEPT} of the
 * fat; the fat soft, pressed thinner over what bulges, gathered in the hollows. Each point moved as far as it is free:
 * the hands' and the feet's held as their cage makes them, the fingers and the toes too thin to be drawn taut, the
 * wrists and the insteps between, partly. How each point follows the body
 * spread along it ({@link Spread}); smoothed once more to be drawn.
 */
public record SkinFit() implements StatelessFunctionalObject {

    public static final SkinFit INSTANCE = new SkinFit();

    /** How many times the cage is smoothed to be fitted, how much of the fat is kept under the skin at least. */
    private static final int STEPS = 2;
    private static final double KEPT = 0.3;
    private static final double BACK = 3;
    private static final double FAR = 4;
    private static final int SPREADING = 20;

    /**
     * The skin over what {@code target} finds, its map's {@code over} the skin's thickness, drawn taut {@code tension}
     * times.
     */
    public Flesh of(final FatTarget target, final double skin, final int tension) {
        return of(target, skin, tension, -1);
    }

    /**
     * As {@link #of(FatTarget, double, int)}; then, if {@code base} is not negative, taken for a scan, a smooth
     * surface fitted to it ({@link ScanFit}), its base the cage smoothed {@code base} times, its points following the
     * body as the scan's do.
     */
    public Flesh of(final FatTarget target, final double skin, final int tension, final int base) {
        final Wrap.Shape shape = Wrap.INSTANCE.start(target, STEPS);
        final Flesh scan = finer(shape, taut(target, shape, skin, tension), target);
        if (base < 0) {
            return scan;
        }
        final Subdivision s = Subdivision.INSTANCE;
        final V3[] points = new V3[scan.size()];
        for (int i = 0; i < points.length; i++) {
            points[i] = scan.point(i);
        }
        return scan.reshaped(ScanFit.INSTANCE.fitted(s.times(s.of(shape.cage(), 1), base), STEPS + 1 - base, points));
    }

    /**
     * As {@link #of(FatTarget, double, int)}, but not smoothed once more to be drawn: a quarter the points, as many as
     * the muscles have, for a body drawn as it moves, each point moved afresh every frame.
     */
    public Flesh coarse(final FatTarget target, final double skin, final int tension) {
        final Wrap.Shape shape = Wrap.INSTANCE.start(target, STEPS);
        final Weights[] w = taut(target, shape, skin, tension);
        final Sheet sheet = new Sheet();
        for (int i = 0; i < w.length; i++) {
            sheet.add(shape.at()[i], w[i]);
        }
        final int[] t = shape.triangles();
        for (int i = 0; i < t.length; i += 3) {
            sheet.triangle(t[i], t[i + 1], t[i + 2]);
        }
        final int[] of = Subdivision.INSTANCE.triangleFaces(shape.level());
        final Part[] patches = new Part[of.length];
        for (int i = 0; i < of.length; i++) {
            patches[i] = shape.parts()[of[i]];
        }
        return sheet.flesh(target.bind()).patched(patches);
    }

    /**
     * {@code shape}'s points set on what {@code target} finds, then drawn taut {@code tension} times, in place; how
     * each follows the body, spread along it.
     */
    private Weights[] taut(final FatTarget target, final Wrap.Shape shape, final double skin, final int tension) {
        final V3[] at = shape.at();
        final Weights[] w = new Weights[at.length];
        Wrap.INSTANCE.onto(target, shape, at.clone(), w, BACK, FAR);
        final int[][] next = Around.INSTANCE.neighbours(at.length, shape.triangles());
        for (int round = 0; round < tension; round++) {
            taut(at, next, shape.held());
            settle(target, shape, skin);
        }
        return Spread.INSTANCE.over(w, next, SPREADING);
    }

    /** Every point moved half way toward its neighbours' middle, as far as it is free. */
    private void taut(final V3[] at, final int[][] next, final double[] held) {
        final V3[] moved = new V3[at.length];
        for (int i = 0; i < at.length; i++) {
            moved[i] = at[i].plus(Around.INSTANCE.middle(at, next[i]).minus(at[i]).times(0.5 * (1 - held[i])));
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }

    /**
     * Every point moved a third of the way to lying as far out as the fat and the skin are thick, and set out to no
     * nearer than the skin and {@link #KEPT} of the fat; each as far as it is free.
     */
    private void settle(final FatTarget target, final Wrap.Shape s, final double skin) {
        final V3[] at = s.at();
        final V3[] out = Around.INSTANCE.normals(at, s.triangles());
        IntStream.range(0, at.length).parallel().filter(i -> s.held()[i] == 0).forEach(i -> {
            final double[] d = target.depth(at[i], s.regions()[i], at[i].x() < 0 ? -1 : 1, 3);
            final double floor = skin + KEPT * (d[1] - skin);
            final double toward = (d[1] - d[0]) / 3;
            final double by = Math.max(toward, floor - d[0]);
            at[i] = at[i].plus(out[i].times(by));
        });
    }

    /**
     * The skin smoothed once more to be drawn, how each point follows the body with it; bound to the body, each
     * triangle of the part its piece of the cage is.
     */
    private Flesh finer(final Wrap.Shape s, final Weights[] w, final FatTarget target) {
        final Subdivision sub = Subdivision.INSTANCE;
        final Subdivision.Level fine = sub.once(new Subdivision.Level(flat(s.at()), s.level().faces(),
                s.level().sharp()));
        final int parts = Part.values().length;
        final float[][] by = new float[fine.size()][parts];
        for (int c = 0; c < parts; c += 3) {
            final double[] channel = new double[3 * w.length];
            for (int i = 0; i < w.length; i++) {
                for (int k = 0; k < 3 && c + k < parts; k++) {
                    channel[3 * i + k] = w[i].by()[c + k];
                }
            }
            final Subdivision.Level f = sub.once(new Subdivision.Level(channel, s.level().faces(), s.level().sharp()));
            for (int i = 0; i < fine.size(); i++) {
                for (int k = 0; k < 3 && c + k < parts; k++) {
                    by[i][c + k] = (float) f.points()[3 * i + k];
                }
            }
        }
        final Sheet sheet = new Sheet();
        for (int i = 0; i < fine.size(); i++) {
            sheet.add(fine.point(i), new Weights(by[i]));
        }
        final int[] t = sub.mesh(fine).triangles();
        for (int i = 0; i < t.length; i += 3) {
            sheet.triangle(t[i], t[i + 1], t[i + 2]);
        }
        final Part[] faces = Wrap.INSTANCE.carried(s.parts(), sub.parents(s.level()));
        final int[] of = sub.triangleFaces(fine);
        final Part[] patches = new Part[of.length];
        for (int i = 0; i < of.length; i++) {
            patches[i] = faces[of[i]];
        }
        return sheet.flesh(target.bind()).patched(patches);
    }

    private double[] flat(final V3[] at) {
        final double[] p = new double[3 * at.length];
        for (int i = 0; i < at.length; i++) {
            p[3 * i] = at[i].x();
            p[3 * i + 1] = at[i].y();
            p[3 * i + 2] = at[i].z();
        }
        return p;
    }
}
