package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A robot's own parts drawn from its design ({@link RobotDesign}), each in its part's own frame: its spine's
 * vertebrae up the waist, the chest and the neck, machined blocks on dark discs, fins behind, arms to the sides,
 * struts up the waist; its chest's cage of flat ribs from the spine round to a plate down its front, a glowing core,
 * a yoke across its back; its head a covered helmet, a visor across it, pods at its sides, a guard under its chin, a
 * crest along its top. Every piece named for what it is of, {@code spine}, {@code chest} or {@code head}, for the
 * parts bench to show alone.
 */
final class RobotFrame {

    /** Where the spine runs, front to back, in the waist, the chest and the neck: as a person's does. */
    static final double WAIST_Z = -5.5;
    static final double CHEST_Z = -6.8;
    static final double NECK_Z = -1.5;

    private RobotFrame() {
    }

    /** Whether {@code p} is one of a robot's own parts, drawn from its design, not the skeleton's bones. */
    static boolean designed(final Part p) {
        return p == Part.ABDOMEN || p == Part.CHEST || p == Part.NECK || p == Part.HEAD;
    }

    /**
     * {@code p}'s frame, as {@code design} has it, in its own frame: a robot's own parts, its helmet in {@code shell}
     * (its shell's colour, if shelled; its frame's, exposed).
     */
    static List<Surface> part(final Part p, final RobotDesign design, final Paint shell) {
        final RobotDesign.Spine s = design.spine();
        return switch (p) {
            case ABDOMEN -> waist(s);
            case CHEST -> chest(s, design.chest());
            case NECK -> spine("spine: neck", s, s.neck(), 0.5, 8.5, NECK_Z, 0.6, false);
            case HEAD -> helmet(design.head(), shell);
            default -> List.of();
        };
    }

    /** The waist: its vertebrae, from just over the pelvis to the chest; its struts, if it has them. */
    static List<Surface> waist(final RobotDesign.Spine s) {
        final List<Surface> w = spine("spine: waist", s, s.waist(), -1, 13, WAIST_Z, 1, true);
        if (s.struts()) {
            for (final int side : new int[] {-1, 1}) {
                final double x = side * (s.width() / 2 * (1 + s.arms()) + 1.2);
                final V3 a = new V3(x, -3, WAIST_Z + 0.5);
                final V3 b = new V3(x, 14.5, WAIST_Z + 0.5);
                w.add(Shapes.capsule("spine: strut", Paint.ROD, a, b, new double[] {s.strut(), s.strut()}, 1, 0.3,
                        10));
                w.add(Shapes.ellipsoid("spine: strut's eye", Paint.HOUSING, a, 1.4 * s.strut(), 1.4 * s.strut(),
                        1.4 * s.strut(), 10, 6));
                w.add(Shapes.ellipsoid("spine: strut's eye", Paint.HOUSING, b, 1.4 * s.strut(), 1.4 * s.strut(),
                        1.4 * s.strut(), 10, 6));
            }
        }
        return w;
    }

    /**
     * {@code count} vertebrae up from {@code from} to {@code to}, {@code z} back from the part's middle line, as big as
     * {@code size} of the design's: each a block on its disc, a fin behind, and, if {@code arms}, arms to its sides.
     */
    static List<Surface> spine(final String name, final RobotDesign.Spine s, final int count, final double from,
                               final double to, final double z, final double size, final boolean arms) {
        final List<Surface> v = new ArrayList<>();
        final double each = (to - from) / count;
        final double w = s.width() * size;
        final double d = s.depth() * size;
        for (int i = 0; i < count; i++) {
            final double bottom = from + each * i;
            final double disc = each * s.disc();
            final double top = bottom + each;
            v.add(Skin.ring(name + " disc", Paint.HOUSING, new V3(0, bottom, z), new V3(0, bottom + disc, z),
                    0.42 * w, d / w));
            v.add(Skin.ring(name + " vertebra", Paint.FRAME, new V3(0, bottom + disc, z), new V3(0, top, z), w / 2,
                    d / w));
            final double mid = (bottom + disc + top) / 2;
            final double high = (top - bottom - disc) * 0.85;
            v.add(Shapes.bar(name + " fin", Paint.FRAME, new V3(0, mid, z - d / 2),
                    new V3(0, mid - high * 0.3, z - d / 2 - s.fin() * size), V3.Y, high, 0.25 * w));
            if (arms) {
                final double reach = w / 2 * (1 + s.arms());
                v.add(Shapes.bar(name + " arm", Paint.FRAME, new V3(-reach, mid, z - 0.15 * d),
                        new V3(reach, mid, z - 0.15 * d), V3.Y, high * 0.45, 0.35 * d));
            }
        }
        return v;
    }

