package melons.innocent.vehicles.drawn;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.flight.Attitude;
import com.example.fxsuite.vulkan.flight.Panel;
import com.example.fxsuite.vulkan.flight.Tiltrotor;
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
 * The Osprey on the card, its nacelles up, half way and down, its rotors turned, its display over it: drawn with
 * nothing said by the validation layer, the craft standing out against the sky, and written to
 * {@code target/card/osprey-hover.png}, {@code osprey-converting.png} and {@code osprey-plane.png} for the eye.
 */
class OspreyOnTheCardTest {

    private static final int WIDE = 1280;
    private static final int HIGH = 720;

    @Test
    void itIsDrawnAtEachOfItsNacellesAnglesWithItsDisplayOverItAndNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH); OspreyDrawn osprey = new OspreyDrawn();
             Overlay overlay = new Overlay(card, laid)) {
            osprey.carded(card, laid);
            final Camera camera = new Orbit(new Vec3(0, 300, 0), 215, 12, 2600).camera(48, 10);
            for (final double tilt : new double[] {90, 60, 0}) {
                final Tiltrotor craft = new Tiltrotor(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Attitude(0, 3, -8),
                        tilt, tilt, 0.8, 25, 1, false);
                canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDE / HIGH), pass -> {
                    osprey.drawn(pass, camera, craft.attitude().placed(craft.at()), tilt, craft.turned());
                    new Panel().drawn(pass, overlay.over(camera, (double) WIDE / HIGH), craft, (x, z) -> -1000);
                });
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                final String name = tilt == 90 ? "hover" : tilt == 0 ? "plane" : "converting";
                ImageIO.write(picture.image(), "png", folder.resolve("osprey-" + name + ".png").toFile());
                int craftPixels = 0;
                for (int y = HIGH / 4; y < 3 * HIGH / 4; y++) {
                    for (int x = WIDE / 4; x < 3 * WIDE / 4; x++) {
                        craftPixels += picture.at(x, y) == picture.at(2, 2) ? 0 : 1;
                    }
                }
                assertTrue(craftPixels > 5000, "the craft stands out against the sky: " + craftPixels);
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }
}
