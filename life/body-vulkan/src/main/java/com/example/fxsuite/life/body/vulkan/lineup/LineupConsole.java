package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.vulkan.Motion;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;

/**
 * The avatar bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is: on its
 * left, the light; in its middle, the line-up, each of its figures to be picked, what kind of figure it is and what
 * that means, how tall the picked one stands and what each of its pieces is drawn in ({@link PaintPanel}); how they
 * move, all alike (how they go, how fast, going or stopped); and what the card says it draws. It draws nothing
 * itself: what is on show and how they move go out on the bench's
 * bus ({@link LineupShown}, {@link Motion}).
 */
public final class LineupConsole {

    private static final double WIDE = 640;
    private static final double TALL = 900;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    private static final double FASTEST = 800;
    private static final double SLIDER = 260;

    private final Bus bus;
    private final Asked asked;
    private final List<Leaf<?>> kinds;
    private final AvatarMaker maker;
    private final LightBoard light;
    private LineupShown shown;
    private final Motion firstMotion;
    // JavaFX's own, made once JavaFX has begun, when the window is shown
    private Slider tall;
    private Label meaning;
    private PaintPanel painting;
    private ChoiceBox<Move> how;
    private Slider speed;
    private Button play;
    private boolean playing;

    /**
     * A console on {@code bus}, its light asking {@code asked}, for a line-up of the figures {@code maker} makes, with
     * {@code first} on show and the figures moving as {@code motion} says to begin with.
     */
    public LineupConsole(final Bus bus, final Asked asked, final AvatarMaker maker, final LineupShown first,
                         final Motion motion) {
        this.bus = bus;
        this.asked = asked;
        this.maker = maker;
        this.kinds = maker.batch();
        this.shown = first;
        this.firstMotion = motion;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Shell shell = new Shell("The avatar bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The line-up"), lineup(), new Label("How they move"), motion(),
                shell.working(), shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(stage, WIDE, TALL);
        bus.publish(shown);
        moved();
    }

    /** Its figures, one picked at a time; what the picked one is; how tall it stands; what it is drawn in. */
    private Node lineup() {
        final ToggleGroup group = new ToggleGroup();
        final VBox picks = new VBox(GAP / 2);
        for (int i = 0; i < kinds.size(); i++) {
            final int which = i;
            final RadioButton pick = new RadioButton(said(kinds.get(i)));
            pick.setToggleGroup(group);
            pick.setSelected(i == shown.picked());
            pick.setOnAction(e -> picked(which));
            picks.getChildren().add(pick);
        }
        tall = new Slider(LineupShown.SHORTEST, LineupShown.TALLEST, shown.tall().get(shown.picked()));
        tall.setPrefWidth(SLIDER);
        tall.setShowTickLabels(true);
        tall.setMajorTickUnit(20);
        final Label cm = new Label();
        tall.valueProperty().addListener((o, was, is) -> {
            cm.setText(String.format(Locale.ROOT, "%.0f cm", is.doubleValue()));
            shown = shown.standing(is.doubleValue());
            bus.publish(shown);
        });
        cm.setText(String.format(Locale.ROOT, "%.0f cm", tall.getValue()));
        meaning = new Label(Figures.TREE.meaning(kinds.get(shown.picked())));
        meaning.setWrapText(true);
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.addRow(0, new Label("How tall it stands"), tall, cm);
        painting = new PaintPanel();
        painting.shown(maker.roles(kinds.get(shown.picked())), shown.paint().get(shown.picked()));
        painting.listen(work -> {
            shown = shown.painting(work);
            bus.publish(shown);
        });
        return new VBox(GAP / 2, picks, meaning, grid, new Label("What it is drawn in"), painting.node());
    }

    /** Figure {@code i} picked: its meaning shown, the slider set to how tall it stands, its paintwork shown. */
    private void picked(final int i) {
        shown = shown.picking(i);
        meaning.setText(Figures.TREE.meaning(kinds.get(i)));
        painting.shown(maker.roles(kinds.get(i)), shown.paint().get(i));
        tall.setValue(shown.tall().get(i));
        bus.publish(shown);
    }

    /** How they move: how they go, how fast, going or stopped. */
    private Node motion() {
        how = new ChoiceBox<>();
        how.getItems().addAll(Move.values());
        how.setValue(firstMotion.move());
        how.setOnAction(e -> moved());
        speed = new Slider(0, FASTEST, firstMotion.speed());
        speed.setPrefWidth(SLIDER);
        speed.valueProperty().addListener((o, was, is) -> moved());
        playing = firstMotion.playing();
        play = new Button();
        play.setOnAction(e -> {
            playing = !playing;
            moved();
        });
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP / 2);
        grid.addRow(0, new Label("How they go"), how);
        grid.addRow(1, new Label("How fast"), speed);
        return new VBox(GAP / 2, grid, play);
    }

    /** How they move now, said on the bus. */
    private void moved() {
        play.setText(playing ? "Stop" : "Go");
        bus.publish(new Motion(how.getValue(), speed.getValue(), playing, 0, 1, true));
    }

    /** What {@code kind} is called, in words: {@code FramedRobot} is "Framed robot". */
    private String said(final Leaf<?> kind) {
        final String words = kind.name().replace('-', ' ');
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
