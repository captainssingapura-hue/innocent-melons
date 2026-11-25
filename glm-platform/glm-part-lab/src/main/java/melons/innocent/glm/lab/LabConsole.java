package melons.innocent.glm.lab;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import melons.innocent.glm.bench.MadeOfChoice;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The lab's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its left, the
 * light. In its middle: which part is looked at, among all the platform's, by group; alone or put together, and the
 * rest as glass, finished or hidden; whether the grid, the ground and the part's points are shown, and how finely the
 * grid is ruled; what the parts are made of, another metal for each they are designed in; and the part's points,
 * each by name, where it is in the part's own frame and, put together, on the platform, in centimetres. It draws
 * nothing itself: it says what is shown as a message on the lab's bus ({@link LabShow}), and never calls what draws.
 */
public final class LabConsole {

    private static final double WIDE = 760;
    private static final double TALL = 780;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;

    private final Bus bus;
    private final Asked asked;
    private final Assembled built;
    private final List<PartAt> parts;
    private final LightBoard light;

    /** A console on {@code bus}, its light asking {@code asked}, over {@code built}'s parts. */
    public LabConsole(final Bus bus, final Asked asked, final Assembled built) {
        this.bus = bus;
        this.asked = asked;
        this.built = built;
        this.light = new LightBoard(asked);
        this.parts = built.placed().keySet().stream().sorted(Comparator.comparing((PartAt p) -> p.part().group())
                .thenComparing(p -> p.part().name()).thenComparing(PartAt::side)).toList();
    }

    /** The parts it offers, in the order it lists them: by group, then by name, then by side. */
    public List<PartAt> parts() {
        return parts;
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y, final LabShow first) {
        final ComboBox<String> part = choice(parts.stream().map(p -> p.part().group().title() + ": " + p.said())
                .toList(), parts.indexOf(first.part()));
        final ComboBox<String> level = choice(Arrays.stream(LabShow.Level.values()).map(LabShow.Level::title)
                .toList(), first.level().ordinal());
        final ComboBox<String> others = choice(Arrays.stream(LabShow.Others.values()).map(LabShow.Others::title)
                .toList(), first.others().ordinal());
        final ComboBox<String> spacing = choice(Arrays.stream(Spacing.values()).map(Spacing::title).toList(),
                first.spacing().ordinal());
        final CheckBox grid = check("The grid", first.grid());
        final CheckBox ground = check("The ground", first.ground());
        final CheckBox points = check("Its points marked", first.points());
        final MadeOfChoice made = new MadeOfChoice(built.placed().keySet().stream()
                .map(p -> p.part().made().material()).collect(Collectors.toSet()), first.madeOf());
        final TextArea said = new TextArea();
        said.setEditable(false);
        said.setPrefRowCount(14);
        final Runnable changed = () -> {
            final LabShow show = new LabShow(parts.get(at(part)), LabShow.Level.values()[at(level)],
                    LabShow.Others.values()[at(others)], grid.isSelected(), ground.isSelected(), points.isSelected(),
                    Spacing.values()[at(spacing)], made.madeOf());
            said.setText(said(show.part()));
            bus.publish(show);
        };
        List.of(part, level, others, spacing).forEach(c -> c.setOnAction(set -> changed.run()));
        List.of(grid, ground, points).forEach(c -> c.setOnAction(set -> changed.run()));
        final Shell shell = new Shell("The GLM part lab", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The part"), part, new Label("Looked at"), level, others,
                new Label("Round it"), new HBox(GAP, grid, ground, points), spacing,
                new Label("What the parts are made of, seen where bare"), made.shown(changed),
                new Label("Its points: in its own frame; put together, on the platform (cm)"), said,
                shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(window, WIDE, TALL);
        said.setText(said(first.part()));
        bus.publish(first);
    }

    /** {@code part}'s points, each by name, where it is in its own frame and on the platform. */
    String said(final PartAt part) {
        final StringBuilder out = new StringBuilder(part.said()).append('\n');
        for (final Point p : part.shape().points()) {
            final var at = p.at();
            final var on = at.plus(built.at(part));
            out.append(String.format(Locale.ROOT, "%-28s %7.1f %7.1f %7.1f   %7.1f %7.1f %7.1f%n", p.name(), at.x(),
                    at.y(), at.z(), on.x(), on.y(), on.z()));
        }
        return out.toString();
    }

    private javafx.scene.Node light() {
        return light.shown();
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }

    private ComboBox<String> choice(final List<String> titles, final int chosen) {
        final ComboBox<String> choice = new ComboBox<>();
        choice.getItems().addAll(titles);
        choice.getSelectionModel().select(Math.max(0, chosen));
        return choice;
    }

    private CheckBox check(final String title, final boolean on) {
        final CheckBox box = new CheckBox(title);
        box.setSelected(on);
        return box;
    }

    private int at(final ComboBox<String> choice) {
        return choice.getSelectionModel().getSelectedIndex();
    }
}
