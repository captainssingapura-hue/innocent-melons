package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shadows;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
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
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The place a tree stands in. The engine's own avatar, running straight at the tree, is stopped by its trunk. And
 * the tree is drawn on the card, whole and from among its boughs, with nothing said by the validation layer, its
 * wood standing out against the sky.
 */
class TreePlaceTest {

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();

    @Test
    void theAvatarRunningStraightAtTheTreeIsStoppedByItsTrunk() {
        try (Card card = new Card(); TreePlace place = new TreePlace(card, laid)) {
            Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), false);
            for (int step = 0; step < 6 * 120; step++) {
                avatar = avatar.step(new Controls(1, 0, 0, 0, true), Clock.STEP, place.inTheWay()).avatar();
            }
            final double z = avatar.body().position().z();
            assertTrue(z > 900 && z < place.stands().z(), "it ran up to the trunk and no further: " + z);
            assertEquals(List.of(), card.heard());
        }
    }

    @Test
    void theTreeIsDrawnOnTheCardWholeAndFromAmongItsBoughsWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1000, 700);
             TreePlace place = new TreePlace(card, laid); Shadows shadows = new Shadows(card, laid, 2048)) {
            final double high = place.tree().height();
            final Camera whole = new Orbit(place.stands().plus(new Vec3(0, high * 0.5, 0)), 25, 4, high * 2.1)
                    .camera(48, 10);
            final Picture far = pictured(canvas, place, whole, shadows);
            ImageIO.write(far.image(), "png", folder.resolve("oak-whole.png").toFile());
            final Camera among = new Orbit(place.stands().plus(new Vec3(120, high * 0.55, 60)), 70, -8, 420)
                    .camera(48, 10);
            ImageIO.write(pictured(canvas, place, among, shadows).image(), "png",
                    folder.resolve("oak-among-its-boughs.png").toFile());
            final Camera foot = new Orbit(place.stands().plus(new Vec3(0, 150, 0)), 20, 6, 380).camera(48, 10);
            ImageIO.write(pictured(canvas, place, foot, shadows).image(), "png",
                    folder.resolve("oak-trunk.png").toFile());
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            int wood = 0;
            for (int y = 0; y < far.height(); y++) {
                for (int x = 0; x < far.width(); x++) {
                    wood += far.at(x, y) == 0xffc9dbe8 ? 0 : 1;
                }
            }
            assertTrue(wood > 8_000, "the tree stands out against the sky: " + wood + " pixels");
        }
    }

    /** The place as {@code camera} sees it by day, with its shadows. */
    private Picture pictured(final Canvas canvas, final TreePlace place, final Camera camera,
                             final Shadows shadows) {
        final Framed framed = new Framed(laid, camera, day.light());
        final Beam beam = new Beam(place.stands(), day.light().sunTowards(), 5000, 5000).settled(2048);
        final Consumer<Pass> all = pass -> place.drawn(pass, camera);
        canvas.drawn(day.sky(), framed.filling(1000.0 / 700, beam, 2048), shadows.cast(framed, beam, all), all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
