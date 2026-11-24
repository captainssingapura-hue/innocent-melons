package melons.innocent.plants.bench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * The bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its left,
 * the light: shadows, the sky by hand or by the almanac and the clock. In its middle: what stands, one tree, a patch
 * or an area of forest; of which kinds; at which stage, or whatever its distance calls for; for a patch, how many
 * trees stand along each side; for an area, its outline and how far across it is; whether what is on show is drawn
 * each tree on its own, as copies, or all as one mesh; whether the same stands beside it tree by tree; three ways of
 * looking at it, from above, half way up and low; and, said back from the card, how far off it is, at what stage
 * each is drawn and how dark each half of the picture comes out.
 *
 * <p>It draws nothing itself: it says what is on show as a message on the bench's bus ({@link OnShow}), and a way
 * of looking as another ({@link Angle}), and never calls what draws.
 */
public final class Console {

    private static final double WIDE = 660;
    private static final double TALL = 900;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    private static final String BY_DISTANCE = "Whatever its distance calls for";
    /** The fewest and the most trees along a patch's side the window offers. */
    private static final int FEWEST = 2;
    private static final int MOST = 8;

    private final Bus bus;
    private final Asked asked;
    private final OnShow first;
    private final LightBoard light;
    /** How far across an area the window offers, in metres. */
    private final List<Integer> widths = List.of(25, 50, 100, 150, 200, 300, 400, 500);

    /** A console on {@code bus}, its light asking {@code asked}, with {@code first} on show to begin with. */
    public Console(final Bus bus, final Asked asked, final OnShow first) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final List<String> stages = Arrays.stream(melons.innocent.plants.tree.Stage.values()).map(this::titled)
                .toList();
        final ComboBox<String> subject = choice(titles(Subject.values()), first.subject().ordinal());
        final ComboBox<String> kinds = choice(titles(Kinds.values()), first.kinds().ordinal());
        final ComboBox<String> stage = choice(IntStream.rangeClosed(0, stages.size())
                .mapToObj(k -> k == 0 ? BY_DISTANCE : stages.get(k - 1)).toList(),
                first.stage().map(held -> held.ordinal() + 1).orElse(0));
        final ComboBox<String> rows = choice(IntStream.rangeClosed(FEWEST, MOST).mapToObj(String::valueOf).toList(),
                first.rows() - FEWEST);
        final ComboBox<String> outline = choice(titles(Outline.values()), first.outline().ordinal());
        final ComboBox<String> across = choice(widths.stream().map(metres -> metres + " m").toList(),
                Math.max(0, widths.indexOf(first.across())));
        final ComboBox<String> joined = choice(titles(Joined.values()), first.joined().ordinal());
        final CheckBox logged = new CheckBox("Log a frame's time and the memory held, a line each time");
        logged.setSelected(asked.logged());
        logged.setOnAction(set -> asked.logged(logged.isSelected()));
        final CheckBox beside = new CheckBox("The same beside it, on its left, tree by tree");
        beside.setSelected(first.beside());
        final Runnable changed = () -> bus.publish(new OnShow(Subject.values()[at(subject)], Kinds.values()[at(kinds)],
                at(stage) == 0 ? java.util.Optional.empty()
                        : java.util.Optional.of(melons.innocent.plants.tree.Stage.values()[at(stage) - 1]),
                beside.isSelected(), at(rows) + FEWEST, Joined.values()[at(joined)], Outline.values()[at(outline)],
                widths.get(at(across))));
        List.of(subject, kinds, stage, rows, outline, across, joined)
                .forEach(choice -> choice.setOnAction(set -> changed.run()));
        beside.setOnAction(set -> changed.run());
        final Shell shell = new Shell("The plants bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("What stands"), subject, new Label("Of which kinds"), kinds,
                new Label("At which stage"), stage, new Label("A patch: trees along each side"), rows,
                new Label("An area: its outline, and how far across"), new HBox(GAP, outline, across),
                new Label("Drawn"), joined, beside, new Label("Looked at"), angles(), logged, shell.working(),
                shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(middle).show(window, WIDE, TALL);
        bus.publish(first);
    }

    /** A choice among {@code titles}, the one numbered {@code chosen} chosen to begin with. */
    private ComboBox<String> choice(final List<String> titles, final int chosen) {
        final ComboBox<String> choice = new ComboBox<>();
        choice.getItems().addAll(titles);
        choice.getSelectionModel().select(chosen);
        return choice;
    }

    /** Which of its titles {@code choice} has chosen, by number. */
    private int at(final ComboBox<String> choice) {
        return choice.getSelectionModel().getSelectedIndex();
    }

    /** What each of {@code things} is called in the window, in their order. */
    private List<String> titles(final Object[] things) {
        return Arrays.stream(things).map(thing -> switch (thing) {
            case Subject subject -> subject.title();
            case Kinds kinds -> kinds.title();
            case Outline outline -> outline.title();
            case Joined joined -> joined.title();
            default -> String.valueOf(thing);
        }).toList();
    }

    /** A button for each way of looking, each saying its way on the bus when pressed. */
    private HBox angles() {
        final HBox angles = new HBox(GAP);
        for (final Angle angle : Angle.values()) {
            final Button button = new Button(angle.title());
            button.setOnAction(pressed -> bus.publish(angle));
            angles.getChildren().add(button);
        }
        return angles;
    }

    /** A stage's name, as the window says it. */
    private String titled(final melons.innocent.plants.tree.Stage stage) {
        final String name = stage.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
