package com.example.fxsuite.places.yard;

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
 * The village's car park. A hundred and twenty bays on its concrete, a hundred of them taken, the same every time:
 * each car near the middle of a bay of its own, no two touching, in both paints and many colours, some nose in and
 * some tail in. It lies clear of the lake's kerb and of the village's yards. The engine's own avatar runs an aisle
 * from end to end, and is stopped by a car it runs at. And the whole is drawn on the card, the cars' windows after
 * everything else, with nothing said by the validation layer.
 */
class CarParkTest {

    private final Village village = new Village(8);
    private final CarPark park = village.carPark();
    private final Obstacles inTheWay = village.inTheWay();
    private final Controls ahead = new Controls(1, 0, 0, 0, true);

    @Test
    void aHundredCarsStandEachNearTheMiddleOfABayOfItsOwnNoTwoTouchingTheSameEveryTime() {
        final List<Vec3> bays = park.bays();
        final List<Parked> cars = park.parked();
        assertEquals(120, bays.size());
        assertEquals(100, cars.size());
        assertEquals(cars, village.carPark().parked(), "the same every time");
        assertEquals(120, bays.stream().distinct().count());
        for (final Parked car : cars) {
            assertEquals(1, bays.stream().filter(bay -> Math.abs(bay.x() - car.stands().x()) <= 12
                    && Math.abs(bay.z() - car.stands().z()) <= 12).count(), "in one bay: " + car);
            assertTrue(Math.abs(car.stands().x() - park.stands().x()) + 227.25 <= park.across() / 2
                    && Math.abs(car.stands().z() - park.stands().z()) + 84.75 <= park.deep() / 2, "on the concrete");
            for (final Parked other : cars) {
                assertTrue(other == car || Math.abs(other.stands().x() - car.stands().x()) >= 454.5
                        || Math.abs(other.stands().z() - car.stands().z()) >= 169.5 + 40, car + " clear of " + other);
            }
        }
        assertTrue(cars.stream().map(Parked::livery).distinct().count() >= 10, "in many liveries");
        assertTrue(cars.stream().anyMatch(car -> car.livery().metallic())
                && cars.stream().anyMatch(car -> !car.livery().metallic()), "in both paints");
        final long about = cars.stream().filter(Parked::about).count();
        assertTrue(about > 25 && about < 75, "some nose in, some tail in: " + about + " turned about");
        assertEquals(200, park.inTheWay().size(), "each in the way as its body and its cabin");
    }

    @Test
    void itLiesBesideTheLakeClearOfItsKerbAndOfTheVillagesYards() {
        final double nearEdge = park.stands().x() - park.across() / 2;
        double lakeReaches = -Double.MAX_VALUE;
        for (final Block kerb : village.lake().inTheWay()) {
            lakeReaches = Math.max(lakeReaches, kerb.x() + Math.max(kerb.halfWidth(), kerb.halfDepth()));
        }
        assertTrue(nearEdge - lakeReaches > 200 && nearEdge - lakeReaches < 1200,
                "a few metres from the lake's kerb: " + (nearEdge - lakeReaches) / 100 + " m");
        assertTrue(Math.abs(park.stands().z() - village.lake().stands().z()) < 1e-9, "level with the lake's middle");
        for (final Village.Home home : village.homesteads()) {
            assertTrue(home.yard().middle().x() + home.yard().deep() < nearEdge, "clear of every yard");
        }
        assertEquals(4800, park.across(), 1e-9);
        assertEquals(5600, park.deep(), 1e-9);
    }

