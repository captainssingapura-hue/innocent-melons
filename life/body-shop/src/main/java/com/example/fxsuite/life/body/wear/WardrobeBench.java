package com.example.fxsuite.life.body.wear;

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
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.flesh.FleshView;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.muscle.MusclesView;
import com.example.fxsuite.life.body.skin.Skins;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The wardrobe: what is worn on the body as it is made now ({@link Tailor}), over its muscles on its bones, or over its
 * skin ({@link Skins}), fitted afresh to it: briefs, tights, either or both, the skin, the muscles and the bones shown
 * under them or not; held as it was fitted, its arms out from its sides, or standing at rest, or kicking out in the
 * parade step, what it wears moving with it. The body,
 * what is beneath its clothes and the clothes found once, away from the screen, and kept; its skin and the clothes
 * over it the first time the skin is asked for.
 *
 * <p>{@code -Dwardrobe.wear} (briefs, tights, by commas), {@code -Dwardrobe.hide} (muscles, bones),
 * {@code -Dwardrobe.skin} (its skin, what it wears over it),
 * {@code -Dwardrobe.held} (fitted, rest, kick); {@code -Dwardrobe.yaw}, {@code -Dwardrobe.pitch},
 * {@code -Dwardrobe.distance} where it is seen from.</p>
 */
public final class WardrobeBench extends Application {

    /** How it is held. */
    private enum Held {
        FITTED("As it was fitted"), REST("Standing at rest"), KICK("Kicking out, the parade step");

        private final String said;

        Held(final String said) {
            this.said = said;
        }

        @Override
        public String toString() {
            return said;
        }
    }

    /** The body and what it wears, found once. */
    private record Made(MuscleBody.Bound body, Garment briefs, Garment tights, long ms) {
    }

    /** The body's skin and what it wears fitted over it, found once. */
    private record Skinned(Flesh skin, Garment briefs, Garment tights, long ms) {
    }

