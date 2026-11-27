package com.example.fxsuite.life.body.hand;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * How a hand holds its fingers, still: each segment turned so far each way it turns ({@link Segment#dofs}), no further
 * than it may ({@link FingerDof}). At rest as a hand hangs, each finger's joints curled a little; or closed round a
 * grip, the knuckles bent most. Made afresh for every change, never changed.
 */
public final class HandPose {

    /** How far a finger's joints and the thumb's bend, closed round a grip, and the thumb swung across, degrees. */
    private static final double KNUCKLE = 75;
    private static final double MIDDLE = 100;
    private static final double END = 60;
    private static final double THUMB_BENT = 20;
    private static final double THUMB_ACROSS = 30;
    private static final double THUMB_KNUCKLE = 25;
    private static final double THUMB_END = 35;

    private final Map<Segment, double[]> angles;

    private HandPose(final Map<Segment, double[]> angles) {
        this.angles = angles;
    }

    /** A hand as it hangs at rest. */
    public HandPose() {
        this.angles = new EnumMap<>(Segment.class);
        for (final Segment s : Segment.values()) {
            angles.put(s, s.dofs().stream().mapToDouble(s::rest).toArray());
        }
    }

    /** How far {@code segment} is turned {@code dof}'s way. */
    public double get(final Segment segment, final FingerDof dof) {
        final int k = segment.dofs().indexOf(dof);
        if (k < 0) {
            throw new IllegalArgumentException(segment + " does not turn " + dof);
        }
        return angles.get(segment)[k];
    }

    /** This pose with {@code segment} turned {@code dof}'s way to {@code v}, no further than it may. */
    public HandPose with(final Segment segment, final FingerDof dof, final double v) {
        final int k = segment.dofs().indexOf(dof);
        if (k < 0) {
            throw new IllegalArgumentException(segment + " does not turn " + dof);
        }
        final Map<Segment, double[]> a = new EnumMap<>(angles);
        final double[] mine = angles.get(segment).clone();
        mine[k] = dof.clamp(v);
        a.put(segment, mine);
        return new HandPose(a);
    }

    /** This hand closed round a grip: each finger's knuckle, middle and end joints bent, the thumb in over it. */
    public HandPose gripping() {
        HandPose p = this;
        for (final Segment s : Segment.values()) {
            for (final FingerDof d : s.dofs()) {
                p = p.with(s, d, switch (d) {
                    case KNUCKLE_BEND -> KNUCKLE;
                    case MIDDLE_BEND -> MIDDLE;
                    case END_BEND -> END;
                    case THUMB_BEND -> THUMB_BENT;
                    case THUMB_SWING -> THUMB_ACROSS;
                    case THUMB_KNUCKLE -> THUMB_KNUCKLE;
                    case THUMB_END -> THUMB_END;
                    case KNUCKLE_SPREAD -> 0;
                });
            }
        }
        return p;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof HandPose p && Arrays.stream(Segment.values())
                .allMatch(s -> Arrays.equals(angles.get(s), p.angles.get(s)));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(Segment.values()).mapToInt(s -> Arrays.hashCode(angles.get(s))).sum();
    }
}
