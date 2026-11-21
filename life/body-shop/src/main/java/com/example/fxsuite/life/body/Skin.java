package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.example.fxsuite.life.body.Loft.endF;
import static com.example.fxsuite.life.body.Loft.ip;
import static com.example.fxsuite.life.body.Loft.prof;
import static com.example.fxsuite.life.body.Loft.se;
import static com.example.fxsuite.life.body.Loft.sw;

/**
 * A robot's shell, part by part, each lofted in its own frame in flat panels from profiles of how broad and how deep
 * it is along it ({@link Loft}), as the interactive model of the papers draws a body's legs and arms: the thigh, the
 * shin, the foot and its toes, the upper arm and the forearm (twisting with it) its own; the pelvis, the abdomen, the
 * chest, the shoulder girdles, the neck and the hands drawn the same way, in the same figure's centimetres; its joints
 * ringed, its limbs banded, its head a helmet. Broader and deeper as the body's build is; each a right side's, a left
 * its mirror.
 */
final class Skin {

    // the interactive model's profiles: across and front to back, by how far along (thigh, shin, upper arm, forearm),
    // or by how far forward (foot, toes)
    static final double[][] THIGH_W = {{0, 15.0}, {0.12, 16.2}, {0.35, 15.0}, {0.65, 13.6}, {0.88, 11.6}, {1, 10.8}};
    static final double[][] THIGH_D = {{0, 14.0}, {0.12, 15.2}, {0.35, 14.6}, {0.65, 13.0}, {0.88, 10.8}, {1, 10.2}};
    static final double[][] SHIN_W = {{0, 10.4}, {0.25, 11.4}, {0.55, 9.4}, {0.85, 7.0}, {1, 6.6}};
    static final double[][] SHIN_D = {{0, 10.2}, {0.25, 12.2}, {0.55, 9.6}, {0.85, 6.6}, {1, 6.0}};
    static final double[][] FOOT_W = {{-5.5, 6.0}, {-2, 6.8}, {4, 7.8}, {10, 9.8}, {13.1, 9.8}, {15.5, 9.0}};
    static final double[][] FOOT_TOP = {{-5.5, -1.8}, {-1, 1.6}, {2, 2.0}, {6, -0.8}, {10, -3.0}, {13.1, -3.6},
        {15.5, -4.4}};
    static final double[][] TOES_W = {{0, 9.4}, {4, 8.8}, {7.2, 7.4}};
    static final double[][] TOES_TOP = {{0, 1.6}, {7.2, 0.6}};
    static final double[][] UPPER_W = {{0, 10.4}, {0.12, 11.2}, {0.3, 9.4}, {0.55, 8.6}, {0.85, 8.6}, {1, 7.8}};
    static final double[][] UPPER_D = {{0, 10.0}, {0.12, 10.8}, {0.3, 9.4}, {0.55, 9.2}, {0.85, 7.2}, {1, 6.8}};
    static final double[][] FORE_W = {{0, 9.0}, {0.22, 9.8}, {0.55, 8.0}, {0.85, 6.6}, {1, 6.4}};
    static final double[][] FORE_D = {{0, 8.0}, {0.22, 8.4}, {0.55, 6.6}, {0.85, 4.8}, {1, 4.4}};

