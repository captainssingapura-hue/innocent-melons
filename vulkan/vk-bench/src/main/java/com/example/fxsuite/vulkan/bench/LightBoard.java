package com.example.fxsuite.vulkan.bench;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * What sets the light of whatever the card draws, as a part of any JavaFX window beside the card's: whether
 * shadows are drawn, by a box to tick; and the sky, one of two ways. By hand: whether it is night, by a box, and
 * where the sun stands, by two sliders, how high and which way round. Or by the almanac and the clock
 * ({@link SkyBoard}), which then leaves the night's box and the sliders idle.
 *
 * <p>It sets nothing itself: each thing set is asked of {@link Asked}, which the card's thread reads as each frame
 * begins, and {@link Skies} turns into that frame's light and sky. A walk and a bench set their light the same way.
 */
public final class LightBoard {

    private static final double GAP = 10;
    /** The lowest the sun can be set, and the highest, in degrees above level; and all the way round. */
    private static final double LOWEST = 5;
    private static final double OVERHEAD = 90;
    private static final double WHOLE = 360;
    /** The sign for degrees. */
    private static final char DEGREE = 0xb0;

    private final Asked asked;
    private final SkyBoard board;

    /** A board asking {@code asked}. */
    public LightBoard(final Asked asked) {
        this.asked = asked;
        this.board = new SkyBoard(asked);
    }

    /** The board, made. To be called on JavaFX's thread. */
    public VBox shown() {
        final CheckBox shadows = new CheckBox("Shadows");
        shadows.setSelected(asked.shadowed());
        shadows.setOnAction(ticked -> asked.shadowed(shadows.isSelected()));
        final CheckBox night = new CheckBox("Night");
        night.setSelected(asked.night());
        night.setOnAction(ticked -> asked.night(night.isSelected()));
        final Label stands = new Label(standing(asked.sun()));
        final Slider high = new Slider(LOWEST, OVERHEAD, asked.sun().high());
        final Slider round = new Slider(0, WHOLE, asked.sun().round());
        for (final Slider slider : List.of(high, round)) {
            slider.valueProperty().addListener((moved, was, now) -> {
                asked.sun(high.getValue(), round.getValue());
                stands.setText(standing(asked.sun()));
            });
        }
        // set by the almanac and the clock, the sky is not set by hand: what sets it by hand then stands idle
        final SkyBoard.Shown sky = board.shown();
        for (final Node byHand : List.of(night, high, round)) {
            byHand.disableProperty().bind(sky.clocked().selectedProperty());
        }
        return new VBox(GAP, shadows, night, stands, new Label("How high the sun is, or by night the moon:"), high,
                new Label("Which way round it stands:"), round, new Separator(), sky.all());
    }

    /** Where the sun stands, said in a line. */
    private String standing(final Asked.Sun sun) {
        return String.format("The sun: %.0f%c up, %.0f%c round", sun.high(), DEGREE, sun.round(), DEGREE);
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        board.told(said);
    }
}
