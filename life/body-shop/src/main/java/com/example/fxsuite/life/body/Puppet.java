package com.example.fxsuite.life.body;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A humanoid drawn to be moved: each part drawn once, rigid, in its own frame, and put each frame where its skeleton
 * has it, as a puppet's; an exposed robot's pistons drawn afresh as they slide. A human, or a robot exposed or shelled.
 * Drawn into a scene whose frame is the body's with each way's sign as {@code signs} has it (the game's turns the body
 * up its down; a bench's, round too).
 */
public final class Puppet {

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

        @Override
        public String toString() {
            return label;
        }
    }

    /** How far its forearms are turned at rest, palm in, as a pose holds them. */
    static final double TWIST = 85;

    private final Kind kind;
    private final Proportions proportions;
    private final RobotDesign design;
    private final Palette palette;
    private final V3 signs;
    private final Rig rest;
    private final Group node = new Group();
    private final Map<Part, Group> parts = new EnumMap<>(Part.class);
    private final Map<Part, Affine> placed = new EnumMap<>(Part.class);
    private final Group pistons = new Group();
    private boolean drawPistons = true;

    public Puppet(final Kind kind, final Proportions proportions, final RobotDesign design, final Palette palette,
                  final V3 signs) {
        this.kind = kind;
        this.proportions = proportions;
        this.design = design;
        this.palette = palette;
        this.signs = signs;
        rest = new Rig(proportions, Pose.REST);
        for (final Part p : Part.values()) {
            final List<Surface> baked = new ArrayList<>();
            for (final Surface s : local(p)) {
                baked.add(s.moved(v -> bake(p, v)));
            }
            // drawn in the part's own frame; its frame put where it is each frame, into the scene's
            final Group g = FigureModel.meshes(baked, palette, v -> new Point3D(v.x(), v.y(), v.z()));
            final Affine a = new Affine();
            g.getTransforms().add(a);
            parts.put(p, g);
            placed.put(p, a);
            node.getChildren().add(g);
        }
        node.getChildren().add(pistons);
    }

    public Kind kind() {
        return kind;
    }

    /** The puppet, to put in the scene. */
    public Node node() {
        return node;
    }

    /** The group a part is drawn in, moving with it: to hang more on it, in its own frame. */
    public Group part(final Part p) {
        return parts.get(p);
    }

    /** Whether its pistons are drawn, if it has any. */
    public void pistons(final boolean drawn) {
        drawPistons = drawn;
        pistons.setVisible(drawn);
    }

    /** Every part where {@code rig} has it; an exposed robot's pistons drawn afresh to it. */
    public void show(final Rig rig) {
        for (final Part p : Part.values()) {
            final Rig.Frame f = rig.frame(p);
            place(placed.get(p), f.at(), f.turn());
        }
        if (kind == Kind.EXPOSED && drawPistons) {
            final List<Surface> drawn = new ArrayList<>();
            Muscles.pistons(rig, rest).values().forEach(drawn::addAll);
            pistons.getChildren().setAll(FigureModel.meshes(drawn, palette,
                    v -> new Point3D(signs.x() * v.x(), signs.y() * v.y(), signs.z() * v.z())));
        } else {
            pistons.getChildren().clear();
        }
    }

    /** {@code surfaces}, in the body's frame where it stands, drawn into the scene's: what it carries, as it is now. */
    public Group drawn(final List<Surface> surfaces) {
        return FigureModel.meshes(surfaces, palette, v -> new Point3D(signs.x() * v.x(), signs.y() * v.y(), signs.z() * v.z()));
    }

    /** {@code a} set to a frame at {@code at}, turned {@code turn}, in the body's frame, put into the scene's. */
    public void place(final Affine a, final V3 at, final Rot turn) {
        a.setToTransform(signs.x() * turn.xx(), signs.x() * turn.xy(), signs.x() * turn.xz(), signs.x() * at.x(),
                signs.y() * turn.yx(), signs.y() * turn.yy(), signs.y() * turn.yz(), signs.y() * at.y(),
                signs.z() * turn.zx(), signs.z() * turn.zy(), signs.z() * turn.zz(), signs.z() * at.z());
    }

    /** {@code a} set to a frame at {@code at} with its ways {@code x}, {@code y} and {@code z}, in the body's frame. */
    public void place(final Affine a, final V3 at, final V3 x, final V3 y, final V3 z) {
        place(a, at, new Rot(x.x(), y.x(), z.x(), x.y(), y.y(), z.y(), x.z(), y.z(), z.z()));
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
}
