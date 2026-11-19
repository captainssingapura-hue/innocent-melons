package com.example.fxsuite.campus.emblem;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * The emblem's bench: the melon ({@link Emblem}) still, a model to look at through the bench kit's model view, and any
 * of its eight octants lifted out of it or cut away: ticked, each by its name; how far, by a slider; whether its halo is
 * over it. Shapes it is tried in, at a press: whole, a quarter cut out as it is drawn, an eighth, half. Each change
 * makes it afresh and shows it in place of what was. Looked at from the south, its front, a little from the east and
 * above. Put together of the bench kit's parts.
 *
 * <p>{@code -Demblem.yaw}, {@code -Demblem.pitch}, {@code -Demblem.distance} (m) where it is seen from.
 */
public final class EmblemBench extends Application {

    /** How big it is on the bench: a metre to its rind. */
    static final double RADIUS = 100;
    /** How far a lifted octant may be moved out, at most; how far at first. */
    static final double LIFT_MOST = 150;
    static final double LIFT_FIRST = 40;

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the emblem", bus);
    private final ModelView view = new ModelView();

    private record Asked(Emblem emblem) {
    }

    private record Made(Emblem emblem, ModelView.Model model) {
    }

    private final Workshop<Asked, Made> workshop = new Workshop<>("emblem", bus, Asked.class, this::make);

    private final CheckBox[] octants = new CheckBox[8];
    private final Slider lift = new Slider(0, LIFT_MOST, LIFT_FIRST);
    private final CheckBox cutAway = new CheckBox("Cut away, not lifted");
    private final CheckBox halo = new CheckBox("Its halo");
    /** While a preset ticks its boxes: asked once, after, not for each. */
    private boolean setting;

    @Override
    public void start(final Stage stage) {
        bus.subscribe(Made.class, this::show, Bus.SCREEN);
        final GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(6);
        // laid out as it is seen from the south: west to the left, upper above; the south (near) ones first
        for (int o = 0; o < 8; o++) {
            octants[o] = new CheckBox(Emblem.name(o));
            octants[o].selectedProperty().addListener((obs, was, is) -> ask());
            final int column = ((o & 1) != 0 ? 1 : 0);
            final int row = ((o & 4) != 0 ? 0 : 2) + ((o & 2) != 0 ? 0 : 1);
            grid.add(octants[o], column, row);
        }
        lift.setShowTickLabels(true);
        lift.setMajorTickUnit(50);
        lift.valueProperty().addListener((obs, was, is) -> ask());
        cutAway.selectedProperty().addListener((obs, was, is) -> ask());
        halo.setSelected(true);
        halo.selectedProperty().addListener((obs, was, is) -> ask());
        final FlowPane presets = new FlowPane(6, 6,
                preset("Whole", Set.of()),
                preset("A quarter, as drawn", Emblem.QUARTER),
                preset("An eighth", Emblem.EIGHTH),
                preset("Half", Emblem.HALF));
        final VBox side = new VBox(10,
                new Label("Lifted out, as seen from the front"), grid,
                new Label("How far out, cm"), lift,
                cutAway, halo,
                new Label("Tried in"), presets);
        side.setPadding(new Insets(8));
        shell.bar(List.of());
        shell.side(new Label("The emblem"), side, 380);
        shell.middle(view.node());
        view.ground(0, 0, 0, 2_000);
        view.lookAt(0, RADIUS * 1.2, 0);
        view.from("emblem", 25, 15, 6);
        view.zoom(150, 3_000);
        preset(Emblem.QUARTER);
        shell.show(stage, 1280, 860);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /** A button that ticks just {@code lifted}, the rest left as they are. */
    private Button preset(final String name, final Set<Integer> lifted) {
        final Button b = new Button(name);
        b.setOnAction(e -> preset(lifted));
        return b;
    }

    private void preset(final Set<Integer> lifted) {
        setting = true;
        for (int o = 0; o < 8; o++) {
            octants[o].setSelected(lifted.contains(o));
        }
        setting = false;
        ask();
    }

    /** The emblem as its boxes and its slider say, asked of the workshop. */
    private void ask() {
        if (setting) {
            return;
        }
        final Set<Integer> lifted = new HashSet<>();
        for (int o = 0; o < 8; o++) {
            if (octants[o].isSelected()) {
                lifted.add(o);
            }
        }
        bus.publish(new Asked(new Emblem(RADIUS, lifted, lift.getValue(), cutAway.isSelected(), halo.isSelected())));
    }

    /** On the workshop's worker: its solids drawn. */
    private Made make(final Asked a) {
        return new Made(a.emblem(), ModelView.Model.of(a.emblem().solids()));
    }

    /** On the screen's thread: shown in place of what was; what it is, in its figures. */
    private void show(final Made m) {
        view.show(m.model());
        final Emblem e = m.emblem();
        final List<String> names = new TreeSet<>(e.lifted()).stream().map(Emblem::name).toList();
        final String what = names.isEmpty() ? "whole"
                : String.format(Locale.ROOT, "%s %s%s", String.join(", ", names),
                        e.cutAway() ? "cut away" : String.format(Locale.ROOT, "lifted %.0f cm out", e.lift()),
                        names.size() == 1 ? "" : String.format(Locale.ROOT, " (%d of 8)", names.size()));
        bus.publish(new Bus.Figures(String.format(Locale.ROOT,
                "The melon, %.0f cm round: %s%s%n%d solids", e.radius() * 2, what, e.halo() ? "; under its halo" : "",
                e.solids().size())));
        bus.publish(new Bus.Problems(""));
    }
}
