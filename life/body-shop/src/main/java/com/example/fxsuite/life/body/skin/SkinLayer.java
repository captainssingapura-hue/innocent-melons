package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.FatLayer;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Underneath;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.stream.IntStream;

/**
 * A body's skin over its fat, every body's the same points and faces as its fat ({@link FatLayer}): each point out
 * from the fat's by as thick as the skin is; then drawn taut, so many times as its tension says, each point moved half
 * way toward its neighbours' middle, and held off the muscles and the bones beneath ({@link FatTarget}) by as thick as
 * the skin is and {@link #KEPT} of the fat that should be there: the fat soft, pressed thinner over what bulges and
 * gathered in the hollows between, so that the skin lies smooth over them, as stretched skin does, never on a muscle.
 * Each point following the body as the fat's under it does.
 */
public record SkinLayer() implements StatelessFunctionalObject {

    public static final SkinLayer INSTANCE = new SkinLayer();

    /** How much of the fat is kept under the skin, at least, however taut it is drawn over what bulges. */
    private static final double KEPT = 0.3;

    /** The skin over {@code fat}, found on {@code target}, {@code thick} cm, drawn taut {@code tension} times. */
    public Flesh of(final FatLayer.Fat fat, final FatTarget target, final double thick, final int tension) {
        final Flesh f = fat.flesh();
        final Beneath under = Underneath.INSTANCE.muscles(f);
        final int[] t = f.triangles();
        final int[][] next = Around.INSTANCE.neighbours(f.size(), t);
        final V3[] at = new V3[f.size()];
        for (int i = 0; i < at.length; i++) {
            at[i] = f.point(i);
        }
        final V3[] out = Around.INSTANCE.normals(at, t);
        for (int i = 0; i < at.length; i++) {
            at[i] = at[i].plus(out[i].times(thick));
        }
        for (int round = 0; round < tension; round++) {
            taut(at, next);
            hold(target, fat.regions(), at, t, thick);
        }
        final Sheet sheet = new Sheet();
        for (int i = 0; i < at.length; i++) {
            sheet.add(at[i], under.weights()[i]);
        }
        for (int i = 0; i < t.length; i += 3) {
            sheet.triangle(t[i], t[i + 1], t[i + 2]);
        }
        return sheet.flesh(target.bind());
    }

    /** Every point moved half way toward its neighbours' middle. */
    private void taut(final V3[] at, final int[][] next) {
        final V3[] moved = new V3[at.length];
        for (int i = 0; i < at.length; i++) {
            moved[i] = at[i].plus(Around.INSTANCE.middle(at, next[i]).minus(at[i]).times(0.5));
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }

    /**
     * Every point nearer the muscles and bones beneath than {@code thick} and {@link #KEPT} of the fat there set out
     * along its way out till it is not.
     */
    private void hold(final FatTarget target, final FatTarget.Region[] regions, final V3[] at, final int[] t,
                      final double thick) {
        final V3[] out = Around.INSTANCE.normals(at, t);
        IntStream.range(0, at.length).parallel().forEach(i -> {
            final double[] d = target.depth(at[i], regions[i], at[i].x() < 0 ? -1 : 1, 3);
            final double need = thick + KEPT * d[1];
            if (d[0] < need) {
                at[i] = at[i].plus(out[i].times(need - d[0]));
            }
        });
    }
}
