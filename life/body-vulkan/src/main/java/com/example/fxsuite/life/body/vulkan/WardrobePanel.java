package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.space.Vec3;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The body bench's wardrobe, in its console: a box for each kind of thing it may wear, ticked if it wears it; what the
 * armour is made of; its colour, if painted; its visor's colour; the cloth's colour. Each change told to whoever
 * listens.
 */
final class WardrobePanel {

    private static final double GAP = 8;

    private final Map<Leaf<?>, CheckBox> wears = new LinkedHashMap<>();
    private final ChoiceBox<Material> made = new ChoiceBox<>();
    private final ColorPicker colour;
    private final ColorPicker visor;
    private final ColorPicker cloth;

    /** The wardrobe, showing {@code first} to begin with. */
    WardrobePanel(final WornShown first) {
        for (final Leaf<?> k : first.kinds()) {
            final CheckBox box = new CheckBox(k.title());
            box.setSelected(first.wears(k));
            wears.put(k, box);
        }
        made.getItems().addAll(first.offered());
        made.setValue(first.material());
        made.setConverter(new StringConverter<>() {
            @Override
            public String toString(final Material m) {
                return m == null ? "" : m.title();
            }

            @Override
            public Material fromString(final String s) {
                return null;
            }
        });
        colour = picker(first.colour());
        visor = picker(first.visor());
        cloth = picker(first.cloth());
    }

    /** {@code changed} told whenever anything in it changes. */
    void listen(final Runnable changed) {
        wears.values().forEach(box -> box.setOnAction(set -> changed.run()));
        made.valueProperty().addListener((o, was, is) -> changed.run());
        colour.setOnAction(set -> changed.run());
        visor.setOnAction(set -> changed.run());
        cloth.setOnAction(set -> changed.run());
    }

    /** What it shows now. */
    WornShown shown() {
        final List<Leaf<?>> on = new ArrayList<>();
        wears.forEach((k, box) -> {
            if (box.isSelected()) {
                on.add(k);
            }
        });
        return new WornShown(on, made.getValue(), said(colour), said(visor), said(cloth));
    }

    /** Its controls. */
    Node node() {
        final FlowPane boxes = new FlowPane(GAP, GAP / 2);
        boxes.getChildren().addAll(wears.values());
        final GridPane grid = new GridPane();
        grid.setHgap(GAP);
        grid.setVgap(GAP / 2);
        grid.addRow(0, new Label("Armour made of"), made);
        grid.addRow(1, new Label("Its colour, if painted"), colour);
        grid.addRow(2, new Label("Its visor's colour"), visor);
        grid.addRow(3, new Label("The cloth's colour"), cloth);
        return new VBox(GAP / 2, new Label("Wearing"), boxes, grid);
    }

    private ColorPicker picker(final Vec3 c) {
        return new ColorPicker(Color.color(c.x(), c.y(), c.z()));
    }

    private Vec3 said(final ColorPicker p) {
        final Color c = p.getValue();
        return new Vec3(c.getRed(), c.getGreen(), c.getBlue());
    }
}
