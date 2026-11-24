package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Tree;
import melons.innocent.plants.tree.Wooded;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every kind of tree on the card, each in the bark the lab has for it, pictured whole from far enough off to see
 * all of it, and its trunk from close to, with nothing said by the validation layer: something of each is drawn
 * against the sky. The pictures are written to {@code target/card/kind-<kind>-whole.png} and {@code -trunk.png},
 * for the eye to judge each kind by.
 */
class KindsOnTheCardTest {

    private static final int WIDTH = 540;
    private static final int HEIGHT = 720;
    private static final int SKY = 0xffc9dbe8;

    @Test
    void everyKindIsDrawnWholeAndItsTrunkCloseToInItsOwnBark() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT)) {
            for (final Kind kind : Kind.values()) {
                final Tree tree = kind.tree(1);
                final String named = kind.name().toLowerCase(Locale.ROOT);
                try (Formed wood = new Formed(card, new Look(laid, new Shades().of(new Barks().of(kind))),
                        new Wooded(tree).formed())) {
                    final Camera whole = new Orbit(new Vec3(0, tree.height() / 2, 0), 25, 4, tree.height() * 1.45)
                            .camera(48, 10);
                    final Camera trunk = new Orbit(new Vec3(0, 160, 0), 25, 2, 420).camera(48, 10);
                    for (final Camera camera : List.of(whole, trunk)) {
                        canvas.drawn(day.sky(), new Framed(laid, camera, day.light())
                                .filling((double) WIDTH / HEIGHT), pass -> wood.drawn(pass, new Vec3(0, 0, 0),
                                camera));
                        final Picture picture = new Picture(canvas.width(), canvas.pixels());
                        ImageIO.write(picture.image(), "png", folder.resolve("kind-" + named
                                + (camera == whole ? "-whole.png" : "-trunk.png")).toFile());
                        int drawn = 0;
                        for (int y = 0; y < HEIGHT; y += 4) {
                            for (int x = 0; x < WIDTH; x += 4) {
                                drawn += picture.at(x, y) == SKY ? 0 : 1;
                            }
                        }
                        assertTrue(drawn > 200, kind.said() + ": something of it drawn against the sky: " + drawn);
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }
}
