package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.March;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.CageView;
import com.example.fxsuite.life.body.flesh.FleshView;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.muscle.MusclesView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The fat bench: a man's fat over his muscles and bones, no skin yet ({@link FatLayer}), as thick as asked, from a lean
 * athlete's to a heavy man's, as Störchle's ten young men had it on average by default ({@link FatMap}); the muscles
 * and the bones under it shown or not; held as it was found, its arms out from its sides, or standing at rest, or
 * kicking out in the parade step, the fat moving with it. The body found once, away from the screen; the fat for each
 * thickness asked, and kept.
 *
 * <p>{@code -Dfat.mean} (mm), {@code -Dfat.hide} (fat, muscles, bones), {@code -Dfat.held} (fitted, rest, kick);
 * {@code -Dfat.yaw}, {@code -Dfat.pitch}, {@code -Dfat.distance} where it is seen from.</p>
 */
public final class FatBench extends Application {

    /** How thick, on average, mm: a lean athlete, the men measured, their fattest, heavier. */
    private final List<Double> means = List.of(1.5, FatMap.MEASURED, 5.5, 8.0, 12.0);
    private static final V3 SIGNS = new V3(-1, -1, -1);

    private final Proportions proportions = Proportions.PAPERS;
    private final Rig bind = new Rig(proportions, BoundPose.INSTANCE.pose(), false);
    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final CheckBox fat = new CheckBox("The fat");
    private final CheckBox muscles = new CheckBox("The muscles");
    private final CheckBox bones = new CheckBox("The bones");
    private final ChoiceBox<Double> mean = new ChoiceBox<>();
    private final ChoiceBox<String> held = new ChoiceBox<>();
    private final Label about = new Label("Finding the body");
    private final Map<Double, FatLayer.Fat> fats = new ConcurrentHashMap<>();
    private final Map<Double, Long> took = new ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "fat");
        t.setDaemon(true);
        return t;
    });
    private MuscleBody.Bound body;
    private Nearness near;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Fat", bus);
        final String hide = System.getProperty("fat.hide", "bones").toLowerCase(Locale.ROOT);
        fat.setSelected(!hide.contains("fat"));
        muscles.setSelected(!hide.contains("muscles"));
        bones.setSelected(!hide.contains("bones"));
        mean.getItems().setAll(means);
        mean.setValue(Double.parseDouble(System.getProperty("fat.mean", String.valueOf(FatMap.MEASURED))));
        held.getItems().setAll("As it was found", "Standing at rest", "Kicking out, the parade step");
        final String h = System.getProperty("fat.held", "rest").toLowerCase(Locale.ROOT);
        held.getSelectionModel().select(h.startsWith("f") ? 0 : h.startsWith("k") ? 2 : 1);
        for (final CheckBox c : List.of(fat, muscles, bones)) {
            c.setOnAction(e -> draw());
        }
        held.setOnAction(e -> draw());
        mean.setOnAction(e -> make());
        about.setWrapText(true);
        final VBox side = new VBox(8, new Label("Shown"), fat, muscles, bones,
                new Label("How thick on average, mm"), mean, new Label("Held"), held, about, shell.problems());
        side.setPadding(new Insets(10));
        shell.bar(List.of());
        shell.side(new Label("The fat"), side, 320);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("fat", 200, 6, 2.6);
        view.lookAt(0, 90, 0);
        shell.show(stage, 1400, 920);
        make();
    }

    /** The body, if not yet found, and the fat as thick as asked, found away from the screen; then drawn. */
    private void make() {
        final double m = mean.getValue();
        bus.publish(new Bus.Working(true, "fat"));
        worker.submit(() -> {
            if (body == null) {
                body = MuscleBody.INSTANCE.make(proportions, true, 1);
                near = new Nearness(FatBeneath.INSTANCE.of(body, bind));
            }
            final long t0 = System.nanoTime();
            fats.computeIfAbsent(m, k -> FatLayer.INSTANCE.of(new FatTarget(near, new FatMap(k), bind)));
            took.putIfAbsent(m, (System.nanoTime() - t0) / 1_000_000);
            Platform.runLater(() -> {
                bus.publish(new Bus.Working(false, "fat"));
                draw();
            });
        });
    }

    /** What is chosen drawn, held as asked, standing on the ground. */
    private void draw() {
        final FatLayer.Fat f = fats.get(mean.getValue());
        if (body == null || f == null) {
            return;
        }
        final Rig rig = new Rig(proportions, pose(), false);
        final List<Node> nodes = new ArrayList<>();
        if (muscles.isSelected()) {
            nodes.add(new MusclesView(body, SIGNS, rig).node());
        }
        if (bones.isSelected()) {
            nodes.add(CageView.INSTANCE.bones(rig, 0, new Detail(0.6), p -> true));
        }
        if (fat.isSelected()) {
            nodes.add(new FleshView(f.flesh(), "#e3c27a", SIGNS, rig).node());
        }
        final Group world = new Group(nodes);
        final double ground = rig.contacts().stream().mapToDouble(V3::y).min().orElse(0);
        world.getTransforms().add(new Translate(0, ground, 0));
        view.show(ModelView.Model.of(world));
        about.setText(String.format(Locale.ROOT, "The fat: its cage %,d points, smoothed to %,d; %,d found no"
                        + " fat outside.%nFound in %,d ms.", f.cage().size(), f.flesh().size(), f.missed().size(),
                took.getOrDefault(mean.getValue(), 0L)));
    }

    private Pose pose() {
        return switch (held.getSelectionModel().getSelectedIndex()) {
            case 0 -> BoundPose.INSTANCE.pose();
            case 2 -> March.INSTANCE.at(Move.PARADE_STEP, 0.82, 0, false);
            default -> Pose.REST;
        };
    }
}
