package melons.innocent.terrain.bench;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import melons.innocent.terrain.Ground;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The route bench's map and the route's profile under it. On the map, north up: the land's picture, the route drawn
 * smoothly through its waypoints, each waypoint marked and its height written by it, the one chosen ringed, and the
 * camera marked where it is, a line the way it looks. Under it, the route's profile: how high the route goes along
 * its length, and how high the ground under it is, so where the camera would come low over the ground or into it
 * shows. Made and used on JavaFX's thread only.
 */
public final class RouteMap {

    private static final double PROFILE = 80;
    private static final double MARK = 4;
    private static final double LINE = 16;
    private static final double METRE = 100;
    private static final int ALONG = 160;
    private static final double FONT = 10;

    private final MapPicture picture;
    private final Ground ground;
    private final Canvas map;
    private final Canvas profile;
    private final WritableImage image;

    /** A map of {@code picture}, the profile's ground read from {@code ground}. */
    public RouteMap(final MapPicture picture, final Ground ground) {
        this.picture = picture;
        this.ground = ground;
        this.map = new Canvas(picture.size(), picture.size());
        this.profile = new Canvas(picture.size(), PROFILE);
        this.image = new WritableImage(picture.size(), picture.size());
        image.getPixelWriter().setPixels(0, 0, picture.size(), picture.size(), PixelFormat.getIntArgbInstance(),
                picture.pixels(), 0, picture.size());
    }

    /** The map, to be laid out and clicked on. */
    public Canvas map() {
        return map;
    }

    /** The profile, to be laid out. */
    public Canvas profile() {
        return profile;
    }

    /** The map's picture, for where on the map a click is. */
    public MapPicture picture() {
        return picture;
    }

    /** Both drawn again: {@code waypoints}, the one at {@code chosen} ringed, and the camera as {@code eye} has it. */
    public void drawn(final List<Waypoint> waypoints, final int chosen, final Optional<Flight> eye,
                      final double travelled) {
        final GraphicsContext g = map.getGraphicsContext2D();
        g.drawImage(image, 0, 0);
        final Optional<Route> route = waypoints.size() < 2 ? Optional.empty() : Optional.of(new Route(waypoints));
        route.ifPresent(r -> line(g, r));
        g.setFont(Font.font(FONT));
        for (int k = 0; k < waypoints.size(); k++) {
            final Waypoint w = waypoints.get(k);
            final double x = picture.column(w.x()) + 0.5;
            final double y = picture.row(w.z()) + 0.5;
            g.setFill(k == chosen ? Color.YELLOW : Color.WHITE);
            g.fillOval(x - MARK, y - MARK, 2 * MARK, 2 * MARK);
            g.setStroke(Color.BLACK);
            g.strokeOval(x - MARK, y - MARK, 2 * MARK, 2 * MARK);
            g.setFill(Color.BLACK);
            g.fillText(String.format(Locale.ROOT, "%.0f m", w.height() / METRE), x + MARK + 2, y - 2);
        }
        eye.ifPresent(flight -> camera(g, flight));
        profiled(route, travelled);
    }

    private void line(final GraphicsContext g, final Route route) {
        g.setStroke(Color.YELLOW);
        g.setLineWidth(2);
        g.beginPath();
        for (int k = 0; k <= ALONG; k++) {
            final Waypoint at = route.at(route.length() * k / ALONG);
            final double x = picture.column(at.x()) + 0.5;
            final double y = picture.row(at.z()) + 0.5;
            if (k == 0) {
                g.moveTo(x, y);
            } else {
                g.lineTo(x, y);
            }
        }
        g.stroke();
    }

    private void camera(final GraphicsContext g, final Flight flight) {
        final double x = picture.column(flight.x()) + 0.5;
        final double y = picture.row(flight.z()) + 0.5;
        final double round = Math.toRadians(flight.yaw());
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        g.strokeLine(x, y, x + Math.sin(round) * LINE, y - Math.cos(round) * LINE);
        g.setFill(Color.RED);
        g.fillOval(x - MARK - 1, y - MARK - 1, 2 * MARK + 2, 2 * MARK + 2);
    }

    /** The profile drawn: the ground along the route in brown, the route in yellow, the camera's place in red. */
    private void profiled(final Optional<Route> route, final double travelled) {
        final GraphicsContext g = profile.getGraphicsContext2D();
        g.setFill(Color.gray(0.95));
        g.fillRect(0, 0, profile.getWidth(), PROFILE);
        if (route.isEmpty()) {
            return;
        }
        final Route r = route.get();
        final double[] grounds = new double[ALONG + 1];
        final double[] routes = new double[ALONG + 1];
        double low = Double.MAX_VALUE;
        double high = -Double.MAX_VALUE;
        for (int k = 0; k <= ALONG; k++) {
            final Waypoint at = r.at(r.length() * k / ALONG);
            grounds[k] = ground.height(at.x(), at.z());
            routes[k] = at.height();
            low = Math.min(low, Math.min(grounds[k], routes[k]));
            high = Math.max(high, Math.max(grounds[k], routes[k]));
        }
        final double span = Math.max(high - low, METRE);
        final double w = profile.getWidth();
        for (int k = 0; k < ALONG; k++) {
            final double x0 = w * k / ALONG;
            final double x1 = w * (k + 1) / ALONG;
            g.setStroke(Color.SADDLEBROWN);
            g.strokeLine(x0, y(grounds[k], low, span), x1, y(grounds[k + 1], low, span));
            g.setStroke(Color.GOLDENROD);
            g.strokeLine(x0, y(routes[k], low, span), x1, y(routes[k + 1], low, span));
        }
        g.setStroke(Color.RED);
        final double at = w * Math.clamp(travelled / r.length(), 0, 1);
        g.strokeLine(at, 0, at, PROFILE);
    }

    private double y(final double height, final double low, final double span) {
        return PROFILE - 4 - (height - low) / span * (PROFILE - 8);
    }
}
