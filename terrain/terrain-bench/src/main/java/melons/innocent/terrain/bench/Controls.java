package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.view.Orbit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    /** Made and shown at ({@code x}, {@code y}) on the screen: on JavaFX's thread. */
    public void shown(final double x, final double y) {
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
        final Label how = new Label("Drag to turn; right-drag or Shift-drag to carry across; scroll to come nearer.");
        how.setWrapText(true);
        final VBox key = new VBox(GAP / 2);
        for (final Painting.Shown shown : legend) {
            key.getChildren().add(new HBox(GAP, new Rectangle(SWATCH, SWATCH,
                    Color.color(shown.red(), shown.green(), shown.blue())), new Label(shown.label())));
        }
        final VBox box = new VBox(GAP, what, seen, took, coloured, first, how, key);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Terrain bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        stage.setX(x);
        stage.setY(y);
        stage.setOnHidden(event -> wanted.stop());
        stage.show();
    }

    /** Where the land is looked at from now, and what lies where it is looked at, told on the card's thread. */
    public void seen(final Orbit orbit, final String on) {
        final String said = String.format(Locale.ROOT, "Looking at %.0f m across, %.0f m along, %.0f m up%n"
                + "on %s,%nfrom %.0f m off, turned %.0f°, %.0f° down", orbit.round().x() / METRE,
                orbit.round().z() / METRE, orbit.round().y() / METRE, on, orbit.distance() / METRE, orbit.yaw(),
                orbit.pitch());
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
