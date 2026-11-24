package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
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
 * A forest without end on the card, drawn from its samples: from among its trees, from low over them, from high
 * above, and from among them again twelve kilometres from the world's middle, each once everything wanted has been
 * baked, with nothing said by the validation layer. It is drawn so with a few samples and with many, and with the
 * finest reaching further: the pictures are written to {@code target/card/endless-*.png}, and what is drawn, what
 * the card holds and how long a frame takes are told.
 */
class EndlessForestTest {

    private static final long PATIENCE = 240_000;

    @Test
    void itIsDrawnFromAmongLowOverHighAboveAndFarFromTheWorldsMiddleWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final List<String> named = List.of("among", "low-over", "high-above", "far-out");
        final List<Camera> cameras = List.of(
                new Orbit(new Vec3(300, 170, 200), 30, -3, 300).camera(48, 10),
                new Orbit(new Vec3(0, 1_500, 0), 20, 14, 6_000).camera(48, 10),
                new Orbit(new Vec3(0, 0, 0), 20, 40, 150_000).camera(48, 10),
                new Orbit(new Vec3(1_200_300, 170, -1_200_200), 30, -3, 300).camera(48, 10));
        try (Card card = new Card(); Canvas canvas = card.canvas(1280, 720)) {
            final Laid laid = new Laid().asking(card.rays());
            for (final int[] set : new int[][] {{8, 20, 300, 2_000}, {64, 20, 0, 2_000}, {32, 25, 300, 300}}) {
                // the last set is as the walk has it: three levels and a cut-off, everything baked beforehand
                final boolean low = set[3] == 300;
                final Reaches reaches = low ? new Reaches(set[1], 60, 300, 300, 300)
                        : new Reaches(set[1], 60, 200, 600, set[3]);
                final long opened = System.currentTimeMillis();
                try (EndlessForestPlace place = new EndlessForestPlace(card, laid, set[0], 25, reaches, set[2], low);
                     Shaded shadows = new Shaded(card, laid, 4096)) {
                    System.out.printf("ENDLESS FOREST: %d samples made ready in %.1f s; %s%n", set[0],
                            (System.currentTimeMillis() - opened) / 1e3, card.memory().said());
                    for (int view = 0; view < cameras.size(); view++) {
                        final Camera camera = cameras.get(view);
                        final long began = System.currentTimeMillis();
                        do {
                            place.readied(camera);
                            pictured(canvas, place, camera, laid, shadows);
                        } while (!place.settled() && System.currentTimeMillis() - began < PATIENCE);
                        assertTrue(place.settled(), "everything wanted baked: " + place.said());
                        ImageIO.write(pictured(canvas, place, camera, laid, shadows).image(), "png", folder.resolve(
                                "endless-" + set[0] + "-samples-fine-" + set[1] + "-shadows-" + set[2] + "-far-" + set[3] + "-" + named.get(view)
                                + ".png")
                                .toFile());
                        final long settledAt = System.currentTimeMillis();
                        final long timed = System.nanoTime();
                        for (int frame = 0; frame < 20; frame++) {
                            place.readied(camera);
                            pictured(canvas, place, camera, laid, shadows);
                        }
                        System.out.printf("ENDLESS FOREST: %d samples, finest to %d m, shadows to %d m, %s: %.1f ms a frame,"
                                + " settled in %.1f s; %s; %s%n", set[0], set[1], set[2], named.get(view),
                                (System.nanoTime() - timed) / 1e6 / 20, (settledAt - began) / 1e3, place.said(),
                                card.memory().said());
                        // at rest the forest is drawn all round only as far as its boughs go: from high above, nothing
                        assertTrue(place.triangles() > 0 || camera.eye().y() > 30_000,
                                "something of it drawn, unless the eye is higher than it is drawn far");
                        if (view == cameras.size() - 1) {
                            final int posts = place.rearranged().orElseThrow().blocks().size();
                            assertEquals(13 * 13 * 16, posts, "its trunks in the way round where the eye now is");
                            assertTrue(place.rearranged().isEmpty(), "and not said again till the eye has gone on");
                        }
                    }
                }
                assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            }
        }
    }

    /** The place as {@code camera} sees it by day, its shadows found by rays where the card answers them. */
    private Picture pictured(final Canvas canvas, final EndlessForestPlace place, final Camera camera,
                             final Laid laid, final Shaded shadows) {
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Vec3 under = camera.eye().plus(camera.ahead().times(3_000));
        final Beam beam = new Beam(new Vec3(under.x(), 0, under.z()), day.light().sunTowards(), 12_000, 12_000);
        final Consumer<Pass> all = pass -> place.drawn(pass, camera);
        final Shadowing by = shadows.offered().getLast();
        shadows.drawn(canvas, day.sky(), framed, by, beam, all, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
