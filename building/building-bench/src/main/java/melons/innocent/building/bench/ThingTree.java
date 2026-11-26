package melons.innocent.building.bench;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * What can be built, as a tree of the taxonomy of what is made: only the branches that lead to a thing the catalogue
 * offers, each thing a leaf under its own lineage; and above it a field to search it by, a thing kept if its name or
 * what it means has what is typed in it, and the branches it is under kept with it. Picking a thing offered tells
 * whoever asked. Made and used on JavaFX's thread.
 */
public final class ThingTree {

    private final Taxonomy taxonomy;
    private final Catalogue catalogue;
    private final TreeView<Node> view = new TreeView<>();
    private final TextField search = new TextField();

    /** The tree of {@code taxonomy}'s things {@code catalogue} offers, each picked told to {@code picked}. */
    public ThingTree(final Taxonomy taxonomy, final Catalogue catalogue, final Consumer<Offered> picked) {
        this.taxonomy = taxonomy;
        this.catalogue = catalogue;
        search.setPromptText("Search what can be built");
        search.textProperty().addListener((property, was, now) -> filled(now));
        view.setShowRoot(false);
        view.setCellFactory(tree -> new TreeCell<>() {
            @Override
            protected void updateItem(final Node node, final boolean empty) {
                super.updateItem(node, empty);
                setText(empty || node == null ? null : spoken(node));
            }
        });
        view.getSelectionModel().selectedItemProperty().addListener((property, was, now) -> {
            if (now != null && now.getValue() instanceof Leaf<?> leaf) {
                catalogue.of(leaf).ifPresent(picked);
            }
        });
        filled("");
    }

    /** The search field over the tree, the tree taking what room there is. */
    public VBox node() {
        VBox.setVgrow(view, Priority.ALWAYS);
        return new VBox(6, search, view);
    }

    /** The first thing offered whose name begins with {@code name}, picked; or, if none does, the first. */
    public void picked(final String name) {
        final String wanted = name.strip().toLowerCase(Locale.ROOT);
        for (int row = 0; row < view.getExpandedItemCount() && !wanted.isEmpty(); row++) {
            final Node node = view.getTreeItem(row).getValue();
            if (node instanceof Leaf<?> && spoken(node).toLowerCase(Locale.ROOT).startsWith(wanted)) {
                view.getSelectionModel().select(row);
                return;
            }
        }
        first();
    }

    /** The first thing offered, picked. */
    public void first() {
        for (int row = 0; row < view.getExpandedItemCount(); row++) {
            if (view.getTreeItem(row).getValue() instanceof Leaf<?>) {
                view.getSelectionModel().select(row);
                return;
            }
        }
    }

    /** The tree made again of the things offered that {@code text} finds, and the branches each is under. */
    private void filled(final String text) {
        final String wanted = text.strip().toLowerCase(Locale.ROOT);
        final TreeItem<Node> root = new TreeItem<>(taxonomy.root());
        final Map<Node, TreeItem<Node>> items = new HashMap<>(Map.of(taxonomy.root(), root));
        for (final Offered offered : catalogue.offered()) {
            final Leaf<?> thing = offered.thing();
            if (wanted.isEmpty() || found(thing, wanted)) {
                final List<Node> lineage = thing.lineage().reversed();
                TreeItem<Node> under = root;
                for (final Node node : lineage.subList(1, lineage.size())) {
                    final TreeItem<Node> parent = under;
                    under = items.computeIfAbsent(node, n -> {
                        final TreeItem<Node> item = new TreeItem<>(n);
                        item.setExpanded(true);
                        parent.getChildren().add(item);
                        return item;
                    });
                }
            }
        }
        view.setRoot(root);
    }

    /** Whether {@code thing}'s name, or what it means, or any of the branches it is under, has {@code wanted}. */
    private boolean found(final Leaf<?> thing, final String wanted) {
        for (final Node node : thing.lineage()) {
            if (spoken(node).toLowerCase(Locale.ROOT).contains(wanted)
                    || taxonomy.meaning(node).toLowerCase(Locale.ROOT).contains(wanted)) {
                return true;
            }
        }
        return false;
    }

    /** {@code node}'s name as said: {@code FestiveGateway} as {@code Festive gateway}. */
    private String spoken(final Node node) {
        final String words = node.title().replaceAll("([a-z0-9])([A-Z])", "$1 $2").toLowerCase(Locale.ROOT);
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }
}
