package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The bone bench: one bone at a time, on its own ({@link Bone}), its surface found as finely as asked, a man's or a
 * woman's or between; every bone in a tree by where in the body it is. A model, still: chosen or changed, it is made
 * afresh away from the screen and shown in place of the one before.
 *
 * <p>{@code -Dbones.bone} (a bone's name) the one shown first; {@code -Dbones.detail} (cm), {@code -Dbones.female}
 * (0 to 1), {@code -Dbones.yaw} and {@code -Dbones.pitch} how.</p>
 */
public final class BoneBench extends Application {

    /** How big a bone is shown at the least, cm: one smaller is shown bigger. */
    private static final double SEEN = 12;

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final Slider female = new Slider(0, 1, Double.parseDouble(System.getProperty("bones.female", "0")));
    private final ChoiceBox<Double> detail = new ChoiceBox<>();
    private final TreeView<Object> tree = new TreeView<>();
    private final Label about = new Label();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "bone");
        t.setDaemon(true);
        return t;
    });
    private Bone shown;
    private int asked;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Bone bench", bus);
        final ScrollPane side = new ScrollPane(controls(shell));
        side.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The bones"), side, 360);
        shell.middle(view.node());
        view.zoom(0.5, 400);
        view.lookAt(0, 0, 0);
        view.from("bones", Double.parseDouble(System.getProperty("bones.yaw", "200")),
                Double.parseDouble(System.getProperty("bones.pitch", "10")), 1);
        fill();
        shell.show(stage, 1280, 860);
    }

    /** The bench's side: whose bones, how fine, which. */
    private Node controls(final Shell shell) {
        detail.getItems().setAll(0.04, 0.06, 0.1, 0.15, 0.25, 0.4);
        detail.setValue(Double.parseDouble(System.getProperty("bones.detail", "0.1")));
        detail.setOnAction(e -> make());
        female.setShowTickLabels(true);
        female.setMajorTickUnit(0.5);
        female.valueChangingProperty().addListener((o, was, is) -> {
            if (!is) {
                make();
            }
        });
        tree.setShowRoot(false);
        tree.setPrefHeight(520);
        tree.setCellFactory(t -> new TreeCell<>() {
            @Override
            protected void updateItem(final Object item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item instanceof Bone b ? label(b) : item.toString());
            }
        });
        tree.getSelectionModel().selectedItemProperty().addListener((o, was, is) -> {
            if (is != null && is.getValue() instanceof Bone b) {
                shown = b;
                make();
            }
        });
        about.setWrapText(true);
        final VBox box = new VBox(8, new Label("Whose: a man's (0), a woman's (1)"), female,
                new Label("Detail, cm between samples"), detail, new Label("The bones, by where they are"), tree,
                about, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    private String label(final Bone b) {
        return b.name() + (b.paired() ? ", each side" : "") + (b.kind() == Kind.BONE ? ""
                : " (" + b.kind().name().toLowerCase(Locale.ROOT) + ")");
    }

    /** The tree filled with the skeleton's bones by region, the one asked for first chosen. */
    private void fill() {
        final TreeItem<Object> root = new TreeItem<>("Skeleton");
        final Map<Region, TreeItem<Object>> by = new EnumMap<>(Region.class);
        final List<Bone> bones = Skeleton.INSTANCE.bones();
        for (final Region r : Region.values()) {
            final int n = bones.stream().filter(b -> b.region() == r && b.kind() == Kind.BONE)
                    .mapToInt(b -> b.paired() ? 2 : 1).sum();
            by.put(r, new TreeItem<>(r.label() + " (" + n + ")"));
            root.getChildren().add(by.get(r));
        }
        final String wanted = System.getProperty("bones.bone", bones.getFirst().name());
        TreeItem<Object> pick = null;
        for (final Bone b : bones) {
            final TreeItem<Object> item = new TreeItem<>(b);
            by.get(b.region()).getChildren().add(item);
            if (b.name().equalsIgnoreCase(wanted)) {
                pick = item;
                item.getParent().setExpanded(true);
            }
        }
        tree.setRoot(root);
        tree.getSelectionModel().select(pick != null ? pick : root.getChildren().getFirst());
    }

    /** The bone shown made afresh, away from the screen; shown when made, unless another has been asked for since. */
    private void make() {
        if (shown == null) {
            return;
        }
        final Bone bone = shown;
        final int mine = ++asked;
        final double f = female.getValue();
        final double cell = Math.min(detail.getValue(), bone.coarsest());
        worker.submit(() -> {
            final long t0 = System.nanoTime();
            final Mesher.Mesh mesh = Mesher.of(new Field(Drawn.INSTANCE.shape(bone, f).elements()), cell);
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            final Node node = BoneView.INSTANCE.node(mesh, bone.paint());
            final double[] size = BoneView.INSTANCE.size(mesh);
            Platform.runLater(() -> {
                if (mine == asked) {
                    show(bone, mesh, node, size, cell, ms);
                }
            });
        });
    }

    private void show(final Bone bone, final Mesher.Mesh mesh, final Node node, final double[] size, final double cell,
                      final long ms) {
        // a small bone shown bigger, as under a glass, so that it is seen as well as a big one; standing where it stood
        final double most = Math.max(size[0], Math.max(size[1], size[2]));
        final double glass = Math.max(1, SEEN / most);
        node.getTransforms().add(new Scale(glass, glass, glass, 0, 0, 0));
        view.show(ModelView.Model.of(node));
        view.lookAt(0, size[1] * glass / 2, 0);
        view.distance(most * glass * 2.6);
        about.setText(String.format(Locale.ROOT, "%s: %s, moving with the %s%s.%n%.1f by %.1f by %.1f cm%s;"
                        + " %,d triangles, found on a grid %.2f cm apart in %,d ms.", bone.name(),
                bone.region().label(), bone.part().said(), bone.paired() ? " (and its fellow on the left)" : "",
                size[0], size[1], size[2], glass > 1 ? String.format(Locale.ROOT, ", shown %.0f times as big", glass)
                        : "", mesh.triangleCount(), cell, ms));
    }
}
