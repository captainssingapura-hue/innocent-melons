package com.example.fxsuite.vulkan.bench;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The part of the JavaFX window that sets the sky by the almanac and the clock: a box to tick for the sun and the
 * moon to stand where they truly do; where on the Earth, so far north and so far east; the date and the hour to
 * begin from, and whose clocks they are told by; a button that begins the clock again from there; and a slider
 * for how fast the clock runs. It shows the moment the clock has come to, and where the sun and the moon stand.
 *
 * <p>What is typed is taken up only when the button is pressed; something that cannot be read is said beside the
 * button and changes nothing.
 */
public final class SkyBoard {

    private static final double GAP = 6;
    private static final double NARROW = 110;

    private final Asked asked;
    /** Where the moment is shown, once the window is: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Label> moment = new AtomicReference<>();

    /** What is shown: all of it, to be set in the window; and the box that is ticked for the sky to go by it. */
    public record Shown(VBox all, CheckBox clocked) {
    }

    /** A board asking {@code asked}. */
    public SkyBoard(final Asked asked) {
        this.asked = asked;
    }

    /** The board, made. To be called on JavaFX's thread. */
    public Shown shown() {
        final Asked.Almanac begun = asked.almanac();
        final CheckBox clocked = new CheckBox("Sun and moon by place, date and clock");
        clocked.setSelected(asked.clocked());
        clocked.setOnAction(ticked -> asked.clocked(clocked.isSelected()));
        final Label now = new Label("The clock has not begun\n ");
        moment.set(now);
        final TextField place = new TextField(begun.latitude() + ", " + begun.longitude());
        final TextField when = new TextField(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm")
                .format(begun.begins().atZone(begun.zone())));
        final TextField zone = new TextField(begun.zone().getId());
        final Label refused = new Label("");
        final Button begin = new Button("Begin from there");
        begin.setOnAction(pressed -> {
            try {
                asked.almanac(asked.almanac().told(place.getText(), when.getText(), zone.getText()));
                refused.setText("");
            } catch (final RuntimeException unread) {
                refused.setText("Not read: " + unread.getMessage());
            }
        });
        final Pace first = Pace.STILL.nearest(asked.quickened());
        final Label pace = new Label("The clock runs " + first.said());
        final Slider quickened = new Slider(0, Pace.values().length - 1, first.ordinal());
        quickened.setMajorTickUnit(1);
        quickened.setMinorTickCount(0);
        quickened.setSnapToTicks(true);
        quickened.valueProperty().addListener((moved, was, set) -> {
            final Pace chosen = Pace.values()[(int) Math.round(set.doubleValue())];
            asked.quickened(chosen.times());
            pace.setText("The clock runs " + chosen.said());
        });
        return new Shown(new VBox(GAP, clocked, now, row("North, east:", place), row("Date and hour:", when),
                row("By the clocks of:", zone), new HBox(GAP, begin, refused), pace, quickened), clocked);
    }

    /** A thing to type in, with what it is said before it. */
    private HBox row(final String what, final TextField typed) {
        final Label said = new Label(what);
        said.setMinWidth(NARROW);
        return new HBox(GAP, said, typed);
    }

    /** Told the moment the clock has come to and where the sun and the moon stand, in words. From any thread. */
    public void told(final String said) {
        Platform.runLater(() -> Optional.ofNullable(moment.get()).ifPresent(now -> now.setText(said)));
    }
}