    @Test
    void theAvatarRunsAnAisleFromEndToEndAndIsStoppedByACarItRunsAt() {
        // the first aisle's middle, from before the concrete begins
        final double aisle = park.stands().x() - park.across() / 2 + 500 + 300;
        final double before = park.stands().z() - park.deep() / 2 - 200;
        final Avatar along = walked(new Avatar(Body.standing(
                new com.example.fxsuite.avatar.sim.Vec3(aisle, 0, before), 0), false), 8);
        assertTrue(along.body().position().z() > before + 4000 && Math.abs(along.body().position().x() - aisle) < 1e-6,
                "the aisle is clear: " + along.body().position());
        // a car in the row to the aisle's right, run at from the aisle
        final Parked car = park.parked().stream()
                .filter(parked -> parked.stands().x() > aisle && parked.stands().x() < aisle + 600).findFirst()
                .orElseThrow();
        final Avatar into = walked(new Avatar(Body.standing(
                new com.example.fxsuite.avatar.sim.Vec3(aisle, 0, car.stands().z()), 90), false), 4);
        final double x = into.body().position().x();
        assertTrue(x > aisle + 50, "it ran toward the car: " + into.body().position());
        assertTrue(x < car.stands().x() - 227.25, "and was stopped short of its nearer end: " + x);
    }

    @Test
    void theWholeIsDrawnOnTheCardItsWindowsAfterEverythingElseWithNothingSaid() throws IOException {
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1000, 640);
             Place place = new VillagePlace(card, laid)) {
            final Camera above = new Orbit(park.stands(), 35, 38, 7500).camera(48, 10);
            final Picture sky = pictured(canvas, place, above, day, laid);
            ImageIO.write(sky.image(), "png", folder.resolve("car-park-from-the-sky.png").toFile());
            final Parked car = park.parked().get(40);
            final Camera among = new Orbit(car.stands().plus(new Vec3(0, 90, 0)), 25, 7, 1100).camera(48, 10);
            ImageIO.write(pictured(canvas, place, among, day, laid).image(), "png",
                    folder.resolve("car-park-among-the-cars.png").toFile());
            // from over a car, with the sun's own picture drawn first: the cars shade the concrete beside them
            final Camera over = new Orbit(car.stands(), 200, 48, 1500).camera(48, 10);
            final Picture unshaded = pictured(canvas, place, over, day, laid);
            try (Shadows shadows = new Shadows(card, laid, 2048)) {
                final Framed framed = new Framed(laid, over, day.light());
                final Beam beam = new Beam(car.stands(), day.light().sunTowards(), 4000, 4000).settled(2048);
                final Consumer<Pass> all = pass -> {
                    place.drawn(pass, over);
                    place.seenThrough(pass, over);
                };
                canvas.drawn(day.sky(), framed.filling(1000.0 / 640, beam, 2048), shadows.cast(framed, beam, all),
                        all);
                final Picture shaded = new Picture(canvas.width(), canvas.pixels());
                ImageIO.write(shaded.image(), "png", folder.resolve("car-park-shaded.png").toFile());
                int darker = 0;
                for (int y = 0; y < shaded.height(); y++) {
                    for (int x = 0; x < shaded.width(); x++) {
                        darker += shaded.part(x, y, 1) < unshaded.part(x, y, 1) - 20 ? 1 : 0;
                    }
                }
                assertTrue(darker > 5_000, "the cars shade the concrete beside them: " + darker + " pixels darker");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            int drawn = 0;
            for (int y = 0; y < sky.height(); y++) {
                for (int x = 0; x < sky.width(); x++) {
                    drawn += sky.at(x, y) == 0xffc9dbe8 ? 0 : 1;
                }
            }
            assertTrue(drawn > 60_000, "the concrete and its cars drawn over the sky: " + drawn + " pixels");
        }
    }

    /** The place as {@code camera} sees it by day: what hides what is behind it, then what is seen through. */
    private Picture pictured(final Canvas canvas, final Place place, final Camera camera, final Daylight day,
                             final Laid laid) {
        canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(1000.0 / 640), pass -> {
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        });
        return new Picture(canvas.width(), canvas.pixels());
    }

    /** {@code avatar} after {@code seconds} of running ahead, by the engine's own steps. */
    private Avatar walked(final Avatar avatar, final double seconds) {
        Avatar now = avatar;
        for (int step = 0; step < seconds * 120; step++) {
            now = now.step(ahead, Clock.STEP, inTheWay).avatar();
        }
        return now;
    }
}
