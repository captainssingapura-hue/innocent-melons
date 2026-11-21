package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.CageView;
import com.example.fxsuite.life.body.flesh.Fleshing;
import com.example.fxsuite.life.body.muscle.leg.LegVolumes;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The muscle bench: the body's muscles on its bones ({@link Musculature}), still, held as the flesh is bound, its arms
 * out from its sides; every muscle in a tree by region; the one chosen alone, or with its region's, or with every one,
 * brighter than the rest; on both sides or the right; the deep ones and the bones if asked. Each muscle's surface found
 * once, away from the screen, and kept.
 *
 * <p>{@code -Dmuscles.muscle} (a muscle's name) the one chosen first; {@code -Dmuscles.shown} (alone, region, all);
 * {@code -Dmuscles.hide} (any of bones, deep, left); {@code -Dmuscles.yaw}, {@code -Dmuscles.pitch} and
 * {@code -Dmuscles.distance} where it is seen from.</p>
 */
public final class MuscleBench extends Application {

    /** What is shown with the muscle chosen. */
    private enum Shown {
        ALONE("This muscle alone"), REGION("Its region's"), ALL("Every muscle");

        private final String said;

        Shown(final String said) {
            this.said = said;
        }

        @Override
        public String toString() {
            return said;
        }
    }

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final TreeView<Object> tree = new TreeView<>();
    private final ChoiceBox<Shown> shown = new ChoiceBox<>();
    private final CheckBox both = new CheckBox("Both sides");
    private final CheckBox deep = new CheckBox("The deep muscles");
    private final CheckBox bones = new CheckBox("The bones");
    private final CheckBox measured = new CheckBox("Sized as measured, not as drawn");
    private final Label about = new Label();
    private final Rig rig = new Rig(Proportions.DRAWN, Fleshing.INSTANCE.bind(), false);
    private final Measured asMeasured = new Measured(Proportions.DRAWN.stature() / 100, Proportions.DRAWN.mass());
    private final Map<String, MuscleSurface.Surface> surfaces = new ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "muscles");
        t.setDaemon(true);
        return t;
    });
    private Group skeleton;
    private Muscle chosen;
    private int asked;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Muscle bench", bus);
        final ScrollPane side = new ScrollPane(controls(shell));
        side.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The muscles"), side, 380);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("muscles", 200, 6, 2.6);
        fill();
        shell.show(stage, 1400, 920);
    }

    /** The bench's side: which muscle, what with it, which sides, the deep ones, the bones. */
    private Node controls(final Shell shell) {
        shown.getItems().setAll(Shown.values());
        final String s = System.getProperty("muscles.shown", "region").toLowerCase(Locale.ROOT);
        shown.setValue(s.startsWith("al") && !s.startsWith("all") ? Shown.ALONE : s.startsWith("all") ? Shown.ALL
                : Shown.REGION);
        shown.setOnAction(e -> make());
        final String hide = System.getProperty("muscles.hide", "").toLowerCase(Locale.ROOT);
        both.setSelected(!hide.contains("left"));
        measured.setSelected(!"drawn".equalsIgnoreCase(System.getProperty("muscles.sized", "measured")));
        deep.setSelected(!hide.contains("deep"));
        bones.setSelected(!hide.contains("bones"));
        for (final CheckBox c : List.of(both, deep, bones, measured)) {
            c.setOnAction(e -> make());
        }
        tree.setShowRoot(false);
        tree.setPrefHeight(480);
        tree.setCellFactory(t -> new TreeCell<>() {
            @Override
            protected void updateItem(final Object item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item instanceof Muscle m ? m.name()
                        + (m.layer() == Layer.SUPERFICIAL ? "" : " (" + m.layer().said() + ")") : item.toString());
            }
        });
        tree.getSelectionModel().selectedItemProperty().addListener((o, was, is) -> {
            if (is != null && is.getValue() instanceof Muscle m) {
                chosen = m;
                make();
            }
        });
        about.setWrapText(true);
        final VBox box = new VBox(8, new Label("Shown with it"), shown, both, deep, bones, measured,
                new Label("The muscles, by where they are"), tree, about, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    /** The tree filled with the muscles by region, the one asked for first chosen. */
    private void fill() {
        final TreeItem<Object> root = new TreeItem<>("Muscles");
        final Map<MuscleRegion, TreeItem<Object>> by = new EnumMap<>(MuscleRegion.class);
        final List<Muscle> all = Musculature.INSTANCE.muscles();
        for (final MuscleRegion r : MuscleRegion.values()) {
            by.put(r, new TreeItem<>(r.label() + " (" + all.stream().filter(m -> m.region() == r).count() + ")"));
            root.getChildren().add(by.get(r));
        }
        final String wanted = System.getProperty("muscles.muscle", all.getFirst().name());
        TreeItem<Object> pick = null;
        for (final Muscle m : all) {
            final TreeItem<Object> item = new TreeItem<>(m);
            by.get(m.region()).getChildren().add(item);
            if (m.name().equalsIgnoreCase(wanted)) {
                pick = item;
                item.getParent().setExpanded(true);
            }
        }
        tree.setRoot(root);
        tree.getSelectionModel().select(pick != null ? pick : root.getChildren().getFirst().getChildren().getFirst());
    }

    /** What is to be shown made, away from the screen, the surfaces not yet found found; shown when all are made. */
    private void make() {
        if (chosen == null) {
            return;
        }
        final int mine = ++asked;
        final Muscle m = chosen;
        final List<Muscle> with = Musculature.INSTANCE.muscles().stream().filter(x -> x == m
                || shown.getValue() == Shown.ALL || shown.getValue() == Shown.REGION && x.region() == m.region())
                .filter(x -> x == m || deep.isSelected() || x.layer() != Layer.DEEP).toList();
        final boolean left = both.isSelected();
        final boolean withBones = bones.isSelected();
        final Muscularity bulk = measured.isSelected() ? asMeasured : AsDrawn.INSTANCE;
        bus.publish(new Bus.Working(true, "muscles"));
        worker.submit(() -> {
            final long t0 = System.nanoTime();
            final List<Node> nodes = new ArrayList<>();
            for (final Muscle x : with) {
                for (final boolean side : left && x.paired() ? new boolean[] {false, true} : new boolean[] {false}) {
                    nodes.add(drawn(x, side, x == m, bulk));
                }
            }
            if (withBones && skeleton == null) {
                skeleton = CageView.INSTANCE.bones(rig, 0, new Detail(0.4), p -> true);
            }
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            Platform.runLater(() -> {
                if (mine == asked) {
                    show(m, nodes, withBones, ms);
                }
                bus.publish(new Bus.Working(false, "muscles"));
            });
        });
    }

    /** Muscle {@code m} on one side drawn, its belly coloured as deep as it lies, brighter if chosen, its tendons pale. */
    private Node drawn(final Muscle m, final boolean left, final boolean picked, final Muscularity bulk) {
        final MuscleSurface.Surface s = surfaces.computeIfAbsent(m.name() + (left ? " left" : " right") + bulk,
                k -> MuscleSurface.INSTANCE.surface(m, bulk, rig, left, 2));
        final String belly = picked ? "#e8a03a" : switch (m.layer()) {
            case SUPERFICIAL -> "#b0453a";
            case DEEP -> "#7c3029";
            case TENDON -> "#ddd3bf";
        };
        return MuscleView.INSTANCE.node(s, belly, picked ? "#f2dfb0" : "#ddd3bf", CageView.INSTANCE::scene, true);
    }

    /** {@code nodes} shown, the bones with them if asked; looked at round the muscle chosen; told. */
    private void show(final Muscle m, final List<Node> nodes, final boolean withBones, final long ms) {
        final Group world = new Group(nodes);
        if (withBones) {
            world.getChildren().add(skeleton);
        }
        final V3 heel = rig.frame(Part.RIGHT_FOOT).place(new V3(0, -6.4, -4.0));
        world.getTransforms().add(new Translate(0, heel.y(), 0));
        view.show(ModelView.Model.of(world));
        V3 mid = V3.ZERO;
        double reach = 0;
        for (final Section s : m.sections()) {
            mid = mid.plus(s.at().in(rig, false).times(1.0 / m.sections().size()));
        }
        for (final Section s : m.sections()) {
            reach = Math.max(reach, s.at().in(rig, false).minus(mid).length());
        }
        if (shown.getValue() == Shown.ALL) {
            view.lookAt(0, 90, 0);
            view.distance(260);
        } else {
            view.lookAt(-mid.x(), mid.y() - heel.y(), -mid.z());
            view.distance(Math.max(60, reach * (shown.getValue() == Shown.ALONE ? 3.2 : 4.5)));
        }
        about.setText(String.format(Locale.ROOT, "The %s: %s, %s.%nFrom the %s to the %s, %d sections.%n"
                        + "%s%nMade in %,d ms.", m.name(), m.region().label().toLowerCase(Locale.ROOT), m.layer().said(),
                m.origin(), m.insertion(), m.sections().size(), measure(m), ms));
    }

    /** How big {@code m} was measured in a body the drawn figure's height and mass, if it was. */
    private String measure(final Muscle m) {
        final String by = LegVolumes.INSTANCE.volume(m.name(), 1).isPresent() ? "Handsfield" : "Holzbaur";
        return asMeasured.measured(m)
                .map(x -> String.format(Locale.ROOT, "Measured %.0f cm3 in a body %.0f cm tall and %.0f kg"
                        + " (%s).", x, Proportions.DRAWN.stature(), Proportions.DRAWN.mass(), by))
                .orElse("Not measured: as drawn.");
    }
}
