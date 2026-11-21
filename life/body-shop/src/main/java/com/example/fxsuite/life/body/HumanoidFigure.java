package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.look.Figure;
import com.example.fxsuite.avatar.look.JetPack;
import com.example.fxsuite.avatar.sim.Arms;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.avatar.sim.Weapon;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * The body shop's humanoid as the avatar: a robot, exposed or shelled, or a human, drawn as the game's avatar in
 * place of the courier ({@link Figure}), as a puppet ({@link Puppet}).
 *
 * <p>On its feet, it walks and runs as its gait has it ({@link Gait}), at the speed the avatar goes over the ground,
 * carried on by its own feet ({@link Walker}): its feet do not slide, though its steps fall in their own time, not the
 * game's; walking back, its stride run backward; its head turned and tipped as the avatar looks, on top. Off its feet,
 * jumping, falling or flying, the avatar's pose read onto its joints ({@link #read}); tipped, banked and raised whole as
 * the courier is. Of the weapons, the Gatling gun only, slung at its right hip ({@link GatlingHold}), its barrels
 * turning as fast as the game says they spin, a flash at its muzzle as it fires. Its jet-pack on its back.
 */
public final class HumanoidFigure implements Figure {

    /** How far its soles stand clear of the ground; its arms drawn up and ahead seen from its eyes, as the courier's. */
    static final double SOLE = 0.5;
    static final double IN_VIEW_UP = 30;
    static final double IN_VIEW_AHEAD = 30;
    /** How fast the barrels are drawn turning at full speed, degrees a second; how long a flash shows, seconds. */
    static final double BARRELS_TURN = 1500;
    static final double FLASH = 0.035;

    private final Puppet puppet;
    private final Proportions proportions;
    private final double middle;
    private final Walker walker;
    private final GatlingHold hold;
    private Pose arms = GatlingHold.ready(Pose.REST);
    private double was = Double.NaN;

    private final Group root = new Group();
    private final Translate at = new Translate();
    private final Rotate turn = new Rotate(0, Rotate.Y_AXIS);
    private final Translate inView = new Translate();
    private final Translate raise = new Translate();
    private final Rotate bank;
    private final Rotate lean;
    private final JetPack pack = new JetPack();
    private final Group gun = new Group();
    private final Affine gunAt = new Affine();
    private final Rotate barrels = new Rotate(0, Rotate.Z_AXIS);
    private final Node flash;
    private final Group strap = new Group();
    private final Group effects = new Group();
    private boolean fromEyes;
    private boolean headless;
    private Rig last;

    /** Where its eyes are, in its head's frame. */
    static final V3 EYES = new V3(0, 7, 7.5);
    /** The cameras on its body, and where each is, in its chest's frame: on its breastbone; on the strap, on its left shoulder. */
    static final List<String> CAMS = List.of("chest", "shoulder");
    static final V3 ON_CHEST = new V3(0, 12, 15);
    static final V3 ON_SHOULDER = new V3(-10, 19.5, 12);

    public HumanoidFigure(final Puppet.Kind kind) {
        proportions = Proportions.PAPERS;
        puppet = new Puppet(kind, proportions, RobotDesign.DEFAULT, Palette.DEFAULT, new V3(1, -1, 1));
        walker = new Walker(proportions);
        hold = new GatlingHold(proportions);
        final Rig rest = new Rig(proportions, Pose.REST);
        middle = rest.lift();
        bank = new Rotate(0, 0, -middle, 0, Rotate.Z_AXIS);
        lean = new Rotate(0, 0, -middle, 0, Rotate.X_AXIS);

        // the gun, drawn in its own frame: its barrels turning about its length, the rest still; a flash at its muzzle
        final List<Surface> still = new ArrayList<>();
        final List<Surface> turning = new ArrayList<>();
        for (final Surface s : GatlingHold.model(GatlingHold.Sling.DEFAULT)) {
            (GatlingHold.turns(s) ? turning : still).add(s);
        }
        final Group spun = FigureModel.meshes(turning, Palette.DEFAULT, Puppet::point);
        spun.getTransforms().add(barrels);
        flash = FigureModel.meshes(List.of(Shapes.ellipsoid("muzzle flash", Paint.FLASH,
                new V3(0, 0, GatlingHold.MUZZLE + 5), 6, 6, 9, 10, 6)), Palette.DEFAULT, Puppet::point);
        flash.setVisible(false);
        gun.getChildren().addAll(FigureModel.meshes(still, Palette.DEFAULT, Puppet::point), spun, flash);
        gun.getTransforms().add(gunAt);
        gun.setVisible(false);
        strap.setVisible(false);

        final Group body = new Group(puppet.node(), gun, strap);
        body.getTransforms().addAll(inView, raise, bank, lean);
        // its jet-pack on its back, on its chest, as the courier wears one from its feet up
        final double chest = rest.frame(Part.CHEST).at().y();
        final Group carry = new Group(pack.node());
        carry.getTransforms().addAll(new Scale(1, -1, 1), new Translate(0, chest, 7));
        puppet.part(Part.CHEST).getChildren().add(carry);
        root.getChildren().add(body);
        root.getTransforms().addAll(at, turn);
    }

