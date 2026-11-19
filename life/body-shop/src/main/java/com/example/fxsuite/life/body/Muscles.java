package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A body's muscles, as the interactive model draws them (Papers/Body): each a belly between two tendons, from its
 * origin on one part to its insertion on another; as it shortens, a pose bringing its ends nearer, its belly thickens,
 * keeping its volume ({@code sqrt} of how much shorter it is than at rest), pushed out the way it bulges. The thigh's,
 * the shin's and the upper arm's as the model has them; the shoulder's, the chest's, the belly's, the seat's and the
 * neck's added the same way. How thick each thigh, calf and upper arm's muscle is drives how its skin swells
 * ({@link Bulge}).
 */
final class Muscles {

    /** Which way a muscle is coloured: at the front, at the back, deep. */
    enum Side { FRONT, BACK, DEEP }

    /**
     * A muscle on the right side (its left the same in a mirror): its ends, where they are as posed; how long its
     * tendons are at each end; its belly's radius at rest, how much broader across than deep; which way it bulges
     * ({@code push}, along its part's front, and {@code shift} more); the part it is drawn with.
     */
    record Muscle(String name, Part part, Side side, BiFunction<Rig, Integer, V3> origin,
                  BiFunction<Rig, Integer, V3> insertion, double tendon0, double tendon1, double radius,
                  double across, double push, double shift) {
    }

    /** How swollen each muscle that shapes the skin is on one side: 1 at rest, more as it shortens. */
    record Bulge(double quads, double hamstrings, double calf, double biceps, double triceps) {
        static final Bulge REST = new Bulge(1, 1, 1, 1, 1);
    }

    private Muscles() {
    }

    /** {@code local} on part {@code right}'s side {@code side} (1 right, -1 left). */
    private static BiFunction<Rig, Integer, V3> on(final Part right, final V3 local) {
        return (rig, side) -> rig.place(side > 0 ? right : right.other(), local);
    }

    /** {@code local} on the pelvis, measured from the hip joint of side {@code side}, as on the right. */
    private static BiFunction<Rig, Integer, V3> atHip(final V3 local) {
        return (rig, side) -> {
            final double x = 8.5 * rig.proportions().hipScale() + local.x();
            return rig.place(Part.PELVIS, new V3(side * x, local.y(), local.z()));
        };
    }

    /** {@code local} on the pelvis, as on the right, mirrored for the left. */
    private static BiFunction<Rig, Integer, V3> onPelvis(final V3 local) {
        return (rig, side) -> rig.place(Part.PELVIS, new V3(side * local.x(), local.y(), local.z()));
    }

    /** As {@link #onPelvis}, on a part down the middle. */
    private static BiFunction<Rig, Integer, V3> onMiddle(final Part part, final V3 local) {
        return (rig, side) -> rig.place(part, new V3(side * local.x(), local.y(), local.z()));
    }

    /** The kneecap, on the thigh: in front of the knee, riding down as it bends. */
    private static BiFunction<Rig, Integer, V3> kneecap() {
        return (rig, side) -> {
            final Part thigh = side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final double knee = rig.pose().get(side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN, Dof.KNEE);
            return rig.place(thigh, Rot.aboutX(knee * 0.75).apply(new V3(0, 1.2, 5.6)).plus(new V3(0, -42, 0)));
        };
    }

    /** The kneecap, where it is on {@code side}. */
    static V3 kneecap(final Rig rig, final int side) {
        return kneecap().apply(rig, side);
    }

    static final V3 CALCANEUS = new V3(0, -3.4, -5.4);

