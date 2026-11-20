package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.BoneView;
import com.example.fxsuite.life.body.bone.Drawn;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The assembly bench: the bones of the groups ticked, put together from their joints, found by those groups alone
 * ({@link Scope}); held by whichever of them is chosen, it where it stands at rest; posed from a posture to start from,
 * any joint then moved on its own, each way within its range; how far any ring of bones failed to close, told. Each
 * bone's surface found once; moved, the bones only placed afresh.
 *
 * <p>{@code -Dassembly.groups} (names, by commas), {@code -Dassembly.posture} (a posture's name),
 * {@code -Dassembly.holder} (a bone, "left calcaneus") and {@code -Dassembly.yaw}, {@code -Dassembly.pitch},
 * {@code -Dassembly.distance} what it shows first.</p>
 */
public final class AssemblyBench extends Application {

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final Rig rest = new Rig(Proportions.DRAWN, Pose.REST, false);
    private final Map<Group, CheckBox> groups = new EnumMap<>(Group.class);
    private final ChoiceBox<BoneAt> holder = new ChoiceBox<>();
    private final ChoiceBox<Postures.Named> postures = new ChoiceBox<>();
    private final ChoiceBox<Posture.Key> joint = new ChoiceBox<>();
    private final VBox sliders = new VBox(4);
    private final Label about = new Label();
    private final Map<Bone, Mesher.Mesh> meshes = new ConcurrentHashMap<>();
    private final Map<BoneAt, Affine> placed = new HashMap<>();
    private final javafx.scene.Group bones = new javafx.scene.Group();
    private Posture posture = Posture.REST;
    private Scope scope = Group.SPINE.with();

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Assembly bench", bus);
        final ScrollPane side = new ScrollPane(controls(shell));
        side.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The skeleton, put together"), side, 360);
        shell.middle(view.node());
        view.zoom(10, 800);
        view.lookAt(0, 70, 0);
        view.from("assembly", 200, 8, 2.2);
        final javafx.scene.Group world = new javafx.scene.Group(bones);
        world.getTransforms().add(new Translate(0, -90.6, 0));
        view.show(ModelView.Model.moving(world));
        rescope();
        shell.show(stage, 1360, 900);
    }

    /** The bench's side: which groups, held by which bone, posed how, which joint moved how far. */
    private Node controls(final Shell shell) {
        final String asked = System.getProperty("assembly.groups", "spine,pelvis,legs,feet").toLowerCase(Locale.ROOT);
        final VBox box = new VBox(8, new Label("Put together"));
        for (final Group g : Group.values()) {
            final CheckBox c = new CheckBox(g.said());
            c.setSelected(asked.contains(g.name().toLowerCase(Locale.ROOT)));
            c.setOnAction(e -> rescope());
            groups.put(g, c);
            box.getChildren().add(c);
        }
        holder.setConverter(new Said<>(BoneAt::said));
        holder.setOnAction(e -> place());
        postures.setConverter(new Said<>(Postures.Named::name));
        postures.getItems().setAll(Postures.INSTANCE.all());
        final String wanted = System.getProperty("assembly.posture", "At rest");
        postures.setValue(postures.getItems().stream().filter(p -> p.name().equalsIgnoreCase(wanted)).findFirst()
                .orElse(postures.getItems().getFirst()));
        postures.setOnAction(e -> {
            posture = postures.getValue().posture();
            sliders();
            place();
        });
        posture = postures.getValue().posture();
        joint.setConverter(new Said<>(k -> (k.side() == Side.MIDDLE ? "" : k.side().name().toLowerCase(Locale.ROOT)
                + " ") + k.joint().name()));
        joint.setOnAction(e -> sliders());
        about.setWrapText(true);
        box.getChildren().addAll(new Label("Held by"), holder, new Label("Posed"), postures, new Label("A joint"),
                joint, sliders, about, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    /** The groups ticked put together afresh: the surfaces of bones not yet drawn found away from the screen. */
    private void rescope() {
        final List<Group> ticked = groups.entrySet().stream().filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey).toList();
        if (ticked.isEmpty()) {
            return;
        }
        scope = ticked.getFirst().with(ticked.toArray(Group[]::new));
        bus.publish(new Bus.Working(true, "assembly"));
        final Scope mine = scope;
        new Thread(() -> {
            mine.bones().parallelStream().forEach(b -> meshes.computeIfAbsent(b, x -> Mesher.of(
                    new Field(Drawn.INSTANCE.shape(x, 0).elements()), Math.min(0.2, x.coarsest()))));
            Platform.runLater(() -> {
                if (mine == scope) {
                    draw();
                }
                bus.publish(new Bus.Working(false, "assembly"));
            });
        }, "assembly").start();
    }

    /** Every bone of the scope drawn, on its side, to be placed; the choices of holder and joint made afresh. */
    private void draw() {
        final Bone base = scope.holds(Sacrum.INSTANCE) ? Sacrum.INSTANCE : scope.bones().iterator().next();
        final BoneAt first = new BoneAt(base, Side.MIDDLE);
        final Assembly.Assembled a = Assembly.INSTANCE.build(scope, first, home(first), Posture.REST);
        final List<BoneAt> all = new ArrayList<>(a.places().keySet());
        all.sort((x, y) -> x.said().compareTo(y.said()));
        placed.clear();
        final List<Node> nodes = new ArrayList<>();
        for (final BoneAt b : all) {
            final Mesher.Mesh mesh = meshes.get(b.bone());
            final javafx.scene.Group node = b.side() == Side.LEFT ? BoneView.INSTANCE.mirrored(mesh, b.bone().paint())
                    : BoneView.INSTANCE.inItsFrame(mesh, b.bone().paint());
            final Affine affine = new Affine();
            node.getTransforms().add(affine);
            placed.put(b, affine);
            nodes.add(node);
        }
        bones.getChildren().setAll(nodes);
        holder.getItems().setAll(all);
        final String held = System.getProperty("assembly.holder", first.said());
        holder.setValue(all.stream().filter(b -> b.said().equalsIgnoreCase(held)).findFirst().orElse(first));
        final List<Posture.Key> keys = new ArrayList<>();
        for (final Complex c : scope.joints()) {
            if (!c.movements().isEmpty()) {
                final boolean paired = c.articulations().stream().anyMatch(j -> j.from().paired() || j.to().paired());
                for (final Side s : paired ? List.of(Side.RIGHT, Side.LEFT) : List.of(Side.MIDDLE)) {
                    keys.add(new Posture.Key(c, s, c.movements().getFirst()));
                }
            }
        }
        joint.getItems().setAll(keys);
        joint.setValue(keys.isEmpty() ? null : keys.getFirst());
        sliders();
        place();
    }

    /** A slider for each way the joint chosen moves, as far as the posture has it moved. */
    private void sliders() {
        sliders.getChildren().clear();
        final Posture.Key k = joint.getValue();
        if (k == null) {
            return;
        }
        for (final Movement m : k.joint().movements()) {
            final Range r = k.joint().range(m);
            final Slider s = new Slider(r.least(), r.most(), posture.degrees(k.joint(), k.side(), m));
            s.setShowTickLabels(true);
            s.setMajorTickUnit(Math.max(5, Math.round((r.most() - r.least()) / 4)));
            s.valueProperty().addListener((o, was, is) -> {
                posture = posture.with(k.joint(), k.side(), m, is.doubleValue());
                place();
            });
            sliders.getChildren().addAll(new Label(m.said() + String.format(Locale.ROOT, ", %.0f to %.0f degrees",
                    r.least(), r.most())), s);
        }
    }

    /** Every bone placed afresh: put together from the bone holding them, posed as asked. */
    private void place() {
        final BoneAt root = holder.getValue();
        if (root == null) {
            return;
        }
        final Assembly.Assembled a = Assembly.INSTANCE.build(scope, root, home(root), posture);
        a.places().forEach((b, p) -> {
            final Affine affine = placed.get(b);
            if (affine != null) {
                final Rot r = p.turn();
                affine.setToTransform(r.xx(), r.xy(), r.xz(), -p.at().x(), r.yx(), r.yy(), r.yz(), -p.at().y(),
                        r.zx(), r.zy(), r.zz(), -p.at().z());
            }
        });
        about.setText(String.format(Locale.ROOT, "%d bones on %d articulations, held by the %s. %d rings; the widest "
                + "gap %.2f mm%s.", a.places().size(), a.articulations(), root.said(), a.gaps().size(),
                a.widest() * 10, a.widest() > 0.01 ? ", at the " + widest(a) : ""));
    }

    private String widest(final Assembly.Assembled a) {
        return a.gaps().stream().max((x, y) -> Double.compare(x.apart(), y.apart())).map(Assembly.Gap::joint)
                .orElse("");
    }

    /** Where {@code b} stands at rest: where its part does, a left limb's on the left, unturned. */
    private Place home(final BoneAt b) {
        final Part part = b.side() == Side.LEFT && b.bone().part().side() != 0 ? b.bone().part().other()
                : b.bone().part();
        return new Place(rest.frame(part).at(), Rot.NONE);
    }

    /** A choice's words. */
    private static final class Said<T> extends javafx.util.StringConverter<T> {

        private final java.util.function.Function<T, String> words;

        Said(final java.util.function.Function<T, String> words) {
            this.words = words;
        }

        @Override
        public String toString(final T t) {
            return t == null ? "" : words.apply(t);
        }

        @Override
        public T fromString(final String s) {
            return null;
        }
    }
}
