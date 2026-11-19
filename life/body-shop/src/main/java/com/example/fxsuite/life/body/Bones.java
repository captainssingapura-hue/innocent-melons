package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A body's bones, part by part, as the interactive model of the papers draws them, balls and rods: the thigh bone and
 * its kneecap, the shin's two bones and the ankle's knobs, the foot's heel and its long bones, the toes'; the upper
 * arm's, the forearm's ulna and its radius turning across it as it twists. The pelvis, the spine and the ribs, the
 * collarbones and shoulder blades, the neck's, the skull and the jaw, and the hand's drawn the same way. Each in its
 * part's frame as on the right side; scaled with the body, not broadened with its build.
 */
final class Bones {

    private Bones() {
    }

    /** Every part's bones, posed as {@code rig} is. */
    static Map<Part, List<Surface>> drawn(final Rig rig) {
        return drawn(rig, false);
    }

    /**
     * Every part's bones, posed as {@code rig} is: a human's, or, for a robot, its frame: the same bones in polished
     * metal, and a housing round each joint.
     */
    static Map<Part, List<Surface>> drawn(final Rig rig, final boolean robot) {
        return drawn(rig, robot, RobotDesign.DEFAULT);
    }

    /** As {@link #drawn(Rig, boolean)}, a robot's spine, chest, neck and head as {@code design} has them. */
    static Map<Part, List<Surface>> drawn(final Rig rig, final boolean robot, final RobotDesign design) {
        final Map<Part, List<Surface>> by = new EnumMap<>(Part.class);
        for (final Part p : Part.values()) {
            final List<Surface> placed = new ArrayList<>();
            final List<Surface> local = new ArrayList<>(robot && RobotFrame.designed(p)
                    ? RobotFrame.part(p, design, Paint.FRAME) : local(p, rig.twist(p.side())));
            if (robot && !RobotFrame.designed(p)) {
                local.replaceAll(s -> new Surface(s.name(), Paint.FRAME, s.rings(), s.start(), s.end(), s.smooth()));
                final double r = HOUSING.getOrDefault(p.name().replace("RIGHT_", "").replace("LEFT_", ""), 0.0);
                if (r > 0) {
                    local.add(Shapes.ellipsoid(p.said() + " housing", Paint.HOUSING, V3.ZERO, r, r, r, 16, 10));
                }
            }
            for (final Surface s : local) {
                placed.add(s.moved(v -> rig.place(p, v)));
            }
            by.put(p, placed);
        }
        // the kneecaps ride on the thigh, down the front of the knee as it bends
        for (final int side : new int[] {1, -1}) {
            final V3 at = Muscles.kneecap(rig, side);
            final double s = rig.proportions().scale();
            by.get(side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH).add(
                    Shapes.ellipsoid("kneecap", robot ? Paint.HOUSING : Paint.PATELLA, at, 2.3 * s, 2.5 * s, 1.15 * s,
                            14, 8));
        }
        return by;
    }

    /** How big a robot's housing is round each joint, by the part it turns: none for the pelvis, the girdles, the toes. */
    static final java.util.Map<String, Double> HOUSING = java.util.Map.of("ABDOMEN", 3.6, "CHEST", 3.6, "NECK", 2.6,
            "HEAD", 2.4, "UPPER_ARM", 3.4, "FOREARM", 2.6, "HAND", 1.8, "THIGH", 3.6, "SHIN", 3.0, "FOOT", 2.4);

    private static Surface ball(final String n, final V3 c, final double r) {
        return Shapes.ellipsoid(n, Paint.BONE, c, r, r, r, 14, 8);
    }

    private static Surface ball(final String n, final V3 c, final double rx, final double ry, final double rz) {
        return Shapes.ellipsoid(n, Paint.BONE, c, rx, ry, rz, 14, 8);
    }

