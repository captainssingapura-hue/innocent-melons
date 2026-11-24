package melons.innocent.terrain.bench;

import melons.innocent.terrain.drawn.Cached;
import melons.innocent.terrain.drawn.Painting;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The band bench's console, in a JavaFX window beside the card's, where the eye is placed: a small map of the land,
 * clicked where the eye is wanted over it; the band of height it is kept in, with a step up and a step down to the
 * next, and how finely the land is seen from it; a slider for how high the eye is within its band, which never takes
 * it out of the band. Then how long a frame takes; what is kept of the land, level by level, in a table: how far
 * apart each level's points are, how many of its tiles are shown, wanted ahead and drawn, how many have their data
 * built and their meshes made and how much room those take, how many are being built, and how many are on the card
 * and how much room they take there, all of them together under them, and the card's budget; and the legend. Made
 * and shown on JavaFX's thread; what the card's thread tells it is shown there in turn. Closing it ends the bench.
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
    private final AtomicReference<GridPane> cache = new AtomicReference<>();
    private final AtomicReference<Label> budget = new AtomicReference<>();
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
        final Label took = new Label();
        frame.set(took);
        final GridPane table = new GridPane(GAP, GAP / 4);
        cache.set(table);
        final Label room = new Label();
        budget.set(room);
        final TitledPane monitor = new TitledPane("What is kept", new VBox(GAP / 2, table, room));
        final VBox key = new VBox(GAP / 2);
        for (final Painting.Shown shown : legend) {
            key.getChildren().add(new HBox(GAP, new Rectangle(SWATCH, SWATCH,
                    Color.color(shown.red(), shown.green(), shown.blue())), new Label(shown.label())));
        }
        final VBox box = new VBox(GAP, what, how, pane.canvas(), heights(), took, monitor, key);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Band terrain bench");
        stage.setScene(new Scene(box, WIDTH + 120, -1));
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

    /** What is kept of the land, level by level, told on the card's thread and shown in the table. */
    public void cached(final Cached cached) {
        final List<String[]> lines = new ArrayList<>();
        lines.add(new String[] {"Level", "Apart", "Shown", "Ahead", "Drawn", "Data", "Meshes", "Building", "On card"});
        cached.rows().forEach(row -> lines.add(line(String.valueOf(row.level()), row)));
        lines.add(line("All", cached.total()));
        final String room = String.format(Locale.ROOT, "On the card %.0f MB of %.0f MB; built and kept here %.0f MB;%n"
                + "%d tiles put on the card, %.2f ms each", megabytes(cached.total().cardBytes()),
                megabytes(cached.budget()), megabytes(cached.total().dataBytes() + cached.total().meshBytes()),
                cached.put(), cached.putting());
        Platform.runLater(() -> {
            final GridPane table = cache.get();
            if (table != null) {
                table.getChildren().clear();
                for (int r = 0; r < lines.size(); r++) {
                    for (int c = 0; c < lines.get(r).length; c++) {
                        table.add(new Label(lines.get(r)[c]), c, r);
                    }
                }
            }
            shown(budget, room);
        });
    }

    /** A row of the table: {@code name}, then what {@code row} says, rooms in megabytes. */
    private String[] line(final String name, final Cached.Row row) {
        return new String[] {name, row.level() < 0 ? "" : String.format(Locale.ROOT, "%.1f m", row.spacing() / METRE),
            String.valueOf(row.shown()), String.valueOf(row.ahead()), String.valueOf(row.drawn()),
            String.format(Locale.ROOT, "%d, %.0f MB", row.data(), megabytes(row.dataBytes())),
            String.format(Locale.ROOT, "%d, %.0f MB", row.meshes(), megabytes(row.meshBytes())),
            String.valueOf(row.building()),
            String.format(Locale.ROOT, "%d, %.0f MB", row.carded(), megabytes(row.cardBytes()))};
    }

    /** {@code bytes} in megabytes. */
    private double megabytes(final long bytes) {
        return bytes / (double) (1L << 20);
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