    /**
     * The chest: its vertebrae; its cage of ribs, each a flat bar round from beside the spine to near the front,
     * falling as it goes; the plate down its front, the core glowing under it, the yoke across its back.
     */
    static List<Surface> chest(final RobotDesign.Spine s, final RobotDesign.Chest c) {
        final List<Surface> ch = spine("spine: chest", s, s.chest(), 0, 25, CHEST_Z, 0.8, false);
        final double centre = CHEST_Z + c.deep();
        final double front = centre + c.deep();
        for (int k = 0; k < c.ribs(); k++) {
            final double kk = c.ribs() == 1 ? 0 : 5.0 * k / (c.ribs() - 1);
            final double y = 5 + 17.0 * k / Math.max(1, c.ribs() - 1);
            final double half = c.broad() * (10 + 2.2 * kk - 0.22 * kk * kk);
            for (final int side : new int[] {-1, 1}) {
                V3 last = null;
                final int steps = 8;
                for (int j = 0; j <= steps; j++) {
                    final double a = Math.toRadians(14 + 150.0 * j / steps);
                    final V3 at = new V3(side * half * Math.sin(a), y - 2.5 * j / steps, centre - c.deep() * Math.cos(a));
                    if (last != null) {
                        ch.add(Shapes.bar("chest: rib", Paint.FRAME, last, at, V3.Y, c.rib(), c.thick()));
                    }
                    last = at;
                }
            }
        }
        if (c.plate()) {
            ch.add(Shapes.bar("chest: plate", Paint.FRAME, new V3(0, 11, front - 1.2), new V3(0, 24, front - 2.2),
                    V3.X, 4.6, 1.2));
        }
        if (c.core()) {
            ch.add(Skin.ring("chest: core's housing", Paint.HOUSING, new V3(0, 7.5, front - 3.2),
                    new V3(0, 7.5, front - 1.4), c.radius() + 0.7, 1));
            ch.add(Skin.ring("chest: core", Paint.VISOR, new V3(0, 7.5, front - 1.6), new V3(0, 7.5, front - 0.9),
                    c.radius(), 1));
        }
        if (c.yoke()) {
            ch.add(Shapes.bar("chest: yoke", Paint.FRAME, new V3(-17, 19.5, CHEST_Z + 0.5),
                    new V3(17, 19.5, CHEST_Z + 0.5), V3.Y, 2.6, 2.0));
        }
        return ch;
    }

