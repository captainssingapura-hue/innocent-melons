package melons.innocent.terrain.bench;

import melons.innocent.terrain.drawn.Painting;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
 * The tiled bench's console, in a JavaFX window beside the card's, where the eye is placed, here and only here: a
 * small map of the land, clicked where the eye is wanted over it, the eye marked on it and the way it is turned; a
 * slider for how high it is, from two metres to three kilometres, evenly by how many times ten. Then how many tiles
 * are wanted around it and how many are built and drawn, how long a frame takes, and the legend. Made and shown on
 * JavaFX's thread; what the card's thread tells it is shown there in turn. Closing it ends the bench.
 */
public final class TiledControls {

    private static final double WIDTH = 360;
    private static final double GAP = 8;
    private static final double SWATCH = 14;
    private static final double METRE = 100;
    /** How low and how high the eye may be put with the slider, in metres. */
    private static final double LOWEST = 2;
    private static final double HIGHEST = 3_000;

    private final Wanted wanted;
    private final String about;
    private final List<Painting.Shown> legend;
    private final MapPicture picture;
    private final Flight first;
    private final AtomicReference<MapPane> map = new AtomicReference<>();
    private final AtomicReference<Label> tiles = new AtomicReference<>();
    private final AtomicReference<Label> frame = new AtomicReference<>();

    /** A console asking {@code wanted}, saying {@code about} of what is drawn, its map {@code picture}. */
    public TiledControls(final Wanted wanted, final String about, final List<Painting.Shown> legend,
                         final MapPicture picture, final Flight first) {
        this.wanted = wanted;
        this.about = about;
        this.legend = List.copyOf(legend);
        this.picture = picture;
        this.first = first;
    }

    /** Made and shown at ({@code x}, {@code y}) on the screen: on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label what = new Label(about);
        what.setWrapText(true);
        final Label how = new Label("Click the map to place the eye; drag in the card's window to look round.");
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
        final VBox box = new VBox(GAP, what, how, pane.canvas(), height(), built, took, key);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Tiled terrain bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        stage.setX(x);
        stage.setY(y);
        stage.setOnHidden(event -> wanted.stop());
        stage.show();
    }

    /** A slider for how high the eye is, evenly by how many times ten; what it says wanted at once, as it is moved. */
    private VBox height() {
        final Label said = new Label(String.format(Locale.ROOT, "The eye %.0f m up", first.height() / METRE));
        final Slider tens = new Slider(Math.log10(LOWEST), Math.log10(HIGHEST),
                Math.log10(Math.clamp(first.height() / METRE, LOWEST, HIGHEST)));
        tens.valueProperty().addListener((property, was, now) -> {
            final double metres = Math.pow(10, now.doubleValue());
            wanted.raise(metres * METRE);
            said.setText(String.format(Locale.ROOT, "The eye %.0f m up", metres));
        });
        return new VBox(GAP / 2, said, tens);
    }

    /** Where the eye is now, and which way it is turned, told on the card's thread and marked on the map. */
    public void seen(final Flight flight) {
        Platform.runLater(() -> {
            if (map.get() != null) {
                map.get().eye(flight);
            }
        });
    }

    /** How many tiles are wanted, built and drawn, and the finest and coarsest of them, told on the card's thread. */
    public void tiles(final int chosen, final int built, final int drawn, final int finest, final int coarsest) {
        final String said = String.format(Locale.ROOT, "Tiles: %d wanted, %d built, %d drawn;%nlevels %d to %d",
                chosen, built, drawn, coarsest, finest);
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
