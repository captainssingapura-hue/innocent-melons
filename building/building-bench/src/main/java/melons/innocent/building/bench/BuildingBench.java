package melons.innocent.building.bench;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Spec;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Made;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import melons.innocent.forms.Facets;

import java.util.List;
import java.util.Locale;

/**
 * The buildings bench. On the left, what can be built, found in a tree of the taxonomy of what is made
 * ({@link ThingTree}), each thing by its default implementation ({@link Catalogue}); under it, the one picked's
 * specification, set field by field ({@link SpecForm}); then how finely it is drawn, its figures and what is wrong with
 * it. In the middle, it drawn by JavaFX 3D as one object ({@link BuildingView}). A button opens it in a window of the
 * card's, drawn by Vulkan as finely as it is built ({@link Inspector}). No game engine. The thread it begins on serves
 * the card's windows; JavaFX's runs the rest. Closing the bench ends it.
 */
public final class BuildingBench {

    private static final double M = 100;
    /** How finely it may be drawn here: the smallest solid drawn, in centimetres, by name. */
    private static final List<String> FINENESS = List.of("coarse, 50 cm", "medium, 10 cm", "fine, everything");
    private static final double[] FINEST = {50, 10, 0};

    private final Catalogue catalogue;
    private final Inspector inspector;
    private final BuildingView view = new BuildingView(900, 700);
    private final Label figures = new Label();
    private final ComboBox<String> fineness = new ComboBox<>();
    private final SpecForm form = new SpecForm(this::built);
    private Offered picked;
    private Structure structure;

    /** A bench offering {@code catalogue}'s things, looked into closely by {@code inspector}. */
    public BuildingBench(final Catalogue catalogue, final Inspector inspector) {
        this.catalogue = catalogue;
        this.inspector = inspector;
    }

    public static void main(final String[] args) throws InterruptedException {
        final Inspector inspector = new Inspector();
        final Catalogue catalogue = new Catalogue();
        Platform.startup(() -> new BuildingBench(catalogue, inspector).shown());
        inspector.served();
        Platform.exit();
    }

    /** Made and shown: on JavaFX's thread. */
    public void shown() {
        final ThingTree tree = new ThingTree(Made.TREE, catalogue, this::picked);
        fineness.getItems().setAll(FINENESS);
        fineness.getSelectionModel().select(1);
        fineness.setOnAction(event -> drawn());
        final Button inspect = new Button("Inspect in Vulkan");
        inspect.setOnAction(event -> {
            if (structure != null) {
                inspector.ask(picked.title(), structure);
            }
        });
        figures.setWrapText(true);
        final VBox left = new VBox(8, tree.node(), new TitledPane("Specification", form.node()),
                new HBox(8, new Label("Drawn"), fineness), figures, inspect);
        VBox.setVgrow(left.getChildren().getFirst(), Priority.ALWAYS);
        left.setPadding(new Insets(10));
        left.setPrefWidth(340);
        final BorderPane pane = new BorderPane(view.node(), null, null, null, left);
        final Stage stage = new Stage();
        stage.setTitle("Buildings bench");
        stage.setScene(new Scene(pane));
        stage.setOnHidden(event -> inspector.end());
        stage.show();
        tree.picked(System.getProperty("bench.pick", ""));
        snapshot(stage);
    }

    /**
     * For a run with nobody watching: {@code -Dbench.snapshot=} a file, the bench saved to it once it has settled, and
     * then closed; or, with {@code -Dbench.inspect=true}, what is picked looked into, and that saved as the inspector
     * says, before it all ends.
     */
    private void snapshot(final Stage stage) {
        final String shot = System.getProperty("bench.snapshot", "");
        if (!shot.isEmpty()) {
            final PauseTransition settling = new PauseTransition(Duration.seconds(2));
            settling.setOnFinished(event -> {
                new Snapshot().saved(stage.getScene(), shot);
                if (Boolean.getBoolean("bench.inspect") && structure != null) {
                    inspector.ask(picked.title(), structure);
                } else {
                    stage.close();
                }
            });
            settling.play();
        }
    }

    /** {@code offered} picked: its sample built and shown, its specification in the form. */
    private void picked(final Offered offered) {
        picked = offered;
        form.show(offered.sample());
        built(offered.sample());
    }

    /** The thing picked built from {@code spec}, and drawn. */
    private void built(final Spec<?, ?, ?> spec) {
        try {
            structure = picked.built(spec);
            drawn();
        } catch (final RuntimeException e) {
            figures.setText("Not built: " + e.getMessage());
        }
    }

    /** The building drawn as finely as chosen, and its figures told. */
    private void drawn() {
        if (structure == null) {
            return;
        }
        final Facets facets = new Facets(structure.solids(), FINEST[fineness.getSelectionModel().getSelectedIndex()]);
        view.show(facets);
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (final Solid solid : structure.solids()) {
            solid.corners().forEach(p -> {
                final double[] at = {p.across(), p.along(), p.up()};
                for (int k = 0; k < 3; k++) {
                    low[k] = Math.min(low[k], at[k]);
                    high[k] = Math.max(high[k], at[k]);
                }
            });
        }
        final List<String> problems = structure.problems();
        figures.setText(String.format(Locale.ROOT, "%s%n%d parts, %d drawn as %d triangles%n%.1f m across, %.1f m"
                + " along, %.1f m high%n%s", picked.title(), structure.solids().size(),
                facets.sheets().values().stream().mapToInt(sheet -> sheet.solids().length / 12).sum(),
                facets.triangles(), (high[0] - low[0]) / M, (high[1] - low[1]) / M, (high[2] - low[2]) / M,
                problems.isEmpty() ? "Nothing wrong with it." : "Wrong: " + String.join("; ", problems)));
    }
}
