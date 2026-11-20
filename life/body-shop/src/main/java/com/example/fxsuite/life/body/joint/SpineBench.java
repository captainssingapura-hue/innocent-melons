package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.BoneView;
import com.example.fxsuite.life.body.bone.Drawn;
import com.example.fxsuite.life.body.bone.spine.SpineBones;
import com.example.fxsuite.life.body.joint.spine.SpineJoints;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The spine bench: the spine put together from its joints ({@link Assembly}), held by whichever of its bones is chosen,
 * that bone where it stands at rest and the rest put together from it; bent forward or back, to the side, twisted,
 * every joint as far its share of the way to its end as asked; each joint's frame shown, if asked. Each bone's surface
 * found once; moved, the bones only placed afresh.
 *
 * <p>{@code -Dspine.root} (a bone's name), {@code -Dspine.bend}, {@code -Dspine.side}, {@code -Dspine.twist} (shares
 * of the way, -1 to 1) and {@code -Dspine.frames} what it shows first.</p>
 */
public final class SpineBench extends Application {

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final List<Complex> joints = SpineJoints.INSTANCE.joints();
    private final List<Bone> bones = SpineBones.INSTANCE.bones();
    private final Rig rest = new Rig(Proportions.DRAWN, Pose.REST, false);
    private final ChoiceBox<Bone> root = new ChoiceBox<>();
    private final Map<Movement, Slider> shares = new HashMap<>();
    private final CheckBox frames = new CheckBox("Show each joint's frame");
    private final Label about = new Label();
    private final Map<Bone, Affine> placed = new HashMap<>();
    private final Group axes = new Group();

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Spine bench", bus);
        shell.bar(List.of());
        shell.side(new Label("The spine, put together"), controls(shell), 340);
        shell.middle(view.node());
        view.zoom(10, 600);
        view.lookAt(0, 34, 0);
        view.from("spine", -70, 8, 1.3);
        final Group spine = new Group();
        spine.getTransforms().add(new javafx.scene.transform.Translate(0, -3, 0));
        view.show(ModelView.Model.moving(spine));
        bus.publish(new Bus.Working(true, "spine"));
        new Thread(() -> {
            final List<Node> nodes = bones.stream().map(this::drawn).toList();
            Platform.runLater(() -> {
                spine.getChildren().setAll(nodes);
                spine.getChildren().add(axes);
                place();
                bus.publish(new Bus.Working(false, "spine"));
            });
        }, "spine").start();
        shell.show(stage, 1280, 860);
    }

    /** The bench's side: which bone holds the spine; how far it is bent, each way; whether its joints are shown. */
    private Node controls(final Shell shell) {
        root.getItems().setAll(bones);
        root.setConverter(new StringConverter<>() {
            @Override
            public String toString(final Bone b) {
                return b == null ? "" : b.name();
            }

            @Override
            public Bone fromString(final String s) {
                return null;
            }
        });
        final String asked = System.getProperty("spine.root", "sacrum");
        root.setValue(bones.stream().filter(b -> b.name().equalsIgnoreCase(asked)).findFirst().orElse(bones.getLast()));
        root.setOnAction(e -> place());
        final VBox box = new VBox(8, new Label("Held by"), root);
        for (final Movement m : Movement.values()) {
            final String key = switch (m) {
                case FLEXION -> "bend";
                case SIDE_BEND -> "side";
                case TWIST -> "twist";
            };
            final Slider s = new Slider(-1, 1, Double.parseDouble(System.getProperty("spine." + key, "0")));
            s.setShowTickLabels(true);
            s.setMajorTickUnit(0.5);
            s.valueProperty().addListener((o, was, is) -> place());
            shares.put(m, s);
            box.getChildren().addAll(new Label(label(m)), s);
        }
        frames.setSelected(Boolean.getBoolean("spine.frames"));
        frames.setOnAction(e -> place());
        about.setWrapText(true);
        box.getChildren().addAll(frames, about, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    private String label(final Movement m) {
        return switch (m) {
            case FLEXION -> "Bend: back (-1) to forward (1), each joint its share of its range";
            case SIDE_BEND -> "Bend sideways: right (-1) to left (1)";
            case TWIST -> "Twist: one way (-1) to the other (1)";
        };
    }

    /** {@code bone}'s surface, drawn in its own frame, to be placed. */
    private Node drawn(final Bone bone) {
        final Mesher.Mesh mesh = Mesher.of(new Field(Drawn.INSTANCE.shape(bone, 0).elements()),
                Math.min(0.15, bone.coarsest()));
        final Group node = BoneView.INSTANCE.inItsFrame(mesh, bone.paint());
        final Affine a = new Affine();
        node.getTransforms().add(a);
        placed.put(bone, a);
        return node;
    }

    /** Every bone placed afresh: the spine put together from the bone holding it, posed as the sliders say. */
    private void place() {
        Posture posture = Posture.REST;
        for (final Complex j : joints) {
            for (final Movement m : j.movements()) {
                posture = posture.with(j, Side.MIDDLE, m, j.range(m).at(shares.get(m).getValue()));
            }
        }
        final Bone holder = root.getValue();
        final Assembly.Assembled a = Assembly.INSTANCE.build(joints, new BoneAt(holder, Side.MIDDLE),
                new Place(rest.frame(holder.part()).at(), Rot.NONE), posture);
        a.places().forEach((at, p) -> {
            final Affine affine = placed.get(at.bone());
            if (affine != null) {
                final Rot r = p.turn();
                affine.setToTransform(r.xx(), r.xy(), r.xz(), -p.at().x(), r.yx(), r.yy(), r.yz(), -p.at().y(),
                        r.zx(), r.zy(), r.zz(), -p.at().z());
            }
        });
        axes.getChildren().setAll(frames.isSelected() ? frames(a) : List.of());
        double bend = 0;
        for (final Complex j : joints) {
            bend += posture.degrees(j, Side.MIDDLE, Movement.FLEXION);
        }
        about.setText(String.format(Locale.ROOT, "%d bones, %d joints, held by the %s; bent %.0f degrees forward in "
                + "all.", a.places().size(), joints.size(), holder.name(), bend));
    }

    /** Each joint's frame, where its bone it is carried by has it: its x red, y green, z blue, 2 cm long. */
    private List<Node> frames(final Assembly.Assembled a) {
        return joints.stream().flatMap(c -> c.articulations().stream()).map(j -> {
            final Place p = a.places().get(new BoneAt(j.from(), Side.MIDDLE)).then(j.fromPoint().frame());
            final Group g = new Group(bar(V3.X, "#d0453a"), bar(V3.Y, "#3a9d4a"), bar(V3.Z, "#3a6ad0"));
            final Rot r = p.turn();
            final Affine affine = new Affine(r.xx(), r.xy(), r.xz(), -p.at().x(), r.yx(), r.yy(), r.yz(), -p.at().y(),
                    r.zx(), r.zy(), r.zz(), -p.at().z());
            g.getTransforms().add(affine);
            return (Node) g;
        }).toList();
    }

    /** A bar 2 cm along {@code way} from the origin, in the scene's frame. */
    private Node bar(final V3 way, final String colour) {
        final Box b = new Box(way.x() != 0 ? 2 : 0.12, way.y() != 0 ? 2 : 0.12, way.z() != 0 ? 2 : 0.12);
        b.setTranslateX(-way.x());
        b.setTranslateY(-way.y());
        b.setTranslateZ(-way.z());
        b.setMaterial(new PhongMaterial(Color.web(colour)));
        return b;
    }
}
