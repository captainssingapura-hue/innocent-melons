package com.example.fxsuite.life.body.head;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.BoneView;
import com.example.fxsuite.life.body.cage.CageView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The head bench, a head made as clay is: its blank first, the head's shape with nothing on it ({@link BlankFit}),
 * fitted to a reference head read from a file on the machine, never kept in the code ({@link ReferenceReading}), its
 * features masked out of the fit ({@link FeatureMasks}); then its pieces pressed on it, its nose from the reference
 * ({@link Pieces}); the reference beside it or over it as lines, its features in
 * a colour of their own, to see what was left out; the blank as lines, to see the reference through it.
 *
 * <p>{@code -Dhead.reference} (the reference's path), {@code -Dhead.compare} (beside, over), {@code -Dhead.show}
 * (blank, lines, reference, features, bare: its nose not put on, by commas); {@code -Dhead.yaw}, {@code
 * -Dhead.pitch}, {@code -Dhead.distance} where it is seen from.</p>
 */
public final class HeadBench extends Application {

    /** How far beside the blank the reference is shown, cm. */
    private static final double BESIDE = 24;
    /** How finely the blank is made a solid, and the head's surface found, cm. */
    private static final double BLANK_CELL = 0.25;
    private static final double HEAD_CELL = 0.15;

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final CheckBox blank = new CheckBox("The blank");
    private final CheckBox lines = new CheckBox("The blank as lines");
    private final CheckBox reference = new CheckBox("The reference");
    private final CheckBox features = new CheckBox("Its features, masked, in their own colour");
    private final CheckBox nose = new CheckBox("Put on: its nose");
    private final ChoiceBox<String> compare = new ChoiceBox<>();
    private final Label about = new Label("Reading the reference");
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "head");
        t.setDaemon(true);
        return t;
    });
    private ReferenceHead referred;
    private Feature[] masks;
    private Mesher.Mesh fitted;
    /** The blank made a solid, the reference's nose pressed on it. */
    private Mesher.Mesh withNose;
    private double lowest;

    @Override
    public void start(final Stage window) {
        final Shell shell = new Shell("Head", bus);
        shell.bar(List.of());
        shell.side(new Label("The head"), controls(shell), 340);
        shell.middle(view.node());
        view.zoom(2, 300);
        view.from("head", 25, 5, 0.7);
        shell.show(window, 1400, 920);
        final String path = System.getProperty("head.reference");
        if (path == null) {
            about.setText("No reference asked for: -Dhead.reference=<the path of a body's OBJ>.");
            return;
        }
        bus.publish(new Bus.Working(true, "head"));
        worker.submit(() -> make(Path.of(path)));
    }

    /** The reference read, its features masked, the blank fitted to it, away from the screen; then drawn. */
    private void make(final Path path) {
        try {
            final long t0 = System.nanoTime();
            final ReferenceHead r = ReferenceReading.INSTANCE.of(path);
            final Feature[] m = FeatureMasks.INSTANCE.of(r);
            final Mesher.Mesh b = BlankFit.INSTANCE.of(r, m);
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            final long t1 = System.nanoTime();
            final Field.Shape solid = MeshSolid.INSTANCE.closed(b, BLANK_CELL);
            final Mesher.Mesh n = Mesher.of(Pieces.INSTANCE.head(solid,
                    List.of(Pieces.INSTANCE.of(r, Feature.NOSE, solid))), HEAD_CELL);
            final long put = (System.nanoTime() - t1) / 1_000_000;
            Platform.runLater(() -> {
                referred = r;
                masks = m;
                fitted = b;
                withNose = n;
                lowest = lowest(r.mesh());
                view.lookAt(0, (highest(r.mesh()) - lowest) / 2, 0);
                bus.publish(new Bus.Working(false, "head"));
                about.setText(String.format(Locale.ROOT, "The reference, %s: %,d triangles. Its blank fitted in %,d"
                        + " ms: %,d points. Made a solid, its nose put on, in %,d ms: %,d points.", path.getFileName(),
                        r.mesh().triangleCount(), ms, b.size(), put, n.size()));
                draw();
            });
        } catch (final IOException | RuntimeException e) {
            Platform.runLater(() -> {
                bus.publish(new Bus.Working(false, "head"));
                about.setText("The reference not read: " + e.getMessage());
            });
        }
    }

    /** The bench's side: what is shown, and how the reference is. */
    private Node controls(final Shell shell) {
        final String show = System.getProperty("head.show", "blank,reference,features").toLowerCase(Locale.ROOT);
        blank.setSelected(show.contains("blank"));
        lines.setSelected(show.contains("lines"));
        reference.setSelected(show.contains("reference"));
        features.setSelected(show.contains("features"));
        nose.setSelected(!show.contains("bare"));
        compare.getItems().setAll("Beside it", "Over it, as lines");
        compare.getSelectionModel().select(System.getProperty("head.compare", "beside").startsWith("o") ? 1 : 0);
        compare.setOnAction(e -> draw());
        for (final CheckBox c : List.of(blank, lines, reference, features, nose)) {
            c.setOnAction(e -> draw());
        }
        about.setWrapText(true);
        final VBox side = new VBox(8, new Label("Shown"), blank, nose, lines, reference, compare, features, about,
                shell.problems());
        side.setPadding(new Insets(10));
        return side;
    }

    /** What is chosen drawn, standing on the ground. */
    private void draw() {
        if (referred == null) {
            return;
        }
        final List<Node> nodes = new ArrayList<>();
        if (blank.isSelected()) {
            nodes.add(lined(BoneView.INSTANCE.mapped(nose.isSelected() ? withNose : fitted, Paint.SKIN,
                    CageView.INSTANCE::scene, true), lines.isSelected()));
        }
        if (reference.isSelected()) {
            final boolean over = compare.getSelectionModel().getSelectedIndex() == 1;
            final V3 by = over ? V3.ZERO : new V3(-BESIDE, 0, 0);
            final Mesher.Mesh rest = features.isSelected() ? only(referred.mesh(), false) : referred.mesh();
            nodes.add(lined(BoneView.INSTANCE.mapped(rest, over ? Paint.FRAME : Paint.SKIN,
                    v -> CageView.INSTANCE.scene(v.plus(by)), true), over));
            if (features.isSelected()) {
                nodes.add(lined(BoneView.INSTANCE.mapped(only(referred.mesh(), true), Paint.PANEL,
                        v -> CageView.INSTANCE.scene(v.plus(by)), true), over));
            }
        }
        final Group world = new Group(nodes);
        world.getTransforms().add(new Translate(0, lowest, 0));
        view.show(ModelView.Model.of(world));
    }

    /** Of {@code m}, only its triangles of a feature, if {@code masked}, or only those of none. */
    private Mesher.Mesh only(final Mesher.Mesh m, final boolean masked) {
        final int[] t = m.triangles();
        final List<Integer> kept = new ArrayList<>();
        for (int k = 0; k < masks.length; k++) {
            if ((masks[k] != null) == masked) {
                kept.addAll(List.of(t[3 * k], t[3 * k + 1], t[3 * k + 2]));
            }
        }
        return new Mesher.Mesh(m.points(), kept.stream().mapToInt(Integer::intValue).toArray());
    }

    /** {@code g}, its surfaces drawn as lines if {@code asLines}, to see through. */
    private Group lined(final Group g, final boolean asLines) {
        if (asLines) {
            g.getChildren().forEach(n -> {
                if (n instanceof MeshView v) {
                    v.setDrawMode(DrawMode.LINE);
                }
            });
        }
        return g;
    }

    private double lowest(final Mesher.Mesh m) {
        double y = 1e9;
        for (int i = 1; i < m.points().length; i += 3) {
            y = Math.min(y, m.points()[i]);
        }
        return y;
    }

    private double highest(final Mesher.Mesh m) {
        double y = -1e9;
        for (int i = 1; i < m.points().length; i += 3) {
            y = Math.max(y, m.points()[i]);
        }
        return y;
    }
}
