package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A body as the shop makes it: so made ({@link Proportions}), so held ({@link Pose}), standing on its feet
 * ({@link Rig}); in layers, as the interactive model of the papers shows one: its skin, its bones, its muscles, and the
 * marks of its balance (its centre of mass over the ground, the area its feet hold it up over, the points they touch
 * it by). Each piece with the part it goes with, to be taken hold of by.
 */
public final class Body {

    /** What of it is shown. */
    public enum Layer { SKIN, BONES, MUSCLES, MARKS }

    /** A piece of it: what part it goes with (none for a mark), what layer it is of, its surface. */
    public record Piece(Part part, Layer layer, Surface surface) {
    }

    private final Rig rig;
    private final Rig rest;

    public Body(final Proportions proportions, final Pose pose) {
        rig = new Rig(proportions, pose);
        rest = new Rig(proportions, Pose.REST);
    }

    public Rig rig() {
        return rig;
    }

    /** How swollen each side's muscles are that shape its skin. */
    public Map<Integer, Muscles.Bulge> bulges() {
        return Muscles.bulges(rig, rest);
    }

    /** Its pieces in {@code shown}'s layers. */
    public List<Piece> pieces(final Set<Layer> shown) {
        final List<Piece> all = new ArrayList<>();
        if (shown.contains(Layer.BONES)) {
            Bones.drawn(rig).forEach((p, l) -> l.forEach(s -> all.add(new Piece(p, Layer.BONES, s))));
        }
        if (shown.contains(Layer.MUSCLES)) {
            Muscles.drawn(rig, rest).forEach((p, l) -> l.forEach(s -> all.add(new Piece(p, Layer.MUSCLES, s))));
        }
        if (shown.contains(Layer.MARKS)) {
            marks().forEach(s -> all.add(new Piece(null, Layer.MARKS, s)));
        }
        if (shown.contains(Layer.SKIN)) {
            Skin.drawn(rig, bulges()).forEach((p, l) -> l.forEach(s -> all.add(new Piece(p, Layer.SKIN, s))));
        }
        return all;
    }

    /** How far above the ground its marks lie on it, cm: clear of it, not in it. */
    static final double MARK_OVER = 0.3;

    /**
     * Its balance, marked: its centre of mass, a ball; the point under it on the ground, red if it falls outside the
     * area its feet hold it up over; that area's outline on the ground; each point of its feet touching the ground.
     */
    List<Surface> marks() {
        final List<Surface> marks = new ArrayList<>();
        final V3 c = rig.centreOfMass();
        final boolean standing = rig.margin() >= 0;
        marks.add(Shapes.ellipsoid("centre of mass", Paint.MARK_MASS, c, 2.2, 2.2, 2.2, 16, 10));
        final V3 under = new V3(c.x(), MARK_OVER, c.z());
        marks.add(Shapes.ellipsoid("under the centre of mass", standing ? Paint.MARK_MASS : Paint.MARK_FALLING, under,
                1.4, 0.4, 1.4, 14, 6));
        marks.add(Shapes.capsule("plumb line", Paint.MARK_MASS, under, c, 0.25, 1, 6));
        final List<double[]> support = rig.support();
        for (int i = 0; support.size() > 1 && i < support.size(); i++) {
            final double[] a = support.get(i);
            final double[] b = support.get((i + 1) % support.size());
            if (support.size() == 2 && i == 1) {
                break;
            }
            marks.add(Shapes.capsule("support", Paint.MARK_SUPPORT, new V3(a[0], MARK_OVER, a[1]),
                    new V3(b[0], MARK_OVER, b[1]), 0.35, 1, 6));
        }
        for (final V3 t : rig.touching()) {
            marks.add(Shapes.ellipsoid("touching", Paint.MARK_SUPPORT, new V3(t.x(), MARK_OVER, t.z()), 0.9, 0.5, 0.9,
                    10, 6));
        }
        return marks;
    }
}