    private static final V3 SIGNS = new V3(-1, -1, -1);
    /** The briefs' colour, the tights'. */
    private static final String WHITE = "#efece4";
    private static final String BLACK = "#2c2c33";
    private final Proportions proportions = Proportions.PAPERS;
    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final CheckBox briefs = new CheckBox("Briefs");
    private final CheckBox tights = new CheckBox("Tights");
    private final CheckBox muscles = new CheckBox("The muscles");
    private final CheckBox bones = new CheckBox("The bones");
    private final CheckBox skin = new CheckBox("The skin, what it wears fitted over it");
    private final Skins skins = new Skins();
    private Skinned skinned;
    private final ChoiceBox<Held> held = new ChoiceBox<>();
    private final Label about = new Label("Finding the body and what it wears");
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "wardrobe");
        t.setDaemon(true);
        return t;
    });
    private Made made;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Wardrobe", bus);
        final String wear = System.getProperty("wardrobe.wear", "briefs,tights").toLowerCase(Locale.ROOT);
        final String hide = System.getProperty("wardrobe.hide", "").toLowerCase(Locale.ROOT);
        briefs.setSelected(wear.contains("briefs"));
        tights.setSelected(wear.contains("tights"));
        muscles.setSelected(!hide.contains("muscles"));
        bones.setSelected(!hide.contains("bones"));
        skin.setSelected(Boolean.getBoolean("wardrobe.skin"));
        skin.setOnAction(e -> overSkin());
        held.getItems().setAll(Held.values());
        final String h = System.getProperty("wardrobe.held", "rest").toLowerCase(Locale.ROOT);
        held.setValue(h.startsWith("f") ? Held.FITTED : h.startsWith("k") ? Held.KICK : Held.REST);
        for (final CheckBox c : List.of(briefs, tights, muscles, bones)) {
            c.setOnAction(e -> draw());
        }
        held.setOnAction(e -> draw());
        about.setWrapText(true);
        final VBox side = new VBox(8, new Label("Worn"), briefs, tights, new Label("Under them"), skin, muscles, bones,
                new Label("Held"), held, about, shell.problems());
        side.setPadding(new Insets(10));
        shell.bar(List.of());
        shell.side(new Label("The wardrobe"), side, 320);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("wardrobe", 200, 6, 2.6);
        view.lookAt(0, 90, 0);
        shell.show(stage, 1400, 920);
        bus.publish(new Bus.Working(true, "wardrobe"));
        worker.submit(this::make);
        overSkin();
    }

    /** The body, what is beneath its clothes and its clothes, found away from the screen; then drawn. */
    private void make() {
        final long t0 = System.nanoTime();
        final Rig bind = new Rig(proportions, BoundPose.INSTANCE.pose(), false);
        final MuscleBody.Bound body = MuscleBody.INSTANCE.make(proportions, false, 1);
        final Beneath under = Underneath.INSTANCE.of(body, bind, new Detail(0.6));
        final Garment b = Tailor.INSTANCE.briefs(under, bind, WHITE);
        final Garment t = Tailor.INSTANCE.tights(under, bind, BLACK);
        final long ms = (System.nanoTime() - t0) / 1_000_000;
        Platform.runLater(() -> {
            made = new Made(body, b, t, ms);
            bus.publish(new Bus.Working(false, "wardrobe"));
            draw();
        });
    }

    /** Its skin and what it wears fitted over it, if the skin is asked for and not yet found: found away; drawn. */
    private void overSkin() {
        if (!skin.isSelected() || skinned != null) {
            draw();
            return;
        }
        bus.publish(new Bus.Working(true, "skin"));
        worker.submit(() -> {
            final long t0 = System.nanoTime();
            final Rig bind = new Rig(proportions, BoundPose.INSTANCE.pose(), false);
            final Flesh s = skins.of(proportions);
            final Beneath over = skins.beneath(s);
            final Garment b = Tailor.INSTANCE.briefs(over, bind, WHITE);
            final Garment t = Tailor.INSTANCE.tights(over, bind, BLACK);
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            Platform.runLater(() -> {
                skinned = new Skinned(s, b, t, ms);
                bus.publish(new Bus.Working(false, "skin"));
                draw();
            });
        });
    }

    /**
     * What is chosen drawn, held as asked, standing on the ground: what it wears fitted over its skin, the skin with
     * it, if the skin is asked for and found; else over its muscles.
     */
    private void draw() {
        if (made == null) {
            return;
        }
        final Rig rig = new Rig(proportions, pose(held.getValue()), false);
        final boolean onSkin = skin.isSelected() && skinned != null;
        final Garment b = onSkin ? skinned.briefs() : made.briefs();
        final Garment t = onSkin ? skinned.tights() : made.tights();
        final List<Node> nodes = new ArrayList<>();
        if (muscles.isSelected()) {
            nodes.add(new MusclesView(made.body(), SIGNS, rig).node());
        }
        if (bones.isSelected()) {
            nodes.add(CageView.INSTANCE.bones(rig, 0, new Detail(0.6), p -> true));
        }
        if (onSkin) {
            nodes.add(new FleshView(skinned.skin(), Skins.COLOUR, SIGNS, rig).node());
        }
        if (tights.isSelected()) {
            nodes.add(new FleshView(t.cloth(), t.colour(), SIGNS, rig).node());
        }
        if (briefs.isSelected()) {
            nodes.add(new FleshView(b.cloth(), b.colour(), SIGNS, rig).node());
        }
        final Group world = new Group(nodes);
        final double ground = rig.contacts().stream().mapToDouble(V3::y).min().orElse(0);
        world.getTransforms().add(new Translate(0, ground, 0));
        view.show(ModelView.Model.of(world));
        about.setText(String.format(Locale.ROOT, "Briefs: %,d points. Tights: %,d points%s.%nThe body and what it"
                        + " wears found in %,d ms%s.", b.cloth().size(), t.cloth().size(), onSkin ? ", over the skin"
                        : "", made.ms(), skinned == null ? "" : String.format(Locale.ROOT, "; its skin and what it"
                        + " wears over it in %,d ms", skinned.ms())));
    }

    private Pose pose(final Held h) {
        return switch (h) {
            case FITTED -> BoundPose.INSTANCE.pose();
            case REST -> Pose.REST;
            case KICK -> March.INSTANCE.at(Move.PARADE_STEP, 0.82, 0, false);
        };
    }
}