    // the rest of the body, drawn the same way: across, and front and back of its middle line, by how far along;
    // the pelvis from under the seat up to the top of its hip bones
    static final double[][] PELVIS_W = {{0, 18}, {0.2, 30}, {0.45, 34}, {0.7, 32.5}, {1, 29}};
    static final double[][] PELVIS_F = {{0, 6}, {0.3, 9}, {0.6, 8.6}, {1, 8.8}};
    static final double[][] PELVIS_B = {{0, 7}, {0.35, 11.5}, {0.6, 10.5}, {1, 9}};
    // the abdomen from the hips to the bottom of the ribs, its belly before it
    static final double[][] ABDOMEN_W = {{0, 28.5}, {0.4, 27}, {0.7, 27.5}, {1, 29}};
    static final double[][] ABDOMEN_F = {{0, 8.8}, {0.35, 9.8}, {0.7, 9.3}, {1, 9.5}};
    static final double[][] ABDOMEN_B = {{0, 9}, {0.5, 8.4}, {1, 9}};
    // the chest from the bottom of the ribs up over the shoulders to the foot of the neck
    static final double[][] CHEST_W = {{0, 29}, {0.3, 31}, {0.5, 33}, {0.68, 35}, {0.84, 30}, {1, 17}};
    static final double[][] CHEST_F = {{0, 9.5}, {0.35, 10.6}, {0.6, 10.6}, {0.8, 8.6}, {1, 5}};
    static final double[][] CHEST_B = {{0, 9}, {0.4, 10}, {0.7, 10.5}, {0.88, 9}, {1, 6}};
    // the neck
    static final double[][] NECK_W = {{0, 13}, {0.4, 11}, {1, 11}};
    static final double[][] NECK_F = {{0, 6}, {1, 5.4}};
    static final double[][] NECK_B = {{0, 6.2}, {1, 5.6}};
    // the head from under the chin to the crown: its middle line forward at the chin, back at the crown
    static final double[][] HEAD_W = {{0, 6}, {0.12, 10.5}, {0.3, 12.5}, {0.55, 14.6}, {0.78, 15}, {1, 12}};
    static final double[][] HEAD_F = {{0, 4}, {0.15, 8}, {0.35, 9.5}, {0.6, 10}, {0.85, 9}, {1, 6}};
    static final double[][] HEAD_B = {{0, 2}, {0.2, 5}, {0.45, 9}, {0.7, 10}, {1, 8}};
    static final double[][] HEAD_Z = {{0, 4}, {0.3, 2.5}, {0.7, 0.5}, {1, 0}};
    // the hand, palm forward: its palm, then its fingers held together, curling a little toward the palm
    static final double[][] PALM_W = {{0, 5.8}, {0.4, 8.2}, {1, 8.6}};
    static final double[][] PALM_T = {{0, 1.8}, {0.5, 1.55}, {1, 1.3}};
    static final double[][] FINGERS_W = {{0, 8.4}, {0.4, 8.2}, {1, 6.5}};
    static final double[][] FINGERS_T = {{0, 1.25}, {1, 0.9}};
    static final double[][] FINGERS_Z = {{0, 0}, {1, 1.2}};

    /** How many rings along and points round each skin: the interactive model's. */
    static final int ALONG = 44;
    static final int ROUND = 32;

    private Skin() {
    }

    /**
     * Every part's shell, posed as {@code rig} is: the lofts in flat panels, its joints ringed, its limbs banded, its
     * head the helmet {@code design} has.
     */
    static Map<Part, List<Surface>> drawn(final Rig rig, final RobotDesign design) {
        final Map<Part, List<Surface>> by = new EnumMap<>(Part.class);
        final double b = rig.proportions().build();
        for (final Part p : Part.values()) {
            final List<Surface> placed = new ArrayList<>();
            for (final Surface s : shell(p, rig.twist(p.side()), design)) {
                // broader and deeper about the part's own axis as the body's build is; then where the part is
                placed.add(s.moved(v -> rig.place(p, new V3(v.x() * b, v.y(), v.z() * b))));
            }
            by.put(p, placed);
        }
        return by;
    }

    /** How a shell is lofted: in so many flat panels round. */
    record Make(int panels) {
        Surface of(final String name, final int along, final int round, final Loft.Skin skin) {
            return Loft.of(name, Paint.SHELL, along, panels, skin, false);
        }
    }

    /** How many flat panels round a robot's shell is. */
    static final int PANELS = 10;
    static final Make ROBOT = new Make(PANELS);