    private static Surface rod(final String n, final V3 a, final V3 b, final double r0, final double r1) {
        return Shapes.capsule(n, Paint.BONE, a, b, new double[] {r0, r1}, 1, 0.3, 10);
    }

    /** {@code p}'s bones in its own frame, as on the right side; a forearm's twisted so far. */
    static List<Surface> local(final Part p, final double twist) {
        final List<Surface> b = new ArrayList<>();
        switch (p) {
            case PELVIS -> {
                b.add(ball("sacrum", new V3(0, 5, -5), 3.5, 5, 2));
                for (final int s : new int[] {-1, 1}) {
                    b.add(ball("hip bone", new V3(s * 7, 8, -1.5), 5, 4, 1.4));
                    b.add(rod("hip socket", new V3(s * 5, 6, -1), new V3(s * 8.5, 0, 0), 1.4, 1.6));
                    b.add(rod("sitting bone", new V3(s * 4, -3, 4), new V3(s * 5, -5, -3), 1, 1.2));
                }
                b.add(rod("pubis", new V3(-4, -3, 4), new V3(4, -3, 4), 1.1, 1.1));
            }
            case ABDOMEN -> {
                for (int i = 0; i < 5; i++) {
                    b.add(ball("lumbar vertebra", new V3(0, -1 + 3 * i, -5.5), 2.6, 1.2, 2.0));
                }
            }
            case CHEST -> {
                for (int i = 0; i < 12; i++) {
                    b.add(ball("thoracic vertebra", new V3(0, 0.5 + 2.1 * i, -6.8), 2.0, 0.9, 1.7));
                }
                b.add(rod("sternum", new V3(0, 9, 9), new V3(0, 22, 9.5), 1.1, 1.3));
                // the ribs: six pairs, each round from the spine to the front, falling as it goes
                for (int k = 0; k < 6; k++) {
                    final double y = 6 + 2.8 * k;
                    final double w = 10 + 2.2 * k - 0.22 * k * k;
                    for (final int s : new int[] {-1, 1}) {
                        V3 last = null;
                        for (int j = 0; j <= 7; j++) {
                            final double a = Math.toRadians(10 + 160.0 * j / 7);
                            final V3 at = new V3(s * w * Math.sin(a), y - 3.2 * j / 7, -8.5 * Math.cos(a));
                            if (last != null) {
                                b.add(rod("rib", last, at, 0.55, 0.55));
                            }
                            last = at;
                        }
                    }
                }
            }
            case NECK -> {
                for (int i = 0; i < 4; i++) {
                    b.add(ball("cervical vertebra", new V3(0, 0.5 + 2.3 * i, -1.5), 1.6, 0.8, 1.4));
                }
            }
            case HEAD -> {
                b.add(ball("skull", new V3(0, 8, 0), 6.9, 7.5, 8.6));
                b.add(ball("jaw", new V3(0, 0, 4), 5, 3, 4.5));
            }
            case RIGHT_GIRDLE, LEFT_GIRDLE -> {
                b.add(rod("collarbone", new V3(0.5, 0.5, 0), new V3(15.5, 1.8, -6.5), 0.8, 0.9));
                b.add(ball("shoulder blade", new V3(7, -4, -15.5), 4.5, 6.5, 0.8));
            }
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM -> {
                b.add(ball("head of the humerus", V3.ZERO, 2.6));
                b.add(rod("humerus", new V3(0, -2, 0), new V3(0, -29, 0), 1.15, 1.05));
                b.add(rod("elbow", new V3(-3.2, -31, 0), new V3(3.2, -31, 0), 1.5, 1.5));
            }
            case RIGHT_FOREARM, LEFT_FOREARM -> {
                b.add(rod("ulna", Rig.ULNA_TOP, Rig.ULNA_BOTTOM, 1.3, 0.7));
                b.add(ball("olecranon", Rig.ULNA_TOP, 1.7));
                b.add(ball("ulna's head", Rig.ULNA_BOTTOM, 0.85));
                final V3 foot = Rig.twisted(Rig.RADIUS_BOTTOM, twist, 1);
                b.add(Shapes.capsule("radius", Paint.RADIUS, Rig.RADIUS_TOP, foot, new double[] {1.25, 0.8}, 1, 0.3,
                        10));
                b.add(Shapes.ellipsoid("radius's head", Paint.RADIUS, Rig.RADIUS_TOP, 1.15, 1.15, 1.15, 12, 7));
                b.add(Shapes.ellipsoid("radius's foot", Paint.RADIUS, foot, 1.25, 1.25, 1.25, 12, 7));
            }
            case RIGHT_HAND, LEFT_HAND -> {
                for (int i = 0; i < 4; i++) {
                    final double x0 = -2.4 + 1.2 * i;
                    final double x1 = -3.3 + 1.65 * i;
                    b.add(rod("metacarpal", new V3(x0, -1.5, 0), new V3(x1, -9, 0), 0.45, 0.4));
                    b.add(rod("finger", new V3(x1, -9.5, 0.1), new V3(x1 * 0.95, -17, 1.1), 0.36, 0.3));
                }
                b.add(rod("thumb's metacarpal", new V3(2.6, -1.5, 0.6), new V3(4.6, -5.5, 1.8), 0.5, 0.45));
                b.add(rod("thumb", new V3(4.7, -6, 1.9), new V3(5.6, -8.6, 2.8), 0.4, 0.35));
            }
            case RIGHT_THIGH, LEFT_THIGH -> {
                b.add(ball("head of the femur", V3.ZERO, 2.5));
                b.add(rod("neck of the femur", V3.ZERO, new V3(5.4, -3.6, 0), 1.5, 1.5));
                b.add(ball("trochanter", new V3(6.2, -3.2, -0.3), 1.9));
                b.add(rod("femur", new V3(5.4, -4.2, 0.2), new V3(0.6, -39.5, 0.4), 1.5, 1.35));
                b.add(ball("inner condyle", new V3(-2.4, -41.4, -0.4), 2.5));
                b.add(ball("outer condyle", new V3(2.4, -41.4, -0.4), 2.4));
            }
            case RIGHT_SHIN, LEFT_SHIN -> {
                b.add(rod("tibial plateau", new V3(-3.4, -1.6, 0.2), new V3(3.4, -1.6, 0.2), 1.3, 1.3));
                b.add(rod("tibia", new V3(0, -2.5, 0.6), new V3(-0.4, -40.5, 0.4), 1.9, 1.3));
                b.add(ball("inner ankle", new V3(-2.4, -41.6, 0.5), 1.3));
                b.add(ball("fibula's head", new V3(3.3, -4.8, -1.3), 1.1));
                b.add(rod("fibula", new V3(3.3, -5, -1.3), new V3(3.1, -43, -0.9), 0.7, 0.6));
                b.add(ball("outer ankle", new V3(3.1, -43.4, -0.9), 1.25));
            }
            case RIGHT_FOOT, LEFT_FOOT -> {
                b.add(ball("talus", new V3(0, 0.2, 0.5), 2.1, 1.7, 2.5));
                b.add(ball("heel bone", new V3(0, -4.2, -3.0), 2.3, 2.3, 3.4));
                for (int i = 0; i < 5; i++) {
                    b.add(rod("metatarsal", new V3(-2 + i, -3, 5.5), new V3(-3.6 + 1.9 * i, -5, 12.6 - 0.45 * i),
                            0.55, 0.45));
                }
            }
            case RIGHT_TOES, LEFT_TOES -> {
                for (int i = 0; i < 5; i++) {
                    final double x = -3.4 + 1.75 * i;
                    b.add(rod("toe", new V3(x, 0.2, 0.4), new V3(x * 1.05, -0.2, 6.6 - 0.6 * i), 0.45, 0.35));
                }
            }
        }
        return b;
    }
}
