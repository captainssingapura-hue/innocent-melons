package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A cage moved so that the surface smoothed from it passes through its points as they were drawn: smoothing draws a
 * surface in from its cage, nearer its middle where it bulges, so each point is set out again by as far as the smoothed
 * surface fell short of it, and again, a few times. So the points of a cage are drawn where the skin is, and the cage
 * found from them. Each point's place on the smooth surface taken as where it is after a few steps of smoothing, near
 * enough where it would end.
 */
public record Fitting() implements StatelessFunctionalObject {

    public static final Fitting INSTANCE = new Fitting();

    /** How many times the cage is set out again, and how many steps of smoothing each is judged by. */
    private static final int ROUNDS = 8;
    private static final int STEPS = 3;

    /** {@code cage} moved so that its surface, smoothed, passes through its points, creased as {@code definition}. */
    public Cage fitted(final Cage cage, final double definition) {
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level drawn = s.of(cage, definition);
        final double[] want = drawn.points();
        final double[] at = want.clone();
        for (int round = 0; round < ROUNDS; round++) {
            final Subdivision.Level smooth = s.times(new Subdivision.Level(at, drawn.faces(), drawn.sharp()), STEPS);
            for (int i = 0; i < want.length; i++) {
                at[i] += want[i] - smooth.points()[i];
            }
        }
        final List<V3> moved = new ArrayList<>();
        for (int i = 0; i < cage.size(); i++) {
            moved.add(new V3(at[3 * i], at[3 * i + 1], at[3 * i + 2]));
        }
        return cage.moved(moved);
    }

    /** How far, at most, the surface smoothed from {@code cage} misses its points as they were drawn. */
    public double miss(final Cage fitted, final Cage drawn, final double definition) {
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level smooth = s.times(s.of(fitted, definition), STEPS);
        double most = 0;
        for (int i = 0; i < drawn.size(); i++) {
            most = Math.max(most, smooth.point(i).minus(drawn.points().get(i)).length());
        }
        return most;
    }
}
