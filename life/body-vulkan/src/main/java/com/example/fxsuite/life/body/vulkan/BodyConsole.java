package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.BodyGroup;
import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import com.example.fxsuite.vulkan.space.Vec3;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The body bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is: on its
 * left, the light; in its middle, how the body moves (how it goes, how fast, going or stopped, where in its stride,
 * how slowly), how deep each group of its parts is drawn (its skin, its muscles on its bones, its bones, or not at
 * all), the skin's colour, what it wears ({@link WardrobePanel}), and what the card says it draws. It draws nothing
 * itself: what is on show and how the body moves go out on the bench's bus ({@link BodyShown}, {@link Motion});
 * where the going body has got to in its stride comes back on it ({@link Strided}).
 */
public final class BodyConsole {

    private static final double WIDE = 680;
    private static final double TALL = 860;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    private static final double FASTEST = 800;
    private static final double SLOWEST = 0.05;

    private final Bus bus;
    private final Asked asked;
    private final BodyShown first;
    private final Motion firstMotion;
    private final LightBoard light;
    // JavaFX's own, made once JavaFX has begun, when the window is shown
    private ChoiceBox<Move> how;
    private Slider speed;
    private Slider at;
    private Slider slow;
    private Button play;
    private Label said;
    private boolean playing;
    /** Whether the stride's slider is being moved by the card's thread, not by hand. */
    private boolean setting;

    /** Where in its stride the going body has got to, said by the card's thread. */
    public record Strided(double cycle) {
    }

    /**
     * A console on {@code bus}, its light asking {@code asked}, with {@code first} on show and the body moving as
     * {@code motion} says to begin with.
     */
    public BodyConsole(final Bus bus, final Asked asked, final BodyShown first, final Motion motion) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.firstMotion = motion;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        how = new ChoiceBox<>();
        speed = new Slider(0, FASTEST, 0);
        at = new Slider(0, 1, 0);
        slow = new Slider(SLOWEST, 1, 1);
        play = new Button();
        said = new Label();
        final Shell shell = new Shell("The body bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("How it moves"), motion(),
                new Label("How deep each part is drawn"), layers(), shell.working(), shell.figures(),
                shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(stage, WIDE, TALL);
        bus.subscribe(Strided.class, s -> {
            setting = true;
            at.setValue(s.cycle());
            setting = false;
        }, Bus.SCREEN);
        bus.publish(first);
        moved();
    }

    /** How it moves: how it goes, how fast, going or stopped, where in its stride, how slowly. */
    private Node motion() {
        how.getItems().addAll(Move.values());
        how.setValue(firstMotion.move());
        speed.setValue(firstMotion.speed());
        at.setValue(firstMotion.at());
        slow.setValue(firstMotion.slow());
        playing = firstMotion.playing();
        play.setOnAction(e -> {
            playing = !playing;
            moved();
        });
        how.setOnAction(e -> moved());
        for (final Slider s : new Slider[] {speed, at, slow}) {
            s.setPrefWidth(260);
            s.valueProperty().addListener((o, was, is) -> {
                if (!setting) {
                    moved();
                }
            });
            s.valueChangingProperty().addListener((o, was, is) -> moved());
        }
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP / 2);
        grid.addRow(0, new Label("How it goes"), how);
        grid.addRow(1, new Label("How fast"), speed);
        grid.addRow(2, new Label("Where in its stride"), at);
        grid.addRow(3, new Label("Time, as fast as"), slow);
        said.setWrapText(true);
        return new VBox(GAP / 2, grid, play, said);
    }

    /** How deep each group of its parts is drawn, and the skin's colour. */
    private Node layers() {
        final Map<BodyGroup, ChoiceBox<Layer>> boxes = new EnumMap<>(BodyGroup.class);
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP / 2);
        int row = 0;
        for (final BodyGroup g : BodyGroup.values()) {
            final ChoiceBox<Layer> box = new ChoiceBox<>();
            box.getItems().addAll(Layer.values());
            box.setValue(first.of(g));
            boxes.put(g, box);
            grid.addRow(row++, new Label(g.label()), box);
        }
        final ColorPicker skin = picker(first.skin());
        final WardrobePanel wardrobe = new WardrobePanel(first.worn());
        final Runnable changed = () -> {
            final Map<BodyGroup, Layer> layers = new EnumMap<>(BodyGroup.class);
            boxes.forEach((g, box) -> layers.put(g, box.getValue()));
            bus.publish(new BodyShown(layers, said(skin), wardrobe.shown()));
        };
        boxes.values().forEach(box -> box.valueProperty().addListener((o, was, is) -> changed.run()));
        skin.setOnAction(set -> changed.run());
        wardrobe.listen(changed);
        return new VBox(GAP / 2, grid, new Label("The skin's colour"), skin, wardrobe.node());
    }

    /** A colour picker showing {@code c}. */
    private ColorPicker picker(final Vec3 c) {
        return new ColorPicker(Color.color(c.x(), c.y(), c.z()));
    }

    /** The colour {@code p} shows, as much red, green and blue as it has. */
    private Vec3 said(final ColorPicker p) {
        final Color c = p.getValue();
        return new Vec3(c.getRed(), c.getGreen(), c.getBlue());
    }

    /** How it moves now, said on the bus. */
    private void moved() {
        play.setText(playing ? "Stop" : "Go");
        final boolean settled = !speed.isValueChanging() && !at.isValueChanging() && !slow.isValueChanging();
        final Move m = how.getValue();
        said.setText(String.format(Locale.ROOT, "%s. %s", m.marching() ? m.label()
                        : String.format(Locale.ROOT, "%.2f m/s", speed.getValue() / 100),
                playing || !settled ? "Its bones only, while it goes or a slider is held; stopped, its skin and"
                        + " muscles are posed and shown again."
                        : "Stopped: its skin and muscles posed as it stands."));
        bus.publish(new Motion(m, speed.getValue(), playing, at.getValue(), slow.getValue(), settled));
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
