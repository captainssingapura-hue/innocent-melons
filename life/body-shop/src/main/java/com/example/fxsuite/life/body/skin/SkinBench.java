package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.March;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.Part;
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
import com.example.fxsuite.life.body.flesh.PartCut;
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
import java.util.function.Predicate;

/**
 * The skin bench: a man's skin over his muscles and bones, the fat under it as how thick it is ({@link SkinFit}); or,
 * slower, over his fat as a layer of its own ({@link SkinLayer}); each shown or not; his fat as thick as asked, from a
 * lean athlete's to a heavy man's, as Störchle's ten young men had it on average by default ({@link FatMap}); his
 * skin drawn as taut as asked; held as it was found, its arms out from its sides, or standing at rest, or kicking out
 * in the parade step, all of it moving with him. The body found once, away from the screen; the fat and the skin for
 * each asked, and kept.
 *
 * <p>{@code -Dskin.mean} (mm), {@code -Dskin.tension}, {@code -Dskin.scan} (0 or 1: fitted to it as a scan, over
 * the cage or the cage smoothed once), {@code -Dskin.fat=layer} (the fat a layer of its own),
 * {@code -Dskin.show} (skin, fat, muscles, bones, by commas),
 * {@code -Dskin.held} (fitted, rest, kick); {@code -Dskin.at} how high it is looked at, {@code -Dskin.across} how far
 * to its left (cm); {@code -Dskin.part} one part alone, as it is called, {@code right hand}, say;
 * {@code -Dskin.attached} with what it hangs from; {@code -Dskin.yaw},
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
    private final PartCut partCut = new PartCut(bind);
    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final CheckBox skin = new CheckBox("The skin");
    private final CheckBox fat = new CheckBox("The fat");
    private final CheckBox muscles = new CheckBox("The muscles");
    private final CheckBox bones = new CheckBox("The bones");
    private final CheckBox layered = new CheckBox("The fat a layer of its own (slow)");
    private final ChoiceBox<Double> mean = new ChoiceBox<>();
    private final ChoiceBox<Integer> tension = new ChoiceBox<>();
    private final ChoiceBox<String> scan = new ChoiceBox<>();
    private final ChoiceBox<String> held = new ChoiceBox<>();
    private final ChoiceBox<String> part = new ChoiceBox<>();
    private final CheckBox attached = new CheckBox("With what it hangs from");
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
    /** Whether to look afresh at what is drawn, a part or how it is held chosen since it was last. */
    private boolean aim = true;

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
        scan.getItems().setAll("As found", "Fitted to it as a scan, over the cage",
                "Fitted to it as a scan, over the cage smoothed once");
        scan.getSelectionModel().select(Integer.getInteger("skin.scan", -1) + 1);
        held.getItems().setAll("As it was found", "Standing at rest", "Kicking out, the parade step");
        final String h = System.getProperty("skin.held", "rest").toLowerCase(Locale.ROOT);
        held.getSelectionModel().select(h.startsWith("f") ? 0 : h.startsWith("k") ? 2 : 1);
        for (final CheckBox c : List.of(skin, fat, muscles, bones)) {
            c.setOnAction(e -> draw());
        }
        parts();
        held.setOnAction(e -> again());
        mean.setOnAction(e -> make());
        tension.setOnAction(e -> make());
        scan.setOnAction(e -> make());
        about.setWrapText(true);
        final VBox side = new VBox(8, new Label("Shown"), skin, fat, muscles, bones, layered,
                new Label("How thick the fat is on average, mm"), mean, new Label("How taut the skin is"), tension,
                scan,
                new Label("Held"), held, new Label("Of the body"), part, attached, about, shell.problems());
        side.setPadding(new Insets(10));
        shell.bar(List.of());
        shell.side(new Label("The skin"), side, 320);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("skin", 200, 6, 2.6);
        shell.show(stage, 1400, 920);
        make();
    }

    /** Which part is shown, the whole body or one, with what it hangs from or not: as asked at first. */
    private void parts() {
        part.getItems().add("The whole body");
        for (final Part p : Part.values()) {
            part.getItems().add(Character.toUpperCase(p.said().charAt(0)) + p.said().substring(1));
        }
        final String one = System.getProperty("skin.part", "").replace('_', ' ');
        part.getSelectionModel().select(Math.max(0, part.getItems().stream().map(s -> s.toLowerCase(Locale.ROOT))
                .toList().indexOf(one.toLowerCase(Locale.ROOT))));
        part.setOnAction(e -> again());
        attached.setSelected(Boolean.getBoolean("skin.attached"));
        attached.setOnAction(e -> again());
    }

    /** The body, if not yet found, and the fat and the skin as asked, found away from the screen; then drawn. */
    private void make() {
        final double m = mean.getValue();
        final int k = tension.getValue();
        final boolean asLayer = layered.isSelected();
        final int base = scan.getSelectionModel().getSelectedIndex() - 1;
        final String id = key(m, k, asLayer);
        bus.publish(new Bus.Working(true, "skin"));
        worker.submit(() -> {
            try {
                if (body == null) {
                    body = com.example.fxsuite.life.body.fat.SheetBed.INSTANCE.body(proportions, true, 1);
                    near = new Nearness(FatBeneath.INSTANCE.of(body, bind));
                }
                final long t0 = System.nanoTime();
                if (asLayer) {
                    final FatTarget target = new FatTarget(near, new FatMap(m), bind);
                    final FatLayer.Fat f = fats.computeIfAbsent(m, x -> FatLayer.INSTANCE.of(target));
                    skins.computeIfAbsent(id, x -> SkinLayer.INSTANCE.of(f, target, SKIN, k));
                } else {
                    skins.computeIfAbsent(id, x -> SkinFit.INSTANCE.of(new FatTarget(near,
                            new FatMap(m, SKIN), bind), SKIN, k, base));
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

    /**
     * What is chosen drawn, of the whole body or of one part, with what it hangs from or not, held as asked, standing
     * on the ground; looked at afresh, if a part or how it is held was chosen since.
     */
    private void draw() {
        final FatLayer.Fat f = layered.isSelected() ? fats.get(mean.getValue()) : null;
        final Flesh s = skins.get(key(mean.getValue(), tension.getValue(), layered.isSelected()));
        if (body == null || s == null) {
            return;
        }
        final Rig rig = new Rig(proportions, pose(), false);
        final Part one = chosen();
        final Predicate<Part> keep = p -> one == null || p == one || attached.isSelected() && p == one.parent();
        final Flesh cut = one == null ? s : alone(s, keep);
        final List<Node> nodes = new ArrayList<>();
        if (muscles.isSelected()) {
            nodes.add(new MusclesView(one == null ? body : new MuscleBody.Bound(alone(body.flesh(), keep),
                    body.tendon(), body.parts(), body.main()), SIGNS, rig).node());
        }
        if (bones.isSelected()) {
            nodes.add(CageView.INSTANCE.bones(rig, 0, new Detail(0.6), keep));
        }
        if (fat.isSelected() && f != null) {
            nodes.add(new FleshView(one == null ? f.flesh() : alone(f.flesh(), keep), "#e3c27a", SIGNS, rig).node());
        }
        if (skin.isSelected()) {
            nodes.add(new FleshView(cut, "#d9a98c", SIGNS, rig).node());
        }
        final Group world = new Group(nodes);
        final double ground = rig.contacts().stream().mapToDouble(V3::y).min().orElse(0);
        world.getTransforms().add(new Translate(0, ground, 0));
        view.show(ModelView.Model.of(world));
        if (aim) {
            aim = false;
            look(one == null ? null : cut, rig, ground);
        }
        about.setText(f != null ? String.format(Locale.ROOT, "The fat a layer of its own: its cage %,d points,"
                + " smoothed to %,d; %,d found no fat outside.%nThe skin over it, %.1f mm, as many points.%nFound in"
                + " %,d ms.", f.cage().size(), f.flesh().size(), f.missed().size(), SKIN * 10, took)
                : String.format(Locale.ROOT, "The skin, %.1f mm, over the muscles and bones, the fat under it as how"
                + " thick it is: %,d points.%nFound in %,d ms.", SKIN * 10, s.size(), took));
    }

    /** What is chosen drawn, and looked at afresh. */
    private void again() {
        aim = true;
        draw();
    }

    /**
     * What of {@code f} is shown with the parts {@code keep} says, they alone shown: its pieces of its cage, if it has
     * them, else as {@link PartCut} cuts it.
     */
    private Flesh alone(final Flesh f, final Predicate<Part> keep) {
        if (f.inPatches()) {
            return f.patch(keep);
        }
        final Part[] owner = partCut.of(f);
        return f.only(i -> keep.test(owner[i]));
    }

    /** The part alone chosen to be shown, or none, the whole body. */
    private Part chosen() {
        final int i = part.getSelectionModel().getSelectedIndex();
        return i <= 0 ? null : Part.values()[i - 1];
    }

    /**
     * The view turned to look at the middle of {@code cut} as {@code rig} holds it, standing {@code ground} up; at
     * where it was asked to look at first, if none.
     */
    private void look(final Flesh cut, final Rig rig, final double ground) {
        if (cut == null) {
            view.lookAt(Double.parseDouble(System.getProperty("skin.across", "0")),
                    Double.parseDouble(System.getProperty("skin.at", "90")), 0);
            return;
        }
        final float[] at = new float[3 * cut.size()];
        cut.posed(rig, at, new float[at.length]);
        final int[] t = cut.triangles();
        double x = 0;
        double y = 0;
        double z = 0;
        for (final int i : t) {
            x += at[3 * i];
            y += at[3 * i + 1];
            z += at[3 * i + 2];
        }
        final int n = Math.max(t.length, 1);
        view.lookAt(SIGNS.x() * x / n, -(SIGNS.y() * y / n + ground), SIGNS.z() * z / n);
    }

    /**
     * Which skin: over how much fat, how taut, over the fat as a layer of its own or not; if not, fitted to as a scan
     * over what base, if it is.
     */
    private String key(final double m, final int k, final boolean asLayer) {
        return m + "/" + k + (asLayer ? "/layer" : "/" + scan.getSelectionModel().getSelectedIndex());
    }

    private Pose pose() {
        return switch (held.getSelectionModel().getSelectedIndex()) {
            case 0 -> BoundPose.INSTANCE.pose();
            case 2 -> March.INSTANCE.at(Move.PARADE_STEP, 0.82, 0, false);
            default -> Pose.REST;
        };
    }
}
