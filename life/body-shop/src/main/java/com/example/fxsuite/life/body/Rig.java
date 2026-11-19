package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A body posed: where each part is and how it is turned, each turned at its joint and carried by every part it hangs
 * from (forward kinematics), in the body's frame ({@link V3}: x its right, y up, z ahead); standing on the lowest of
 * the points its feet touch the ground by, the ground {@code y} 0, unless asked to hang free. And whether it would
 * stand so: its centre of mass, the area its feet hold up, how far inside that its centre of mass falls (the papers'
 * balance check).
 *
 * <p>Every part is the papers' figure's, in its centimetres, made to the body's proportions: scaled all over, its legs
 * and its arms each as much longer as they are, its hip and its shoulder joints as much further apart; a left part
 * its right's in a mirror.
 */
public final class Rig {

    /** Where a part is: its joint, how it is turned, and how its own figures are made to the body's. */
    public record Frame(V3 at, Rot turn, V3 scale, boolean left) {

        /** {@code local}, in the part's own frame as the papers' figure has it on the right side, in the body's. */
        public V3 place(final V3 local) {
            V3 v = new V3(local.x() * scale.x(), local.y() * scale.y(), local.z() * scale.z());
            if (left) {
                v = new V3(-v.x(), v.y(), v.z());
            }
            return at.plus(turn.apply(v));
        }

        /** {@code body}, a point in the body's frame, in the part's own (as on the right side): {@link #place} undone. */
        public V3 local(final V3 body) {
            V3 v = turn.back().apply(body.minus(at));
            if (left) {
                v = new V3(-v.x(), v.y(), v.z());
            }
            return new V3(v.x() / scale.x(), v.y() / scale.y(), v.z() / scale.z());
        }

        /** {@code way}, a direction in the part's own frame, in the body's (not scaled). */
        public V3 way(final V3 local) {
            return turn.apply(left ? new V3(-local.x(), local.y(), local.z()) : local);
        }
    }

    /** The forearm's bones, in its frame: the ulna's top and foot, the radius's top and foot, palm forward. */
    static final V3 ULNA_TOP = new V3(-1.2, 0.4, -1.5);
    static final V3 ULNA_BOTTOM = new V3(-1.9, -24.6, -0.5);
    static final V3 RADIUS_TOP = new V3(1.6, -1.0, 0.3);
    static final V3 RADIUS_BOTTOM = new V3(1.9, -24.7, 0.3);
    /** The line the forearm twists about: from the radius's top to the ulna's foot. */
    static final V3 PRONATION = ULNA_BOTTOM.minus(RADIUS_TOP).unit();

    /** Where a foot touches the ground, in its frame and its toes': its heel, the balls of its foot, its toes. */
    static final List<V3> HEEL_AND_BALL = List.of(new V3(0, -6.4, -4.0), new V3(-3.8, -6.5, 12.4),
            new V3(4.0, -6.4, 10.6));
    static final List<V3> TOE_TIPS = List.of(new V3(-2.6, -1.1, 7.6), new V3(3.4, -1.0, 6.4));
    /** How near the lowest a point must be to count as touching the ground, cm. */
    static final double TOUCHING = 0.7;

    private final Proportions proportions;
    private final Pose pose;
    private final Map<Part, Frame> frames = new EnumMap<>(Part.class);
    private final double lift;

    /** {@code proportions} holding {@code pose}, standing on the ground. */
    public Rig(final Proportions proportions, final Pose pose) {
        this(proportions, pose, true);
    }

    /** {@code proportions} holding {@code pose}: standing on the ground, or hanging free from its pelvis at 0. */
    public Rig(final Proportions proportions, final Pose pose, final boolean standing) {
        this.proportions = proportions;
        this.pose = pose;
        final Map<Part, Frame> free = new EnumMap<>(Part.class);
        for (final Part p : Part.values()) {
            final boolean left = p.side() < 0;
            final Rot own = left ? turned(p).mirrored() : turned(p);
            if (p.parent() == null) {
                free.put(p, new Frame(pose.root(), own, scale(p), false));
            } else {
                final Frame up = free.get(p.parent());
                free.put(p, new Frame(up.place(joint(p)), up.turn().then(own), scale(p), left));
            }
        }
        double low = 0;
        if (standing) {
            low = Double.POSITIVE_INFINITY;
            for (final V3 c : contacts(free)) {
                low = Math.min(low, c.y());
            }
        }
        lift = -low;
        final V3 up = new V3(0, lift, 0);
        free.forEach((p, f) -> frames.put(p, new Frame(f.at().plus(up), f.turn(), f.scale(), f.left())));
    }

    public Proportions proportions() {
        return proportions;
    }

    public Pose pose() {
        return pose;
    }

    /** Where {@code p} is. */
    public Frame frame(final Part p) {
        return frames.get(p);
    }

    /** {@code local}, in {@code p}'s own frame (as on the right side), in the body's. */
    public V3 place(final Part p, final V3 local) {
        return frames.get(p).place(local);
    }

    /** How high its hip joints are, standing so. */
    public double lift() {
        return lift;
    }

