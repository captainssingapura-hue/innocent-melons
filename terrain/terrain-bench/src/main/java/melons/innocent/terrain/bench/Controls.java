package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.view.Orbit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The bench's controls, in a JavaFX window beside the card's: what ground is drawn and how fine, where it is looked
 * at from, how long a frame takes, a button for the first view back. Made and shown on JavaFX's thread; what the card's
 * thread tells it is shown there in turn. Closing it ends the bench.
 */
public final class Controls {

    private static final double WIDTH = 340;
    private static final double GAP = 8;
    private static final double METRE = 100;

    private final Wanted wanted;
    private final String about;
    private final AtomicReference<Label> view = new AtomicReference<>();
    private final AtomicReference<Label> frame = new AtomicReference<>();

    /** Controls asking {@code wanted}, saying {@code about} of what is drawn. */
    public Controls(final Wanted wanted, final String about) {
        this.wanted = wanted;
        this.about = about;
    }

    /** Made and shown at ({@code x}, {@code y}) on the screen: on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label what = new Label(about);
        what.setWrapText(true);
        final Label seen = new Label();
        final Label took = new Label();
        view.set(seen);
        frame.set(took);
        final Button first = new Button("First view");
        first.setOnAction(event -> wanted.first());
        final Label how = new Label("Drag to turn; right-drag or Shift-drag to carry across; scroll to come nearer.");
        how.setWrapText(true);
        final VBox box = new VBox(GAP, what, seen, took, first, how);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Terrain bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        stage.setX(x);
        stage.setY(y);
        stage.setOnHidden(event -> wanted.stop());
        stage.show();
    }

    /** Where the land is looked at from now, told on the card's thread. */
    public void seen(final Orbit orbit) {
        final String said = String.format(Locale.ROOT, "Looking at %.0f m across, %.0f m along, %.0f m up%n"
                + "from %.0f m off, turned %.0f°, %.0f° down", orbit.round().x() / METRE, orbit.round().z() / METRE,
                orbit.round().y() / METRE, orbit.distance() / METRE, orbit.yaw(), orbit.pitch());
        Platform.runLater(() -> shown(view, said));
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
