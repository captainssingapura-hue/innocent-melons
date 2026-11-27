package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.space.Vec3;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;

/**
 * The avatar bench's painting, in its JavaFX window ({@link LineupConsole}): for the picked figure, a whole look to
 * begin from ({@link Looks}); and what each of its pieces is of drawn in, a row to each, its shell, its panels, its
 * visor, its frame, its housings, its rods, as it has them: the material, and its colour, chosen only for a paint, a
 * bare metal, enamel, rubber or lit glass keeping its own. Each change said at once to whoever listens. Made once
 * JavaFX has begun.
 */
final class PaintPanel {

    private static final double GAP = 8;

    private final VBox node = new VBox(GAP / 2);
    private final ChoiceBox<Looks> looks = new ChoiceBox<>();
    private final GridPane rows = new GridPane();
    private Consumer<Paintwork> listener = work -> { };
    private Paintwork work = new Paintwork();
    private List<Paint> roles = List.of();
    /** Whether the rows are being set to a figure's paintwork, not changed by hand. */
    private boolean setting;

    PaintPanel() {
        rows.setHgap(GAP);
        rows.setVgap(GAP / 2);
        looks.getItems().addAll(Looks.values());
        looks.setOnAction(e -> {
            if (!setting && looks.getValue() != null) {
                work = looks.getValue().work();
                shown(roles, work);
                listener.accept(work);
            }
        });
        final GridPane top = new GridPane();
        top.setHgap(GAP);
        top.addRow(0, new Label("A whole look"), looks);
        node.getChildren().addAll(top, rows);
    }

    /** Where it is laid out. */
    Node node() {
        return node;
    }

    /** Each change to the paintwork told to {@code to}. */
    void listen(final Consumer<Paintwork> to) {
        listener = to;
    }

    /** A figure's pieces of each of {@code of} shown drawn as {@code paintwork} has them. */
    void shown(final List<Paint> of, final Paintwork paintwork) {
        setting = true;
        roles = List.copyOf(of);
        work = paintwork;
        rows.getChildren().clear();
        int row = 0;
        for (final Paint p : roles) {
            rows.addRow(row++, new Label(p.label()), material(p), colour(p));
        }
        looks.setValue(null);
        setting = false;
    }

    /** What {@code p}'s pieces are made of, to be chosen of all that is offered. */
    private ChoiceBox<Material> material(final Paint p) {
        final ChoiceBox<Material> box = new ChoiceBox<>();
        box.getItems().addAll(work.offered());
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(final Material m) {
                return m == null ? "" : said(m);
            }

            @Override
            public Material fromString(final String s) {
                return null;
            }
        });
        box.setValue(work.of(p).material());
        box.setOnAction(e -> changed(p, new Paintwork.Coat(box.getValue(), work.of(p).colour())));
        return box;
    }

    /** The colour {@code p}'s pieces are painted, to be chosen if they are of a paint. */
    private ColorPicker colour(final Paint p) {
        final Vec3 c = work.of(p).colour();
        final ColorPicker picker = new ColorPicker(Color.color(c.x(), c.y(), c.z()));
        picker.setDisable(!work.of(p).painted());
        picker.setOnAction(e -> {
            final Color v = picker.getValue();
            changed(p, new Paintwork.Coat(work.of(p).material(), new Vec3(v.getRed(), v.getGreen(), v.getBlue())));
        });
        return picker;
    }

    /** {@code p}'s pieces drawn in {@code coat} from now on: the rows shown afresh, and said. */
    private void changed(final Paint p, final Paintwork.Coat coat) {
        if (setting) {
            return;
        }
        work = work.with(p, coat);
        shown(roles, work);
        listener.accept(work);
    }

    /** What {@code m} is called, in words: {@code GlossPaint} is "Gloss paint". */
    private String said(final Material m) {
        final String words = m.getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2")
                .toLowerCase(java.util.Locale.ROOT);
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }
}