    /**
     * Every kind, offered as the avatar's figure to the game ({@link com.example.fxsuite.avatar.look.Figures}); but the
     * muscles, found too slowly yet for a figure made as it is chosen, on the motion bench only.
     */
    public static void offer() {
        java.util.Arrays.stream(Puppet.Kind.values()).filter(k -> k != Puppet.Kind.MUSCLES).forEach(k ->
                com.example.fxsuite.avatar.look.Figures.offer(k.label(), () -> new HumanoidFigure(k)));
    }

    @Override
    public boolean holds(final Weapon weapon) {
        return weapon == Weapon.GATLING;
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public Node effects() {
        return effects;
    }

    @Override
    public void show(final Avatar avatar, final com.example.fxsuite.avatar.look.Pose pose) {
        final com.example.fxsuite.avatar.sim.Body where = avatar.body();
        at.setX(where.position().x());
        at.setY(where.position().y());
        at.setZ(where.position().z());
        turn.setAngle(where.yaw());
        posed(avatar, pose);
    }

    @Override
    public void showFromEyes(final Avatar avatar, final com.example.fxsuite.avatar.look.Pose pose, final Vec3 eye,
                             final double yaw) {
        posed(avatar, pose.upright());
        // its head's middle where the eye is, as the courier's
        final V3 head = last.place(Part.HEAD, new V3(0, 6, 0));
        final Point3D eyes = new Rotate(yaw, Rotate.Y_AXIS).transform(new Point3D(0, -(head.y() + SOLE), 0));
        at.setX(eye.x() - eyes.getX());
        at.setY(eye.y() - eyes.getY());
        at.setZ(eye.z() - eyes.getZ());
        turn.setAngle(yaw);
    }

    @Override
    public boolean seesFromItsHead() {
        return true;
    }

    @Override
    public void headless(final boolean off) {
        headless = off;
        fromItsEyes(fromEyes);
    }

    @Override
    public Vec3 eyesNow() {
        return last == null ? null : world(last.place(Part.HEAD, EYES));
    }

    @Override
    public List<String> bodyCams() {
        return CAMS;
    }

    @Override
    public Viewpoint bodyCam(final String name) {
        if (last == null) {
            return null;
        }
        final Rig.Frame chest = last.frame(Part.CHEST);
        final V3 on = name.equals(CAMS.get(1)) ? ON_SHOULDER : ON_CHEST;
        final Point3D ahead = way(chest.way(V3.Z));
        final Point3D right = way(chest.way(V3.X));
        final double yaw = Math.toDegrees(Math.atan2(ahead.getX(), ahead.getZ()));
        final double pitch = Math.toDegrees(Math.atan2(-ahead.getY(), Math.hypot(ahead.getX(), ahead.getZ())));
        final Vec3 level = com.example.fxsuite.avatar.sim.Num.right(yaw);
        final double roll = Math.toDegrees(Math.atan2(right.getY(),
                right.getX() * level.x() + right.getZ() * level.z()));
        return new Viewpoint(world(last.place(Part.CHEST, on)), yaw, pitch, roll);
    }

    /** {@code b}, a point in the body's frame as it was last shown, in the world. */
    private Vec3 world(final V3 b) {
        Point3D p = new Point3D(b.x(), -b.y(), b.z());
        p = lean.transform(p);
        p = bank.transform(p);
        p = raise.transform(p);
        p = inView.transform(p);
        p = turn.transform(p);
        p = at.transform(p);
        return new Vec3(p.getX(), p.getY(), p.getZ());
    }

    /** {@code d}, a way in the body's frame as it was last shown, in the world's. */
    private Point3D way(final V3 d) {
        Point3D p = new Point3D(d.x(), -d.y(), d.z());
        p = lean.deltaTransform(p);
        p = bank.deltaTransform(p);
        return turn.deltaTransform(p);
    }

    @Override
    public void fromItsEyes(final boolean eyes) {
        fromEyes = eyes;
        for (final Part p : Part.values()) {
            puppet.part(p).setVisible((!eyes || arm(p)) && !(headless && (p == Part.HEAD || p == Part.NECK)));
        }
        puppet.pistons(!eyes);
        strap.setVisible(!eyes && gun.isVisible());
        inView.setY(eyes ? -IN_VIEW_UP : 0);
        inView.setZ(eyes ? IN_VIEW_AHEAD : 0);
    }

    private static boolean arm(final Part p) {
        return p.name().contains("ARM") || p.name().contains("HAND");
    }

    @Override
    public Vec3 eyes(final com.example.fxsuite.avatar.sim.Body where, final com.example.fxsuite.avatar.look.Pose pose) {
        final Rig r = last != null ? last : new Rig(proportions, read(pose));
        final V3 e = r.place(Part.HEAD, new V3(0, 6, 9));
        Point3D p = new Point3D(e.x(), -e.y(), e.z());
        p = new Rotate(lean.getAngle(), 0, -middle, 0, Rotate.X_AXIS).transform(p);
        p = new Rotate(bank.getAngle(), 0, -middle, 0, Rotate.Z_AXIS).transform(p);
        p = p.add(0, raise.getY(), 0);
        p = new Rotate(where.yaw(), Rotate.Y_AXIS).transform(p);
        return where.position().add(p.getX(), p.getY(), p.getZ());
    }

    /**
     * Holds the avatar as it is: on its feet, its gait at its speed, looking as {@code pose} has it; off them,
     * {@code pose} read onto its joints. The gun slung, if it is drawn; its pack.
     */
    private void posed(final Avatar avatar, final com.example.fxsuite.avatar.look.Pose pose) {
        final com.example.fxsuite.avatar.sim.Body b = avatar.body();
        final double seconds = Double.isNaN(was) ? 0 : Math.clamp(b.time() - was, 0, 0.1);
        was = b.time();
        final Arms held = avatar.arms();
        final boolean gatling = held.held() == Weapon.GATLING;
        final boolean onFoot = b.onGround() && !b.flying();
        Pose p;
        double up;
        if (onFoot) {
            // its stride run backward walking back; at a standstill, standing
            final double ground = b.groundSpeed();
            final boolean back = ground > 1e-6 && b.speed() / ground < -0.3;
            walker.step(back ? -seconds : seconds, ground, gatling);
            p = walker.rig().pose();
            for (final Part n : List.of(Part.NECK, Part.HEAD)) {
                p = p.with(n, Dof.NECK_TURN, p.get(n, Dof.NECK_TURN) - pose.headYaw() / 2)
                        .with(n, Dof.NECK_BEND, p.get(n, Dof.NECK_BEND) - pose.headPitch() / 2);
            }
            up = walker.flying(ground);
            // its gait leans it as it runs; only banked into its turns, as the avatar is
            lean.setAngle(0);
        } else {
            p = read(pose);
            up = pose.lift();
            lean.setAngle(-pose.lean());
        }
        bank.setAngle(pose.bank());
        raise.setY(-(up + SOLE));
        gun.setVisible(gatling);
        strap.setVisible(gatling && !fromEyes);
        if (gatling) {
            final GatlingHold.Held h = hold.hold(GatlingHold.arms(p, arms), held.pitch());
            arms = h.pose();
            p = h.pose();
            final GatlingHold.Frame f = h.gun();
            puppet.place(gunAt, f.origin(), f.right(), f.up(), f.ahead());
            if (!fromEyes) {
                strap.getChildren().setAll(puppet.drawn(h.strap()));
            }
            barrels.setAngle((barrels.getAngle() + BARRELS_TURN * held.spin() * seconds) % 360);
            flash.setVisible(held.sinceRight() < FLASH);
            flash.setRotate(held.rounds() * 37 % 90);
        }
        last = new Rig(proportions, p);
        puppet.show(last);
        pack.show(avatar);
        if (fromEyes) {
            pack.node().setVisible(false);
        }
    }

    /**
     * The avatar's pose read onto the humanoid's joints: each leg swung at the hip, out as the legs are spread, its
     * knee bending as it swings back, its foot kept level; each arm swung and spread at the shoulder, turned, its elbow
     * bent; the head turned and tipped, half at the neck and half at the head. Each no further than its joint goes.
     * For when it is off its feet, where its gait has nothing to say.
     */
    static Pose read(final com.example.fxsuite.avatar.look.Pose ep) {
        Pose p = Pose.REST;
        p = leg(p, Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, ep.rightLeg(), ep.legSpread());
        p = leg(p, Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT, ep.leftLeg(), ep.legSpread());
        p = p.with(Part.RIGHT_UPPER_ARM, Dof.RAISE_FORWARD, ep.rightArm())
                .with(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS, ep.rightSpread() + 6)
                .with(Part.RIGHT_UPPER_ARM, Dof.ARM_ROTATION, -ep.rightTwist())
                .with(Part.RIGHT_FOREARM, Dof.ELBOW, ep.rightElbow());
        p = p.with(Part.LEFT_UPPER_ARM, Dof.RAISE_FORWARD, ep.leftArm())
                .with(Part.LEFT_UPPER_ARM, Dof.RAISE_SIDEWAYS, ep.leftSpread() + 6)
                .with(Part.LEFT_UPPER_ARM, Dof.ARM_ROTATION, ep.leftTwist())
                .with(Part.LEFT_FOREARM, Dof.ELBOW, ep.leftElbow());
        for (final Part n : List.of(Part.NECK, Part.HEAD)) {
            p = p.with(n, Dof.NECK_TURN, -ep.headYaw() / 2).with(n, Dof.NECK_BEND, -ep.headPitch() / 2);
        }
        return p;
    }

    private static Pose leg(final Pose p, final Part thigh, final Part shin, final Part foot, final double swing,
                            final double spread) {
        final double knee = swing < 0 ? -swing * 1.3 : swing * 0.25;
        return p.with(thigh, Dof.HIP_FORWARD, swing).with(thigh, Dof.HIP_SIDEWAYS, spread)
                .with(shin, Dof.KNEE, knee).with(foot, Dof.ANKLE, swing - knee);
    }
}
