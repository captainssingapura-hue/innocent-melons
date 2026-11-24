package melons.innocent.demos.village;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
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
 * The village's small wood. Thirty trees, of six different trees, two each of oak, walnut and white birch, the
 * same every time, each on the wood's ground, no two
 * trunks standing on one another, turned every way and of different sizes. It lies across the lake from the car
 * park, clear of the lake's kerb. The engine's own avatar, running at a tree, is stopped by its trunk. And the
 * whole is drawn on the card with its shadows, from the sky and from among the trees, with nothing said by the
 * validation layer; how long a frame of it takes is told, each tree at the stage its distance calls for, and again
 * with every tree as finely as it is grown.
 */
class GroveTest {

    private final Village village = new Village(8);
    private final Grove grove = village.grove();

    @Test
    void thirtyTreesOfSixStandOnItsGroundTheSameEveryTimeAndNoTwoOnOneAnother() {
        final List<Grove.Standing> standing = grove.standing();
        assertEquals(30, standing.size());
        assertEquals(standing, village.grove().standing(), "the same every time");
        assertEquals(6, grove.kinds().size());
        assertEquals(3, grove.kinds().stream().map(Grove.Grown::kind).distinct().count(), "oak, walnut and birch");
        assertEquals(6, standing.stream().map(Grove.Standing::kind).distinct().count(), "every tree stands in it");
        for (final Grove.Standing tree : standing) {
            assertTrue(Math.abs(tree.stands().x() - grove.middle().x()) <= grove.across() / 2 + 320
                    && Math.abs(tree.stands().z() - grove.middle().z()) <= grove.deep() / 2 + 320, "on its ground");
            assertTrue(tree.times() >= 0.78 && tree.times() <= 1.12 && tree.turned() >= 0 && tree.turned() < 360);
            for (final Grove.Standing other : standing) {
                assertTrue(other == tree || other.stands().minus(tree.stands()).length() > 200,
                        "two trunks apart: " + tree + " and " + other);
            }
        }
        assertEquals(30, grove.inTheWay().size(), "each trunk in the way, as a post");
    }

    @Test
    void itLiesAcrossTheLakeFromTheCarParkClearOfTheLakesKerb() {
        double lakeReaches = Double.MAX_VALUE;
        for (final Block kerb : village.lake().inTheWay()) {
            lakeReaches = Math.min(lakeReaches, kerb.x() - Math.max(kerb.halfWidth(), kerb.halfDepth()));
        }
        for (final Grove.Standing tree : grove.standing()) {
            assertTrue(tree.stands().x() < lakeReaches - 100, "its trunk is clear of the kerb: " + tree.stands());
        }
        assertTrue(grove.middle().x() < 0 && village.carPark().stands().x() > 0, "the lake lies between them");
    }

    @Test
    void theAvatarRunningAtATreeIsStoppedByItsTrunk() {
        final Obstacles inTheWay = village.inTheWay();
        final Vec3 tree = grove.standing().getFirst().stands();
        Avatar avatar = new Avatar(Body.standing(
                new com.example.fxsuite.avatar.sim.Vec3(tree.x(), 0, tree.z() - 600), 0), false);
        for (int step = 0; step < 4 * 120; step++) {
            avatar = avatar.step(new Controls(1, 0, 0, 0, true), Clock.STEP, inTheWay).avatar();
        }
        final double z = avatar.body().position().z();
        assertTrue(z > tree.z() - 500 && z < tree.z(), "it ran up to the trunk and no further: " + z);
    }

    @Test
    void theWoodIsDrawnOnTheCardWithItsShadowsAndHowLongAFrameTakesIsTold() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1000, 640)) {
            final Laid laid = new Laid().asking(card.rays());
            try (Place place = new VillagePlace(card, laid); Shaded shadows = new Shaded(card, laid, 4096)) {
                final Camera above = new Orbit(grove.middle().plus(new Vec3(0, 600, 0)), 200, 24, 9000)
                        .camera(48, 10);
                final Vec3 within = grove.standing().get(12).stands().plus(new Vec3(350, 170, 250));
                final Camera among = new Orbit(within, 150, -6, 300).camera(48, 10);
                // each way the card finds shadows: the sun's picture, and rays where the card answers them
                for (final Shadowing by : shadows.offered()) {
                    final String found = by == Shadowing.PICTURE ? "" : "-by-" + by.name().toLowerCase(Locale.ROOT);
                    ImageIO.write(pictured(canvas, place, above, laid, shadows, by).image(), "png",
                            folder.resolve("wood-from-the-sky" + found + ".png").toFile());
                    ImageIO.write(pictured(canvas, place, among, laid, shadows, by).image(), "png",
                            folder.resolve("wood-among-the-trees" + found + ".png").toFile());
                    System.out.printf("WOOD: a frame of the whole village, shadows found by %s, %.1f ms%n",
                            by.said(), timed(canvas, place, above, laid, shadows, by));
                }
            }
            // the same again with every tree held as finely as it is grown, as the wood was drawn before its stages
            System.setProperty("bench.trees.stage", "fine");
            try (Place place = new VillagePlace(card, laid); Shaded shadows = new Shaded(card, laid, 4096)) {
                final Camera above = new Orbit(grove.middle().plus(new Vec3(0, 600, 0)), 200, 24, 9000)
                        .camera(48, 10);
                for (final Shadowing by : shadows.offered()) {
                    System.out.printf("WOOD: the same, every tree drawn as grown, shadows found by %s, %.1f ms%n",
                            by.said(), timed(canvas, place, above, laid, shadows, by));
                }
            } finally {
                System.clearProperty("bench.trees.stage");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** How long a frame of the place takes, in thousandths of a second: forty frames, after five to warm up. */
    private double timed(final Canvas canvas, final Place place, final Camera camera, final Laid laid,
                         final Shaded shadows, final Shadowing by) {
        for (int frame = 0; frame < 5; frame++) {
            pictured(canvas, place, camera, laid, shadows, by);
        }
        final long began = System.nanoTime();
        for (int frame = 0; frame < 40; frame++) {
            pictured(canvas, place, camera, laid, shadows, by);
        }
        return (System.nanoTime() - began) / 1e6 / 40;
    }

    /** The place as {@code camera} sees it by day, with its shadows about the wood, found {@code by}. */
    private Picture pictured(final Canvas canvas, final Place place, final Camera camera, final Laid laid,
                             final Shaded shadows, final Shadowing by) {
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Beam beam = new Beam(grove.middle(), day.light().sunTowards(), 12_000, 12_000);
        final Consumer<Pass> all = pass -> {
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        };
        shadows.drawn(canvas, day.sky(), framed, by, beam, all, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