    /** {@code p}'s shell in its own frame, as on the right side, lofted as {@code make} lofts. */
    static List<Surface> shapes(final Part p, final double twist, final Make make) {
        final String n = p.said();
        return switch (p) {
            case PELVIS -> List.of(make.of(n, ALONG, ROUND, (t, phi) ->
                    trunk(t, phi, -9, 24, PELVIS_W, PELVIS_F, PELVIS_B, 0.8, 8)));
            case ABDOMEN -> List.of(make.of(n, ALONG, ROUND, (t, phi) ->
                    trunk(t, phi, -6, 24, ABDOMEN_W, ABDOMEN_F, ABDOMEN_B, 0.85, 8).plus(new V3(0, 0, 0.5))));
            case CHEST -> List.of(make.of(n, ALONG, ROUND, (t, phi) ->
                    trunk(t, phi, -5, 35, CHEST_W, CHEST_F, CHEST_B, 0.85, 6).plus(new V3(0, 0, -0.5))));
            case NECK -> List.of(make.of(n, 24, ROUND, (t, phi) ->
                    trunk(t, phi, -4, 16, NECK_W, NECK_F, NECK_B, 1, 8).plus(new V3(0, 0, 0.5))));
            case HEAD -> head(make);
            case RIGHT_GIRDLE, LEFT_GIRDLE -> List.of(Shapes.capsule(n, Paint.SKIN, new V3(4, 8.8, -9.8),
                    new V3(15.5, 3.0, -7.2), new double[] {4.2, 4.6, 5.0}, 0.8, 0.8, 20));
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM -> List.of(make.of(n, ALONG, ROUND, (t, phi) -> {
                final double f = endF(t);
                final double a = ip(UPPER_W, t) / 2 * f;
                final double d = ip(UPPER_D, t) / 2 * f;
                return new V3(a * Math.cos(phi), 3 - 36 * t, d * Math.sin(phi));
            }));
            case RIGHT_FOREARM, LEFT_FOREARM -> List.of(make.of(n, 40, 30, (t, phi) -> {
                final double f = endF(t);
                final V3 v = new V3(ip(FORE_W, t) / 2 * f * Math.cos(phi), 1 - 27.5 * t,
                        ip(FORE_D, t) / 2 * f * Math.sin(phi));
                return Rig.twisted(v, twist, sw(t));
            }));
            case RIGHT_HAND, LEFT_HAND -> hand(n, make);
            case RIGHT_THIGH, LEFT_THIGH -> List.of(make.of(n, 48, ROUND, (t, phi) -> {
                final double f = endF(t);
                final double a = ip(THIGH_W, t) / 2 * f;
                final double d = ip(THIGH_D, t) / 2 * f;
                return new V3(2.4 * (1 - t) + a * Math.cos(phi), 4 - 48 * t, d * Math.sin(phi));
            }));
            case RIGHT_SHIN, LEFT_SHIN -> List.of(make.of(n, ALONG, ROUND, (t, phi) -> {
                final double f = endF(t);
                final double a = ip(SHIN_W, t) / 2 * f;
                final double d = ip(SHIN_D, t) / 2 * f;
                return new V3(a * Math.cos(phi), 2 - 46 * t, -0.8 * prof(t, 0.3, 0.25) + d * Math.sin(phi));
            }));
            case RIGHT_FOOT, LEFT_FOOT -> List.of(make.of(n, 36, 30, (s, phi) -> {
                final double z = -5.5 + 21 * s;
                final double f = endF(s, 10);
                final double a = ip(FOOT_W, z) / 2 * f;
                final double top = ip(FOOT_TOP, z);
                final double bottom = -6.6;
                final double yc = (top + bottom) / 2;
                final double d = (top - bottom) / 2 * f;
                final double c = Math.cos(phi);
                final double sn = Math.sin(phi);
                final double x = a * se(c, 2.0 / 3);
                double y = yc + d * se(sn, 2.0 / 3);
                // its arch, under its inner side
                if (sn < 0 && x < 0) {
                    y += 1.8 * prof(z, 4, 4) * (-x / Math.max(a, 0.01)) * Math.min(1, (yc - y) / Math.max(d, 0.01));
                }
                return new V3(x, y, z);
            }));
            case RIGHT_TOES, LEFT_TOES -> List.of(make.of(n, 16, 30, (s, phi) -> {
                final double z0 = 7.2 * s;
                final double f = endF(s, 6);
                final double a = ip(TOES_W, z0) / 2 * f;
                final double top = ip(TOES_TOP, z0);
                final double bottom = -1.2;
                final double yc = (top + bottom) / 2;
                final double d = (top - bottom) / 2 * f;
                final double x = a * se(Math.cos(phi), 2.0 / 3);
                return new V3(x, yc + d * se(Math.sin(phi), 2.0 / 3), z0 * (1 - 0.045 * x) + 0.4);
            }));
        };
    }

