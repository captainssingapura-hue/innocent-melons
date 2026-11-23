package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.fat.FatCage;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The head's cage as the body's skin has it ({@link FatCage}): rings, level, as many points round as the body's,
 * the first ahead, going round to the right; four-sided faces between one ring and the next; shut over the crown by one
 * face; open at the neck, below. Each ring at first round the points of a surface near its height, an ellipse as wide
 * and as deep as they reach: where a fit of it starts. In the head's own frame, cm.
 */
public record HeadCage() implements StatelessFunctionalObject {

    public static final HeadCage INSTANCE = new HeadCage();

    /** How near a ring's height a surface's point is to count for where the ring starts, cm. */
    private static final double NEAR = 0.8;

    /** The cage of rings at {@code heights}, bottom to top, round the surface whose points are {@code around}. */
    public Cage of(final float[] around, final double[] heights) {
        final int n = FatCage.COLUMNS;
        final List<V3> points = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        double[] reach = {5, 5, -5};
        for (int r = 0; r < heights.length; r++) {
            reach = reach(around, heights[r], reach);
            final double mid = (reach[1] + reach[2]) / 2;
            final double deep = (reach[1] - reach[2]) / 2;
            for (int j = 0; j < n; j++) {
                final double a = 2 * Math.PI * j / n;
                points.add(new V3(reach[0] * Math.sin(a), heights[r], mid + deep * Math.cos(a)));
                names.add("head " + r + ", column " + j);
            }
        }
        final List<int[]> faces = new ArrayList<>();
        for (int r = 0; r + 1 < heights.length; r++) {
            for (int j = 0; j < n; j++) {
                final int k = (j + 1) % n;
                faces.add(new int[] {r * n + j, r * n + k, (r + 1) * n + k, (r + 1) * n + j});
            }
        }
        final int[] crown = new int[n];
        for (int j = 0; j < n; j++) {
            crown[j] = (heights.length - 1) * n + j;
        }
        faces.add(crown);
        return new Cage(points, names, faces, List.of());
    }

    /**
     * How far the points {@code around} near height {@code y} reach: out to the side, ahead, behind; {@code before},
     * the ring below's, if none are near it.
     */
    private double[] reach(final float[] around, final double y, final double[] before) {
        final double[] r = {0, -1e9, 1e9};
        for (int i = 0; i < around.length; i += 3) {
            if (Math.abs(around[i + 1] - y) < NEAR) {
                r[0] = Math.max(r[0], Math.abs(around[i]));
                r[1] = Math.max(r[1], around[i + 2]);
                r[2] = Math.min(r[2], around[i + 2]);
            }
        }
        return r[0] > 0 ? r : before;
    }
}
