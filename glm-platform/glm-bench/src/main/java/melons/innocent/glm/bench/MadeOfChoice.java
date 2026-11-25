package melons.innocent.glm.bench;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.model.MadeOf;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A window's rows to say what the platform's parts are made of: for each metal its parts are designed in, which metal
 * they are made of instead, titanium wherever aluminium is, say; to begin with as {@code first} says. To be used on
 * JavaFX's thread.
 */
public final class MadeOfChoice {

    private static final double GAP = 8;

    private final List<Material> metals = Arrays.stream(Material.values()).filter(Material::metal).toList();
    private final Map<Material, ComboBox<String>> choices = new EnumMap<>(Material.class);

    /** Rows for each metal in {@code used}, chosen as {@code first} says. */
    public MadeOfChoice(final Collection<Material> used, final MadeOf first) {
        for (final Material designed : metals) {
            if (used.contains(designed)) {
                final ComboBox<String> choice = new ComboBox<>();
                choice.getItems().addAll(metals.stream().map(Material::title).toList());
                choice.getSelectionModel().select(metals.indexOf(first.of(designed)));
                choices.put(designed, choice);
            }
        }
    }

    /** Its rows, {@code changed} told whenever one is changed. */
    public GridPane shown(final Runnable changed) {
        final GridPane rows = new GridPane(GAP, GAP);
        int row = 0;
        for (final Map.Entry<Material, ComboBox<String>> c : choices.entrySet()) {
            c.getValue().setOnAction(set -> changed.run());
            rows.addRow(row++, new Label(c.getKey().title() + ", made of"), c.getValue());
        }
        return rows;
    }

    /** What the parts are made of, as the rows say. */
    public MadeOf madeOf() {
        MadeOf made = new MadeOf();
        for (final Map.Entry<Material, ComboBox<String>> c : choices.entrySet()) {
            made = made.with(c.getKey(), metals.get(c.getValue().getSelectionModel().getSelectedIndex()));
        }
        return made;
    }
}