    /**
     * A point of a part up the middle: {@code t} along from {@code from} up {@code tall}, its width and its depth
     * before and behind its middle line read off profiles, its section a superellipse ({@code square} below 1 the
     * squarer), drawn in at its ends ({@code ends} the more steeply the higher).
     */
    static V3 trunk(final double t, final double phi, final double from, final double tall, final double[][] wide,
                    final double[][] front, final double[][] back, final double square, final double ends) {
        final double f = endF(t, ends);
        final double sn = Math.sin(phi);
        final double a = ip(wide, t) / 2 * f;
        final double d = (sn > 0 ? ip(front, t) : ip(back, t)) * f;
        return new V3(a * se(Math.cos(phi), square), from + tall * t, d * se(sn, square));
    }

    /**
     * A point of the head, {@code t} from under its chin up to its crown, {@code phi} round from its right (its face
     * at a quarter round): one egg, narrow at the chin, broadest at the temples, its middle line forward at the chin.
     */
    static V3 headAt(final double t, final double phi) {
        final double f = endF(t, 5);
        final double sn = Math.sin(phi);
        final double a = ip(HEAD_W, t) / 2 * f;
        final double d = (sn > 0 ? ip(HEAD_F, t) : ip(HEAD_B, t)) * f;
        return new V3(a * se(Math.cos(phi), 0.9), -7 + 23 * t, ip(HEAD_Z, t) + d * se(sn, 0.9));
    }

    /** The head: one egg from under its chin to its crown; its ears; its nose. */
    static List<Surface> head(final Make make) {
        final List<Surface> head = new ArrayList<>();
        head.add(make.of("head", ALONG, ROUND, Skin::headAt));
        for (final int side : new int[] {-1, 1}) {
            head.add(Shapes.ellipsoid(side > 0 ? "right ear" : "left ear", Paint.SKIN, new V3(side * 7.2, 4, -1),
                    0.8, 3, 1.8, 10, 6));
        }
        head.add(Shapes.capsule("nose", Paint.SKIN, new V3(0, 6, 10.8), new V3(0, 2.6, 12.2), 1.0, 1, 10));
        return head;
    }

    /** A hand, palm forward, thumb out: its palm, its fingers held together, its thumb. */
    static List<Surface> hand(final String n, final Make make) {
        return List.of(
                make.of(n + " palm", 24, 24, (t, phi) -> {
                    final double f = endF(t, 6);
                    return new V3(ip(PALM_W, t) / 2 * f * se(Math.cos(phi), 0.8), 1 - 11 * t,
                            ip(PALM_T, t) * f * se(Math.sin(phi), 0.8));
                }),
                make.of(n + " fingers", 24, 24, (t, phi) -> {
                    final double f = endF(t, 4);
                    return new V3(ip(FINGERS_W, t) / 2 * f * se(Math.cos(phi), 0.7), -8.5 - 9.5 * t,
                            ip(FINGERS_Z, t) + ip(FINGERS_T, t) * f * se(Math.sin(phi), 0.8));
                }),
                Shapes.capsule(n + " thumb", Paint.SKIN, new V3(3.4, -1.5, 0.8), new V3(5.6, -7.5, 2.6),
                        new double[] {1.3, 1.1, 0.9}, 1, 12));
    }

