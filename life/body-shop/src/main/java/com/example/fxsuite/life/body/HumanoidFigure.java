package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.look.Arsenal;
import com.example.fxsuite.avatar.look.Figure;
import com.example.fxsuite.avatar.look.JetPack;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.avatar.sim.Weapon;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.List;

/**
 * The body shop's humanoid as the avatar: a robot, exposed or shelled, or a human, drawn as the game's avatar in
 * place of the courier ({@link Figure}), as a puppet ({@link Puppet}), the avatar's pose
 * ({@link com.example.fxsuite.avatar.look.Pose}) read onto its joints: its legs swung, its knees bending as a leg swings
 * back, its feet kept level; its arms swung and spread, its elbows bent; its head turned and tipped; tipped, banked and
 * raised whole as the courier is. Its jet-pack on its back, what its hands hold at its forearms, as the courier
 * carries them. Of the weapons, the Gatling gun only. Not yet as quick to draw as the courier: the pistons each frame.
 */
public final class HumanoidFigure implements Figure {

    /** How far its soles stand clear of the ground; its arms drawn up and ahead seen from its eyes, as the courier's. */
    static final double SOLE = 0.5;
    static final double IN_VIEW_UP = 30;
    static final double IN_VIEW_AHEAD = 30;

    private final Puppet puppet;
    private final Proportions proportions = Proportions.PAPERS;
    private final double middle;

    private final Group root = new Group();
    private final Translate at = new Translate();
    private final Rotate turn = new Rotate(0, Rotate.Y_AXIS);
    private final Translate inView = new Translate();
    private final Translate raise = new Translate();
    private final Rotate bank;
    private final Rotate lean;
    private final JetPack pack = new JetPack();
    private final Arsenal arsenal = new Arsenal();
    private boolean fromEyes;
    private Rig last;

    public HumanoidFigure(final Puppet.Kind kind) {
        puppet = new Puppet(kind, proportions, RobotDesign.DEFAULT, Palette.DEFAULT, new V3(1, -1, 1));
        final Rig rest = new Rig(proportions, Pose.REST);
        middle = rest.lift();
        bank = new Rotate(0, 0, -middle, 0, Rotate.Z_AXIS);
        lean = new Rotate(0, 0, -middle, 0, Rotate.X_AXIS);
        final Group body = new Group(puppet.node());
        body.getTransforms().addAll(inView, raise, bank, lean);
        // what its hands hold, at its forearms, from the elbow down as the courier's forearm has them
        for (final Part fore : List.of(Part.RIGHT_FOREARM, Part.LEFT_FOREARM)) {
            final Group hold = new Group(fore == Part.RIGHT_FOREARM ? arsenal.rightHand() : arsenal.leftHand());
            hold.getTransforms().add(new Scale(1, -1, 1));
            puppet.part(fore).getChildren().add(hold);
        }
        // its jet-pack on its back, on its chest, as the courier wears one from its feet up
        final double chest = rest.frame(Part.CHEST).at().y();
        final Group carry = new Group(pack.node());
        carry.getTransforms().addAll(new Scale(1, -1, 1), new Translate(0, chest, 7));
        puppet.part(Part.CHEST).getChildren().add(carry);
        root.getChildren().add(body);
        root.getTransforms().addAll(at, turn);
    }

    /** Every kind, offered as the avatar's figure to the game ({@link com.example.fxsuite.avatar.look.Figures}). */
    public static void offer() {
        for (final Puppet.Kind k : Puppet.Kind.values()) {
            com.example.fxsuite.avatar.look.Figures.offer(k.label(), () -> new HumanoidFigure(k));
        }
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
        return arsenal.effects();
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
    public void fromItsEyes(final boolean eyes) {
        fromEyes = eyes;
        for (final Part p : Part.values()) {
            puppet.part(p).setVisible(!eyes || arm(p));
        }
        puppet.pistons(!eyes);
        inView.setY(eyes ? -IN_VIEW_UP : 0);
        inView.setZ(eyes ? IN_VIEW_AHEAD : 0);
    }

    private static boolean arm(final Part p) {
        return p.name().contains("ARM") || p.name().contains("HAND");
    }

    @Override
    public Vec3 eyes(final com.example.fxsuite.avatar.sim.Body where, final com.example.fxsuite.avatar.look.Pose pose) {
        final Rig r = new Rig(proportions, read(pose));
        final V3 e = r.place(Part.HEAD, new V3(0, 6, 9));
        Point3D p = new Point3D(e.x(), -e.y(), e.z());
        p = new Rotate(-pose.lean(), 0, -middle, 0, Rotate.X_AXIS).transform(p);
        p = new Rotate(pose.bank(), 0, -middle, 0, Rotate.Z_AXIS).transform(p);
        p = p.add(0, -(pose.lift() + SOLE), 0);
        p = new Rotate(where.yaw(), Rotate.Y_AXIS).transform(p);
        return where.position().add(p.getX(), p.getY(), p.getZ());
    }

    /** Holds {@code pose}: every part where its skeleton has it, the body tipped and raised; its pack and its hands. */
    private void posed(final Avatar avatar, final com.example.fxsuite.avatar.look.Pose pose) {
        last = new Rig(proportions, read(pose));
        puppet.show(last);
        raise.setY(-(pose.lift() + SOLE));
        lean.setAngle(-pose.lean());
        bank.setAngle(pose.bank());
        pack.show(avatar);
        if (fromEyes) {
            pack.node().setVisible(false);
        }
        arsenal.show(avatar, pose);
    }

    /**
     * The avatar's pose read onto the humanoid's joints: each leg swung at the hip, out as the legs are spread, its
     * knee bending as it swings back, its foot kept level; each arm swung and spread at the shoulder, turned, its elbow
     * bent; the head turned and tipped, half at the neck and half at the head. Each no further than its joint goes.
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
