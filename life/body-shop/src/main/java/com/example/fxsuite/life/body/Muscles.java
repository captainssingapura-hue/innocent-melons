package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A body's muscles as the interactive model of the papers has them (Papers/Body), read as a robot's pistons: each from
 * its origin on one part to its insertion on another. The thigh's, the shin's and the upper arm's as the model has
 * them; the shoulder's, the chest's, the belly's, the seat's and the neck's added the same way. Where the kneecap rides.
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

    /** How long a piston's barrel is, as a share of how far apart its ends are at rest; how thick, of the muscle. */
    static final double BARREL = 0.55;
    static final double BORE = 0.5;

    /**
     * Every muscle on both sides read as a robot's piston: a barrel fixed at its origin, along the line to its
     * insertion, as long as a share of that line at rest; a rod sliding out of it to the insertion, out of sight in it
     * as the ends come together; an eye at each end. Its muscle's own ends, so it works as the muscle would.
     */
    static Map<Part, List<Surface>> pistons(final Rig rig, final Rig rest) {
        final Map<Part, List<Surface>> by = new EnumMap<>(Part.class);
        final double s = rig.proportions().scale();
        final double b = rig.proportions().build();
        for (final Muscle m : ALL) {
            for (final int side : new int[] {1, -1}) {
                final Part part = side > 0 || m.part().side() == 0 ? m.part() : m.part().other();
                final V3 o = m.origin().apply(rig, side);
                final V3 i = m.insertion().apply(rig, side);
                final double apart = i.minus(o).length();
                final V3 dir = i.minus(o).unit();
                final double was = m.insertion().apply(rest, side).minus(m.origin().apply(rest, side)).length();
                final double barrel = Math.min(BARREL * was, 0.9 * apart);
                final double bore = Math.max(0.9, m.radius() * BORE) * s * b;
                final String name = (side > 0 ? "right " : "left ") + m.name();
                final List<Surface> list = by.computeIfAbsent(part, x -> new ArrayList<>());
                list.add(Shapes.capsule(name + " barrel", Paint.BARREL, o, o.plus(dir.times(barrel)),
                        new double[] {bore, bore}, 1, 0.15, 14));
                list.add(Shapes.capsule(name + " rod", Paint.ROD, o.plus(dir.times(0.6 * barrel)), i,
                        new double[] {0.45 * bore, 0.45 * bore}, 1, 0.3, 10));
                list.add(Shapes.ellipsoid(name + " eye", Paint.HOUSING, o, 0.8 * bore, 0.8 * bore, 0.8 * bore, 10, 6));
                list.add(Shapes.ellipsoid(name + " eye", Paint.HOUSING, i, 0.6 * bore, 0.6 * bore, 0.6 * bore, 10, 6));
            }
        }
        return by;
    }
}
