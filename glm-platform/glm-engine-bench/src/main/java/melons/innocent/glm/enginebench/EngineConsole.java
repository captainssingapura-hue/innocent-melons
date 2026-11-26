package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.engine.EngineFigures;

import java.util.Arrays;
import java.util.List;

/**
 * The engine bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its
 * left, the light. In its middle: which engine of the range is fitted, and whether it is turbocharged; how much
 * slower than it runs it is shown turning, or at its actual speed; how loud it is; how its block is shown; and what
 * its turning parts are made of; and what the bench says back. It is not driven from here: the driver drives at the
 * card's window, its keys. It draws and plays nothing itself: it says how the engine is set up as a message on the
 * bench's bus ({@link EngineRun}).
 */
final class EngineConsole {

    private static final double WIDE = 760;
    private static final double TALL = 700;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    /** The slowest and fastest the turning may be shown, as a share of the revs, on a scale of tens. */
    private static final double SLOWEST = Math.log10(0.002);
    private static final double FASTEST = Math.log10(0.2);
    private static final List<Material> METALS = List.of(Material.TITANIUM, Material.STEEL, Material.ALUMINIUM);

    private final Bus bus;
    private final Asked asked;
    private final List<EngineFigures> range;
    private final LightBoard light;

    /** A window on {@code bus}, its light asking {@code asked}, offering {@code range}. */
    EngineConsole(final Bus bus, final Asked asked, final List<EngineFigures> range) {
        this.bus = bus;
        this.asked = asked;
        this.range = List.copyOf(range);
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down, {@code first} said to begin with. */
    void shown(final double x, final double y, final EngineRun first) {
        final ComboBox<String> engine = choice(range.stream().map(e -> e.name() + ", " + Math.round(e.displacement())
                + " cc").toList(), first.engine());
        final CheckBox turbo = new CheckBox("Turbocharged");
        turbo.setSelected(first.turbo());
        final CheckBox actual = new CheckBox("Shown at its actual speed, as fast as it runs");
        actual.setSelected(first.slow() >= 1);
        final CheckBox covered = new CheckBox("Covered: its head covers, its timing cover and its trim cover on");
        covered.setSelected(first.covered());
        final Slider slow = new Slider(SLOWEST, FASTEST, Math.log10(Math.min(first.slow(), Math.pow(10, FASTEST))));
        slow.disableProperty().bind(actual.selectedProperty());
        final Slider volume = new Slider(0, 1, first.volume());
        final ComboBox<String> block = choice(Arrays.stream(EngineRun.Block.values()).map(EngineRun.Block::title)
                .toList(), first.block().ordinal());
        final ComboBox<String> made = choice(METALS.stream().map(Material::title).toList(),
                METALS.indexOf(first.made()));
        final Runnable changed = () -> bus.publish(new EngineRun(engine.getSelectionModel().getSelectedIndex(),
                actual.isSelected() ? 1 : Math.pow(10, slow.getValue()), turbo.isSelected(), volume.getValue(),
                EngineRun.Block.values()[block.getSelectionModel().getSelectedIndex()],
                METALS.get(made.getSelectionModel().getSelectedIndex()), covered.isSelected()));
        List.of(engine, block, made).forEach(c -> c.setOnAction(set -> changed.run()));
        List.of(turbo, actual, covered).forEach(c -> c.setOnAction(set -> changed.run()));
        List.of(slow, volume).forEach(s -> s.valueProperty().addListener((o, was, now) -> changed.run()));
        final GridPane rows = new GridPane(GAP, GAP);
        rows.addRow(0, new Label("Engine"), engine);
        rows.addRow(1, new Label("Shown slower"), slow);
        rows.addRow(2, new Label("Volume"), volume);
        rows.addRow(3, new Label("Block"), block);
        rows.addRow(4, new Label("Turning parts"), made);
        final Label keys = new Label("Drive it at the engine's window: W or Up the throttle, S or Down the brake,"
                + " E change up, Q change down, Shift automatic or manual, C cruise control, R and F its speed.");
        keys.setWrapText(true);
        final Shell shell = new Shell("The GLM engine bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The engine fitted"), rows, turbo, actual, covered, keys,
                shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(window, WIDE, TALL);
        bus.publish(first);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    void sky(final String said) {
        light.sky(said);
    }

    private ComboBox<String> choice(final List<String> titles, final int chosen) {
        final ComboBox<String> choice = new ComboBox<>();
        choice.getItems().addAll(titles);
        choice.getSelectionModel().select(Math.max(0, chosen));
        return choice;
    }
}
