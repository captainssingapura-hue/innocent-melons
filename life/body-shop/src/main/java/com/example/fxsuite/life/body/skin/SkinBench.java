package com.example.fxsuite.life.body.skin;

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
import com.example.fxsuite.life.body.fat.FatBeneath;
import com.example.fxsuite.life.body.fat.FatLayer;
import com.example.fxsuite.life.body.fat.FatMap;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.flesh.Flesh;
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
 * The skin bench: a man's skin over his muscles and bones, the fat under it as how thick it is ({@link SkinFit}); or,
 * slower, over his fat as a layer of its own ({@link SkinLayer}); each shown or not; his fat as thick as asked, from a
 * lean athlete's to a heavy man's, as Störchle's ten young men had it on average by default ({@link FatMap}); his
 * skin drawn as taut as asked; held as it was found, its arms out from its sides, or standing at rest, or kicking out
 * in the parade step, all of it moving with him. The body found once, away from the screen; the fat and the skin for
 * each asked, and kept.
 *
 * <p>{@code -Dskin.mean} (mm), {@code -Dskin.tension}, {@code -Dskin.fat=layer} (the fat a layer of its own),
 * {@code -Dskin.show} (skin, fat, muscles, bones, by commas),
 * {@code -Dskin.held} (fitted, rest, kick); {@code -Dskin.at} how high it is looked at (cm); {@code -Dskin.yaw},
 * {@code -Dskin.pitch}, {@code -Dskin.distance} where
 * it is seen from.</p>
 */
public final class SkinBench extends Application {

    private static final V3 SIGNS = new V3(-1, -1, -1);
    /** How thick the skin is, cm, everywhere for now. */
    private static final double SKIN = 0.2;

