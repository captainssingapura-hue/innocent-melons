package com.example.fxsuite.vulkan.bench;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
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
    /** How many lines' room a dial takes. */
    private static final int DIALLED = 3;
    /** The tallest the window is made, and the room its scroll bar takes. */
    private static final double TALLEST = 860;
    private static final double SCROLLED = 24;

    private final Asked asked;
    private final String card;
    private final List<String> lines;
    private final List<Dial> dials;
    private final LightBoard light;
    /** Where a frame's time is shown, once the window is: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Label> taken = new AtomicReference<>();
    /** Its window, while it is shown. */
    private final AtomicReference<Stage> window = new AtomicReference<>();

    /** A guide saying {@code lines}, for what is drawn on the card called {@code card}. */
    public Guide(final Asked asked, final String card, final List<String> lines) {
        this(asked, card, lines, List.of());
    }

    /** The same, with a slider for each of {@code dials}, the place's figures that may be set while walking. */
    public Guide(final Asked asked, final String card, final List<String> lines, final List<Dial> dials) {
        this.dials = List.copyOf(dials);
        this.asked = asked;
        this.card = card;
        this.lines = List.copyOf(lines);
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        shown(Optional.empty(), x, y);
    }

    /** Shown where {@code arranged} puts a console, with its two buttons. To be called on JavaFX's thread. */
    public void shown(final Arranged arranged) {
        shown(Optional.of(arranged), 0, 0);
    }

    private void shown(final Optional<Arranged> arranged, final double x, final double y) {
        final Stage stage = new Stage();
        final Label time = new Label("A frame: not drawn yet");
        taken.set(time);
        final CheckBox logged = new CheckBox("Log these, a line each time they are told");
        logged.setSelected(asked.logged());
        logged.setOnAction(set -> asked.logged(logged.isSelected()));
        time.setWrapText(true);
        final VBox all = new VBox(GAP, new Label("Drawn on: " + card), time, logged, dialled(), light.shown(),
                new Separator(), new Label(String.join("\n", lines)));
        all.setPadding(new Insets(GAP));
        arranged.ifPresent(spread -> all.getChildren().add(1, spread.bar()));
        stage.setTitle("The renderer's bench");
        // scrolled where it is taller than a screen has room for, as it is with several dials
        stage.setScene(new Scene(new ScrollPane(all), WIDTH + SCROLLED,
                Math.min(TALLEST, LINE * (lines.size() + SET + DIALLED * dials.size()))));
        stage.setX(x);
        stage.setY(y);
        arranged.ifPresent(spread -> spread.placed(stage));
        stage.setOnCloseRequest(closing -> asked.stop());
        stage.show();
        window.set(stage);
    }

    /**
     * A slider for each dial, under its name and what it now is, setting the dial as it is moved; or, for a dial
     * with named steps, a list of them to choose from.
     */
    private VBox dialled() {
        final VBox sliders = new VBox(GAP / 2);
        for (final Dial dial : dials) {
            if (!dial.steps().isEmpty()) {
                sliders.getChildren().addAll(new Label(dial.name()), stepped(dial));
                continue;
            }
            final Label said = new Label(String.format(Locale.ENGLISH, "%s: %.0f", dial.name(), dial.value()));
            final Slider slider = new Slider(dial.least(), dial.most(), dial.value());
            slider.valueProperty().addListener((moved, was, now) -> {
                dial.set(now.doubleValue());
                said.setText(String.format(Locale.ENGLISH, "%s: %.0f", dial.name(), dial.value()));
            });
            sliders.getChildren().addAll(said, slider);
        }
        return sliders;
    }

    /**
     * A list of a dial's named steps to choose one from, setting the dial to the one chosen. Where the dial asks,
     * each choosing is told to it as an asking and the list then goes back to saying nothing, so the same one can be
     * chosen again.
     */
    private ComboBox<String> stepped(final Dial dial) {
        final ComboBox<String> chosen = new ComboBox<>();
        chosen.getItems().addAll(dial.steps());
        chosen.setMaxWidth(Double.MAX_VALUE);
        if (!dial.asking()) {
            chosen.getSelectionModel().select(dial.step());
            chosen.setOnAction(set -> dial.set(chosen.getSelectionModel().getSelectedIndex()));
            return chosen;
        }
        chosen.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "choose one" : item);
            }
        });
        chosen.setOnAction(set -> {
            final int step = chosen.getSelectionModel().getSelectedIndex();
            if (step >= 0) {
                dial.chosen(step);
                Platform.runLater(() -> chosen.getSelectionModel().clearSelection());
            }
        });
        return chosen;
    }

    /** Taken away again, if it was shown. To be called on JavaFX's thread. */
    public void hidden() {
        Optional.ofNullable(window.getAndSet(null)).ifPresent(Stage::close);
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
