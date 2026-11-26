package melons.innocent.terrain.bench;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.function.BiConsumer;

/**
 * The console's small map: the land's picture, north up, with the eye marked where it is over the map and a line
 * the way it is turned; a click on the map says where the eye is wanted, across and along. Made and used on
 * JavaFX's thread only.
 */
public final class MapPane {

    /** How big the eye's mark is, and how long the line the way it is turned. */
    private static final double MARK = 5;
    private static final double LINE = 16;

    private final MapPicture picture;
    private final Canvas canvas;
    private final WritableImage image;

    /** {@code picture} shown, a click on it told to {@code clicked} as where on the map, across and along. */
    public MapPane(final MapPicture picture, final BiConsumer<Double, Double> clicked) {
        this.picture = picture;
        this.canvas = new Canvas(picture.size(), picture.size());
        this.image = new WritableImage(picture.size(), picture.size());
        image.getPixelWriter().setPixels(0, 0, picture.size(), picture.size(), PixelFormat.getIntArgbInstance(),
                picture.pixels(), 0, picture.size());
        canvas.setOnMouseClicked(event -> clicked.accept(picture.x(event.getX() - 0.5),
                picture.z(event.getY() - 0.5)));
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    /** The map, to be laid out. */
    public Canvas canvas() {
        return canvas;
    }

    /** The map drawn again, the eye marked at {@code flight}'s place, a line the way it is turned. */
    public void eye(final Flight flight) {
        final GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(image, 0, 0);
        final double column = picture.column(flight.x()) + 0.5;
        final double row = picture.row(flight.z()) + 0.5;
        final double round = Math.toRadians(flight.yaw());
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        g.strokeLine(column, row, column + Math.sin(round) * LINE, row - Math.cos(round) * LINE);
        g.setFill(Color.RED);
        g.fillOval(column - MARK, row - MARK, 2 * MARK, 2 * MARK);
        g.strokeOval(column - MARK, row - MARK, 2 * MARK, 2 * MARK);
    }
}
