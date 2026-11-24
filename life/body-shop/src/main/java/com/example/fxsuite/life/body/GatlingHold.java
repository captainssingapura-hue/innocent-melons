package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * The Gatling gun slung at the right hip: hung from one point on its top, over its centre of mass, by a strap round the
 * body over the left shoulder, so it hangs level of itself, pivoting there; its left side against the outside of the
 * right hip, which keeps it from swinging in toward the middle of the body as the strap pulls it; carried there by the
 * pelvis. Pointing straight ahead, square to the torso, tipped up or down by the hands, as aimed. The hands steady it,
 * the right on the D handle at its back, the left reaching across to the side handle out on its left, the arm near straight:
 * the torso, the gun and the left arm a triangle, square at the hip. Each arm reaches its easy way ({@link Reach}). What
 * it takes of the hands, the strap and the hip to hold it still: {@link GunStatics}.
 *
 * <p>The gun is drawn in a frame of its own ({@link #model}): from the middle of its receiver's back, to its right, up
 * and ahead along its barrels; cm.
 */
public final class GatlingHold {

    /** Where the right hand closes on the D handle and the left on the side handle, in the gun's frame. */
    static final V3 REAR_GRIP = new V3(9, 1, 42);
    static final V3 CARRY_GRIP = new V3(-16, 4, 68);
    /** How far along the gun the side handle runs: the left hand takes it wherever its arm is near straight. */
    static final double CARRY_FROM = 56;
    static final double CARRY_TO = 84;
    /** How near straight the left arm, its reach as a share of all it could. */
    static final double NEAR_STRAIGHT = 0.97;
    /** How far the muzzle is along the gun. */
    static final double MUZZLE = 96;
    /** Where the palm closes, in the hand's own frame. */
    static final V3 PALM = new V3(0, -8, 1);

    /**
     * How the gun is slung: where its back's middle is, from the right hip joint standing at rest (out, up, ahead, cm);
     * the one point it hangs from, its swivel, where both ends of the strap meet; and where its side bears on the hip;
     * both in its own frame.
     */
    public record Sling(V3 fromHip, V3 swivel, V3 bears) {

        /**
         * As the gun is slung unless said otherwise: beside the hip, its left side on the hip's outside; its swivel on
         * its top, a little to the left, over its centre of mass; hung as far back as the shoulders stand, the girdle hung
         * from the sternum as it is measured, so the left hand reaches the side handle with its arm near straight.
         */
        public static final Sling DEFAULT = new Sling(new V3(13, 1, -31), new V3(-5, 7, 32.6), new V3(-5.5, 1, 32.6));

        public Sling with(final V3 fromHip, final V3 swivel) {
            return new Sling(fromHip, swivel, bears);
        }
    }
    /** How far the chest is turned to the right over the gun, bringing the left shoulder forward, degrees. */
    static final double SQUARED = 20;
    /** The strap's width and thickness, cm. */
    static final double STRAP_WIDE = 5;
    static final double STRAP_THICK = 0.6;
    /**
     * The strap's way round the body, each point in a part's frame, as on the right side: from the swivel up the chest
     * to the left shoulder, over it, down the back, and back down to the swivel; both its ends leaving the swivel
     * upward, so it hangs the gun.
     */
    private static final List<On> STRAP = List.of(
            new On(Part.CHEST, new V3(3, 3, 12.5)),
            new On(Part.CHEST, new V3(-5, 11, 12)),
            new On(Part.CHEST, new V3(-10, 19, 9.5)),
            new On(Part.CHEST, new V3(-11, 24, 1)),
            new On(Part.CHEST, new V3(-11, 21.5, -6)),
            new On(Part.CHEST, new V3(-5, 13, -11.5)),
            new On(Part.CHEST, new V3(6, 3, -11.5)));

    private record On(Part part, V3 at) {
    }

    /** The gun's frame: its back's middle, and its ways right, up and ahead, in the body's frame. */
    public record Frame(V3 origin, V3 right, V3 up, V3 ahead) {

        /** {@code local}, in the gun's frame (right, up, ahead), in the body's. */
        public V3 place(final V3 local) {
            return origin.plus(right.times(local.x())).plus(up.times(local.y())).plus(ahead.times(local.z()));
        }
    }

    /**
     * Held: the pose, its arms on the gun; where the gun is, and how slung; the strap's way round the body from the
     * back lug to the front, and the strap drawn, in the body's frame.
     */
    public record Held(Pose pose, Frame gun, Sling sling, List<V3> way, List<Surface> strap) {
    }

    private final Proportions proportions;
    private final Sling sling;
    /** Where the gun's swivel hangs, in the pelvis's own frame: the gun turns about it as it is aimed. */
    private final V3 rests;
    /** How far the left arm reaches, its shoulder to its palm, straight. */
    private final double reach;

    public GatlingHold(final Proportions proportions) {
        this(proportions, Sling.DEFAULT);
    }

    public GatlingHold(final Proportions proportions, final Sling sling) {
        this.proportions = proportions;
        this.sling = sling;
        final Rig rest = new Rig(proportions, Pose.REST);
        final V3 hip = rest.frame(Part.RIGHT_THIGH).at();
        rests = rest.frame(Part.PELVIS).local(hip.plus(sling.fromHip().times(proportions.scale())).plus(sling.swivel()));
        // the left arm's whole reach, its shoulder to its palm held straight
        final Rig straight = new Rig(proportions, Pose.REST.with(Part.LEFT_FOREARM, Dof.ELBOW, 0));
        reach = straight.held(Part.LEFT_HAND, PALM).minus(straight.frame(Part.LEFT_UPPER_ARM).at()).length();
    }

    /**
     * {@code pose} (its arms as they last held the gun, or ready) with the gun held, aimed {@code aim} degrees up: its
     * back where the pelvis carries it, turned up about it; the hands reaching to its handles; its strap round the body.
     */
    public Held hold(final Pose pose, final double aim) {
        final Rig rig = new Rig(proportions, pose);
        final V3 swivel = rig.frame(Part.PELVIS).place(rests);
        final double a = Math.toRadians(aim);
        final V3 ahead = new V3(0, Math.sin(a), Math.cos(a));
        final V3 up = new V3(0, Math.cos(a), -Math.sin(a));
        final V3 s = sling.swivel();
        final Frame gun = new Frame(swivel.minus(V3.X.times(s.x())).minus(up.times(s.y())).minus(ahead.times(s.z())), V3.X,
                up, ahead);
        // the chest turned to the right over the gun, the left shoulder brought forward; the head turned back to look
        // ahead; the left hand to come over the handle palm down, the right round the D handle palm in
        Pose p = pose;
        for (final Part trunk : new Part[] {Part.ABDOMEN, Part.CHEST}) {
            p = p.with(trunk, Dof.SPINE_TWIST, pose.get(trunk, Dof.SPINE_TWIST) - SQUARED / 2);
        }
        for (final Part n : new Part[] {Part.NECK, Part.HEAD}) {
            p = p.with(n, Dof.NECK_TURN, pose.get(n, Dof.NECK_TURN) + SQUARED / 2);
        }
        p = p.with(Part.LEFT_FOREARM, Dof.FOREARM_TWIST, 150).with(Part.RIGHT_FOREARM, Dof.FOREARM_TWIST, 85);
        p = Reach.toward(proportions, p, Reach.arm(1, 15, 8, 75), Part.RIGHT_HAND, PALM, gun.place(REAR_GRIP));
        // the left arm reaching across, the chest turning further with it if it must; the right hand back on its grip
        final List<Reach.Way> across = new ArrayList<>(Reach.arm(-1, 50, -8, 15));
        for (final Part trunk : new Part[] {Part.ABDOMEN, Part.CHEST}) {
            across.add(new Reach.Way(trunk, Dof.SPINE_TWIST, p.get(trunk, Dof.SPINE_TWIST), 0.01));
        }
        p = Reach.toward(proportions, p, across, Part.LEFT_HAND, PALM, carried(p, gun));
        p = Reach.toward(proportions, p, Reach.arm(1, 15, 8, 75), Part.RIGHT_HAND, PALM, gun.place(REAR_GRIP));
        final List<V3> way = way(new Rig(proportions, p), gun, sling);
        return new Held(p, gun, sling, way, strap(way, new Rig(proportions, p)));
    }

    public Sling sling() {
        return sling;
    }

    /**
     * Where along the side handle the left hand takes it, standing as {@code pose} has it: where its arm reaches near
     * straight from its shoulder, or the handle's nearer end or further, if that is beyond it.
     */
    V3 carried(final Pose pose, final Frame gun) {
        final Rig rig = new Rig(proportions, pose);
        final V3 shoulder = rig.frame(Part.LEFT_UPPER_ARM).at();
        final double want = NEAR_STRAIGHT * reach;
        V3 best = gun.place(CARRY_GRIP);
        double off = Double.POSITIVE_INFINITY;
        for (double z = CARRY_FROM + 3; z <= CARRY_TO - 3; z += 0.5) {
            final V3 at = gun.place(new V3(CARRY_GRIP.x(), CARRY_GRIP.y(), z));
            final double miss = Math.abs(at.minus(shoulder).length() - want);
            if (miss < off) {
                off = miss;
                best = at;
            }
        }
        return best;
    }

    /** The strap's way: from the gun's swivel round the body as it stands, and back to it. */
    static List<V3> way(final Rig rig, final Frame gun, final Sling sling) {
        final List<V3> way = new ArrayList<>();
        way.add(gun.place(sling.swivel()));
        for (final On o : STRAP) {
            way.add(rig.place(o.part(), o.at()));
        }
        way.add(gun.place(sling.swivel()));
        return way;
    }

    /** The strap along {@code way}: a flat band, piece by piece, lying on the body. */
    static List<Surface> strap(final List<V3> way, final Rig rig) {
        final V3 middle = rig.place(Part.CHEST, new V3(0, 6, 0));
        final List<Surface> band = new ArrayList<>();
        for (int k = 0; k + 1 < way.size(); k++) {
            final V3 a = way.get(k);
            final V3 b = way.get(k + 1);
            // flat on the body: wide across it, square to the way it runs
            final V3 out = a.plus(b).times(0.5).minus(middle);
            final V3 across = out.cross(b.minus(a));
            // a little past each end, so the pieces meet without a gap
            final V3 u = b.minus(a).unit().times(STRAP_WIDE * 0.2);
            band.add(Shapes.bar("strap " + k, Paint.STRAP, a.minus(u), b.plus(u), across, STRAP_WIDE, STRAP_THICK));
        }
        return band;
    }

    /** Arms at the start, before they first reach: as they hold it, near enough. */
    public static Pose ready(final Pose pose) {
        return pose.with(Part.RIGHT_UPPER_ARM, Dof.RAISE_FORWARD, 15).with(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 8)
                .with(Part.RIGHT_FOREARM, Dof.ELBOW, 75).with(Part.LEFT_UPPER_ARM, Dof.RAISE_FORWARD, 50)
                .with(Part.LEFT_UPPER_ARM, Dof.RAISE_SIDEWAYS, -8).with(Part.LEFT_FOREARM, Dof.ELBOW, 15);
    }

    /** The arms of {@code from} on {@code pose}: to start reaching where they last were. */
    public static Pose arms(final Pose pose, final Pose from) {
        Pose p = pose;
        for (final Part part : new Part[] {Part.RIGHT_GIRDLE, Part.LEFT_GIRDLE, Part.RIGHT_UPPER_ARM,
            Part.LEFT_UPPER_ARM, Part.RIGHT_FOREARM, Part.LEFT_FOREARM}) {
            for (final Dof d : part.dofs()) {
                p = p.with(part, d, from.get(part, d));
            }
        }
        return p;
    }

    /** Whether {@code piece} of the gun turns with its barrels: the barrels, the spindle and the clamps. */
    public static boolean turns(final Surface piece) {
        return piece.name().startsWith("barrel") || piece.name().startsWith("spindle")
                || piece.name().startsWith("clamp");
    }

    /**
     * The gun, in its own frame: its receiver, the motor on its back, a butt plate; six barrels round a spindle, held
     * by three clamps; the D handle on its right, the side handle out on its left, ahead; the ammunition box
     * under it; the swivel on its top it hangs from.
     */
    public static List<Surface> model(final Sling sling) {
        final List<Surface> s = new ArrayList<>();
        s.add(Shapes.box("receiver", Paint.GUN, new V3(0, 0, 15), 11, 12, 30));
        s.add(Shapes.box("butt plate", Paint.GUN_FITTINGS, new V3(0, 0, -0.8), 12.5, 13, 1.6));
        s.add(Shapes.capsule("motor", Paint.GUN_FITTINGS, new V3(0, 8.5, 2), new V3(0, 8.5, 16), new double[] {3.8,
            3.8}, 1, 0.3, 16));
        for (int i = 0; i < 6; i++) {
            final double t = Math.toRadians(i * 60 + 30);
            final V3 at = new V3(4.2 * Math.cos(t), 4.2 * Math.sin(t), 0);
            s.add(Shapes.capsule("barrel " + i, Paint.GUN, at.plus(new V3(0, 0, 30)), at.plus(new V3(0, 0, MUZZLE)),
                    new double[] {1.4, 1.4}, 1, 0.2, 8));
        }
        s.add(Shapes.capsule("spindle", Paint.GUN_FITTINGS, new V3(0, 0, 30), new V3(0, 0, MUZZLE + 2),
                new double[] {1.2, 1.2}, 1, 0.3, 8));
        for (final double z : new double[] {33, 63, 92}) {
            s.add(Shapes.capsule("clamp at " + (int) z, Paint.GUN_FITTINGS, new V3(0, 0, z), new V3(0, 0, z + 3),
                    new double[] {6.6, 6.6}, 1, 0.1, 18));
        }
        // the D handle: a grip upright on the right, two arms back to the receiver
        s.add(Shapes.capsule("rear grip", Paint.GUN_FITTINGS, new V3(9, -5, REAR_GRIP.z()), new V3(9, 7, REAR_GRIP.z()),
                new double[] {1.7, 1.7}, 1, 0.5, 10));
        s.add(Shapes.bar("rear grip arm low", Paint.GUN_FITTINGS, new V3(5, -5, REAR_GRIP.z()), new V3(9, -5, REAR_GRIP.z()),
                V3.Z, 2.4, 2));
        s.add(Shapes.bar("rear grip arm high", Paint.GUN_FITTINGS, new V3(5, 7, REAR_GRIP.z()), new V3(9, 7, REAR_GRIP.z()),
                V3.Z, 2.4, 2));
        // the side handle: a bar along the barrels out on their left, on two arms from them
        s.add(Shapes.bar("side arm back", Paint.GUN_FITTINGS, new V3(-5, CARRY_GRIP.y(), CARRY_FROM + 1.5),
                new V3(CARRY_GRIP.x(), CARRY_GRIP.y(), CARRY_FROM + 1.5), V3.Y, 2.5, 2));
        s.add(Shapes.bar("side arm front", Paint.GUN_FITTINGS, new V3(-5, CARRY_GRIP.y(), CARRY_TO - 1.5),
                new V3(CARRY_GRIP.x(), CARRY_GRIP.y(), CARRY_TO - 1.5), V3.Y, 2.5, 2));
        s.add(Shapes.capsule("side handle", Paint.GUN_FITTINGS, new V3(CARRY_GRIP.x(), CARRY_GRIP.y(), CARRY_FROM),
                new V3(CARRY_GRIP.x(), CARRY_GRIP.y(), CARRY_TO),
                new double[] {1.6, 1.6}, 1, 0.5, 10));
        s.add(Shapes.box("ammunition box", Paint.AMMUNITION, new V3(0, -12, 17), 10, 11, 18));
        s.add(Shapes.box("feed chute", Paint.GUN_FITTINGS, new V3(0, -5.5, 24), 4, 3, 5));
        for (final V3 lug : new V3[] {sling.swivel()}) {
            s.add(Shapes.ellipsoid("swivel", Paint.GUN_FITTINGS, lug, 1.8, 1.8, 1.8, 10, 6));
            s.add(Shapes.bar("swivel post", Paint.GUN_FITTINGS, lug, lug.plus(new V3(0, -3, 0)), V3.Z, 1.6, 1.6));
        }
        return s;
    }
}
