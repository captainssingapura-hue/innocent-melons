package melons.innocent.plants.walk;

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
import melons.innocent.plants.tree.Kind;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The forest of four woods. A thousand trees stand in it, the same every time, each wood on its own ground ahead of
 * where the avatar starts and clear of the others, the oak wood of oaks alone, the walnut's of walnuts, the birch's
 * of birches and the mixed of all three, and no two trunks on one another. It is drawn on the card with nothing
 * said by the validation layer, from the forest's edge, from above it and from within the mixed wood; and how long a
 * frame takes is told, with a hundred trees, three hundred and a thousand, each way the card finds shadows.
 */
class ForestTest {

    private static final int WIDE = 1280;
    private static final int HIGH = 720;

    private final Laid plain = new Laid();

    @Test
    void aThousandTreesInFourWoodsEachOfItsOwnKindsAndNoTwoTrunksOnOneAnother() {
        try (Card card = new Card(); ForestPlace forest = new ForestPlace(card, plain)) {
            assertEquals(1000, forest.trees());
            final List<Woodland> woods = forest.woods();
            assertEquals(List.of(Set.of(Kind.OAK), Set.of(Kind.WALNUT), Set.of(Kind.BIRCH),
                    Set.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH)),
                    woods.stream().map(wood -> wood.standing().stream().map(Woodland.Planted::kind)
                            .collect(Collectors.toSet())).toList());
            for (final Woodland wood : woods) {
                assertEquals(wood.standing(), wood.standing(), "the same every time");
                assertTrue(wood.middle().z() - wood.deep() / 2 > 0, "ahead of where the avatar starts");
                final List<Woodland.Planted> trees = wood.standing();
                for (final Woodland.Planted tree : trees) {
                    for (final Woodland other : woods) {
                        final boolean within = Math.abs(tree.stands().x() - other.middle().x()) < other.across() / 2
                                + 300 && Math.abs(tree.stands().z() - other.middle().z()) < other.deep() / 2 + 300;
                        assertEquals(other == wood, within, "a tree on its own wood's ground and no other's");
                    }
                    for (final Woodland.Planted near : trees) {
                        assertTrue(near == tree || near.stands().minus(tree.stands()).length() > 120,
                                "two trunks apart");
                    }
                }
            }
            assertEquals(1000, forest.inTheWay().blocks().size(), "each trunk in the way, as a post");
            assertEquals(List.of(), card.heard());
        }
    }

    @Test
    void theForestIsDrawnOnTheCardAndHowLongAFrameTakesIsTold() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH)) {
            final Laid laid = new Laid().asking(card.rays());
            final Camera edge = new Orbit(new Vec3(0, 170, 0), 0, -3, 1).camera(48, 10);
            final Camera above = new Orbit(new Vec3(0, 0, 16_500), 0, 32, 22_000).camera(48, 10);
            final Camera within = new Orbit(new Vec3(6_500, 170, 21_000), 0, -4, 1).camera(48, 10);
            for (final int each : new int[] {25, 75, 250}) {
                try (Place forest = new ForestPlace(card, laid, each); Shaded shadows = new Shaded(card, laid, 4096)) {
                    for (final Shadowing by : shadows.offered()) {
                        for (final Camera camera : List.of(edge, above, within)) {
                            System.out.printf(Locale.ROOT, "FOREST: %d trees, %s, from %s: %.1f ms%n", 4 * each,
                                    by.said(), camera == edge ? "its edge" : camera == above ? "above" : "within",
                                    timed(canvas, forest, camera, laid, shadows, by));
                        }
                    }
                    if (each == 250) {
                        ImageIO.write(pictured(canvas, forest, edge, laid, shadows, Shadowing.RAYS).image(), "png",
                                folder.resolve("forest-at-its-edge.png").toFile());
                        ImageIO.write(pictured(canvas, forest, above, laid, shadows, Shadowing.RAYS).image(), "png",
                                folder.resolve("forest-from-above.png").toFile());
                        ImageIO.write(pictured(canvas, forest, within, laid, shadows, Shadowing.RAYS).image(), "png",
                                folder.resolve("forest-within-the-mixed-wood.png").toFile());
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** How long a frame of the forest takes, in thousandths of a second: thirty frames, after five to warm up. */
    private double timed(final Canvas canvas, final Place forest, final Camera camera, final Laid laid,
                         final Shaded shadows, final Shadowing by) {
        for (int frame = 0; frame < 5; frame++) {
            pictured(canvas, forest, camera, laid, shadows, by);
        }
        final long began = System.nanoTime();
        for (int frame = 0; frame < 30; frame++) {
            pictured(canvas, forest, camera, laid, shadows, by);
        }
        return (System.nanoTime() - began) / 1e6 / 30;
    }

    /** The forest as {@code camera} sees it by day, its shadows found {@code by}. */
    private Picture pictured(final Canvas canvas, final Place forest, final Camera camera, final Laid laid,
                             final Shaded shadows, final Shadowing by) {
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Beam beam = new Beam(new Vec3(0, 0, 16_500), day.light().sunTowards(), 30_000, 30_000);
        final Consumer<Pass> casting = pass -> forest.drawn(pass, camera);
        final Consumer<Pass> all = pass -> {
            forest.drawn(pass, camera);
            forest.seenThrough(pass, camera);
        };
        shadows.drawn(canvas, day.sky(), framed, by, beam, casting, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
