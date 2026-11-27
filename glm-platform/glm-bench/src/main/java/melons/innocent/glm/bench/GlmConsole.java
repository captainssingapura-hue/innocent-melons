package melons.innocent.glm.bench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its left,
 * the light. In its middle: for each group of the platform's parts, whether it is shown, what it is finished in, and
 * its colour where its finish takes one, and over them, for the motorcycle, how far it is leaned over; for each metal
 * the parts are designed in, which metal they are made of instead; and what the assembly found, said back from the
 * bench. It draws nothing itself: it says what is on show, and how far the motorcycle is leaned, as messages on the
 * bench's bus ({@link OnShow}, {@link Leaned}), and never calls what draws.
 */
public final class GlmConsole {

    private static final double WIDE = 760;
    private static final double TALL = 760;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;

    private final Bus bus;
    private final Asked asked;
    private final OnShow first;
    private final LightBoard light;
    private final List<Material> used;
    private final Leaned leaning;

    /**
     * A console on {@code bus}, its light asking {@code asked}, with {@code first} on show to begin with, offering
     * another metal for each in {@code used}; and, where it is a motorcycle on show, leaned as {@code leaning} to begin
     * with, a slider to lean it over, or none where {@code leaning} is null.
     */
    public GlmConsole(final Bus bus, final Asked asked, final OnShow first, final Collection<Material> used,
                      final Leaned leaning) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.light = new LightBoard(asked);
        this.used = List.copyOf(used);
        this.leaning = leaning;
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        // its controls made here, on JavaFX's thread, as JavaFX has them made
        final MadeOfChoice made = new MadeOfChoice(used, first.madeOf());
        final Map<Group, CheckBox> showing = new EnumMap<>(Group.class);
        final Map<Group, ComboBox<String>> kinds = new EnumMap<>(Group.class);
        final Map<Group, ColorPicker> colours = new EnumMap<>(Group.class);
        final GridPane groups = new GridPane(GAP, GAP);
        int row = 0;
        for (final Group group : Group.values()) {
            final Coat coat = first.coats().get(group);
            final CheckBox shown = new CheckBox(group.title());
            shown.setSelected(!first.hidden().contains(group));
            final ComboBox<String> kind = new ComboBox<>();
            kind.getItems().addAll(Arrays.stream(Coat.Kind.values()).map(Coat.Kind::title).toList());
            kind.getSelectionModel().select(coat.kind().ordinal());
            final ColorPicker colour = new ColorPicker(Color.web(coat.colour()));
            colour.setDisable(!coat.kind().coloured());
            showing.put(group, shown);
            kinds.put(group, kind);
            colours.put(group, colour);
            groups.addRow(row++, shown, kind, colour);
        }
        final Runnable changed = () -> {
            kinds.forEach((group, kind) -> colours.get(group).setDisable(!Coat.Kind.values()[kind
                    .getSelectionModel().getSelectedIndex()].coloured()));
            bus.publish(new OnShow(coats(kinds, colours), hidden(showing), made.madeOf()));
        };
        showing.values().forEach(box -> box.setOnAction(set -> changed.run()));
        kinds.values().forEach(choice -> choice.setOnAction(set -> changed.run()));
        colours.values().forEach(picker -> picker.setOnAction(set -> changed.run()));
        final Shell shell = new Shell("The GLM bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The groups: shown, and their coated parts coated in"), groups,
                new Label("What their parts are made of, seen where bare"), made.shown(changed),
                shell.working(), shell.figures(), shell.problems());
        if (leaning != null) {
            middle.getChildren().addAll(0, leans());
        }
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(window, WIDE, TALL);
        bus.publish(first);
    }

    /** A slider to lean the motorcycle over, to its left or its right, and what it says it is leaned. */
    private List<Node> leans() {
        final Slider slider = new Slider(-Riding.MOST, Riding.MOST, leaning.degrees());
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(15);
        slider.setBlockIncrement(1);
        final Label said = new Label();
        final Runnable leant = () -> {
            said.setText(String.format(Locale.ENGLISH, "Leaned %.0f° to its %s", Math.abs(slider.getValue()),
                    slider.getValue() < 0 ? "left" : "right"));
            bus.publish(new Leaned(slider.getValue()));
        };
        slider.valueProperty().addListener((seen, was, now) -> leant.run());
        leant.run();
        return List.of(said, slider);
    }

    /** What each group is finished in, as the window says. */
    private Map<Group, Coat> coats(final Map<Group, ComboBox<String>> kinds, final Map<Group, ColorPicker> colours) {
        final Map<Group, Coat> coats = new EnumMap<>(Group.class);
        for (final Group group : Group.values()) {
            final Color c = colours.get(group).getValue();
            coats.put(group, new Coat(Coat.Kind.values()[kinds.get(group).getSelectionModel().getSelectedIndex()],
                    String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(c.getRed() * 255),
                            Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255))));
        }
        return coats;
    }

    /** The groups the window does not show. */
    private Set<Group> hidden(final Map<Group, CheckBox> showing) {
        final Set<Group> hidden = EnumSet.noneOf(Group.class);
        showing.forEach((group, box) -> {
            if (!box.isSelected()) {
                hidden.add(group);
            }
        });
        return hidden;
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
