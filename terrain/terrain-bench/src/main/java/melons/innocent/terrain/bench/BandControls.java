package melons.innocent.terrain.bench;

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
 * The band bench's console, in a JavaFX window beside the card's, where the eye is placed: a small map of the land,
 * clicked where the eye is wanted over it; the band of height it is kept in, with a step up and a step down to the
 * next, and how finely the land is seen from it; a slider for how high the eye is within its band, which never takes
 * it out of the band. Then how many tiles are wanted and how many built and drawn, how long a frame takes, and the
 * legend. Made and shown on JavaFX's thread; what the card's thread tells it is shown there in turn. Closing it ends
 * the bench.
 */
public final class BandControls {

    private static final double WIDTH = 360;
    private static final double GAP = 8;
    private static final double SWATCH = 14;
    private static final double METRE = 100;

    private final Wanted wanted;
    private final String about;
    private final List<Painting.Shown> legend;
    private final MapPicture picture;
    private final Flight first;
    private final Altitudes altitudes;
    private final AtomicReference<MapPane> map = new AtomicReference<>();
    private final AtomicReference<Label> tiles = new AtomicReference<>();
    private final AtomicReference<Label> frame = new AtomicReference<>();
    /** The band the eye is kept in: JavaFX's thread's own. */
    private int band;

    /** A console asking {@code wanted}, saying {@code about} of what is drawn, its map {@code picture}. */
    public BandControls(final Wanted wanted, final String about, final List<Painting.Shown> legend,
                        final MapPicture picture, final Flight first, final Altitudes altitudes) {
        this.wanted = wanted;
        this.about = about;
        this.legend = List.copyOf(legend);
        this.picture = picture;
        this.first = first;
        this.altitudes = altitudes;
        this.band = altitudes.band(first.height());
    }

    /** Made and shown at ({@code x}, {@code y}) on the screen: on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label what = new Label(about);
        what.setWrapText(true);
        final Label how = new Label("Click the map to place the eye; drag in the card's window to look round, never"
                + " less than 45 degrees down. Step the band up or down; the slider moves the eye within it.");
        how.setWrapText(true);
        final MapPane pane = new MapPane(picture, wanted::placeAt);
        pane.eye(first);
        map.set(pane);
        final Label built = new Label("Tiles: none yet");
        final Label took = new Label();
        tiles.set(built);
        frame.set(took);
        final VBox key = new VBox(GAP / 2);
        for (final Painting.Shown shown : legend) {
            key.getChildren().add(new HBox(GAP, new Rectangle(SWATCH, SWATCH,
                    Color.color(shown.red(), shown.green(), shown.blue())), new Label(shown.label())));
        }
        final VBox box = new VBox(GAP, what, how, pane.canvas(), heights(), built, took, key);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Band terrain bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        stage.setX(x);
        stage.setY(y);
        stage.setOnHidden(event -> wanted.stop());
        stage.show();
    }

    /** The band, its steps, and the slider for how high within it; each wanted at once, as it is moved. */
    private VBox heights() {
        final Label named = new Label();
        final Label said = new Label();
        final Slider within = new Slider(0, 1, 0);
        final Runnable banded = () -> {
            named.setText(String.format(Locale.ROOT, "Band %d: %.0f to %.0f m, the land seen at %.1f m", band,
                    altitudes.floor(band) / METRE, altitudes.top(band) / METRE, altitudes.scale(band) / METRE));
            within.setMin(altitudes.floor(band) / METRE);
            within.setMax((altitudes.top(band) - 1) / METRE);
        };
        banded.run();
        within.setValue(first.height() / METRE);
        said.setText(String.format(Locale.ROOT, "The eye %.0f m up", first.height() / METRE));
        within.valueProperty().addListener((property, was, now) -> {
            wanted.raise(altitudes.within(band, now.doubleValue() * METRE));
            said.setText(String.format(Locale.ROOT, "The eye %.0f m up", now.doubleValue()));
        });
        final Button down = new Button("Band down");
        final Button up = new Button("Band up");
        down.setOnAction(event -> stepped(-1, within, banded));
        up.setOnAction(event -> stepped(1, within, banded));
        return new VBox(GAP / 2, named, new HBox(GAP, down, up), said, within);
    }

    /** The eye moved {@code by} bands, as far into the new band as it was into the old. */
    private void stepped(final int by, final Slider within, final Runnable banded) {
        final int next = Math.max(0, band + by);
        if (next != band) {
            final double into = within.getValue() * METRE - altitudes.floor(band);
            band = next;
            banded.run();
            within.setValue((altitudes.floor(band) + into) / METRE);
            wanted.raise(altitudes.within(band, altitudes.floor(band) + into));
        }
    }

    /** Where the eye is now, and which way it is turned, told on the card's thread and marked on the map. */
    public void seen(final Flight flight) {
        Platform.runLater(() -> {
            if (map.get() != null) {
                map.get().eye(flight);
            }
        });
    }

    /**
     * How many tiles are shown, how many wanted are built, how many drawn and how many stored, and how far apart
     * their points are, told on the card's thread.
     */
    public void tiles(final int chosen, final int built, final int drawn, final int stored, final double spacing) {
        final String said = String.format(Locale.ROOT,
                "Tiles: %d shown, %d built of those wanted, %d drawn, %d stored;%npoints %.2f m apart", chosen, built,
                drawn, stored, spacing / METRE);
        Platform.runLater(() -> shown(tiles, said));
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
