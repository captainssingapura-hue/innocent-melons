package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The knobs a head's pieces are adjusted by ({@link Adjust}): which piece, then how far it is moved out, up and ahead,
 * tilted, turned, made bigger, stretched each way and made fuller; each kept for its piece, a pair's for both of it;
 * set back to the reference's at a press. Each turn of a knob said to whoever is told, to put the pieces on afresh.
 */
public final class PieceKnobs {

    /** A knob: what it is called, how far it turns each way, where it rests. */
    private record Knob(String key, String said, double least, double most, double rest) {
    }

    private final List<Knob> knobs = List.of(new Knob("out", "Out, cm", -1, 1, 0), new Knob("up", "Up, cm", -1, 1, 0),
            new Knob("ahead", "Ahead, cm", -1, 1, 0), new Knob("tilt", "Tilted up, °", -20, 20, 0),
            new Knob("turn", "Turned out, °", -30, 30, 0), new Knob("bigger", "Bigger, times", 0.7, 1.3, 1),
            new Knob("wider", "Wider, times", 0.7, 1.4, 1), new Knob("higher", "Higher, times", 0.7, 1.4, 1),
            new Knob("deeper", "Deeper, times", 0.7, 1.4, 1), new Knob("fuller", "Fuller, cm", -0.3, 0.3, 0));
    private final ChoiceBox<Piece> piece = new ChoiceBox<>();
    private final List<Slider> sliders;
    private final List<Label> values;
    private final Map<Piece, double[]> kept = new EnumMap<>(Piece.class);
    private final Runnable told;
    private boolean setting;

    /**
     * Knobs that tell {@code told} when one is turned; turned at first as {@code asked} says, each piece's by
     * semicolons, {@code ears:turn=20,bigger=1.2;nose:tilt=10}, say.
     */
    public PieceKnobs(final Runnable told, final String asked) {
        this.told = told;
        ask(asked);
        sliders = knobs.stream().map(k -> new Slider(k.least(), k.most(), k.rest())).toList();
        values = knobs.stream().map(k -> new Label()).toList();
        piece.getItems().setAll(Piece.values());
        piece.getSelectionModel().selectFirst();
        piece.setOnAction(e -> show());
        for (int i = 0; i < sliders.size(); i++) {
            final int k = i;
            sliders.get(i).valueProperty().addListener((o, was, now) -> turned(k, now.doubleValue()));
        }
        show();
    }

    /** The knobs turned as {@code asked} says. */
    private void ask(final String asked) {
        for (final String one : asked.split(";")) {
            final String[] p = one.split(":");
            if (p.length == 2) {
                final Piece piece = Piece.valueOf(p[0].trim().toUpperCase(Locale.ROOT));
                final double[] v = kept.computeIfAbsent(piece, x -> rest());
                for (final String set : p[1].split(",")) {
                    final String[] kv = set.split("=");
                    for (int i = 0; i < knobs.size(); i++) {
                        v[i] = knobs.get(i).key().equals(kv[0].trim()) ? Double.parseDouble(kv[1]) : v[i];
                    }
                }
            }
        }
    }

    /** How piece {@code p} is adjusted. */
    public Adjust of(final Piece p) {
        final double[] v = kept.computeIfAbsent(p, x -> rest());
        return new Adjust(new V3(v[0], v[1], v[2]), v[3], v[4], v[5], new V3(v[6], v[7], v[8]), v[9]);
    }

    /** The knobs, beside the head. */
    public Node node() {
        final GridPane grid = new GridPane(8, 2);
        for (int i = 0; i < knobs.size(); i++) {
            grid.addRow(i, new Label(knobs.get(i).said()), sliders.get(i), values.get(i));
        }
        final Button back = new Button("As the reference's");
        back.setOnAction(e -> {
            kept.put(piece.getValue(), rest());
            show();
            told.run();
        });
        final VBox box = new VBox(6, new Label("Adjust"), piece, grid, back);
        box.setPadding(new Insets(8, 0, 0, 0));
        return box;
    }

    /** Knob {@code k} of the piece chosen turned to {@code v}: kept, and said. */
    private void turned(final int k, final double v) {
        values.get(k).setText(String.format(Locale.ROOT, "%.2f", v));
        if (setting) {
            return;
        }
        kept.computeIfAbsent(piece.getValue(), x -> rest())[k] = v;
        told.run();
    }

    /** The piece chosen's knobs where it has them, not said. */
    private void show() {
        setting = true;
        final double[] v = kept.computeIfAbsent(piece.getValue(), x -> rest());
        for (int i = 0; i < sliders.size(); i++) {
            sliders.get(i).setValue(v[i]);
            values.get(i).setText(String.format(Locale.ROOT, "%.2f", v[i]));
        }
        setting = false;
    }

    /** Every knob where it rests. */
    private double[] rest() {
        return knobs.stream().mapToDouble(Knob::rest).toArray();
    }
}
