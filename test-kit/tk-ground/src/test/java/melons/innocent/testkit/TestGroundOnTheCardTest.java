package melons.innocent.testkit;

import com.example.fxsuite.places.yard.Homestead;
import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
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
 * The test ground on the card. Each outline is drawn from above with the brick house stood on it, with nothing said
 * by the validation layer: its ground is under the middle of the picture, the sky past its edge, and the house's
 * walls are in the way with the map's own. The pictures are written to {@code target/card/test-ground-*.png}, and
 * one from a man's height beside the house.
 */
class TestGroundOnTheCardTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final int SKY = 0xffc9dbe8;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();

    @Test
    void eachOutlineIsDrawnWithTheHouseOnItTheSkyPastItsEdgeAndNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Camera above = new Orbit(new Vec3(0, 0, 0), 0, 55, 30_000).camera(48, 10);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT)) {
            for (final Outline outline : Outline.values()) {
                final TestKit kit = new TestKit("tried", new BaseMap(outline, 100, Grounds.TURF))
                        .with(Homestead::new);
                try (TestGround ground = (TestGround) kit.placing().apply(card, laid)) {
                    final Picture picture = pictured(canvas, ground, above);
                    ImageIO.write(picture.image(), "png", folder.resolve("test-ground-"
                            + outline.name().toLowerCase(java.util.Locale.ROOT) + ".png").toFile());
                    assertEquals(SKY, picture.at(4, 4), "the sky past its edge, in a corner");
                    assertTrue(picture.part(WIDTH / 2, HEIGHT / 2 + 40, 1) > picture.part(WIDTH / 2, HEIGHT / 2 + 40, 2),
                            "turf, greener than it is blue, under the middle");
                    assertTrue(ground.inTheWay().blocks().size() > outline.edge(1).size() + 3,
                            "its own wall, a run for each side, and the house's walls besides");
                    assertEquals(ground.map().said(), ground.said());
                }
            }
            final TestKit kit = new TestKit("tried", new BaseMap(Outline.CIRCLE, 1000, Grounds.TURF))
                    .with(Homestead::new);
            try (TestGround ground = (TestGround) kit.placing().apply(card, laid)) {
                ImageIO.write(pictured(canvas, ground, new Orbit(new Vec3(0, 170, 700), 25, 4, 1_500).camera(48, 10))
                        .image(), "png", folder.resolve("test-ground-beside-the-house.png").toFile());
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The ground and what stands on it as {@code camera} sees them by day, with no shadows. */
    private Picture pictured(final Canvas canvas, final TestGround ground, final Camera camera) {
        canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDTH / HEIGHT), pass -> {
            ground.drawn(pass, camera);
            ground.seenThrough(pass, camera);
        });
        return new Picture(canvas.width(), canvas.pixels());
    }
}
