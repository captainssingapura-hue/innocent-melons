package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.bench.Arranged;
import melons.innocent.terrain.drawn.Painting;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The bench's controls, in a JavaFX window beside the card's: what ground is drawn, where it is looked at from and
 * what lies there, how long a frame takes, a button for the first view back, and the legend: each label the land is
 * painted by, beside a swatch of its colour. Made and shown on JavaFX's thread; what the card's
 * thread tells it is shown there in turn. Closing it ends the bench.
 */
public final class Controls {

    private static final double WIDTH = 340;
    private static final double GAP = 8;
    private static final double METRE = 100;
    /** How big a legend's swatch is, each way. */
    private static final double SWATCH = 14;
    /** How many times ten slower and faster than at first the eye may be made to fly. */
    private static final double SLOWEST = -1;
    private static final double FASTEST = 3;

    private final Wanted wanted;
    private final String about;
    private final List<Painting.Shown> legend;
    private final AtomicReference<Label> view = new AtomicReference<>();
    private final AtomicReference<Label> frame = new AtomicReference<>();
    private final AtomicReference<Label> colouring = new AtomicReference<>();

    /** Controls asking {@code wanted}, saying {@code about} of what is drawn, its land painted by {@code legend}. */
    public Controls(final Wanted wanted, final String about, final List<Painting.Shown> legend) {
        this.wanted = wanted;
        this.about = about;
        this.legend = List.copyOf(legend);
    }

    /** Made and shown where {@code arranged} puts a console, with its two buttons: on JavaFX's thread. */
    public void shown(final Arranged arranged) {
        final Label what = new Label(about);
        what.setWrapText(true);
        final Label seen = new Label();
        final Label took = new Label();
        final Label coloured = new Label("Coloured: its raw shape");
        colouring.set(coloured);
        view.set(seen);
        frame.set(took);
        final Button first = new Button("First view");
        first.setOnAction(event -> wanted.first());
        final Label how = new Label("In the card's window: W, A, S and D to fly ahead, aside and back; Space up, Ctrl "
                + "down; Shift faster. Drag to look round; scroll to draw the land bigger or smaller.");
        how.setWrapText(true);
        final VBox key = new VBox(GAP / 2);
        for (final Painting.Shown shown : legend) {
            key.getChildren().add(new HBox(GAP, new Rectangle(SWATCH, SWATCH,
                    Color.color(shown.red(), shown.green(), shown.blue())), new Label(shown.label())));
        }
        final VBox box = new VBox(GAP, arranged.bar(), what, seen, took, coloured, first, speed(), how, key);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Terrain bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        arranged.placed(stage);
        stage.setOnHidden(event -> wanted.stop());
        stage.show();
    }

    /**
     * A slider for how fast the eye flies, from a tenth as fast to a thousand times, evenly by how many times ten:
     * as fast as at first in the middle of its left half. What it says is wanted at once, as it is moved.
     */
    private VBox speed() {
        final Label said = new Label("Speed: as at first");
        final Slider tens = new Slider(SLOWEST, FASTEST, Math.log10(wanted.speed()));
        tens.setMajorTickUnit(1);
        tens.setShowTickMarks(true);
        tens.valueProperty().addListener((property, was, now) -> {
            final double times = Math.pow(10, now.doubleValue());
            wanted.speed(times);
            said.setText(String.format(Locale.ROOT, times < 1 ? "Speed: %.2f times as at first"
                    : "Speed: %.0f times as at first", times));
        });
        return new VBox(GAP / 2, said, tens);
    }

    /** Where the eye is now, and what lies on the land under it, told on the card's thread. */
    public void seen(final Flight flight, final String on) {
        final String said = String.format(Locale.ROOT, "Over %.0f m across, %.0f m along, on %s;%n"
                + "the eye %.0f m up as drawn, turned %.0f°, %.0f° down;%nthe land drawn %s", flight.x() / METRE,
                flight.z() / METRE, on, flight.height() / METRE, flight.yaw(), flight.pitch(),
                flight.scale() >= 1 ? String.format(Locale.ROOT, "%.2f times as big", flight.scale())
                        : String.format(Locale.ROOT, "a %.0fth as big", 1 / flight.scale()));
        Platform.runLater(() -> shown(view, said));
    }

    /** How many of its {@code layers} layers the land is coloured in so far, told on the card's thread. */
    public void coloured(final int laid, final int layers) {
        final String said = laid == layers ? "Coloured: all " + layers + " layers"
                : "Coloured: " + laid + " of " + layers + " layers so far";
        Platform.runLater(() -> shown(colouring, said));
    }

    /** How long a frame takes, evened out, told on the card's thread. */
    public void told(final double millis) {
        final String said = String.format(Locale.ROOT, "A frame in %.1f ms", millis);
        Platform.runLater(() -> shown(frame, said));
    }

    private void shown(final AtomicReference<Label> label, final String said) {
        if (label.get() != null) {
            label.get().setText(said);
        }
    }
}
