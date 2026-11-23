package com.example.fxsuite.life.body;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * A bench's choice of how deep each group of the body's parts is drawn ({@link Layers}): for each, as the whole is
 * drawn, or a layer of its own, the raw skull on the skin, say; first as {@code -Dmotion.head}, {@code -Dmotion.neck},
 * {@code -Dmotion.trunk}, {@code -Dmotion.arms}, {@code -Dmotion.legs} say (skeleton, muscles, skin, hidden).
 */
public final class LayerChoice {

    /** What a group drawn as the whole is drawn as says. */
    private static final String WHOLE = "As drawn";

    private final Map<BodyGroup, ChoiceBox<String>> boxes = new EnumMap<>(BodyGroup.class);
    private final GridPane grid = new GridPane();

    /** A choice for each group, {@code changed} run when any changes. */
    public LayerChoice(final Runnable changed) {
        grid.setHgap(8);
        grid.setVgap(4);
        int row = 0;
        for (final BodyGroup g : BodyGroup.values()) {
            final ChoiceBox<String> box = new ChoiceBox<>();
            box.getItems().add(WHOLE);
            for (final Layer l : Layer.values()) {
                box.getItems().add(l.label());
            }
            box.setValue(asked(g));
            box.valueProperty().addListener((o, was, is) -> changed.run());
            boxes.put(g, box);
            grid.addRow(row++, new Label(g.label()), box);
        }
    }

    /** The choice, to put in the bench. */
    public Node node() {
        return grid;
    }

    /** How deep each group is to be drawn, those chosen otherwise than the whole. */
    public Layers layers() {
        final Map<BodyGroup, Layer> chosen = new EnumMap<>(BodyGroup.class);
        boxes.forEach((g, box) -> {
            for (final Layer l : Layer.values()) {
                if (l.label().equals(box.getValue())) {
                    chosen.put(g, l);
                }
            }
        });
        return new Layers(chosen);
    }

    /** The label of the layer {@code -Dmotion.<group>} asks for {@code g}: as the whole, if none or none known. */
    private String asked(final BodyGroup g) {
        final String s = System.getProperty("motion." + g.name().toLowerCase(Locale.ROOT), "").toLowerCase(Locale.ROOT);
        for (final Layer l : Layer.values()) {
            if (!s.isEmpty() && l.name().toLowerCase(Locale.ROOT).startsWith(s)) {
                return l.label();
            }
        }
        return WHOLE;
    }
}