    /** How thick, on average, mm: a lean athlete, the men measured, their fattest, heavier. */
    private final List<Double> means = List.of(1.5, FatMap.MEASURED, 5.5, 8.0, 12.0);
    private final List<Integer> tensions = List.of(0, 5, 15, 30);
    private final Proportions proportions = Proportions.PAPERS;
    private final Rig bind = new Rig(proportions, BoundPose.INSTANCE.pose(), false);
    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final CheckBox skin = new CheckBox("The skin");
    private final CheckBox fat = new CheckBox("The fat");
    private final CheckBox muscles = new CheckBox("The muscles");
    private final CheckBox bones = new CheckBox("The bones");
    private final CheckBox layered = new CheckBox("The fat a layer of its own (slow)");
    private final ChoiceBox<Double> mean = new ChoiceBox<>();
    private final ChoiceBox<Integer> tension = new ChoiceBox<>();
    private final ChoiceBox<String> held = new ChoiceBox<>();
    private final Label about = new Label("Finding the body");
    private final Map<Double, FatLayer.Fat> fats = new ConcurrentHashMap<>();
    private final Map<String, Flesh> skins = new ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "skin");
        t.setDaemon(true);
        return t;
    });
    private MuscleBody.Bound body;
    private Nearness near;
    private long took;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Skin", bus);
        final String show = System.getProperty("skin.show", "skin").toLowerCase(Locale.ROOT);
        skin.setSelected(show.contains("skin"));
        fat.setSelected(show.contains("fat"));
        muscles.setSelected(show.contains("muscles"));
        bones.setSelected(show.contains("bones"));
        layered.setSelected("layer".equalsIgnoreCase(System.getProperty("skin.fat", "")));
        layered.setOnAction(e -> make());
        mean.getItems().setAll(means);
        mean.setValue(Double.parseDouble(System.getProperty("skin.mean", String.valueOf(FatMap.MEASURED))));
        tension.getItems().setAll(tensions);
        tension.setValue(Integer.getInteger("skin.tension", 15));
        held.getItems().setAll("As it was found", "Standing at rest", "Kicking out, the parade step");
        final String h = System.getProperty("skin.held", "rest").toLowerCase(Locale.ROOT);
        held.getSelectionModel().select(h.startsWith("f") ? 0 : h.startsWith("k") ? 2 : 1);
        for (final CheckBox c : List.of(skin, fat, muscles, bones)) {
            c.setOnAction(e -> draw());
        }
        held.setOnAction(e -> draw());
        mean.setOnAction(e -> make());
        tension.setOnAction(e -> make());
        about.setWrapText(true);
        final VBox side = new VBox(8, new Label("Shown"), skin, fat, muscles, bones, layered,
                new Label("How thick the fat is on average, mm"), mean, new Label("How taut the skin is"), tension,
                new Label("Held"), held, about, shell.problems());
        side.setPadding(new Insets(10));
        shell.bar(List.of());
        shell.side(new Label("The skin"), side, 320);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("skin", 200, 6, 2.6);
        view.lookAt(0, Double.parseDouble(System.getProperty("skin.at", "90")), 0);
        shell.show(stage, 1400, 920);
        make();
    }

    /** The body, if not yet found, and the fat and the skin as asked, found away from the screen; then drawn. */
    private void make() {
        final double m = mean.getValue();
        final int k = tension.getValue();
        final boolean asLayer = layered.isSelected();
        bus.publish(new Bus.Working(true, "skin"));
        worker.submit(() -> {
            try {
                if (body == null) {
                    body = MuscleBody.INSTANCE.make(proportions, true, 1);
                    near = new Nearness(FatBeneath.INSTANCE.of(body, bind));
                }
                final long t0 = System.nanoTime();
                if (asLayer) {
                    final FatTarget target = new FatTarget(near, new FatMap(m), bind);
                    final FatLayer.Fat f = fats.computeIfAbsent(m, x -> FatLayer.INSTANCE.of(target));
                    skins.computeIfAbsent(key(m, k, true), x -> SkinLayer.INSTANCE.of(f, target, SKIN, k));
                } else {
                    skins.computeIfAbsent(key(m, k, false), x -> SkinFit.INSTANCE.of(new FatTarget(near,
                            new FatMap(m, SKIN), bind), SKIN, k));
                }
                took = (System.nanoTime() - t0) / 1_000_000;
            } finally {
                Platform.runLater(() -> {
                    bus.publish(new Bus.Working(false, "skin"));
                    draw();
                });
            }
        });
    }

    /** What is chosen drawn, held as asked, standing on the ground. */
    private void draw() {
        final FatLayer.Fat f = layered.isSelected() ? fats.get(mean.getValue()) : null;
        final Flesh s = skins.get(key(mean.getValue(), tension.getValue(), layered.isSelected()));
        if (body == null || s == null) {
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
        if (fat.isSelected() && f != null) {
            nodes.add(new FleshView(f.flesh(), "#e3c27a", SIGNS, rig).node());
        }
        if (skin.isSelected()) {
            nodes.add(new FleshView(s, "#d9a98c", SIGNS, rig).node());
        }
        final Group world = new Group(nodes);
        final double ground = rig.contacts().stream().mapToDouble(V3::y).min().orElse(0);
        world.getTransforms().add(new Translate(0, ground, 0));
        view.show(ModelView.Model.of(world));
        about.setText(f != null ? String.format(Locale.ROOT, "The fat a layer of its own: its cage %,d points,"
                + " smoothed to %,d; %,d found no fat outside.%nThe skin over it, %.1f mm, as many points.%nFound in"
                + " %,d ms.", f.cage().size(), f.flesh().size(), f.missed().size(), SKIN * 10, took)
                : String.format(Locale.ROOT, "The skin, %.1f mm, over the muscles and bones, the fat under it as how"
                + " thick it is: %,d points.%nFound in %,d ms.", SKIN * 10, s.size(), took));
    }

    /** Which skin: over how much fat, how taut, over the fat as a layer of its own or not. */
    private String key(final double m, final int k, final boolean asLayer) {
        return m + "/" + k + (asLayer ? "/layer" : "");
    }

    private Pose pose() {
        return switch (held.getSelectionModel().getSelectedIndex()) {
            case 0 -> BoundPose.INSTANCE.pose();
            case 2 -> March.INSTANCE.at(Move.PARADE_STEP, 0.82, 0, false);
            default -> Pose.REST;
        };
    }
}