    /** Every muscle, on the right side. */
    static final List<Muscle> ALL = List.of(
            new Muscle("rectus femoris", Part.RIGHT_THIGH, Side.FRONT, atHip(new V3(2.4, 4.0, 4.6)), kneecap(), 5, 7,
                    2.0, 1.0, 1, 0),
            new Muscle("vastus", Part.RIGHT_THIGH, Side.FRONT, on(Part.RIGHT_THIGH, new V3(2.2, -11, 3.4)), kneecap(),
                    1, 6, 3.5, 1.5, 1, 0),
            new Muscle("hamstrings", Part.RIGHT_THIGH, Side.BACK, atHip(new V3(-1.6, -4.6, -4.6)),
                    on(Part.RIGHT_SHIN, new V3(0.4, -5.5, -3.4)), 6, 8, 3.0, 1.3, -1, 0),
            new Muscle("gastrocnemius", Part.RIGHT_SHIN, Side.BACK, on(Part.RIGHT_THIGH, new V3(0, -38.6, -3.0)),
                    on(Part.RIGHT_FOOT, CALCANEUS), 4, 20, 2.8, 1.7, -1, -1.6),
            new Muscle("soleus", Part.RIGHT_SHIN, Side.DEEP, on(Part.RIGHT_SHIN, new V3(0.8, -9, -2.4)),
                    on(Part.RIGHT_FOOT, CALCANEUS), 2, 12, 2.4, 1.8, -1, 0.4),
            new Muscle("tibialis anterior", Part.RIGHT_SHIN, Side.FRONT, on(Part.RIGHT_SHIN, new V3(1.8, -7, 2.3)),
                    on(Part.RIGHT_FOOT, new V3(-2.4, -4.4, 5.8)), 3, 18, 1.3, 1.2, 1, 0.3),
            new Muscle("biceps", Part.RIGHT_UPPER_ARM, Side.FRONT, on(Part.RIGHT_UPPER_ARM, new V3(0, -1.5, 2.7)),
                    on(Part.RIGHT_FOREARM, new V3(0.4, -4, 1.6)), 7, 4, 2.1, 1.1, 1, 0),
            new Muscle("triceps", Part.RIGHT_UPPER_ARM, Side.BACK, on(Part.RIGHT_UPPER_ARM, new V3(0, -4, -2.9)),
                    on(Part.RIGHT_FOREARM, new V3(0, 1.2, -2.4)), 3, 5, 2.3, 1.35, -1, 0),
            new Muscle("deltoid", Part.RIGHT_UPPER_ARM, Side.FRONT, on(Part.RIGHT_GIRDLE, new V3(16.5, 1.7, -6.5)),
                    on(Part.RIGHT_UPPER_ARM, new V3(1.2, -14, 0.5)), 1, 3, 3.2, 1.4, 0, 0),
            new Muscle("pectoralis", Part.CHEST, Side.FRONT, onMiddle(Part.CHEST, new V3(3, 15, 9.5)),
                    on(Part.RIGHT_UPPER_ARM, new V3(0, -6, 2)), 2, 2, 2.6, 2.0, 0, 0),
            new Muscle("rectus abdominis", Part.ABDOMEN, Side.FRONT, onPelvis(new V3(2.2, -3, 8.2)),
                    onMiddle(Part.CHEST, new V3(3, 9, 10)), 1, 1, 1.6, 1.7, 0, 0),
            new Muscle("gluteus maximus", Part.PELVIS, Side.BACK, onPelvis(new V3(4, 6, -9.5)),
                    on(Part.RIGHT_THIGH, new V3(2.8, -10, -2.5)), 1, 3, 3.6, 1.5, 0, 0),
            new Muscle("trapezius", Part.NECK, Side.BACK, onMiddle(Part.NECK, new V3(1.5, 6, -4)),
                    on(Part.RIGHT_GIRDLE, new V3(15, 2.0, -8)), 1, 1, 1.8, 1.8, 0, 0));

    /** {@code m}'s belly's length on {@code side}, posed as {@code rig} is: between its ends, less its tendons. */
    static double length(final Muscle m, final Rig rig, final int side) {
        final double s = rig.proportions().scale();
        return Math.max(m.origin().apply(rig, side).minus(m.insertion().apply(rig, side)).length()
                - (m.tendon0() + m.tendon1()) * s, s);
    }

    /** How swollen {@code m} is on {@code side}: as much thicker as it is shorter than at rest, its volume kept. */
    static double swell(final Muscle m, final Rig rig, final Rig rest, final int side) {
        return Math.sqrt(length(m, rest, side) / length(m, rig, side));
    }

    /** How swollen each muscle that shapes the skin is, on each side. */
    static Map<Integer, Bulge> bulges(final Rig rig, final Rig rest) {
        final Map<Integer, Bulge> b = new java.util.HashMap<>();
        for (final int side : new int[] {1, -1}) {
            b.put(side, new Bulge(swell(named("vastus"), rig, rest, side), swell(named("hamstrings"), rig, rest, side),
                    swell(named("gastrocnemius"), rig, rest, side), swell(named("biceps"), rig, rest, side),
                    swell(named("triceps"), rig, rest, side)));
        }
        return b;
    }

    static Muscle named(final String name) {
        return ALL.stream().filter(m -> m.name().equals(name)).findFirst().orElseThrow();
    }

    /** Every muscle on both sides, drawn: its belly and its tendons, each with the part it goes with. */
    static Map<Part, List<Surface>> drawn(final Rig rig, final Rig rest) {
        final Map<Part, List<Surface>> by = new EnumMap<>(Part.class);
        final double s = rig.proportions().scale();
        final double b = rig.proportions().build();
        for (final Muscle m : ALL) {
            for (final int side : new int[] {1, -1}) {
                final Part part = side > 0 || m.part().side() == 0 ? m.part() : m.part().other();
                final Paint paint = switch (m.side()) {
                    case FRONT -> Paint.MUSCLE_FRONT;
                    case BACK -> Paint.MUSCLE_BACK;
                    case DEEP -> Paint.MUSCLE_DEEP;
                };
                final V3 o = m.origin().apply(rig, side);
                final V3 i = m.insertion().apply(rig, side);
                final V3 dir = i.minus(o).unit();
                final double length = length(m, rig, side);
                final V3 p1 = o.plus(dir.times(m.tendon0() * s));
                final V3 p2 = i.minus(dir.times(m.tendon1() * s));
                final double k = swell(m, rig, rest, side);
                final double r0 = m.radius() * s * b;
                final double r = r0 * k;
                final V3 front = rig.frame(part).way(V3.Z);
                final V3 c = p1.plus(p2).times(0.5).plus(front.times(m.push() * (r - r0) + m.shift() * s));
                final String name = (side > 0 ? "right " : "left ") + m.name();
                final List<Surface> list = by.computeIfAbsent(part, x -> new ArrayList<>());
                list.add(Shapes.ellipsoid(name, paint, V3.ZERO, r * m.across(), length / 2, r, 14, 9)
                        .moved(v -> c.plus(Rot.between(V3.Y, dir).apply(v))));
                list.add(Shapes.capsule(name + " tendon", paint, o, p1, 0.4 * s, 1, 6));
                list.add(Shapes.capsule(name + " tendon", paint, p2, i, 0.45 * s, 1, 6));
            }
        }
        return by;
    }
}
