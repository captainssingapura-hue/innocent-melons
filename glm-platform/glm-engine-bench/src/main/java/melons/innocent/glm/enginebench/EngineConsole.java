package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.engine.EngineFigures;

import java.util.Arrays;
import java.util.List;

/**
 * The engine bench's JavaFX window, beside the card's, laid out by the engine's bench kit as every bench's is. On its
 * left, the light. In its middle: which engine of the range runs, and whether it is turbocharged; whether it revs free
 * on its throttle, held open with Space, or is held at the revs said; its throttle, its revs and its load; how much
 * slower than it runs it is shown turning; how loud it is; how its block is shown; and what its turning parts are
 * made of; and what the bench says back, its revs, its firing and its boost. It draws and plays nothing itself: it
 * says how the engine is run as a message on the bench's bus ({@link EngineRun}).
 */
final class EngineConsole {

    private static final double WIDE = 760;
    private static final double TALL = 760;
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
        final ToggleGroup mode = new ToggleGroup();
        final RadioButton free = new RadioButton("Rev free on the throttle (hold Space for full)");
        final RadioButton held = new RadioButton("Hold the revs");
        free.setToggleGroup(mode);
        held.setToggleGroup(mode);
        (first.free() ? free : held).setSelected(true);
        final Slider throttle = new Slider(0, 1, first.throttle());
        final Slider rpm = new Slider(700, 7000, first.rpm());
        final Slider load = new Slider(0, 1, first.load());
        final Slider slow = new Slider(SLOWEST, FASTEST, Math.log10(first.slow()));
        final Slider volume = new Slider(0, 1, first.volume());
        final ComboBox<String> block = choice(Arrays.stream(EngineRun.Block.values()).map(EngineRun.Block::title)
                .toList(), first.block().ordinal());
        final ComboBox<String> made = choice(METALS.stream().map(Material::title).toList(),
                METALS.indexOf(first.made()));
        final boolean[] space = new boolean[1];
        final Runnable changed = () -> bus.publish(new EngineRun(engine.getSelectionModel().getSelectedIndex(),
                free.isSelected(), space[0] ? 1 : throttle.getValue(), rpm.getValue(), load.getValue(),
                Math.pow(10, slow.getValue()), turbo.isSelected(), volume.getValue(),
                EngineRun.Block.values()[block.getSelectionModel().getSelectedIndex()],
                METALS.get(made.getSelectionModel().getSelectedIndex())));
        List.of(engine, block, made).forEach(c -> c.setOnAction(set -> changed.run()));
        List.of(turbo, free, held).forEach(c -> c.setOnAction(set -> changed.run()));
        List.of(throttle, rpm, load, slow, volume).forEach(s -> s.valueProperty().addListener((o, was, now) ->
                changed.run()));
        final GridPane rows = new GridPane(GAP, GAP);
        rows.addRow(0, new Label("Engine"), engine);
        rows.addRow(1, new Label("Throttle"), throttle);
        rows.addRow(2, new Label("Revs held"), rpm);
        rows.addRow(3, new Label("Load"), load);
        rows.addRow(4, new Label("Shown slower"), slow);
        rows.addRow(5, new Label("Volume"), volume);
        rows.addRow(6, new Label("Block"), block);
        rows.addRow(7, new Label("Turning parts"), made);
        final Shell shell = new Shell("The GLM engine bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The engine and how it runs"), rows, turbo, free, held,
                shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage window = new Stage();
        window.setX(x);
        window.setY(y);
        window.setOnCloseRequest(closing -> asked.stop());
        window.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.SPACE && !space[0]) {
                space[0] = true;
                changed.run();
            }
        });
        window.addEventFilter(KeyEvent.KEY_RELEASED, key -> {
            if (key.getCode() == KeyCode.SPACE) {
                space[0] = false;
                changed.run();
            }
        });
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
