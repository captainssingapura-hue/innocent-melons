package com.example.fxsuite.life.body;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A humanoid drawn to be moved: each part drawn once, rigid, in its own frame, and put each frame where its skeleton
 * has it, as a puppet's; an exposed robot's pistons drawn afresh as they slide. A human, or a robot exposed or shelled;
 * or a skeleton, the lab's bones part by part ({@link PartBones}), as finely as asked ({@link #detail}), at first as
 * {@code -Dskeleton.detail} says (mm, the big bones' grid; 8 if not); or a man or a woman, a model's build, their
 * flesh sculpted over their bones ({@link com.example.fxsuite.life.body.flesh.Physique}), as light or dark as
 * {@code -Dskin.tone} says (0 to 1; 0.25 if not); in tights, if {@code -Dwear.tights=true}. Drawn into a scene whose frame is the body's with
 * each way's sign as {@code signs} has it (the game's turns the body up its down; a bench's, round too).
 */
public final class Puppet {

    /** What it is drawn as. */
    public enum Kind {
        EXPOSED("Robot, exposed"), SHELLED("Robot, shelled"), HUMAN("Human"), SKELETON("Skeleton"),
        MAN("A man, a model's build"), WOMAN("A woman, a model's build");

        private final String label;

        Kind(final String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        /** Whose body it is, sculpted: a man's or a woman's, a model's build; none, if not so. */
        public com.example.fxsuite.life.body.flesh.Physique physique() {
            return switch (this) {
                case MAN -> com.example.fxsuite.life.body.flesh.Physique.MAN;
                case WOMAN -> com.example.fxsuite.life.body.flesh.Physique.WOMAN;
                default -> null;
            };
        }

        /** How it is proportioned: its physique's, if it has one, else {@code otherwise}. */
        public Proportions proportions(final Proportions otherwise) {
            return physique() == null ? otherwise : physique().proportions();
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** How far its forearms are turned at rest, palm in, as a pose holds them. */
    public static final double TWIST = 85;

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
    private final Group skin = new Group();
    private com.example.fxsuite.life.body.flesh.Clothed flesh;
    private com.example.fxsuite.life.body.flesh.Tights tights = Boolean.getBoolean("wear.tights")
            ? com.example.fxsuite.life.body.flesh.Tights.BLACK : null;
    private Rig shown;
    private double definition = Double.NaN;
    private final SkeletonLevels levels = new SkeletonLevels();
    private final Map<Part, Group> bones = new EnumMap<>(Part.class);
    private Detail detail = new Detail(Double.parseDouble(System.getProperty("skeleton.detail", "8")) / 10);
    private final List<PhongMaterial> skins = new ArrayList<>();
    private com.example.fxsuite.life.body.flesh.SkinTone tone = new com.example.fxsuite.life.body.flesh.SkinTone(
            Double.parseDouble(System.getProperty("skin.tone", "0.25")));
    private boolean drawPistons = true;

    public Puppet(final Kind kind, final Proportions proportions, final RobotDesign design, final Palette palette,
                  final V3 signs) {
        this(kind, proportions, design, palette, signs, null);
    }

    /** Shapes drawn together in the colours of one palette: a layer of someone's skin or of what they wear. */
    public record Layer(List<Surface> surfaces, Palette palette) {
    }

    /**
     * A human drawn as {@code drawn} has each part, in the part's own frame as on the right side (dressed, say), layer
     * by layer, each in its own colours.
     */
    public Puppet(final java.util.function.Function<Part, List<Layer>> drawn, final Proportions proportions,
                  final V3 signs) {
        this(Kind.HUMAN, proportions, RobotDesign.DEFAULT, Palette.DEFAULT, signs, drawn);
    }

    private Puppet(final Kind kind, final Proportions proportions, final RobotDesign design, final Palette palette,
                   final V3 signs, final java.util.function.Function<Part, List<Layer>> drawn) {
        this.kind = kind;
        this.proportions = proportions;
        this.design = design;
        this.palette = palette;
        this.signs = signs;
        rest = new Rig(proportions, Pose.REST);
        for (final Part p : Part.values()) {
            final List<Layer> layers = drawn != null ? drawn.apply(p)
                    : skeletal() ? List.of() : List.of(new Layer(local(p), palette));
            // drawn in the part's own frame; its frame put where it is each frame, into the scene's
            final Group g = new Group();
            if (drawn == null && skeletal()) {
                bones.put(p, new Group(skeleton(p, detail)));
                g.getChildren().add(bones.get(p));
            }
            for (final Layer layer : layers) {
                final List<Surface> baked = new ArrayList<>();
                for (final Surface s : layer.surfaces()) {
                    baked.add(s.moved(v -> bake(p, v)));
                }
                g.getChildren().add(FigureModel.meshes(baked, layer.palette(), v -> new Point3D(v.x(), v.y(), v.z())));
            }
            final Affine a = new Affine();
            g.getTransforms().add(a);
            parts.put(p, g);
            placed.put(p, a);
            node.getChildren().add(g);
        }
        node.getChildren().addAll(skin, pistons);
        fleshed(detail);
    }

    /** {@code layers}, in the body's frame where it stands, drawn into the scene's, each in its own colours. */
    public Group drawnLayers(final List<Layer> layers) {
        final Group g = new Group();
        for (final Layer layer : layers) {
            g.getChildren().add(FigureModel.meshes(layer.surfaces(), layer.palette(),
                    v -> new Point3D(signs.x() * v.x(), signs.y() * v.y(), signs.z() * v.z())));
        }
        return g;
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

    /**
     * A skeleton's bones drawn as finely as {@code d} says, on the scene's thread: found now if not yet, which, for a
     * level not yet found, is best done first on another ({@link #levels}). Nothing, if it is not a skeleton.
     */
    public void detail(final Detail d) {
        detail = d;
        skins.clear();
        bones.forEach((p, g) -> g.getChildren().setAll(skeleton(p, d)));
        fleshed(d);
    }

    /** How finely its skeleton is drawn. */
    public Detail detail() {
        return detail;
    }

    /** How defined its torso's muscles are drawn, 0 to 1: as its physique's, unless asked otherwise. */
    public double definition() {
        return Double.isNaN(definition) && kind.physique() != null ? kind.physique().torso().definition() : definition;
    }

    /** Its torso's muscles drawn as defined as {@code d} says, on the scene's thread; found first if not yet. */
    public void definition(final double d) {
        definition = d;
        skins.remove(flesh == null ? null : flesh.skin());
        fleshed(detail);
    }

    /** A man or a woman in {@code t}, tights, or none: their flesh drawn afresh, on the scene's thread. */
    public void tights(final com.example.fxsuite.life.body.flesh.Tights t) {
        tights = t;
        skins.remove(flesh == null ? null : flesh.skin());
        fleshed(detail);
    }

    /** A man's or a woman's flesh, one surface, drawn as finely as {@code d} says; none for any other kind. */
    private void fleshed(final Detail d) {
        if (kind.physique() == null) {
            return;
        }
        flesh = new com.example.fxsuite.life.body.flesh.Clothed(levels.flesh(d, kind.physique(), definition()), tights,
                kind.physique().proportions().scale(), signs, shown != null ? shown : rest);
        flesh.skin().setDiffuseColor(Color.web(tone.colour()));
        skins.add(flesh.skin());
        skin.getChildren().setAll(flesh.node());
    }

    /** Whether it is drawn as a skeleton, sculpted or not. */
    private boolean skeletal() {
        return kind == Kind.SKELETON || kind.physique() != null;
    }

    /** Its skeleton's levels of detail, found and kept: to find one on another thread before it is drawn. */
    public SkeletonLevels levels() {
        return levels;
    }

    /** {@code p}'s bones as finely as {@code d} says, made to the body's size and to its side. */
    private List<Node> skeleton(final Part p, final Detail d) {
        final List<Node> nodes = new ArrayList<>();
        for (final PartBones.Piece piece : levels.of(p, d, kind.physique())) {
            final Group g = com.example.fxsuite.life.body.bone.BoneView.INSTANCE.mapped(piece.mesh(), piece.paint(),
                    v -> bake(p, v), p.side() < 0);
            if (piece.paint() == Paint.SKIN) {
                final PhongMaterial skin = (PhongMaterial) ((MeshView) g.getChildren().getFirst()).getMaterial();
                skin.setDiffuseColor(Color.web(tone.colour()));
                skins.add(skin);
            }
            nodes.add(g);
        }
        return nodes;
    }

    /** Its flesh coloured {@code t}, a sculpted skeleton's, as it is: nothing drawn afresh. */
    public void tone(final com.example.fxsuite.life.body.flesh.SkinTone t) {
        tone = t;
        skins.forEach(m -> m.setDiffuseColor(Color.web(t.colour())));
    }

    /** Whether its pistons are drawn, if it has any. */
    public void pistons(final boolean drawn) {
        drawPistons = drawn;
        pistons.setVisible(drawn);
    }

    /** Every part where {@code rig} has it; an exposed robot's pistons drawn afresh to it. */
    public void show(final Rig rig) {
        shown = rig;
        if (flesh != null) {
            flesh.show(rig);
        }
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

    /** {@code v} as it is, for what is drawn in a frame of its own and put into the scene by a transform. */
    public static Point3D point(final V3 v) {
        return new Point3D(v.x(), v.y(), v.z());
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
            case SKELETON, MAN, WOMAN -> List.of();
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
