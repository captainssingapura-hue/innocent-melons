package com.example.fxsuite.life.body;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * How a body holds itself, still: every part's joint turned so far each way it turns ({@link Part#dofs}), each no
 * further than it may ({@link Dof}). Made afresh for every change, never changed.
 */
public final class Pose {

    private final Map<Part, double[]> angles;

    private Pose(final Map<Part, double[]> angles) {
        this.angles = angles;
    }

    /** Standing at rest: arms a little out from the sides, elbows a little bent, palms in. */
    public static final Pose REST = rest();

    private static Pose rest() {
        final Map<Part, double[]> a = new EnumMap<>(Part.class);
        for (final Part p : Part.values()) {
            a.put(p, p.dofs().stream().mapToDouble(Dof::rest).toArray());
        }
        return new Pose(a);
    }

    /** How far {@code part} is turned {@code dof}'s way; at rest, if it does not turn that way. */
    public double get(final Part part, final Dof dof) {
        final int k = part.dofs().indexOf(dof);
        return k < 0 ? dof.rest() : angles.get(part)[k];
    }

    /** This pose with {@code part} turned {@code dof}'s way to {@code v}, no further than it may. */
    public Pose with(final Part part, final Dof dof, final double v) {
        final int k = part.dofs().indexOf(dof);
        if (k < 0) {
            throw new IllegalArgumentException(part + " does not turn " + dof);
        }
        final Map<Part, double[]> a = new EnumMap<>(angles);
        final double[] mine = angles.get(part).clone();
        mine[k] = dof.clamp(v);
        a.put(part, mine);
        return new Pose(a);
    }

    /** This pose with {@code part} held as the same part on the other side is, in a mirror. */
    public Pose mirroredTo(final Part part) {
        final Map<Part, double[]> a = new EnumMap<>(angles);
        a.put(part, angles.get(part.other()).clone());
        return new Pose(a);
    }

    /** This pose with {@code part} back at rest. */
    public Pose rested(final Part part) {
        final Map<Part, double[]> a = new EnumMap<>(angles);
        a.put(part, part.dofs().stream().mapToDouble(Dof::rest).toArray());
        return new Pose(a);
    }

    /**
     * A pose as the interactive model's presets give one: the pelvis tilted, the spine bent forward, sideways and
     * twisted (shared between the abdomen and the chest); each leg its hip forward, sideways and turned, its knee, its
     * ankle and its roll, its toes; each arm raised forward, sideways and turned, its elbow, its forearm's twist. Any
     * not given at rest.
     */
    public static Pose of(final double tilt, final double[] spine, final double[] rightLeg, final double[] leftLeg,
                          final double[] rightArm, final double[] leftArm) {
        Pose p = REST.with(Part.PELVIS, Dof.TILT, tilt);
        for (final Part trunk : List.of(Part.ABDOMEN, Part.CHEST)) {
            p = p.with(trunk, Dof.SPINE_BEND, spine[0] / 2).with(trunk, Dof.SPINE_SIDE, spine[1] / 2)
                    .with(trunk, Dof.SPINE_TWIST, spine[2] / 2);
        }
        p = leg(p, rightLeg, Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, Part.RIGHT_TOES);
        p = leg(p, leftLeg, Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT, Part.LEFT_TOES);
        p = arm(p, rightArm, Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM);
        return arm(p, leftArm, Part.LEFT_UPPER_ARM, Part.LEFT_FOREARM);
    }

    private static Pose leg(final Pose p, final double[] l, final Part thigh, final Part shin, final Part foot,
                            final Part toes) {
        if (l == null) {
            return p;
        }
        return p.with(thigh, Dof.HIP_FORWARD, l[0]).with(thigh, Dof.HIP_SIDEWAYS, l[1])
                .with(thigh, Dof.HIP_ROTATION, l[2]).with(shin, Dof.KNEE, l[3]).with(foot, Dof.ANKLE, l[4])
                .with(foot, Dof.ANKLE_ROLL, l[5]).with(toes, Dof.TOES, l[6]);
    }

    private static Pose arm(final Pose p, final double[] a, final Part upper, final Part fore) {
        if (a == null) {
            return p;
        }
        return p.with(upper, Dof.RAISE_FORWARD, a[0]).with(upper, Dof.RAISE_SIDEWAYS, a[1])
                .with(upper, Dof.ARM_ROTATION, a[2]).with(fore, Dof.ELBOW, a[3]).with(fore, Dof.FOREARM_TWIST, a[4]);
    }

    /** A pose by name. */
    public record Named(String name, Pose pose) {
    }

    private static final double[] Z = {0, 0, 0};

    /** The interactive model's presets, in its order. */
    public static final List<Named> PRESETS = List.of(
            new Named("Standing", REST),
            new Named("T-pose", of(0, Z, null, null, new double[] {0, 90, 0, 0, 85}, new double[] {0, 90, 0, 0, 85})),
            new Named("Squat, arms down", of(30, new double[] {10, 0, 0}, new double[] {110, 15, 15, 120, -40, 0, 0},
                    new double[] {110, 15, 15, 120, -40, 0, 0}, null, null)),
            new Named("Squat, arms forward", of(30, new double[] {10, 0, 0},
                    new double[] {110, 15, 15, 120, -40, 0, 0}, new double[] {110, 15, 15, 120, -40, 0, 0},
                    new double[] {90, 5, 0, 0, 85}, new double[] {90, 5, 0, 0, 85})),
            new Named("Tiptoe reach", of(4, Z, new double[] {0, 0, 0, 0, 36, 0, 45},
                    new double[] {0, 0, 0, 0, 36, 0, 45}, new double[] {170, 10, 0, 0, 85},
                    new double[] {170, 10, 0, 0, 85})),
            new Named("One leg, arms out", of(0, Z, new double[] {0, -7, 0, 5, -5, -7, 0},
                    new double[] {60, 0, 0, 90, 20, 0, 0}, new double[] {0, 85, 0, 0, 85},
                    new double[] {0, 85, 0, 0, 85})),
            new Named("Front kick", of(-5, Z, new double[] {90, 0, 0, 10, 30, 0, 30},
                    new double[] {0, -8, 0, 15, -10, -8, 0}, new double[] {30, 25, 0, 95, 85},
                    new double[] {45, 20, 0, 100, 85})),
            new Named("Single whip", of(0, new double[] {0, 0, 25}, new double[] {-25, 8, 0, 6, -31, 8, 0},
                    new double[] {40, 8, 0, 48, -8, 8, 0}, new double[] {10, 80, 0, 15, 130},
                    new double[] {60, 45, 0, 25, 30})),
            new Named("Overhead cut", of(0, new double[] {8, 0, 0}, new double[] {-25, 8, 0, 6, -31, 8, 0},
                    new double[] {40, 8, 0, 48, -8, 8, 0}, new double[] {160, -5, 0, 35, 85},
                    new double[] {160, -5, 0, 35, 85})));

    @Override
    public boolean equals(final Object o) {
        return o instanceof Pose p && Part.values().length == p.angles.size()
                && java.util.Arrays.stream(Part.values()).allMatch(k -> Arrays.equals(angles.get(k), p.angles.get(k)));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(Part.values()).mapToInt(k -> Arrays.hashCode(angles.get(k))).sum();
    }
}
