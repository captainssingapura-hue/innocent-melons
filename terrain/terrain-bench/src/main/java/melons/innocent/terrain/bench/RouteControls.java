package melons.innocent.terrain.bench;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleFunction;

/**
 * The route bench's console: the map, a click on it adding a waypoint at the height the slider says, or choosing the
 * waypoint clicked near, whose height the slider then sets; the route's profile; sliders for how fast the camera goes
 * and how far ahead tiles are built; whether ahead is along the route or straight on, and whether the camera keeps
 * clear of the ground; buttons to go and pause, to go back to the start, to take the last waypoint off and to clear
 * them all; and how far the camera has come, how many tiles are wanted, built and drawn, and how many gaps there are.
 * The route is kept here, on JavaFX's thread, and told to the bench whole each time it changes.
 */
public final class RouteControls {

    private static final double WIDTH = 360;
    private static final double GAP = 6;
    private static final double METRE = 100;
    /** How near a click must be to a waypoint, in pixels, to choose it. */
    private static final double NEAR = 8;

    private final Steering steering;
    private final RouteMap map;
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final AtomicReference<Label> going = new AtomicReference<>();
    private final AtomicReference<Label> tiles = new AtomicReference<>();
    private int chosen = -1;
    private double height;
    private Optional<Flight> eye = Optional.empty();
    private double travelled;

    /** A console asking {@code steering}, its map {@code map}, new waypoints at first {@code height} high. */
    public RouteControls(final Steering steering, final RouteMap map, final double height) {
        this.steering = steering;
        this.map = map;
        this.height = height;
    }

    /** Made and shown at ({@code x}, {@code y}) on the screen: on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Label how = new Label("Click the map to add a waypoint, or near one to choose it; drag in the card's "
                + "window to look round.");
        how.setWrapText(true);
        steering.route().ifPresent(given -> waypoints.addAll(given.waypoints()));
        map.map().setOnMouseClicked(event -> clicked(event.getX(), event.getY()));
        final Label moving = new Label("Not going yet");
        final Label counted = new Label();
        going.set(moving);
        tiles.set(counted);
        final VBox box = new VBox(GAP, how, map.map(), map.profile(), heights(), speeds(), aheads(), checks(),
                buttons(), moving, counted);
        box.setPadding(new Insets(GAP * 2));
        final Stage stage = new Stage();
        stage.setTitle("Route bench");
        stage.setScene(new Scene(box, WIDTH, -1));
        stage.setX(x);
        stage.setY(y);
        stage.setOnHidden(event -> steering.stop());
        stage.show();
        redrawn();
    }

    private void clicked(final double px, final double py) {
        final MapPicture picture = map.picture();
        chosen = -1;
        for (int k = 0; k < waypoints.size(); k++) {
            final double dx = picture.column(waypoints.get(k).x()) + 0.5 - px;
            final double dy = picture.row(waypoints.get(k).z()) + 0.5 - py;
            if (Math.hypot(dx, dy) <= NEAR) {
                chosen = k;
            }
        }
        if (chosen < 0) {
            final Waypoint added = new Waypoint(picture.x(px - 0.5), picture.z(py - 0.5), height);
            if (waypoints.isEmpty() || waypoints.getLast().over(added) > 0) {
                waypoints.add(added);
                chosen = waypoints.size() - 1;
            }
        }
        told();
    }

    private VBox heights() {
        return slider("Height of the waypoint chosen, or the next", 0.3, Math.log10(3_000),
                Math.log10(height / METRE), tens -> {
                    height = Math.pow(10, tens) * METRE;
                    if (chosen >= 0) {
                        waypoints.set(chosen, waypoints.get(chosen).raised(height));
                        told();
                    }
                    return String.format(Locale.ROOT, "%.0f m", height / METRE);
                });
    }

    private VBox speeds() {
        return slider("Speed", 0, Math.log10(500), Math.log10(steering.speed() / METRE), tens -> {
            steering.speed(Math.pow(10, tens) * METRE);
            return String.format(Locale.ROOT, "%.0f m/s, %.0f km/h", Math.pow(10, tens), Math.pow(10, tens) * 3.6);
        });
    }

    private VBox aheads() {
        return slider("Tiles built ahead", 0, 20, steering.ahead(), seconds -> {
            steering.ahead(seconds);
            return String.format(Locale.ROOT, "%.1f s", seconds);
        });
    }

    /** A slider labelled {@code name}, {@code low} to {@code high}, each move told to {@code moved}, which says. */
    private VBox slider(final String name, final double low, final double high, final double at,
                        final DoubleFunction<String> moved) {
        final Label said = new Label(name + ": " + moved.apply(Math.clamp(at, low, high)));
        final Slider slider = new Slider(low, high, Math.clamp(at, low, high));
        slider.valueProperty().addListener((property, was, now) -> said.setText(name + ": "
                + moved.apply(now.doubleValue())));
        return new VBox(GAP / 2, said, slider);
    }

    private HBox checks() {
        final CheckBox along = new CheckBox("Ahead along the route");
        along.setSelected(steering.along());
        along.setOnAction(event -> steering.along(along.isSelected()));
        final CheckBox clear = new CheckBox("Keep 5 m over the ground");
        clear.setSelected(!Double.isNaN(steering.clearance()));
        clear.setOnAction(event -> steering.clearance(clear.isSelected() ? 5 * METRE : Double.NaN));
        return new HBox(GAP * 2, along, clear);
    }

    private HBox buttons() {
        final Button go = new Button("Go");
        go.setOnAction(event -> {
            steering.going(!steering.going());
            go.setText(steering.going() ? "Pause" : "Go");
        });
        final Button back = new Button("Back to start");
        back.setOnAction(event -> steering.back());
        final Button last = new Button("Take last off");
        last.setOnAction(event -> {
            if (!waypoints.isEmpty()) {
                waypoints.removeLast();
                chosen = -1;
                told();
            }
        });
        final Button clear = new Button("Clear");
        clear.setOnAction(event -> {
            waypoints.clear();
            chosen = -1;
            told();
        });
        return new HBox(GAP, go, back, last, clear);
    }

    /** The route told to the bench, and drawn again. */
    private void told() {
        steering.route(waypoints.size() < 2 ? Optional.empty() : Optional.of(new Route(waypoints)));
        redrawn();
    }

    private void redrawn() {
        map.drawn(waypoints, chosen, eye, travelled);
    }

    /** Where the camera is and how far it has come of {@code length}, told on the card's thread. */
    public void seen(final Flight flight, final double come, final double length) {
        Platform.runLater(() -> {
            eye = Optional.of(flight);
            travelled = come;
            redrawn();
            going.get().setText(String.format(Locale.ROOT, "Come %.0f m of %.0f m, %.0f m up", come / METRE,
                    length / METRE, flight.height() / METRE));
        });
    }

    /** How many tiles are shown, built and drawn, how many gaps now and at most, and a frame's time, told. */
    public void tiles(final TiledLand land, final int worst, final double millis) {
        final String said = String.format(Locale.ROOT, "Tiles: %d shown, %d built, %d drawn%nHoles: %d now, %d at most;"
                + " stale: %d now%nA frame in %.1f ms", land.shown(), land.built(), land.drawn(), land.gaps(), worst,
                land.stale(), millis);
        Platform.runLater(() -> tiles.get().setText(said));
    }
}
