package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.look.Arsenal;
import com.example.fxsuite.avatar.look.Figure;
import com.example.fxsuite.avatar.look.JetPack;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Vec3;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The body shop's humanoid as the avatar: a robot, exposed or shelled, or a human, drawn as the game's avatar in
 * place of the courier ({@link Figure}). Its parts drawn once, each rigid in its own frame, and put where its
 * skeleton has them every frame, the avatar's pose ({@link com.example.fxsuite.avatar.look.Pose}) read onto its
 * joints: its legs swung, its knees bending as a leg swings back, its feet kept level; its arms swung and spread,
 * its elbows bent; its head turned and tipped; tipped, banked and raised whole as the courier is. An exposed robot's
 * pistons drawn afresh as it moves. Its jet-pack on its back, what its hands hold at its forearms, as the courier
 * carries them. Not yet as quick to draw as the courier: the pistons each frame.
 */
public final class HumanoidFigure implements Figure {

    /** What it is drawn as. */
    public enum Kind {
        EXPOSED("Robot, exposed"), SHELLED("Robot, shelled"), HUMAN("Human");

        private final String label;

        Kind(final String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    /** How far its soles stand clear of the ground; its arms drawn up and ahead seen from its eyes, as the courier's. */
    static final double SOLE = 0.5;
    static final double IN_VIEW_UP = 30;
    static final double IN_VIEW_AHEAD = 30;
    /** How far its forearms are turned at rest, palm in, as a pose holds them. */
    static final double TWIST = 85;

    private final Kind kind;
    private final Proportions proportions = Proportions.PAPERS;
    private final RobotDesign design = RobotDesign.DEFAULT;
    private final Palette palette = Palette.DEFAULT;
    private final Rig rest;
    private final double middle;

    private final Group root = new Group();
    private final Translate at = new Translate();
    private final Rotate turn = new Rotate(0, Rotate.Y_AXIS);
    private final Translate inView = new Translate();
    private final Translate raise = new Translate();
    private final Rotate bank;
    private final Rotate lean;
    private final Map<Part, Group> parts = new EnumMap<>(Part.class);
    private final Map<Part, Affine> placed = new EnumMap<>(Part.class);
    private final Group pistons = new Group();
    private final JetPack pack = new JetPack();
    private final Arsenal arsenal = new Arsenal();
    private boolean fromEyes;
    private Rig last;

    public HumanoidFigure(final Kind kind) {
        this.kind = kind;
        rest = new Rig(proportions, Pose.REST);
        middle = rest.lift();
        bank = new Rotate(0, 0, -middle, 0, Rotate.Z_AXIS);
        lean = new Rotate(0, 0, -middle, 0, Rotate.X_AXIS);
        final Group body = new Group();
        body.getTransforms().addAll(inView, raise, bank, lean);
        for (final Part p : Part.values()) {
            final List<Surface> baked = new ArrayList<>();
            for (final Surface s : local(p)) {
                baked.add(s.moved(v -> bake(p, v)));
            }
            // drawn in the part's own frame, as the body's turned into the scene's: its frame put where it is each
            // frame, upside down as the scene is
            final Group g = FigureModel.meshes(baked, palette, v -> new Point3D(v.x(), v.y(), v.z()));
            final Affine a = new Affine();
            g.getTransforms().add(a);
            parts.put(p, g);
            placed.put(p, a);
            body.getChildren().add(g);
        }
        // what its hands hold, at its forearms, from the elbow down as the courier's forearm has them
        for (final Part fore : List.of(Part.RIGHT_FOREARM, Part.LEFT_FOREARM)) {
            final Group hold = new Group(fore == Part.RIGHT_FOREARM ? arsenal.rightHand() : arsenal.leftHand());
            hold.getTransforms().add(new Scale(1, -1, 1));
            parts.get(fore).getChildren().add(hold);
        }
        // its jet-pack on its back, on its chest, as the courier wears one from its feet up
        final double chest = rest.frame(Part.CHEST).at().y();
        final Group carry = new Group(pack.node());
        carry.getTransforms().addAll(new Scale(1, -1, 1), new Translate(0, chest, 7));
        parts.get(Part.CHEST).getChildren().add(carry);
        body.getChildren().add(pistons);
        root.getChildren().add(body);
        root.getTransforms().addAll(at, turn);
    }

    /** Every kind, offered as the avatar's figure to the game ({@link com.example.fxsuite.avatar.look.Figures}). */
    public static void offer() {
        for (final Kind k : Kind.values()) {
            com.example.fxsuite.avatar.look.Figures.offer(k.label(), () -> new HumanoidFigure(k));
        }
    }

    /** {@code p}'s pieces in its own frame, as on the right side, as its kind draws it. */
    private List<Surface> local(final Part p) {
        return switch (kind) {
            case HUMAN -> Skin.shapes(p, Muscles.Bulge.REST, TWIST, Skin.HUMAN);
            case SHELLED -> Skin.shell(p, TWIST, design);
            case EXPOSED -> {
                if (RobotFrame.designed(p)) {
                    yield RobotFrame.part(p, design, Paint.FRAME);
                }
                final List<Surface> frame = new ArrayList<>();
                for (final Surface s : Bones.local(p, TWIST)) {
                    frame.add(new Surface(s.name(), Paint.FRAME, s.rings(), s.start(), s.end(), s.smooth()));
                }
                final double r = Bones.HOUSING.getOrDefault(p.name().replace("RIGHT_", "").replace("LEFT_", ""), 0.0);
                if (r > 0) {
                    frame.add(Shapes.ellipsoid(p.said() + " housing", Paint.HOUSING, V3.ZERO, r, r, r, 16, 10));
                }
                yield frame;
            }
        };
    }

    /** {@code v}, in {@code p}'s frame as on the right side, made to the body's size and to its side. */
    private V3 bake(final Part p, final V3 v) {
        final V3 s = rest.frame(p).scale();
        final double x = v.x() * s.x();
        return new V3(p.side() < 0 ? -x : x, v.y() * s.y(), v.z() * s.z());
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
        parts.forEach((p, g) -> g.setVisible(!eyes || arm(p)));
        pistons.setVisible(!eyes);
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
        for (final Part p : Part.values()) {
            final Rig.Frame f = last.frame(p);
            final Rot r = f.turn();
            final V3 o = f.at();
            // the part's frame, then the body's turned into the scene's, up its down
            placed.get(p).setToTransform(r.xx(), r.xy(), r.xz(), o.x(), -r.yx(), -r.yy(), -r.yz(), -o.y(), r.zx(),
                    r.zy(), r.zz(), o.z());
        }
        if (kind == Kind.EXPOSED && !fromEyes) {
            final List<Surface> drawn = new ArrayList<>();
            Muscles.pistons(last, rest).values().forEach(drawn::addAll);
            pistons.getChildren().setAll(FigureModel.meshes(drawn, palette, v -> new Point3D(v.x(), -v.y(), v.z())));
        }
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
