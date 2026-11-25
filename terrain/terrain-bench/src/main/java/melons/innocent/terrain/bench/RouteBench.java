package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.bench.Arranged;
import melons.innocent.terrain.drawn.TiledLand;
import melons.innocent.terrain.drawn.Painting;
import melons.innocent.terrain.drawn.ByMaterials;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Seen;
import melons.innocent.terrain.tiles.Choice;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.Tiling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The camera sent along a route over the land, at a speed, the land drawn in tiles around it as it goes and built
 * ahead of it against its coming, so how far ahead is enough at a speed can be tried without a game: the gaps, tiles
 * that should be shown and are not drawn yet, counted as it goes. The route is made on the console's map, waypoint by
 * waypoint, each at its own height; the camera faces the way the route goes, the mouse dragged in the card's window
 * turning it round and tipping it from there. Its land is the country its properties ask for ({@link Countries}),
 * seen at a scale, {@code -Dbench.seen=} (metres), 10 m at first, the same for every tile, its features in the lab's
 * materials.
 *
 * <p>For a run with nobody watching: {@code -Dbench.route=} a route, so many metres across, along and up for each
 * waypoint, {@code x,z,h;x,z,h;...}; {@code -Dbench.go=true} sets the camera going at once, {@code -Dbench.speed=}
 * (metres a second) and {@code -Dbench.ahead=} (seconds) how fast and how far ahead; {@code -Dbench.settle=} so many
 * thousandths of a second ends it, telling how far the camera came and the most gaps there were, and
 * {@code -Dbench.snapshot=} a file saves the picture as it ends.
 */
public final class RouteBench {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 720;
    private static final int LEFT = 40;
    private static final int TOP = 40;
    private static final int BESIDE = 380;

    /** How its two windows are arranged over the screens: as they always were, until it runs. */
    private Arranged arranged = new Arranged(false, LEFT, TOP, BESIDE);
    private static final int EVENED = 30;
    private static final int TOLD = 5;
    private static final int MAP = 300;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double FIELD = 50;
    private static final double NEAR = 50;
    /** How far the camera goes before the tiles around it are chosen again, in centimetres. */
    private static final double RECHOOSE = 2_000;
    /** How many spots ahead of the camera tiles are wanted around. */
    private static final int SPOTS = 4;
    private static final double TURNED = 0.4;
    private static final double STEEPEST = 89;

    private final Laid laid = new Laid();
    private final Daylight day = new Daylight();
    private final LandFeatures features;
    /** The ground as the tiles are made of it: seen at one scale, so tiles of every level meet. */
    private final Ground seen;
    private final Painting painting;
    private final Choice choice;
    private final Steering steering;
    private Optional<Route> route = Optional.empty();
    private double travelled;
    private double turned;
    private double down = 12;
    private Flight flight = new Flight(0, -30_000, 12_000, 0, 12, 1);
    private double[] chosenAt = {Double.NaN, Double.NaN};
    private int worst;
    private int staleWorst;
    /** Whether every tile shown has been drawn at some time: gaps are counted only after, not while it first fills. */
    private boolean filled;

    /** A route bench over {@code features}, drawn as {@code painting} says. */
    public RouteBench(final LandFeatures features, final Painting painting) {
        this.features = features;
        this.seen = new Seen(features.ground(), METRE * said("bench.seen", 10));
        this.painting = painting;
        final Tiling tiling = new Tiling(features.ground().extent(), 32);
        this.choice = new Choice(tiling, 1.5, tiling.level(150));
        this.steering = new Steering(METRE * said("bench.speed", 30), said("bench.ahead", 4), 5 * METRE);
    }

    public static void main(final String[] args) throws IOException {
        final LandFeatures features = Countries.INSTANCE.chosen();
        new RouteBench(features, new ByMaterials(features)).run();
    }

