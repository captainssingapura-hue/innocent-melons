package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
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
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A road through a forest without end, on the card. No tree's trunk is in the way within five metres of the road's
 * middle, as far along as its trunks are said; the avatar, walking off sideways, is stopped some thirty metres from
 * the road, and walking back along it is stopped a hundred metres behind. Followed for half a kilometre, the road is
 * made on ahead and let go behind, the squares it touches are drawn tree by tree, and it is pictured along the way
 * with nothing said by the validation layer: the pictures are written to {@code target/card/road-*.png}, and how
 * long a frame takes is told.
 */
class RoadForestTest {

    @Test
    void theRoadIsClearTheAvatarIsKeptToItAndItIsFollowedAndPicturedWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1280, 720)) {
            final Laid laid = new Laid().asking(card.rays());
            try (RoadForestPlace place = new RoadForestPlace(card, laid, new Road(new Wandering(new Random(5))), 16,
                    new Reaches());
                 Shaded shadows = new Shaded(card, laid, 4096)) {
                final Road road = place.road();
                pictured(canvas, place, eye(road, 0), laid, shadows);
                final Obstacles inTheWay = place.inTheWay();
                assertTrue(inTheWay.blocks().stream().filter(block -> block.top() < 1_000_000)
                        .noneMatch(trunk -> road.off(trunk.x(), trunk.z()) <= 500), "no trunk within five metres");
                assertTrue(inTheWay.blocks().stream().anyMatch(trunk -> road.off(trunk.x(), trunk.z()) < 1_200),
                        "but trees not far beyond that");
                final Avatar aside = walked(90, 30, inTheWay);
                final double off = Math.abs(aside.body().position().x() - road.across(aside.body().position().z()));
                assertTrue(off > 300 && off <= 3_100, "kept within thirty metres of the road: " + off);
                final Avatar back = walked(180, 60, inTheWay);
                assertTrue(back.body().position().z() > -10_300, "and stopped a hundred metres behind: "
                        + back.body().position().z());
                final long began = System.nanoTime();
                int frames = 0;
                for (double along = 0; along <= 50_000; along += 500) {
                    final Picture picture = pictured(canvas, place, eye(road, along), laid, shadows);
                    frames++;
                    if (along % 12_500 == 0) {
                        ImageIO.write(picture.image(), "png", folder.resolve("road-" + (int) (along / 100)
                                + "-m-along.png").toFile());
                    }
                    assertTrue(place.touched() > 10, "the squares the road touches drawn tree by tree: "
                            + place.touched());
                    place.rearranged();
                }
                System.out.printf("ROAD: %.1f ms a frame over %d frames five metres apart; %s; %s%n",
                        (System.nanoTime() - began) / 1e6 / frames, frames, place.said().replace("\n", "; "),
                        card.memory().said());
                assertTrue(road.has(50_000 + 40_000), "made on ahead of where it was reached");
                assertFalse(road.has(10_000), "and let go far behind");
                assertTrue(place.rearranged().isEmpty() && !place.inTheWay().blocks().isEmpty());
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The avatar after {@code seconds} of walking the way {@code heading} says from the road's start. */
    private Avatar walked(final double heading, final int seconds, final Obstacles inTheWay) {
        Avatar now = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), heading), false);
        for (int step = 0; step < seconds * 120; step++) {
            now = now.step(new Controls(1, 0, 0, 0, true), Clock.STEP, inTheWay).avatar();
        }
        return now;
    }

    /** A camera behind and above a traveller on {@code road} so far {@code along}, looking on along it. */
    private Camera eye(final Road road, final double along) {
        return new Orbit(new Vec3(road.across(along), 170, along), 0, 12, 700).camera(48, 10);
    }

    /** The place as {@code camera} sees it by day, its shadows found by rays where the card answers them. */
    private Picture pictured(final Canvas canvas, final RoadForestPlace place, final Camera camera, final Laid laid,
                             final Shaded shadows) {
        place.readied(camera);
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Vec3 under = camera.eye().plus(camera.ahead().times(3_000));
        final Beam beam = new Beam(new Vec3(under.x(), 0, under.z()), day.light().sunTowards(), 12_000, 12_000);
        final Consumer<Pass> all = pass -> {
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        };
        shadows.drawn(canvas, day.sky(), framed, shadows.offered().getLast(), beam, all, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
