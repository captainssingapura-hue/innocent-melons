package melons.innocent.vehiclelab.bench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import melons.innocent.vehiclelab.forms.Coat;
import melons.innocent.vehiclelab.motorcycle.Livery;
import melons.innocent.vehiclelab.motorcycle.Part;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javafx.geometry.Insets;
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

/**
 * The bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its left,
 * the light. In its middle: for each of the bike's finishes, what it is, gloss or metallic paint, enamel, titanium,
 * gun metal, steel, bronze, gold, rubber, glass or a lit lamp, and its colour where it takes one; how far its bars are
 * turned; and whether its wheels are turning. It draws nothing itself: it says what is on show as a message on the
 * bench's bus ({@link OnShow}), and never calls what draws.
 */
public final class Console {

    private static final double WIDE = 720;
    private static final double TALL = 760;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    /** How far the bars turn either way, in degrees. */
    private static final double LOCK = 35;

    private final Bus bus;
    private final Asked asked;
    private final OnShow first;
    private final LightBoard light;

    /** A console on {@code bus}, its light asking {@code asked}, with {@code first} on show to begin with. */
    public Console(final Bus bus, final Asked asked, final OnShow first) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Map<Part.Slot, ComboBox<String>> kinds = new EnumMap<>(Part.Slot.class);
        final Map<Part.Slot, ColorPicker> colours = new EnumMap<>(Part.Slot.class);
        final GridPane finishes = new GridPane(GAP, GAP);
        int row = 0;
        for (final Part.Slot slot : Part.Slot.values()) {
            final Coat coat = first.livery().coat(slot);
            final ComboBox<String> kind = new ComboBox<>();
            kind.getItems().addAll(Arrays.stream(Coat.Kind.values()).map(Coat.Kind::title).toList());
            kind.getSelectionModel().select(coat.kind().ordinal());
            final ColorPicker colour = new ColorPicker(Color.web(coat.colour()));
            colour.setDisable(!coat.kind().coloured());
            kinds.put(slot, kind);
            colours.put(slot, colour);
            finishes.addRow(row++, new Label(slot.title()), kind, colour);
        }
        final Slider steer = new Slider(-LOCK, LOCK, first.steer());
        steer.setShowTickMarks(true);
        steer.setMajorTickUnit(LOCK);
        final CheckBox rolling = new CheckBox("Its wheels turning");
        rolling.setSelected(first.rolling());
        final Runnable changed = () -> {
            kinds.forEach((slot, kind) -> colours.get(slot).setDisable(!Coat.Kind.values()[kind.getSelectionModel()
                    .getSelectedIndex()].coloured()));
            bus.publish(new OnShow(livery(kinds, colours), steer.getValue(), rolling.isSelected()));
        };
        kinds.values().forEach(choice -> choice.setOnAction(set -> changed.run()));
        colours.values().forEach(picker -> picker.setOnAction(set -> changed.run()));
        steer.valueProperty().addListener((seen, was, now) -> changed.run());
        rolling.setOnAction(set -> changed.run());
        final Shell shell = new Shell("The vehicles bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The boxer's finishes"), finishes,
                new Label("Its bars, turned"), steer, rolling, shell.working(), shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(window, WIDE, TALL);
        bus.publish(first);
    }

    /** The livery the window says: each slot's kind of finish, in the colour picked for it. */
    private Livery livery(final Map<Part.Slot, ComboBox<String>> kinds, final Map<Part.Slot, ColorPicker> colours) {
        final Map<Part.Slot, Coat> coats = new EnumMap<>(Part.Slot.class);
        for (final Part.Slot slot : Part.Slot.values()) {
            final Color c = colours.get(slot).getValue();
            coats.put(slot, new Coat(Coat.Kind.values()[kinds.get(slot).getSelectionModel().getSelectedIndex()],
                    String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(c.getRed() * 255),
                            Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255))));
        }
        return new Livery(coats);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
