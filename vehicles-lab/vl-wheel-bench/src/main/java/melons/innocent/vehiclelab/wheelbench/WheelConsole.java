package melons.innocent.vehiclelab.wheelbench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import melons.innocent.vehiclelab.wheels.RimDesign;
import melons.innocent.vehiclelab.wheels.WheelSize;

import java.util.Arrays;
import java.util.List;

/**
 * The wheels bench's JavaFX window, beside the card's, laid out by the bench kit as every bench's is. On its left,
 * the light. In its middle: the rim's design; the wheel's size as a sidewall says it, the tyre's width in millimetres,
 * its wall as a share of that and the rim's diameter in inches, each typed or stepped to whatever it is to be, no
 * car's range held to; whether it turns; and what the bench says back. It draws nothing itself: it says what is on show
 * as a message on the bench's bus ({@link WheelShown}).
 */
final class WheelConsole {

    private static final double WIDE = 720;
    private static final double TALL = 560;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    /** How far each figure may be stepped: anything more than nought and short of the absurd. */
    private static final double MOST = 10_000;

    private final Bus bus;
    private final Asked asked;
    private final LightBoard light;

    /** A window on {@code bus}, its light asking {@code asked}. */
    WheelConsole(final Bus bus, final Asked asked) {
        this.bus = bus;
        this.asked = asked;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down, {@code first} said to begin with. */
    void shown(final double x, final double y, final WheelShown first) {
        final ComboBox<String> design = new ComboBox<>();
        design.getItems().addAll(Arrays.stream(RimDesign.values()).map(RimDesign::title).toList());
        design.getSelectionModel().select(first.design().ordinal());
        final Spinner<Double> width = figure(first.size().width(), 5);
        final Spinner<Double> aspect = figure(first.size().aspect(), 5);
        final Spinner<Double> rim = figure(first.size().rim(), 1);
        final CheckBox turning = new CheckBox("Turning, as it would rolling along");
        turning.setSelected(first.turning());
        final Runnable changed = () -> bus.publish(new WheelShown(
                RimDesign.values()[design.getSelectionModel().getSelectedIndex()],
                new WheelSize(width.getValue(), aspect.getValue(), rim.getValue()), turning.isSelected()));
        design.setOnAction(set -> changed.run());
        turning.setOnAction(set -> changed.run());
        List.of(width, aspect, rim).forEach(s -> s.valueProperty().addListener((o, was, now) -> changed.run()));
        final GridPane rows = new GridPane(GAP, GAP);
        rows.addRow(0, new Label("Rim"), design);
        rows.addRow(1, new Label("Tyre's width, mm"), width);
        rows.addRow(2, new Label("Its wall, % of its width"), aspect);
        rows.addRow(3, new Label("Rim's diameter, inches"), rim);
        final Shell shell = new Shell("The wheels bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The wheel"), rows, turning, shell.figures(), shell.problems());
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

    /** A figure that may be typed or stepped by {@code step}, beginning at {@code value}. */
    private Spinner<Double> figure(final double value, final double step) {
        final Spinner<Double> spinner = new Spinner<>(step / 10, MOST, value, step);
        spinner.setEditable(true);
        return spinner;
    }
}
