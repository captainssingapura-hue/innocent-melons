package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The wardrobe bench: people dressed, side by side, standing or walking on the spot over and over, to see their
 * clothes as they move: what is fitted moving with its part, what hangs clearing the legs. For now the temple's monks
 * ({@link Monk}). How loose their robes are, how high their hems and how far their skirts flare, on sliders; each
 * monk's colours on pickers. Slowed, or stopped at any point of the stride.
 *
 * <p>{@code -Dwardrobe.speed} (cm/s), {@code -Dwardrobe.at} (0 to 1, stopped there), {@code -Dwardrobe.yaw},
 * {@code -Dwardrobe.pitch}, {@code -Dwardrobe.distance} (m) what it shows first; {@code -Dbench.snapshot=out.png} saves
 * the window after {@code -Dbench.settle} ms and exits.
 */
public final class WardrobeBench extends Application {

    /** How far apart they stand, cm; how far the grid repeats. */
    private static final double APART = 75;
    private static final double REPEATS = 50;
    private static final double SOLE = 0.5;

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the wardrobe", bus);
    private final ModelView view = new ModelView();

    /** One dressed: who, drawn how, walking how, and what hangs off them. */
    private final class Dressed {
        Monk monk;
        Puppet puppet;
        final Walker walker;
        final Group shown = new Group();
        final Group hanging = new Group();
        final Translate lifted = new Translate();

        Dressed(final Monk monk, final double x) {
            this.monk = monk;
            walker = new Walker(monk.proportions());
            shown.getTransforms().addAll(new Translate(x, 0, 0), lifted);
            dress();
        }

        void dress() {
            puppet = new Puppet(monk::drawn, monk.proportions(), monk.palette(), new V3(-1, -1, -1));
            shown.getChildren().setAll(puppet.node(), hanging);
            draw();
        }

        void draw() {
            final Rig rig = walker.rig();
            puppet.show(rig);
            hanging.getChildren().setAll(puppet.drawn(monk.hanging(rig)));
            lifted.setY(-(walker.flying(speed.getValue()) + SOLE));
        }
    }

    private final List<Dressed> people = new ArrayList<>();
    private final Slider speed = new Slider(0, 250, 90);
    private final Slider slow = new Slider(0.05, 1, 1);
    private final Slider at = new Slider(0, 1, 0);
    private final Slider ease = new Slider(0.5, 5, 1.5);
    private final Slider hem = new Slider(3, 40, 11);
    private final Slider flare = new Slider(0, 1.5, 0.35);
    private final ChoiceBox<String> whose = new ChoiceBox<>();
    private final VBox colours = new VBox(4);
    private final Button play = new Button("Pause");
    private boolean playing = true;
    private boolean setting;
    private long last = -1;
    private double fps;
    private int frames;

