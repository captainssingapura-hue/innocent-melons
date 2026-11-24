package com.example.fxsuite.vulkan.bench;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A JavaFX window beside the window the card draws to, saying what is being shown: which card is drawing, how long
 * a frame takes, how much memory is held on the card and what the place says of itself, and a few lines on what the
 * keys and the mouse do in the card's window. A tick box logs what it is told as it is told, a line each time, to the
 * run's own output and, where {@code -Dbench.log=} names a file, to the end of that file; {@code -Dbench.log=true}
 * begins with it ticked. Between them it sets the
 * light: shadows or none, and the sky by hand or by the almanac and the clock ({@link LightBoard}). Closing it asks
 * for everything to stop.
 */
public final class Guide {

    private static final double WIDTH = 340;
    private static final double GAP = 10;
    private static final double LINE = 22;
    /** How many lines' room what can be set here takes, over the lines it only says. */
    private static final int SET = 37;

    private final Asked asked;
    private final String card;
    private final List<String> lines;
    private final LightBoard light;
    /** Where a frame's time is shown, once the window is: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Label> taken = new AtomicReference<>();

    /** A guide saying {@code lines}, for what is drawn on the card called {@code card}. */
    public Guide(final Asked asked, final String card, final List<String> lines) {
        this.asked = asked;
        this.card = card;
        this.lines = List.copyOf(lines);
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label time = new Label("A frame: not drawn yet");
        taken.set(time);
        final CheckBox logged = new CheckBox("Log these, a line each time they are told");
        logged.setSelected(asked.logged());
        logged.setOnAction(set -> asked.logged(logged.isSelected()));
        time.setWrapText(true);
        final VBox all = new VBox(GAP, new Label("Drawn on: " + card), time, logged, light.shown(), new Separator(),
                new Label(String.join("\n", lines)));
        all.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setTitle("The renderer's bench");
        stage.setScene(new Scene(all, WIDTH, LINE * (lines.size() + SET)));
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        stage.show();
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }

    /**
     * Told how long a frame takes, in thousandths of a second, and {@code more} of what is held, a line each: shown,
     * and logged where that is asked. From any thread.
     */
    public void told(final double millis, final List<String> more) {
        final List<String> lines = new ArrayList<>(List.of(
                String.format(Locale.ENGLISH, "A frame: %.1f ms (%.0f a second)", millis, 1000 / millis)));
        more.stream().filter(line -> !line.isBlank()).forEach(lines::add);
        final String said = String.join(System.lineSeparator(), lines);
        if (asked.logged()) {
            logged(LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "  " + String.join("; ", lines));
        }
        Platform.runLater(() -> Optional.ofNullable(taken.get()).ifPresent(time -> time.setText(said)));
    }

    /** {@code line} logged: to the run's own output, and to the end of the file the run names, if it names one. */
    private void logged(final String line) {
        System.out.println(line);
        final String file = System.getProperty("bench.log", "");
        if (!file.isBlank() && !file.equals("true")) {
            try {
                Files.writeString(Path.of(file), line + System.lineSeparator(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (final IOException unwritten) {
                throw new IllegalStateException("the log could not be written to " + file, unwritten);
            }
        }
    }
}
