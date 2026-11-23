package melons.innocent.demos.village;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Cast;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The village's tall brick towers, and the cars left among the wood's trees. Each tower is hollow, and the engine's
 * avatar walking at it from the side its door is on goes in, and from any other side is stopped by its wall. The
 * towers stand clear of the yards, the lake, the trees and the car park. With the sun where the bench's day has it,
 * the shadows of two fall across the yards on the left of the lane and of the other two into the wood. Five cars are
 * parked in the wood, each clear of every trunk and of the other cars. And it is all drawn on the card from above,
 * with its shadows, with nothing said by the validation layer.
 */
class TowersTest {

    private final Village village = new Village(8);
    private final Daylight day = new Daylight();

    @Test
    void eachTowerIsHollowAndIsGoneIntoByItsDoorAndByNoOtherSide() {
        final Obstacles inTheWay = village.inTheWay();
        for (final Tower tower : village.towers()) {
            int ways = 0;
            for (final double round : new double[] {0, 90, 180, 270}) {
                final double yaw = Math.toRadians(round);
                final Vec3 from = tower.middle().plus(new Vec3(Math.sin(yaw), 0, Math.cos(yaw)).times(800));
                Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(from.x(), 0, from.z()),
                        round + 180), false);
                for (int step = 0; step < 4 * 120; step++) {
                    avatar = avatar.step(new Controls(1, 0, 0, 0, false), Clock.STEP, inTheWay).avatar();
                }
                final var at = avatar.body().position();
                ways += Math.abs(at.x() - tower.middle().x()) < 240 && Math.abs(at.z() - tower.middle().z()) < 240
                        ? 1 : 0;
            }
            assertEquals(1, ways, "one way into " + tower);
        }
    }

    @Test
    void theTowersStandClearOfTheYardsTheLakeTheTreesAndTheCarPark() {
        final List<Block> others = new ArrayList<>();
        village.homesteads().forEach(home -> others.addAll(home.yard().inTheWay()));
        others.addAll(village.lake().inTheWay());
        others.addAll(village.carPark().inTheWay());
        others.addAll(village.grove().inTheWay());
        for (final Tower tower : village.towers()) {
            for (final Block block : others) {
                assertTrue(Math.abs(block.x() - tower.middle().x()) > block.halfWidth() + 300 + 100
                        || Math.abs(block.z() - tower.middle().z()) > block.halfDepth() + 300 + 100,
                        tower + " clear of " + block);
            }
        }
    }

    @Test
    void byTheBenchsSunTheirShadowsFallAcrossTheYardsAndIntoTheWood() {
        final Vec3 towards = day.light().sunTowards();
        final Vec3 falls = new Vec3(towards.x(), 0, towards.z()).unit();
        final Grove grove = village.grove();
        int onYards = 0;
        int inWood = 0;
        for (final Tower tower : village.towers()) {
            final Vec3 tip = tower.middle().plus(falls.times(tower.shadow(day.light().sunHigh())));
            final boolean yard = village.homesteads().stream().anyMatch(home -> crosses(tower.middle(), tip,
                    home.yard().middle(), home.yard().deep() / 2 + 200));
            final boolean wood = Math.abs(tip.x() - grove.middle().x()) < grove.across() / 2
                    && Math.abs(tip.z() - grove.middle().z()) < grove.deep() / 2;
            onYards += yard ? 1 : 0;
            inWood += wood ? 1 : 0;
        }
        assertEquals(2, onYards, "two towers' shadows across the yards");
        assertEquals(2, inWood, "and two into the wood");
    }

    @Test
    void fiveCarsAreParkedInTheWoodClearOfEveryTrunkAndOfEachOther() {
        final List<Parked> cars = village.woodCars();
        assertEquals(5, cars.size());
        assertEquals(cars, village.woodCars(), "the same every time");
        final Grove grove = village.grove();
        for (final Parked car : cars) {
            assertTrue(Math.abs(car.stands().x() - grove.middle().x()) < grove.across() / 2
                    && Math.abs(car.stands().z() - grove.middle().z()) < grove.deep() / 2, "in the wood: " + car);
            for (final Grove.Standing tree : grove.standing()) {
                assertTrue(Math.abs(car.stands().x() - tree.stands().x()) > 300
                        || Math.abs(car.stands().z() - tree.stands().z()) > 150, car + " clear of " + tree);
            }
        }
    }

    @Test
    void itIsAllDrawnFromAboveWithItsShadowsWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1200, 760)) {
            final Laid laid = new Laid().asking(card.rays());
            try (Place place = new VillagePlace(card, laid); Cast cast = new Cast(card, laid);
                 Shaded shadows = new Shaded(card, laid, 4096)) {
                final Camera village = new Orbit(new Vec3(-800, 0, 3800), 140, 42, 9500).camera(48, 10);
                pictured(canvas, place, cast, shadows, village, laid, folder.resolve("towers-over-the-village.png"));
                final Camera wood = new Orbit(new Vec3(-10500, 0, -4500), 200, 40, 9000).camera(48, 10);
                pictured(canvas, place, cast, shadows, wood, laid, folder.resolve("towers-over-the-wood.png"));
                final Parked car = this.village.woodCars().getFirst();
                final Camera among = new Orbit(car.stands().plus(new Vec3(0, 100, 0)), 150, 22, 1400)
                        .camera(48, 10);
                pictured(canvas, place, cast, shadows, among, laid, folder.resolve("a-car-in-the-wood.png"));
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** Whether the line from {@code from} to {@code to} passes within {@code reach} of {@code middle}. */
    private boolean crosses(final Vec3 from, final Vec3 to, final Vec3 middle, final double reach) {
        for (int at = 0; at <= 50; at++) {
            final Vec3 on = from.plus(to.minus(from).times(at / 50.0));
            if (Math.abs(on.x() - middle.x()) < reach && Math.abs(on.z() - middle.z()) < reach) {
                return true;
            }
        }
        return false;
    }

    /** The place and its ground as {@code camera} sees them by day, with their shadows, written to {@code file}. */
    private void pictured(final Canvas canvas, final Place place, final Cast cast, final Shaded shadows,
                          final Camera camera, final Laid laid, final Path file) throws IOException {
        final Framed framed = new Framed(laid, camera, day.light());
        final Consumer<Pass> standing = pass -> place.drawn(pass, camera);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        };
        final Beam beam = new Beam(new Vec3(camera.eye().x(), 0, camera.eye().z()), day.light().sunTowards(), 20000,
                20000);
        shadows.drawn(canvas, day.sky(), framed, Shadowing.RAYS, beam, standing, all);
        ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", file.toFile());
    }
}