    /**
     * The head, covered: a helmet in {@code shell}, the head's own shape as broad, tall and deep as said, in so many
     * flat panels; a visor across it; a pod at either side; a guard under its chin; a crest along its top.
     */
    static List<Surface> helmet(final RobotDesign.Head h, final Paint shell) {
        final List<Surface> hd = new ArrayList<>();
        final Skin.Make make = new Skin.Make(true, h.panels());
        for (final Surface s : Skin.head(make)) {
            if (s.name().equals("head")) {
                hd.add(new Surface("head: helmet", shell, s.rings(), s.start(), s.end(), false)
                        .moved(v -> new V3(v.x() * h.broad(), v.y() * h.tall(), v.z() * h.deep())));
            }
        }
        // the visor: a band of the helmet's own face, standing a little proud of it, where the eyes would be
        final double eyes = (6 + 7) / 23.0;
        final double dt = h.band() / 2 / 23 / h.tall();
        final double span = Math.min(1.45, h.visor() / 2 / 7.5 / h.broad());
        hd.add(patch("head: visor", Paint.VISOR, (t, phi) -> on(h, t, phi, 0.45), eyes - dt, eyes + dt,
                Math.PI / 2 - span, Math.PI / 2 + span, 0.4));
        for (final int side : new int[] {-1, 1}) {
            final double x = 6.6 * h.broad();
            hd.add(Skin.ring("head: pod", Paint.HOUSING, new V3(side * x, 4 * h.tall(), -1),
                    new V3(side * (x + 1.6), 4 * h.tall(), -1), h.pods(), 1));
        }
        if (h.chin()) {
            // a guard round under its chin, along its jaw from cheek to cheek
            hd.add(patch("head: chin guard", Paint.PANEL, (t, phi) -> on(h, t, phi, 0.5), 0.08, 0.2, 0.35,
                    Math.PI - 0.35, 0.8));
        }
        if (h.crest()) {
            // a crest along its middle from its brow over its crown to the back of its head
            hd.add(patch("head: crest", Paint.PANEL, (t, phi) -> on(h, t, phi, 0.7), 0.7, 0.995,
                    Math.PI / 2 - 0.12, Math.PI / 2 + 0.12, 0.7));
            hd.add(patch("head: crest", Paint.PANEL, (t, phi) -> on(h, t, phi, 0.7), 0.55, 0.995,
                    3 * Math.PI / 2 - 0.12, 3 * Math.PI / 2 + 0.12, 0.7));
        }
        return hd;
    }

    /** The helmet's surface, as broad, tall and deep as {@code h} says, {@code out} cm proud of it. */
    static V3 on(final RobotDesign.Head h, final double t, final double phi, final double out) {
        final V3 p = Skin.headAt(t, phi);
        final V3 scaled = new V3(p.x() * h.broad(), p.y() * h.tall(), p.z() * h.deep());
        final V3 axis = new V3(0, scaled.y(), Loft.ip(Skin.HEAD_Z, t) * h.deep());
        final V3 away = scaled.minus(axis);
        return away.length() < 1e-9 ? scaled : scaled.plus(away.unit().times(out));
    }

    /**
     * A patch of a lofted surface ({@code skin}), from {@code t0} to {@code t1} along it and {@code phi0} to
     * {@code phi1} round, made a solid {@code thick} thick, inward of it: as a visor or a guard laid on a helmet.
     */
    static Surface patch(final String name, final Paint paint, final Loft.Skin skin, final double t0, final double t1,
                         final double phi0, final double phi1, final double thick) {
        final int along = 6;
        final int round = 14;
        final List<List<V3>> rings = new ArrayList<>();
        V3 first = null;
        V3 last = null;
        for (int i = 0; i <= along; i++) {
            final double t = t0 + (t1 - t0) * i / along;
            final List<V3> ring = new ArrayList<>();
            for (int j = 0; j <= round; j++) {
                ring.add(skin.at(t, phi0 + (phi1 - phi0) * j / round));
            }
            // back along its inside, the same way in by {@code thick}
            final V3 middle = ring.get(round / 2);
            for (int j = round; j >= 0; j--) {
                final V3 outside = ring.get(j);
                final V3 axis = new V3(0, outside.y(), middle.z() - 8);
                ring.add(outside.minus(outside.minus(axis).unit().times(thick)));
            }
            V3 c = V3.ZERO;
            for (final V3 v : ring) {
                c = c.plus(v);
            }
            c = c.times(1.0 / ring.size());
            if (i == 0) {
                first = c;
            }
            last = c;
            rings.add(ring);
        }
        return new Surface(name, paint, rings, first, last, true);
    }
}
