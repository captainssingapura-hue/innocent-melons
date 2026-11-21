package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Grip;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Fleshing;
import com.example.fxsuite.life.body.flesh.Physique;
import com.example.fxsuite.life.body.flesh.SkinTone;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.PickResult;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The cage bench: a body's flesh as one cage ({@link BodyCage}), on its own, still, held as it is bound; the skin
 * smoothed from it as many steps as asked, its muscles as defined as asked; the cage's edges and points shown over it,
 * its creases coloured, one chosen from the list brighter; a point pressed on told by its name and where it is; the
 * bones under it all, if asked. Changed, it is made afresh away from the screen and shown in place of the one before.
 *
 * <p>{@code -Dcage.who} (man, woman), {@code -Dcage.steps} (0 to 4), {@code -Dcage.definition} (0 to 1),
 * {@code -Dcage.show} (any of skin, cage, bones, drawn), {@code -Dcage.yaw}, {@code -Dcage.pitch} and
 * {@code -Dcage.distance} what it shows first.</p>
 */
public final class CageBench extends Application {

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final ChoiceBox<Physique> who = new ChoiceBox<>();
    private final Slider steps = new Slider(0, 4, Integer.getInteger("cage.steps", 3));
    private final Slider defined = new Slider(0, 1, 0.8);
    private final CheckBox skin = new CheckBox("The skin");
    private final CheckBox cage = new CheckBox("The cage, its edges and points");
    private final CheckBox bones = new CheckBox("The bones under it");
    private final CheckBox ends = new CheckBox("The head, hands and feet, as bones");
    private final CheckBox drawn = new CheckBox("The cage as drawn, not as fitted");
    private final ListView<String> creases = new ListView<>();
    private final Label about = new Label();
    private final Label picked = new Label("Press on a point of the cage to tell it.");
    private final Map<String, Group> skeletons = new java.util.concurrent.ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "cage");
        t.setDaemon(true);
        return t;
    });
    private Made made;
    private int point = -1;
    private int asked;

    /** What was made: the cage as drawn and as fitted, the skin smoothed from it, how long it took. */
    private record Made(Physique who, Cage drawn, Cage fitted, Mesher.Mesh skin, int steps, long ms) {
    }

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Cage bench", bus);
        final ScrollPane side = new ScrollPane(controls(shell));
        side.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("A body's cage"), side, 360);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.lookAt(0, 105, 0);
        view.from("cage", 200, 6, 3.2);
        view.grip(new Grip() {
            @Override
            public boolean take(final PickResult p, final Ray ray, final boolean control) {
                if (p.getIntersectedNode() != null && p.getIntersectedNode().getUserData() instanceof Integer i) {
                    point = i;
                    show();
                }
                return false;
            }

            @Override
            public void move(final Ray ray, final boolean control) {
            }

            @Override
            public void letGo() {
            }
        });
        make();
        shell.show(stage, 1360, 920);
    }

    /** The bench's side: whose, how smooth, how defined, what is shown, which crease. */
    private Node controls(final Shell shell) {
        who.getItems().setAll(Physique.values());
        who.setValue(System.getProperty("cage.who", "man").toLowerCase(Locale.ROOT).startsWith("w") ? Physique.WOMAN
                : Physique.MAN);
        defined.setValue(Double.parseDouble(System.getProperty("cage.definition",
                String.valueOf(who.getValue().torso().definition()))));
        who.setOnAction(e -> {
            defined.setValue(who.getValue().torso().definition());
            make();
        });
        for (final Slider s : List.of(steps, defined)) {
            s.setShowTickLabels(true);
            s.valueChangingProperty().addListener((o, was, is) -> {
                if (!is) {
                    make();
                }
            });
        }
        steps.setMajorTickUnit(1);
        steps.setMinorTickCount(0);
        steps.setSnapToTicks(true);
        defined.setMajorTickUnit(0.25);
        final String shown = System.getProperty("cage.show", "skin,cage").toLowerCase(Locale.ROOT);
        skin.setSelected(shown.contains("skin"));
        cage.setSelected(shown.contains("cage"));
        bones.setSelected(shown.contains("bones"));
        drawn.setSelected(shown.contains("drawn"));
        ends.setSelected(!shown.contains("bare"));
        for (final CheckBox c : List.of(skin, cage, bones, drawn, ends)) {
            c.setOnAction(e -> show());
        }
        creases.setPrefHeight(260);
        creases.getSelectionModel().selectedIndexProperty().addListener((o, was, is) -> show());
        about.setWrapText(true);
        picked.setWrapText(true);
        final VBox box = new VBox(8, new Label("Whose"), who, new Label("Smoothed, steps"), steps,
                new Label("How defined the muscles are"), defined, skin, cage, drawn, bones, ends,
                new Label("Its creases (choose one to see it)"), creases, picked, about, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    /** The cage made afresh, away from the screen; shown when made, unless another has been asked for since. */
    private void make() {
        final int mine = ++asked;
        final Physique p = who.getValue();
        final double d = defined.getValue();
        final int n = (int) Math.round(steps.getValue());
        final String bonesKey = skeleton(p);
        bus.publish(new Bus.Working(true, "cage"));
        worker.submit(() -> {
            final long t0 = System.nanoTime();
            final Cage plain = BodyCage.INSTANCE.drawn(p, d);
            final Cage fitted = Fitting.INSTANCE.fitted(plain, d);
            final Subdivision s = Subdivision.INSTANCE;
            final Mesher.Mesh mesh = s.mesh(s.times(s.of(fitted, d), n));
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            if (bonesKey != null && !skeletons.containsKey(bonesKey)) {
                skeletons.put(bonesKey, CageView.INSTANCE.bones(bound(p), p.female(), new Detail(0.4),
                        bonesKey.endsWith("all") ? q -> true : this::end));
            }
            Platform.runLater(() -> {
                if (mine == asked) {
                    if (made == null || made.who() != p) {
                        point = -1;
                    }
                    made = new Made(p, plain, fitted, mesh, n, ms);
                    creases.getItems().setAll(plain.creases().stream().map(c -> c.said() + String.format(Locale.ROOT,
                            ", %.2f", c.sharpness(d))).toList());
                    show();
                }
                bus.publish(new Bus.Working(false, "cage"));
            });
        });
    }

    /** What is made shown as the boxes say. */
    private void show() {
        if (made == null) {
            return;
        }
        final String bonesKey = skeleton(made.who());
        if (bonesKey != null && !skeletons.containsKey(bonesKey)) {
            make();
            return;
        }
        final Cage c = drawn.isSelected() ? made.drawn() : made.fitted();
        final List<Node> nodes = new ArrayList<>();
        if (skin.isSelected()) {
            final Node s = CageView.INSTANCE.skin(made.skin(), new SkinTone(0.25).colour());
            s.setMouseTransparent(true);
            nodes.add(s);
        }
        if (bonesKey != null) {
            nodes.add(skeletons.get(bonesKey));
        }
        if (cage.isSelected()) {
            nodes.addAll(cageNodes(c));
        }
        final Group world = new Group(nodes);
        final V3 heel = bound(made.who()).frame(Part.RIGHT_FOOT).place(new V3(0, -6.4, -4.0));
        world.getTransforms().add(new Translate(0, heel.y(), 0));
        view.show(ModelView.Model.touchable(world));
        tell(c);
    }

    /** The cage's edges, its creases, the one chosen, and its points. */
    private List<Node> cageNodes(final Cage c) {
        final Subdivision s = Subdivision.INSTANCE;
        final Set<Long> creased = new java.util.HashSet<>();
        final List<int[]> chosen = new ArrayList<>();
        final int pick = creases.getSelectionModel().getSelectedIndex();
        for (int k = 0; k < c.creases().size(); k++) {
            final List<Integer> path = c.creases().get(k).path();
            for (int i = 0; i + 1 < path.size(); i++) {
                creased.add(s.key(path.get(i), path.get(i + 1)));
                if (k == pick) {
                    chosen.add(new int[] {path.get(i), path.get(i + 1)});
                }
            }
        }
        final List<int[]> plain = new ArrayList<>();
        final List<int[]> sharp = new ArrayList<>();
        for (final int[] e : c.edges()) {
            (creased.contains(s.key(e[0], e[1])) ? sharp : plain).add(e);
        }
        final List<Node> nodes = new ArrayList<>(List.of(CageView.INSTANCE.edges(c.points(), plain, "#3d4250", 1),
                CageView.INSTANCE.edges(c.points(), sharp, "#e08a1e", 1.6),
                CageView.INSTANCE.edges(c.points(), chosen, "#e0322a", 2.6)));
        nodes.forEach(n -> n.setMouseTransparent(true));
        nodes.add(CageView.INSTANCE.points(c.points(), point >= 0 ? Set.of(point) : Set.of()));
        return nodes;
    }

    /** What is shown, told: the cage's size, the skin's, the point pressed on. */
    private void tell(final Cage c) {
        about.setText(String.format(Locale.ROOT, "%s. The cage: %,d points, %,d edges, %,d faces, %d creases."
                        + "%nThe skin: smoothed %d steps, %,d triangles, made in %,d ms.", made.who().said(),
                c.size(), c.edges().size(), c.faces().size(), c.creases().size(), made.steps(),
                made.skin().triangleCount(), made.ms()));
        if (point >= 0 && point < c.size()) {
            final V3 at = made.drawn().points().get(point);
            final V3 f = made.fitted().points().get(point);
            picked.setText(String.format(Locale.ROOT, "Point %d: %s.%nDrawn at (%.1f, %.1f, %.1f) cm: out, up,"
                            + " ahead; fitted %.1f cm off it.", point, c.name(point), at.x(), at.y(), at.z(),
                    f.minus(at).length()));
        }
    }

    /** Which bones are to be shown, as a key to keep them by: all, the ends', or none. */
    private String skeleton(final Physique p) {
        return bones.isSelected() ? p + " all" : ends.isSelected() ? p + " ends" : null;
    }

    /** Whether {@code p} is one of the parts the cage does not cover: the head, the hands, the feet. */
    private boolean end(final Part p) {
        return switch (p) {
            case HEAD, RIGHT_HAND, LEFT_HAND, RIGHT_FOOT, LEFT_FOOT, RIGHT_TOES, LEFT_TOES -> true;
            default -> false;
        };
    }

    private Rig bound(final Physique p) {
        return new Rig(p.proportions(), Fleshing.INSTANCE.bind(), false);
    }
}
