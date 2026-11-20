package com.example.fxsuite.bay.world;

import com.example.fxsuite.bay.buildings.Mbfc;
import com.example.fxsuite.bay.buildings.Orq;
import com.example.fxsuite.bay.buildings.AsiaSquare;
import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.buildings.Standing;
import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Player;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.lifts.Built;
import com.example.fxsuite.avatar.sim.Place;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The bay's buildings bench: one of the bay's office towers ({@link Mbfc#standing}, {@link Orq#standing},
 * {@link AsiaSquare#standing}) at a time, where it stands in the bay, turned as it is, as it is built: still, a model to
 * look at from before its front, its lifts standing shut as they are when played; cut away over any of its storeys to
 * be looked into, its glass shown or not, the others shown round it or not; its figures read out. Played, the whole bay
 * laid with every tower standing in it ({@link BayWorld}), the avatar starting before the way in of the one shown: what
 * moves is seen there, not here. Put together of the bench kit's parts, as the tower bench is.
 *
 * <p>{@code -Dbay.tower=3} which tower is shown (1, the first, if none); {@code -Dbay.upto=12} the storey it is cut away
 * over (0 the lobby; none, all of it); {@code -Dbay.glass=false} its glass left off; {@code -Dbay.others=true} the
 * others shown round it; {@code -Dbay.yaw}, {@code -Dbay.pitch}, {@code -Dbay.distance} (m) where it is seen from;
 * {@code -Dbench.play=true} plays it at once; {@code -Dbench.snapshot=out.png} saves the window after
 * {@code -Dbench.settle} ms and exits.
 */
public final class BayBench extends Application {

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Buildings lab: Marina Bay", bus);
    private final ModelView view = new ModelView();
    private final ComboBox<String> chosen = new ComboBox<>();
    private final Spinner<Integer> upTo = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
    private final CheckBox glass = new CheckBox("Its glass");
    private final CheckBox others = new CheckBox("The others round it");
    private final List<Standing> towers = java.util.stream.Stream.of(Mbfc.standing(), Orq.standing(),
            AsiaSquare.standing()).flatMap(List::stream).toList();
    private List<Built> built;
    private final Player player = new Player(this::world, List.of());
    private boolean playWhenShown = Boolean.getBoolean("bench.play");
    /** Which tower the view was last framed on, so it is framed afresh only when another is shown. */
    private int framedOn = -1;
    /** Each tower built whole, once, for the bay played; on the workshop's worker only. */
    private List<Built> whole;
    /** Each tower whole drawn where it stands, made when first wanted; on the workshop's worker only. */
    private ModelView.Model[] drawnWhole;

    /** What is asked: which tower, the storey it is cut away over, whether its glass is shown, whether the others are. */
    private record Asked(int tower, int upTo, boolean glass, boolean others) {
    }

    /** What the workshop made: each tower as built, whole, to play; what is drawn of what was asked; it as built so. */
    private record Made(List<Built> built, List<ModelView.Model> models, Asked asked, Built shown) {
    }

    private final Workshop<Asked, Made> workshop = new Workshop<>("bay", bus, Asked.class, this::make);

    @Override
    public void start(final Stage stage) {
        bus.subscribe(Made.class, this::show, Bus.SCREEN);
        final Button play = new Button("Play");
        play.setStyle("-fx-font-weight: bold;");
        play.setOnAction(e -> player.play());
        shell.bar(List.of(play));
        chosen.getItems().setAll(towers.stream().map(t -> t.tower().name()).toList());
        chosen.getSelectionModel().select(Math.clamp(Integer.getInteger("bay.tower", 1), 1, towers.size()) - 1);
        glass.setSelected(!"false".equalsIgnoreCase(System.getProperty("bay.glass")));
        others.setSelected(Boolean.getBoolean("bay.others"));
        upTo.setEditable(true);
        upTo.setPrefWidth(110);
        fitUpTo(Integer.getInteger("bay.upto", Integer.MAX_VALUE));
        chosen.valueProperty().addListener((o, was, now) -> {
            fitUpTo(Integer.MAX_VALUE);
            request();
        });
        upTo.valueProperty().addListener((o, was, now) -> request());
        glass.selectedProperty().addListener((o, was, now) -> request());
        others.selectedProperty().addListener((o, was, now) -> request());
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.addRow(0, new Label("Tower"), chosen);
        grid.addRow(1, new Label("Up to storey"), upTo);
        grid.add(glass, 1, 2);
        grid.add(others, 1, 3);
        final VBox looking = new VBox(10, grid);
        looking.setPadding(new Insets(8));
        final VBox playing = new VBox(10, new Label("Played, the bay starting before the tower shown"),
                player.panel());
        playing.setPadding(new Insets(8));
        final TabPane tabs = new TabPane(new Tab("Look", looking), new Tab("Play", playing));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // never squeezed out by the figures under it
        tabs.setMinHeight(Region.USE_PREF_SIZE);
        shell.side(new Label("The bay's towers"), tabs, 420);
        shell.middle(view.node());
        request();
        shell.show(stage, 1440, 900);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /** Which tower is shown. */
    private int tower() {
        return Math.max(0, chosen.getSelectionModel().getSelectedIndex());
    }

    /** The storeys to cut away over fitted to the tower shown, from its lobby to its last, set to {@code storey} or its last. */
    private void fitUpTo(final int storey) {
        final int last = towers.get(tower()).tower().storeys() - 1;
        final var range = (SpinnerValueFactory.IntegerSpinnerValueFactory) upTo.getValueFactory();
        range.setMax(last);
        range.setValue(Math.clamp(storey, 0, last));
    }

    private void request() {
        final Integer storey = upTo.getValue();
        bus.publish(new Asked(tower(), storey == null ? 0 : storey, glass.isSelected(), others.isSelected()));
    }

    /** The bay with every tower, to play, starting before the one shown; none until they are built. */
    private BayWorld world() {
        return built == null ? null : new BayWorld(towers, built, tower());
    }

    /**
     * On the workshop's worker: every tower built whole, once, for the bay played; the one asked for built as asked,
     * drawn where it stands; the others round it drawn whole if asked, each the first time it is.
     */
    private Made make(final Asked a) {
        if (whole == null) {
            whole = towers.stream().map(Standing::built).toList();
            drawnWhole = new ModelView.Model[towers.size()];
        }
        final Standing t = towers.get(a.tower());
        final OfficeTower tower = t.tower();
        final boolean all = a.upTo() >= tower.storeys() - 1 && a.glass();
        final Built b = all ? whole.get(a.tower()) : tower.built(a.upTo(), a.glass());
        final List<ModelView.Model> drawn = new ArrayList<>();
        drawn.add(all ? drawnWhole(a.tower()) : ModelView.Model.of(t.placed(still(tower, b, a.upTo()))));
        if (a.others()) {
            for (int k = 0; k < towers.size(); k++) {
                if (k != a.tower()) {
                    drawn.add(drawnWhole(k));
                }
            }
        }
        return new Made(whole, drawn, a, b);
    }

    /** Tower {@code k} whole, drawn where it stands, its landings' doors shut: made the first time it is wanted. */
    private ModelView.Model drawnWhole(final int k) {
        if (drawnWhole[k] == null) {
            final Standing t = towers.get(k);
            drawnWhole[k] = ModelView.Model.of(t.placed(still(t.tower(), whole.get(k), t.tower().storeys())));
        }
        return drawnWhole[k];
    }

    /**
     * What is drawn of {@code tower}, still, built as {@code built} was, up to storey {@code upTo}: what it draws, and
     * its landings' doors standing shut as far up as it is drawn, none over the floor it is cut away under.
     */
    static List<Solid> still(final OfficeTower tower, final Built built, final int upTo) {
        final double cut = upTo >= tower.storeys() - 1 ? Double.POSITIVE_INFINITY : tower.levels().get(upTo + 1);
        final List<Solid> still = new ArrayList<>(built.drawn());
        tower.landingsShut().stream().filter(s -> s.corners().stream().allMatch(p -> p.up() <= cut + 1))
                .forEach(still::add);
        return still;
    }

    /**
     * On the screen's thread: what was made shown, its figures read out; looked at from before its front, on level
     * ground at its foot, when another tower is shown; played if asked.
     */
    private void show(final Made m) {
        built = m.built();
        view.show(m.models());
        final int k = m.asked().tower();
        bus.publish(new Bus.Figures(figures(k, m.shown())));
        bus.publish(new Bus.Problems(""));
        if (framedOn != k) {
            framedOn = k;
            final Standing t = towers.get(k);
            final Built b = m.shown();
            final double[] w = t.world(b.middle()[0], b.middle()[2]);
            view.ground(t.at().x(), t.at().height(), t.at().z(), 100_000);
            view.lookAt(w[0], t.at().height() + b.middle()[1], w[1]);
            view.from("bay", b.yaw() + t.at().yaw(), 20, b.size() * 1.8 / 100 + 10);
            view.zoom(300, 300_000);
        }
        if (playWhenShown) {
            playWhenShown = false;
            player.play();
        }
    }

    /** Tower {@code k}'s figures as {@code b} built it, where it stands; the project's office, if it is in it. */
    private String figures(final int k, final Built b) {
        final Standing t = towers.get(k);
        final Place at = t.at();
        final String office = ProjectOffice.BAY.tower().name().equals(t.tower().name())
                ? String.format("%n%n%s", ProjectOffice.BAY.said()) : "";
        return String.format(Locale.ROOT, "%s at %.0f, %.0f m, its foot %.1f m up, turned %.1f°%n%s%s",
                t.tower().name(), at.x() / 100, at.z() / 100, at.height() / 100, at.yaw(), b.figures(), office);
    }
}