    /** How far the forearm on {@code side} is twisted from palm forward, degrees. */
    public double twist(final int side) {
        return pose.get(side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM, Dof.FOREARM_TWIST);
    }

    /** {@code p}, a point of a forearm in its frame, as its twist carries it, {@code share} of the twist felt there. */
    static V3 twisted(final V3 p, final double degrees, final double share) {
        return RADIUS_TOP.plus(Rot.about(PRONATION, degrees * share).apply(p.minus(RADIUS_TOP)));
    }

    /** How {@code p} is turned at its joint, on the right side, from the pose. */
    private Rot turned(final Part p) {
        final List<Dof> d = p.dofs();
        return switch (p) {
            case PELVIS -> Rot.aboutY(-a(p, Dof.PELVIS_TURN)).then(Rot.aboutX(a(p, Dof.TILT)))
                    .then(Rot.aboutZ(a(p, Dof.PELVIS_LIST)));
            case ABDOMEN, CHEST -> Rot.aboutY(-a(p, Dof.SPINE_TWIST)).then(Rot.aboutX(a(p, Dof.SPINE_BEND)))
                    .then(Rot.aboutZ(a(p, Dof.SPINE_SIDE)));
            case NECK, HEAD -> Rot.aboutY(-a(p, Dof.NECK_TURN)).then(Rot.aboutX(a(p, Dof.NECK_BEND)))
                    .then(Rot.aboutZ(a(p, Dof.NECK_SIDE)));
            case RIGHT_GIRDLE, LEFT_GIRDLE -> Rot.aboutY(-a(p, Dof.PROTRACT)).then(Rot.aboutZ(a(p, Dof.SHRUG)));
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM -> Rot.aboutZ(a(p, Dof.RAISE_SIDEWAYS))
                    .then(Rot.aboutX(-a(p, Dof.RAISE_FORWARD))).then(Rot.aboutY(a(p, Dof.ARM_ROTATION)));
            case RIGHT_FOREARM, LEFT_FOREARM -> Rot.aboutX(-a(p, Dof.ELBOW));
            case RIGHT_HAND, LEFT_HAND -> Rot.about(PRONATION, twist(p.side()))
                    .then(Rot.aboutX(-a(p, Dof.WRIST_BEND))).then(Rot.aboutZ(a(p, Dof.WRIST_SIDE)));
            case RIGHT_THIGH, LEFT_THIGH -> Rot.aboutZ(a(p, Dof.HIP_SIDEWAYS))
                    .then(Rot.aboutX(-a(p, Dof.HIP_FORWARD))).then(Rot.aboutY(a(p, Dof.HIP_ROTATION)));
            case RIGHT_SHIN, LEFT_SHIN -> Rot.aboutX(a(p, Dof.KNEE));
            case RIGHT_FOOT, LEFT_FOOT -> Rot.aboutX(a(p, Dof.ANKLE)).then(Rot.aboutZ(-a(p, Dof.ANKLE_ROLL)));
            case RIGHT_TOES, LEFT_TOES -> Rot.aboutX(-a(p, Dof.TOES));
        };
    }

    private double a(final Part p, final Dof d) {
        return pose.get(p, d);
    }

    /** Where {@code p}'s joint is on its parent, in its parent's frame, as the papers' figure has it; the hand's moved
     * with its forearm's twist; hips and shoulders as far apart as the body's. */
    private V3 joint(final Part p) {
        final V3 j = mirrorIfOff(p, p.joint());
        return switch (p) {
            case RIGHT_HAND, LEFT_HAND -> twisted(j, twist(p.side()), 1);
            case RIGHT_THIGH, LEFT_THIGH -> new V3(j.x() * proportions.hipScale(), j.y(), j.z());
            case RIGHT_GIRDLE, LEFT_GIRDLE, RIGHT_UPPER_ARM, LEFT_UPPER_ARM ->
                    new V3(j.x() * proportions.shoulderScale(), j.y(), j.z());
            default -> j;
        };
    }

    /**
     * {@code j}, where a part's joint is on its parent, as on the right side: on the left, if the part is a left
     * one and its parent, down the middle, does not mirror it already.
     */
    private static V3 mirrorIfOff(final Part p, final V3 j) {
        return p.side() < 0 && p.parent().side() >= 0 ? new V3(-j.x(), j.y(), j.z()) : j;
    }

    /**
     * {@code local}, a point held of part {@code p} in its own frame, where it is: a forearm's turning with its twist,
     * as its skin does near its wrist, a point of any other part as the part is.
     */
    public V3 held(final Part p, final V3 local) {
        return isForearm(p) ? place(p, twisted(local, twist(p.side()), 1)) : place(p, local);
    }

    /** {@code body}, a point of part {@code p} in the body's frame, as {@link #held} has it in the part's. */
    public V3 holding(final Part p, final V3 body) {
        final V3 local = frame(p).local(body);
        return isForearm(p) ? twisted(local, -twist(p.side()), 1) : local;
    }

    private static boolean isForearm(final Part p) {
        return p == Part.RIGHT_FOREARM || p == Part.LEFT_FOREARM;
    }