    /** A robot's shell on part {@code p}: its skin's lofts in panels, a human's face's nose and ears gone; its rings,
     * bands, visor and pods. */
    static List<Surface> shell(final Part p, final double twist, final RobotDesign design) {
        final List<Surface> s = new ArrayList<>();
        if (p == Part.HEAD) {
            // its head the designed helmet, in its shell's colour
            return RobotFrame.helmet(design.head(), Paint.SHELL);
        }
        for (final Surface skin : shapes(p, twist, ROBOT)) {
            if (skin.name().equals("nose") || skin.name().endsWith(" ear")) {
                continue;
            }
            s.add(skin.paint() == Paint.SKIN
                    ? new Surface(skin.name(), Paint.SHELL, skin.rings(), skin.start(), skin.end(), false) : skin);
        }
        switch (p) {
            case HEAD -> {
                s.add(Shapes.capsule("visor", Paint.VISOR, new V3(-5.2, 6, 10.2), new V3(5.2, 6, 10.2),
                        new double[] {1.5, 1.5}, 0.6, 0.6, 12));
                for (final int side : new int[] {-1, 1}) {
                    s.add(ring("pod", Paint.HOUSING, new V3(side * 6.6, 4, -1), new V3(side * 8.2, 4, -1), 2.4, 1));
                }
            }
            case NECK -> s.add(ring("neck ring", Paint.JOINT_RING, new V3(0, -0.5, 0.5), new V3(0, 1.5, 0.5), 6.4, 1));
            case ABDOMEN -> s.add(ring("waist ring", Paint.JOINT_RING, new V3(0, -1, 0.5), new V3(0, 1.5, 0.5), 14.4,
                    0.66));
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM -> {
                s.add(ring("shoulder ring", Paint.JOINT_RING, new V3(0, 1.2, 0), new V3(0, -1.2, 0), 5.8, 1));
                s.add(ring("band", Paint.PANEL, new V3(0, -16, 0), new V3(0, -18, 0), 4.75, 1.07));
            }
            case RIGHT_FOREARM, LEFT_FOREARM -> {
                s.add(ring("elbow ring", Paint.JOINT_RING, new V3(0, 1, 0), new V3(0, -1.2, 0), 4.6, 0.9));
                s.add(ring("band", Paint.PANEL, new V3(0, -12, 0), new V3(0, -13.6, 0), 4.5, 0.85)
                        .moved(v -> Rig.twisted(v, twist, Loft.sw(0.47))));
            }
            case RIGHT_HAND, LEFT_HAND -> s.add(ring("wrist ring", Paint.JOINT_RING, new V3(0, 0.6, 0),
                    new V3(0, -1, 0), 3.2, 0.7));
            case RIGHT_THIGH, LEFT_THIGH -> s.add(ring("band", Paint.PANEL, new V3(1.2, -19, 0), new V3(1.2, -21.4, 0),
                    7.6, 0.96));
            case RIGHT_SHIN, LEFT_SHIN -> {
                s.add(ring("knee ring", Paint.JOINT_RING, new V3(0, 1.2, 0), new V3(0, -1.4, 0), 5.9, 1));
                s.add(ring("band", Paint.PANEL, new V3(0, -20, -0.4), new V3(0, -22.2, -0.4), 5.3, 1));
            }
            case RIGHT_FOOT, LEFT_FOOT -> s.add(ring("ankle ring", Paint.JOINT_RING, new V3(0, 1, 0),
                    new V3(0, -1, 0), 3.8, 1));
            default -> {
            }
        }
        return s;
    }

    /** A ring round a joint or a band round a limb: a short drum from {@code a} to {@code b}, flat-sided. */
    static Surface ring(final String n, final Paint paint, final V3 a, final V3 b, final double r, final double flat) {
        final Surface drum = Shapes.capsule(n, paint, a, b, new double[] {r, r}, flat, 0.15, PANELS);
        return new Surface(drum.name(), paint, drum.rings(), drum.start(), drum.end(), false);
    }
}
