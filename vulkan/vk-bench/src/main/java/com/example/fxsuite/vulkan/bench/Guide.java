package com.example.fxsuite.vulkan.bench;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A JavaFX window beside the window the card draws to, saying what is being shown: which card is drawing, how long
 * a frame takes, and a few lines on what the keys and the mouse do in the card's window. It sets nothing; closing
 * it asks for everything to stop.
 */
public final class Guide {

    private static final double WIDTH = 340;
    private static final double GAP = 10;
    private static final double LINE = 22;

    private final Asked asked;
    private final String card;
    private final List<String> lines;
    /** Where a frame's time is shown, once the window is: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Label> taken = new AtomicReference<>();

    /** A guide saying {@code lines}, for what is drawn on the card called {@code card}. */
    public Guide(final Asked asked, final String card, final List<String> lines) {
        this.asked = asked;
        this.card = card;
        this.lines = List.copyOf(lines);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label time = new Label("A frame: not drawn yet");
        taken.set(time);
        final VBox all = new VBox(GAP, new Label("Drawn on: " + card), time, new Label(String.join("\n", lines)));
        all.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setTitle("The renderer's bench");
        stage.setScene(new Scene(all, WIDTH, LINE * (lines.size() + 4)));
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        stage.show();
    }

    /** Told how long a frame takes, in thousandths of a second. From any thread. */
    public void told(final double millis) {
        final String said = String.format("A frame: %.1f ms (%.0f a second)", millis, 1000 / millis);
        Platform.runLater(() -> Optional.ofNullable(taken.get()).ifPresent(time -> time.setText(said)));
    }
}