    /** How {@code p}'s own figures are made to the body's: scaled all over, a leg's or an arm's along it. */
    private V3 scale(final Part p) {
        final double s = proportions.scale();
        final double along = switch (p) {
            case RIGHT_THIGH, LEFT_THIGH, RIGHT_SHIN, LEFT_SHIN -> proportions.legScale();
            case RIGHT_UPPER_ARM, LEFT_UPPER_ARM, RIGHT_FOREARM, LEFT_FOREARM, RIGHT_HAND, LEFT_HAND ->
                    proportions.armScale();
            default -> 1;
        };
        return new V3(s, s * along, s);
    }

    private static List<V3> contacts(final Map<Part, Frame> f) {
        final List<V3> all = new ArrayList<>();
        for (final Part foot : List.of(Part.RIGHT_FOOT, Part.LEFT_FOOT)) {
            HEEL_AND_BALL.forEach(c -> all.add(f.get(foot).place(c)));
        }
        for (final Part toes : List.of(Part.RIGHT_TOES, Part.LEFT_TOES)) {
            TOE_TIPS.forEach(c -> all.add(f.get(toes).place(c)));
        }
        return all;
    }

    /** Every point its feet may touch the ground by, where it is: the right foot's five, then the left's. */
    public List<V3> contacts() {
        final List<V3> all = contacts(frames);
        // in the order: right heel and balls, left heel and balls, right toes, left toes; as the right's then the left's
        return List.of(all.get(0), all.get(1), all.get(2), all.get(6), all.get(7), all.get(3), all.get(4), all.get(5),
                all.get(8), all.get(9));
    }

    /** Those touching the ground. */
    public List<V3> touching() {
        return contacts().stream().filter(c -> c.y() < TOUCHING).toList();
    }

    /** How heavy each part is, kg. */
    public double mass(final Part p) {
        return proportions.mass() * p.mass() / 100;
    }

    /** Its centre of mass: each part's, by its share. */
    public V3 centreOfMass() {
        V3 sum = V3.ZERO;
        double m = 0;
        for (final Part p : Part.values()) {
            sum = sum.plus(place(p, p.centre()).times(mass(p)));
            m += mass(p);
        }
        return sum.times(1 / m);
    }

    /** The area its feet hold it up over: the outline round every point touching the ground, going round (x, z). */
    public List<double[]> support() {
        return hull(touching().stream().map(c -> new double[] {c.x(), c.z()}).toList());
    }

    /**
     * How far inside the area its feet hold it up over its centre of mass falls, cm: less than nothing if outside it,
     * by how far. The papers' balance margin.
     */
    public double margin() {
        final V3 c = centreOfMass();
        return margin(new double[] {c.x(), c.z()}, support());
    }

    static List<double[]> hull(final List<double[]> given) {
        if (given.size() < 3) {
            return List.copyOf(given);
        }
        final List<double[]> pts = new ArrayList<>(given);
        pts.sort(Comparator.<double[]>comparingDouble(p -> p[0]).thenComparingDouble(p -> p[1]));
        final List<double[]> lower = new ArrayList<>();
        for (final double[] p : pts) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.getLast(), p) <= 0) {
                lower.removeLast();
            }
            lower.add(p);
        }
        final List<double[]> upper = new ArrayList<>();
        for (int i = pts.size() - 1; i >= 0; i--) {
            final double[] p = pts.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.getLast(), p) <= 0) {
                upper.removeLast();
            }
            upper.add(p);
        }
        lower.removeLast();
        upper.removeLast();
        lower.addAll(upper);
        return lower;
    }

    private static double cross(final double[] o, final double[] a, final double[] b) {
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
    }

    private static double toSegment(final double[] p, final double[] a, final double[] b) {
        final double dx = b[0] - a[0];
        final double dz = b[1] - a[1];
        final double l2 = dx * dx + dz * dz;
        final double t = l2 == 0 ? 0 : Math.clamp(((p[0] - a[0]) * dx + (p[1] - a[1]) * dz) / l2, 0, 1);
        return Math.hypot(p[0] - a[0] - t * dx, p[1] - a[1] - t * dz);
    }

    static double margin(final double[] p, final List<double[]> h) {
        if (h.isEmpty()) {
            return Double.NEGATIVE_INFINITY;
        }
        if (h.size() == 1) {
            return -Math.hypot(p[0] - h.getFirst()[0], p[1] - h.getFirst()[1]);
        }
        if (h.size() == 2) {
            return -toSegment(p, h.get(0), h.get(1));
        }
        boolean inside = true;
        double least = Double.POSITIVE_INFINITY;
        double out = Double.POSITIVE_INFINITY;
        for (int i = 0; i < h.size(); i++) {
            final double[] a = h.get(i);
            final double[] b = h.get((i + 1) % h.size());
            final double ex = b[0] - a[0];
            final double ez = b[1] - a[1];
            final double c = (ex * (p[1] - a[1]) - ez * (p[0] - a[0])) / Math.hypot(ex, ez);
            if (c < 0) {
                inside = false;
            }
            least = Math.min(least, c);
            out = Math.min(out, toSegment(p, a, b));
        }
        return inside ? least : -out;
    }
}
