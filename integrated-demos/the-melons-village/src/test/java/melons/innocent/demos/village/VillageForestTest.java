package melons.innocent.demos.village;

import com.example.fxsuite.vulkan.bench.Place;
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
import java.util.Locale;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The village's forest: four thousand trees west of the towers, every one clear of them, drawn with the rest of the
 * village as one wood, its trees as copies. It is drawn on the card with its shadows, each way the card finds them,
 * from its edge, from the sky and from within, with nothing said by the validation layer; the pictures are written
 * to {@code target/card/village-forest-*.png}, and how long a frame takes from each is told.
 */
class VillageForestTest {

    private final Village village = new Village(8);
    private final Grove forest = village.forest();

    @Test
    void itsFourThousandTreesStandClearOfTheTowers() {
        assertEquals(4_000, forest.standing().size());
        for (final Grove.Standing tree : forest.standing()) {
            for (final Tower tower : village.towers()) {
                final Vec3 off = tree.stands().minus(tower.walls().middle());
                assertTrue(Math.hypot(off.x(), off.z()) > 1_500, "a tree clear of a tower: " + tree.stands());
            }
        }
    }

    @Test
    void itIsDrawnFromItsEdgeTheSkyAndWithinAndHowLongAFrameTakesIsTold() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Vec3 middle = forest.middle();
        final List<Camera> cameras = List.of(
                new Orbit(middle.plus(new Vec3(forest.across() / 2 + 3_000, 300, 0)), -90, 4, 2_000).camera(48, 10),
                new Orbit(middle.plus(new Vec3(0, 600, 0)), 120, 30, 45_000).camera(48, 10),
                new Orbit(forest.standing().get(2_010).stands().plus(new Vec3(300, 170, 200)), 70, -4, 300)
                        .camera(48, 10));
        final List<String> named = List.of("edge", "sky", "within");
        try (Card card = new Card(); Canvas canvas = card.canvas(1280, 720)) {
            final Laid laid = new Laid().asking(card.rays());
            try (Place place = new VillagePlace(card, laid); Shaded shadows = new Shaded(card, laid, 4096)) {
                for (final Shadowing by : shadows.offered()) {
                    for (int view = 0; view < cameras.size(); view++) {
                        final Camera camera = cameras.get(view);
                        ImageIO.write(pictured(canvas, place, camera, laid, shadows, by).image(), "png",
                                folder.resolve("village-forest-" + named.get(view) + "-"
                                        + by.name().toLowerCase(Locale.ROOT) + ".png").toFile());
                        System.out.printf("FOREST: from %s, shadows found by %s, %.1f ms a frame%n",
                                named.get(view), by.said(), timed(canvas, place, camera, laid, shadows, by));
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** How long a frame of the place takes, in thousandths of a second: thirty frames, after five to warm up. */
    private double timed(final Canvas canvas, final Place place, final Camera camera, final Laid laid,
                         final Shaded shadows, final Shadowing by) {
        for (int frame = 0; frame < 5; frame++) {
            pictured(canvas, place, camera, laid, shadows, by);
        }
        final long began = System.nanoTime();
        for (int frame = 0; frame < 30; frame++) {
            pictured(canvas, place, camera, laid, shadows, by);
        }
        return (System.nanoTime() - began) / 1e6 / 30;
    }

    /** The place as {@code camera} sees it by day, with its shadows round where it looks, found {@code by}. */
    private Picture pictured(final Canvas canvas, final Place place, final Camera camera, final Laid laid,
                             final Shaded shadows, final Shadowing by) {
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Vec3 under = camera.eye().plus(camera.ahead().times(3_000));
        final Beam beam = new Beam(new Vec3(under.x(), 0, under.z()), day.light().sunTowards(), 12_000, 12_000);
        final Consumer<Pass> all = pass -> {
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        };
        shadows.drawn(canvas, day.sky(), framed, by, beam, all, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
