package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.view.Orbit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The bench's controls, in a JavaFX window of their own beside the window the card draws to: which card is drawing,
 * how long a frame takes, the way the subject is looked at, a button to look at it again as at first, and a slider
 * for how far off the first of the row is shown as from. There is no 3D view in this window; the 3D is the card's,
 * and so is the mouse that moves the view. This window is told the way of looking and shows it; it sets nothing of
 * it.
 */
public final class Controls {

    private static final double WIDTH = 340;
    private static final double HEIGHT = 360;
    /** How many times over the slider doubles how far off the first of the row is shown as from. */
    private static final double DOUBLINGS = 6;
    private static final double GAP = 10;
    private static final double METRE = 100;

    private final Asked asked;
    private final String card;
    /** Where a frame's time is shown, once the controls are: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Label> taken = new AtomicReference<>();
    /** Where the way of looking is shown, once the controls are. */
    private final AtomicReference<Label> looked = new AtomicReference<>();

    /** Controls that ask through {@code asked}, for a bench drawn on the card called {@code card}. */
    public Controls(final Asked asked, final String card) {
        this.asked = asked;
        this.card = card;
    }

    /** Shown where {@code arranged} puts a console, with its two buttons. To be called on JavaFX's thread. */
    public void shown(final Arranged arranged) {
        final Label time = new Label("A frame: not drawn yet");
        final Label view = new Label("");
        taken.set(time);
        looked.set(view);
        final Button first = new Button("First view");
        first.setOnAction(pressed -> asked.first());
        final Label far = new Label(further(asked.further()));
        final Slider doubled = new Slider(0, DOUBLINGS, Math.log(asked.further()) / Math.log(2));
        doubled.valueProperty().addListener((value, was, now) -> {
            asked.further(Math.pow(2, now.doubleValue()));
            far.setText(further(asked.further()));
        });
        final VBox all = new VBox(GAP, new Label("Drawn on: " + card), arranged.bar(), time, view, first, far, doubled,
                new Label("In the card's window:\n  drag to turn\n  right-drag or Shift-drag to move across\n"
                        + "  scroll to come nearer or go further"));
        all.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setTitle("The renderer's bench");
        stage.setScene(new Scene(all, WIDTH, HEIGHT));
        arranged.placed(stage);
        stage.setOnCloseRequest(closing -> asked.stop());
        stage.show();
    }

    private String further(final double times) {
        return String.format("The first melon as from %.1f times as far,%neach next as from twice as far again.%n"
                + "Over each, the same melon really that far off.", times);
    }

    /** Told how long a frame takes, in thousandths of a second. From any thread. */
    public void told(final double millis) {
        final String said = String.format("A frame: %.1f ms (%.0f a second)", millis, 1000 / millis);
        Platform.runLater(() -> Optional.ofNullable(taken.get()).ifPresent(time -> time.setText(said)));
    }

    /** Told the way the subject is looked at now. From any thread. */
    public void seen(final Orbit orbit) {
        final String said = String.format("Turned %.1f° round, looking %.1f° down, from %.2f m", orbit.yaw(),
                orbit.pitch(), orbit.distance() / METRE);
        Platform.runLater(() -> Optional.ofNullable(looked.get()).ifPresent(view -> view.setText(said)));
    }
}