    /** Opened, until a window is closed or the time asked for is up. */
    public void run() throws IOException {
        given();
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Route bench, drawn on " + card.name(), WIDTH, HEIGHT);
             TiledLand land = new TiledLand(card, laid, painting, choice.tiling(), seen, 1)) {
            final RouteControls controls = new RouteControls(steering, new RouteMap(new MapPicture(
                    features.ground(), painting, MAP), seen), 120 * METRE);
            // a run nobody attends keeps the windows where they have always been, and as big
            arranged = new Arranged(Long.getLong("bench.settle", -1) < 0, LEFT, TOP, BESIDE);
            Platform.startup(() -> controls.shown(arranged));
            arranged.shown(window, desk);
            drawn(desk, card, window, land, controls);
            final String snapshot = System.getProperty("bench.snapshot", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, land);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame: the camera moved on along the route, the tiles chosen again as it goes, gaps counted. */
    private void drawn(final Desk desk, final Card card, final Window window, final TiledLand land,
                       final RouteControls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        long before = began;
        int frames = 0;
        while (window.open() && !steering.stopped()) {
            desk.heard();
            arranged.heard(window, desk);
            final long now = System.nanoTime();
            moved(window.mouse(), (now - before) / BILLION);
            before = now;
            chosen(land);
            land.kept();
            filled |= land.gaps() == 0;
            if (steering.going() && filled) {
                worst = Math.max(worst, land.gaps());
                staleWorst = Math.max(staleWorst, land.stale());
            }
            frame(window, land);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (++frames % TOLD == 0) {
                controls.seen(flight, travelled, route.map(Route::length).orElse(0.0));
            }
            if (frames % EVENED == 0) {
                controls.tiles(land, worst, (now - last) / MILLION / EVENED);
                last = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle) {
                System.out.printf("Came %.0f m; holes %d at most, stale %d at most; %d shown, %d built%n",
                        travelled / METRE, worst, staleWorst, land.shown(), land.built());
                return;
            }
        }
    }

    /** The camera moved on {@code seconds} along the route, if going, and looked round as the mouse was dragged. */
    private void moved(final Moved mouse, final double seconds) {
        route = steering.route();
        if (steering.backWanted()) {
            travelled = 0;
            worst = 0;
            staleWorst = 0;
        }
        if (mouse.dragged()) {
            turned += mouse.across() * TURNED;
            down = Math.clamp(down + mouse.down() * TURNED, -STEEPEST, STEEPEST);
        }
        if (route.isEmpty()) {
            flight = new Flight(flight.x(), flight.z(), flight.height(), turned, down, 1);
            return;
        }
        final Route r = route.get();
        if (steering.going()) {
            travelled = Math.min(r.length(), travelled + steering.speed() * Math.min(seconds, 0.1));
        }
        travelled = Math.min(travelled, r.length());
        final Waypoint at = r.at(travelled);
        final double clear = steering.clearance();
        final double up = Double.isNaN(clear) ? at.height()
                : Math.max(at.height(), seen.height(at.x(), at.z()) + clear);
        flight = new Flight(at.x(), at.z(), up, r.heading(travelled) + turned, down, 1);
    }

    /** The tiles around the camera chosen again once it has gone far enough, and those ahead of it wanted built. */
    private void chosen(final TiledLand land) {
        if (Math.hypot(flight.x() - chosenAt[0], flight.z() - chosenAt[1]) < RECHOOSE) {
            return;
        }
        chosenAt = new double[] {flight.x(), flight.z()};
        final List<Chosen> shown = choice.around(flight.x(), flight.z());
        final List<double[]> spots = new ArrayList<>();
        for (int k = 1; k <= SPOTS; k++) {
            final double ahead = steering.speed() * steering.ahead() * k / SPOTS;
            if (steering.along() && route.isPresent()) {
                final Waypoint at = route.get().at(travelled + ahead);
                spots.add(new double[] {at.x(), at.z()});
            } else {
                final double round = Math.toRadians(flight.yaw() - turned);
                spots.add(new double[] {flight.x() + Math.sin(round) * ahead, flight.z() + Math.cos(round) * ahead});
            }
        }
        land.want(shown, steering.ahead() > 0 ? new Ahead(choice).keys(spots, shown) : List.of());
    }

    /** One frame of the land, the camera where it is, drawn on {@code target}. */
    private void frame(final Target target, final TiledLand land) {
        final Camera camera = flight.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> land.drawn(pass, flight.land(), camera));
    }

    /** The route the bench's properties give, if any, and whether to set going at once. */
    private void given() {
        final String written = System.getProperty("bench.route", "");
        if (!written.isEmpty()) {
            final List<Waypoint> points = new ArrayList<>();
            for (final String point : written.split(";")) {
                final String[] xyz = point.split(",");
                points.add(new Waypoint(METRE * Double.parseDouble(xyz[0].trim()),
                        METRE * Double.parseDouble(xyz[1].trim()), METRE * Double.parseDouble(xyz[2].trim())));
            }
            steering.route(Optional.of(new Route(points)));
            steering.going(Boolean.getBoolean("bench.go"));
        }
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