    @Override
    public void start(final Stage stage) {
        final List<Monk> monks = Monk.TEMPLE;
        for (int k = 0; k < monks.size(); k++) {
            people.add(new Dressed(monks.get(k), (k - (monks.size() - 1) / 2.0) * APART));
        }
        speed.setValue(Double.parseDouble(System.getProperty("wardrobe.speed", "90")));
        for (final Slider s : List.of(speed, slow, at, ease, hem, flare)) {
            s.setPrefWidth(200);
        }
        ease.valueChangingProperty().addListener((o, was, is) -> {
            if (!is) {
                refit();
            }
        });
        ease.setOnMouseReleased(e -> refit());
        for (final Slider s : List.of(hem, flare)) {
            s.valueProperty().addListener((o, was, is) -> refit(false));
        }
        at.valueProperty().addListener((o, was, is) -> {
            if (!setting) {
                playing(false);
                still();
            }
        });
        play.setOnAction(e -> playing(!playing));
        final FlowPane paces = new FlowPane(6, 6);
        for (final Object[] p : new Object[][] {{"Stand", 0.0}, {"Stroll", 90.0}, {"Walk", 140.0}, {"Hurry", 220.0}}) {
            final Button b = new Button((String) p[0]);
            b.setOnAction(e -> speed.setValue((Double) p[1]));
            paces.getChildren().add(b);
        }
        final GridPane knobs = new GridPane();
        knobs.setHgap(8);
        knobs.setVgap(6);
        int row = 0;
        for (final Object[] k : new Object[][] {{"Speed, cm/s", speed}, {"Time, share of real", slow},
            {"Point of the stride", at}, {"Robe, how loose, cm", ease}, {"Hem over the ground, cm", hem},
            {"Skirt flaring", flare}}) {
            knobs.add(new Label((String) k[0]), 0, row);
            knobs.add((Slider) k[1], 1, row++);
        }
        whose.getItems().setAll(monks.stream().map(Monk::name).toList());
        whose.setValue(monks.getFirst().name());
        whose.valueProperty().addListener((o, was, is) -> showColours());
        final Label hint = new Label("Fitted cloth moves with its part; the skirt hangs level and clears the legs as"
                + " they swing. Drag to turn the view, right-drag to move it, scroll to come nearer.");
        hint.setWrapText(true);
        final VBox side = new VBox(10, new Label("How fast"), paces, knobs, new FlowPane(10, 6, play),
                new Label("Colours of"), whose, colours, hint);
        side.setPadding(new Insets(8));
        final ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The wardrobe"), scroll, 400);
        shell.middle(view.node());
        view.grid(0, 0, 0, 1_200, 10, 5);
        view.zoom(60, 2_500);
        view.lookAt(0, 95, 0);
        view.from("wardrobe", Double.parseDouble(System.getProperty("wardrobe.yaw", "25")),
                Double.parseDouble(System.getProperty("wardrobe.pitch", "6")),
                Double.parseDouble(System.getProperty("wardrobe.distance", "5")));
        final Group all = new Group();
        people.forEach(d -> all.getChildren().add(d.shown));
        view.show(ModelView.Model.moving(all));
        showColours();
        final String stopped = System.getProperty("wardrobe.at");
        if (stopped != null) {
            setting = true;
            at.setValue(Double.parseDouble(stopped));
            setting = false;
            playing(false);
            still();
        }
        new AnimationTimer() {
            @Override
            public void handle(final long now) {
                frame(now);
            }
        }.start();
        shell.show(stage, 1440, 920);
    }

    private void playing(final boolean on) {
        playing = on;
        play.setText(on ? "Pause" : "Play");
        last = -1;
    }

    /** Each robe made afresh as loose as asked; if {@code fitted}, what is fitted too, else what hangs only. */
    private void refit(final boolean fitted) {
        for (final Dressed d : people) {
            d.monk = d.monk.with(ease.getValue(), hem.getValue(), flare.getValue());
            if (fitted) {
                d.dress();
            } else {
                d.draw();
            }
        }
    }

    private void refit() {
        refit(true);
    }

    private void still() {
        for (final Dressed d : people) {
            d.walker.at(at.getValue(), speed.getValue(), false);
            d.draw();
        }
    }

    private void frame(final long now) {
        if (last >= 0 && playing) {
            final double real = Math.min(0.05, (now - last) / 1e9);
            fps = fps == 0 ? 1 / Math.max(real, 1e-3) : fps * 0.95 + 0.05 / Math.max(real, 1e-3);
            final double seconds = real * slow.getValue();
            for (final Dressed d : people) {
                d.walker.step(seconds, speed.getValue(), false);
                d.draw();
            }
            setting = true;
            at.setValue(people.get(1).walker.cycle());
            setting = false;
            view.groundAt(0, people.get(1).walker.ahead() % REPEATS);
        }
        last = now;
        if (++frames % 15 == 0) {
            bus.publish(new Bus.Figures(String.format(Locale.ROOT, "%s%n%.2f m/s%n%.0f frames a second",
                    String.join(", ", people.stream().map(d -> String.format(Locale.ROOT, "%s %.2f m",
                            d.monk.name().toLowerCase(Locale.ROOT), d.monk.proportions().stature() / 100)).toList()),
                    speed.getValue() / 100, fps)));
        }
    }

    /** A picker for each of the chosen monk's colours. */
    private void showColours() {
        colours.getChildren().clear();
        final Dressed d = people.stream().filter(p -> p.monk.name().equals(whose.getValue())).findFirst()
                .orElse(people.getFirst());
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        int row = 0;
        for (final Paint p : Paint.MONK) {
            final ColorPicker picker = new ColorPicker(Color.web(d.monk.palette().colour(p)));
            picker.setOnAction(e -> {
                final Color c = picker.getValue();
                d.monk = d.monk.with(d.monk.palette().with(p, String.format(Locale.ROOT, "#%02x%02x%02x",
                        Math.round(c.getRed() * 255), Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255))));
                d.dress();
            });
            grid.add(new Label(p.label()), 0, row);
            grid.add(picker, 1, row++);
        }
        colours.getChildren().add(grid);
    }
}
