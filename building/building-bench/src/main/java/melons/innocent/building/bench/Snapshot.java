package melons.innocent.building.bench;

import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/** A JavaFX scene as it is drawn now, saved as a picture: on JavaFX's thread. */
final class Snapshot {

    /** {@code scene} as drawn now, saved to {@code file} as a PNG. */
    void saved(final Scene scene, final String file) {
        final WritableImage image = scene.snapshot(null);
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final PixelReader pixels = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                out.setRGB(x, y, pixels.getArgb(x, y));
            }
        }
        try {
            ImageIO.write(out, "png", new File(file));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
