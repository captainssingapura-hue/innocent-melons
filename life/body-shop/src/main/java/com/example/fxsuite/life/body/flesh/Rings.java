package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Point;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * How rings are written down ({@link Ring}): a limb's, eight spokes round from straight ahead (ahead, ahead and out,
 * out, behind and out, behind, behind and in, in, ahead and in), each reaching so far from the middle or so far beyond
 * the bone, the free ones as thick as the build makes them.
 */
public record Rings() implements StatelessFunctionalObject {

    public static final Rings INSTANCE = new Rings();

    /** How far a spoke reaches, and from what. */
    public record Reach(Spoke.Held held, double reach) {
    }

    /** So far from the middle. */
    public Reach f(final double reach) {
        return new Reach(Spoke.Held.FREE, reach);
    }

    /** So far beyond the bone. */
    public Reach b(final double reach) {
        return new Reach(Spoke.Held.ON_BONE, reach);
    }

    /**
     * A limb's ring, its middle {@code along} the way between two points and {@code x} out and {@code z} ahead of it;
     * its free spokes {@code girth} times as far as {@code reaches} say.
     */
    public Ring limb(final String said, final Point<?> from, final Point<?> to, final double along, final double x,
                     final double z, final double twist, final double girth, final Reach... reaches) {
        final List<Spoke> spokes = new ArrayList<>();
        for (int k = 0; k < reaches.length; k++) {
            final Reach r = reaches[k];
            spokes.add(new Spoke(way(k), 45.0 * k, r.held(), r.held() == Spoke.Held.FREE ? r.reach() * girth
                    : r.reach()));
        }
        return new Ring(said, from, to, along, new V3(x, 0, z), twist, spokes);
    }

    /** The {@code k}th of a limb's ways round from straight ahead, in words. */
    private String way(final int k) {
        return switch (k) {
            case 0 -> "ahead";
            case 1 -> "ahead and out";
            case 2 -> "out";
            case 3 -> "behind and out";
            case 4 -> "behind";
            case 5 -> "behind and in";
            case 6 -> "in";
            default -> "ahead and in";
        };
    }
}
