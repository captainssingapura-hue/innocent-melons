package melons.innocent.vehicles.drawn;

import com.example.fxsuite.vulkan.afloat.Afloat;
import com.example.fxsuite.vulkan.afloat.Helm;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The river boat on the card, from its bow quarter, from its side and from astern, its display over it: drawn with
 * nothing said by the validation layer, the boat standing out against the sky, and written to
 * {@code target/card/boat-bow.png}, {@code boat-side.png} and {@code boat-astern.png} for the eye, and its number on
 * its side close to in {@code boat-lettered.png}.
 */
class BoatOnTheCardTest {

    private static final int WIDE = 1280;
    private static final int HIGH = 720;

    @Test
    void itIsDrawnFromEachSideWithItsDisplayOverItAndNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        final Afloat boat = new Afloat(new Vec3(0, 0, 0), 0, 300, 0.6, 0.4, 3, 1, false);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH); BoatDrawn drawn = new BoatDrawn();
             Overlay overlay = new Overlay(card, laid)) {
            drawn.carded(card, laid);
            for (final String side : List.of("bow", "side", "astern", "lettered")) {
                final double round = switch (side) {
                    case "bow" -> 150;
                    case "side", "lettered" -> 90;
                    default -> -20;
                };
                final Camera camera = side.equals("lettered")
                        ? new Orbit(new Vec3(160, 60, -170), round, 5, 600).camera(48, 10)
                        : new Orbit(new Vec3(0, 150, 0), round, 14, 2000).camera(48, 10);
                canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDE / HIGH), pass -> {
                    drawn.drawn(pass, camera, boat.attitude().placed(boat.floating()), boat.wheel());
                    new Helm().drawn(pass, overlay.over(camera, (double) WIDE / HIGH), boat, 240);
                });
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                ImageIO.write(picture.image(), "png", folder.resolve("boat-" + side + ".png").toFile());
                int boatPixels = 0;
                for (int y = HIGH / 4; y < 3 * HIGH / 4; y++) {
                    for (int x = WIDE / 4; x < 3 * WIDE / 4; x++) {
                        boatPixels += picture.at(x, y) == picture.at(2, 2) ? 0 : 1;
                    }
                }
                assertTrue(boatPixels > 5000, "the boat stands out against the sky: " + boatPixels);
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }
}
