package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The soft bench's JavaFX window, beside the card's, laid out by the engine's bench kit: on its left, the light; in
 * its middle, the region's bounds on sliders, how much of a muscle must be in it to be taken in, how big the cubes of
 * the elements are; whether the taken muscles' surfaces, their elements, the muscles held round them and the bones are
 * shown; what it takes in, listed by how deep each lies, and how much of the region
 * two muscles or more fill. It draws nothing: what is asked goes out on the bench's bus ({@link SoftAsked}), and what
 * is found comes back on it ({@link Listed}).
 */
public final class SoftConsole {

    private static final double WIDE = 720;
    private static final double TALL = 900;
    private static final double LIGHT_WIDE = 330;
    private static final double GAP = 8;
    private static final String[] NAMES = {"Left, cm", "Down, cm", "Back, cm", "Right, cm", "Up, cm", "Ahead, cm"};
    private static final double[][] RANGE = {{-30, 0}, {20, 70}, {-25, 5}, {0, 30}, {45, 80}, {-5, 20}};

    private final Bus bus;
    private final Asked asked;
    private final SoftAsked first;
    private final LightBoard light;

    /** What the region takes in, said in words: in a line, and listed. */
    public record Listed(String said, String list) {
    }

    /** The region's muscles asked to be solved at rest, if {@code go}; else the solve stopped. */
    public record Solve(boolean go) {
    }

    /** How the solve goes, in words. */
    public record Solving(String said) {
    }

    /** A console on {@code bus}, its light asking {@code asked}, {@code first} asked to begin with. */
    public SoftConsole(final Bus bus, final Asked asked, final SoftAsked first) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final double[] at = {first.lo().x(), first.lo().y(), first.lo().z(), first.hi().x(), first.hi().y(),
            first.hi().z()};
        final Slider[] bounds = new Slider[6];
        final Slider share = new Slider(0, 100, 100 * first.share());
        final Slider cell = new Slider(0.25, 1, first.cell());
        final CheckBox surfaces = new CheckBox("The taken muscles' surfaces");
        final CheckBox elements = new CheckBox("Their elements, each drawn smaller, apart; grey where fixed");
        final CheckBox held = new CheckBox("The muscles held round it, seen through");
        final CheckBox solved = new CheckBox("As solved at rest, where it has been; else as drawn");
        solved.setSelected(first.solved());
        final CheckBox fascia = new CheckBox("The fascia wrapping them, seen through, where there is one");
        fascia.setSelected(first.fascia());
        surfaces.setSelected(first.surfaces());
        elements.setSelected(first.elements());
        final CheckBox bones = new CheckBox("The bones");
        held.setSelected(first.held());
        bones.setSelected(first.bones());
        final Runnable changed = () -> bus.publish(new SoftAsked(
                new V3(bounds[0].getValue(), bounds[1].getValue(), bounds[2].getValue()),
                new V3(bounds[3].getValue(), bounds[4].getValue(), bounds[5].getValue()),
                share.getValue() / 100, cell.getValue(), held.isSelected(), bones.isSelected(),
                surfaces.isSelected(), elements.isSelected(), solved.isSelected(), fascia.isSelected()));
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP / 2);
        for (int i = 0; i < 6; i++) {
            bounds[i] = new Slider(RANGE[i][0], RANGE[i][1], at[i]);
            grid.addRow(i, new Label(NAMES[i]), bounds[i]);
        }
        grid.addRow(6, new Label("Taken in, %"), share);
        grid.addRow(7, new Label("Elements' cubes, cm"), cell);
        for (final Slider s : java.util.stream.Stream.concat(java.util.Arrays.stream(bounds),
                java.util.stream.Stream.of(share, cell)).toList()) {
            s.setPrefWidth(240);
            s.valueChangingProperty().addListener((o, was, is) -> {
                if (!is) {
                    changed.run();
                }
            });
        }
        held.setOnAction(e -> changed.run());
        bones.setOnAction(e -> changed.run());
        surfaces.setOnAction(e -> changed.run());
        elements.setOnAction(e -> changed.run());
        solved.setOnAction(e -> changed.run());
        fascia.setOnAction(e -> changed.run());
        final Label said = new Label();
        said.setWrapText(true);
        final TextArea list = new TextArea();
        list.setEditable(false);
        list.setPrefRowCount(20);
        bus.subscribe(Listed.class, l -> {
            said.setText(l.said());
            list.setText(l.list());
        }, Bus.SCREEN);
        final Button solve = new Button("Solve it at rest");
        final Button stop = new Button("Stop");
        solve.setOnAction(e -> bus.publish(new Solve(true)));
        stop.setOnAction(e -> bus.publish(new Solve(false)));
        final Label solving = new Label("Drawn in to lines, grown back with each kept from the others and the bones,"
                + " then let settle.");
        solving.setWrapText(true);
        bus.subscribe(Solving.class, s -> solving.setText(s.said()), Bus.SCREEN);
        final Shell shell = new Shell("The soft bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The region simulated soft, the body's frame"), grid, surfaces,
                elements, held, bones, said, new javafx.scene.layout.HBox(GAP, solve, stop), solved, fascia, solving,
                new Label("Taken in: how deep, the muscle, how much of it in the region"), list,
                shell.working(), shell.figures(), shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(new ScrollPane(middle)).show(stage, WIDE, TALL);
        bus.publish(first);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }
}
