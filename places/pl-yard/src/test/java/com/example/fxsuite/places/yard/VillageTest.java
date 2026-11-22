package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
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
 * The village: the same every time, its yards by turns either side of a lane that is left clear, each yard's
 * gateway open onto the lane and its house within its walls; a house in the way as a stepped roof, each step
 * smaller and higher; the engine's own avatar running the lane's length, going in at a gateway, and stopped by a
 * house; and the whole drawn on the card from the sky, with nothing said by the validation layer.
 */
class VillageTest {

    private final Village village = new Village(8);
    private final Obstacles inTheWay = village.inTheWay();
    private final Controls ahead = new Controls(1, 0, 0, 0, true);

    @Test
    void itIsTheSameEveryTimeItsYardsEitherSideOfTheLaneAndEachHouseWithinItsWalls() {
        final List<Village.Home> homes = village.homesteads();
        assertEquals(homes, new Village(8).homesteads());
        assertEquals(8, homes.size());
        for (int home = 0; home < homes.size(); home++) {
            final Walled yard = homes.get(home).yard();
            assertEquals(home % 2 == 0 ? -1 : 1, Math.signum(yard.middle().x()), "by turns left and right");
            assertEquals(5, yard.walls().size(), "a gateway has no wall over it");
            // turned to face the lane, its front runs along the lane and its depth across it
            assertTrue(Math.abs(yard.middle().x()) - yard.deep() / 2 >= 350 - 1e-9, "clear of the lane");
            final com.example.fxsuite.vulkan.space.Vec3 house = homes.get(home).house().stands();
            assertTrue(Math.abs(house.x() - yard.middle().x()) < yard.deep() / 2 - 400
                    && Math.abs(house.z() - yard.middle().z()) < yard.across() / 2 - 300, "within its walls");
        }
    }

    @Test
    void aHouseIsInTheWayAsASteppedRoofEachStepSmallerAndHigher() {
        final MelonHouse house = village.homesteads().getFirst().house();
        final List<Block> steps = house.inTheWay();
        assertEquals(4, steps.size());
        assertEquals(0, steps.getFirst().bottom());
        assertEquals(house.high(), steps.getLast().top(), 1e-9);
        for (int step = 1; step < steps.size(); step++) {
            assertEquals(steps.get(step - 1).top(), steps.get(step).bottom(), 1e-9, "one on another");
            assertTrue(steps.get(step).halfWidth() < steps.get(step - 1).halfWidth(), "each smaller");
        }
    }

    @Test
    void theAvatarRunsTheLanesLengthGoesInAtAGatewayAndIsStoppedByAHouse() {
        final Avatar down = walked(new Avatar(Body.standing(new Vec3(0, 0, 0), 0), false), 8);
        assertTrue(down.body().position().z() > 5000 && Math.abs(down.body().position().x()) < 1e-6,
                "the lane is clear from end to end: " + down.body().position());
        // from the lane, facing the first yard's gateway on the left, and on into its house
        final Walled yard = village.homesteads().getFirst().yard();
        final Avatar in = walked(new Avatar(Body.standing(new Vec3(0, 0, yard.middle().z()), -90), false), 4);
        final double x = in.body().position().x();
        assertTrue(x < -(350 + 60), "in through the gateway: " + in.body().position());
        assertTrue(x > yard.middle().x(), "and stopped by the house before the yard's middle: " + x);
    }

    @Test
    void theWholeIsDrawnOnTheCardFromTheSkyWithNothingSaid() throws IOException {
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        final Camera camera = new Orbit(new com.example.fxsuite.vulkan.space.Vec3(0, 0, 3500), 0, 40, 6000)
                .camera(48, 10);
        final Picture picture;
        try (Card card = new Card(); Canvas canvas = card.canvas(400, 300);
             Place place = new VillagePlace(card, laid)) {
            canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(400.0 / 300),
                    pass -> place.drawn(pass, camera));
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            picture = new Picture(canvas.width(), canvas.pixels());
        }
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve("village-from-the-sky.png").toFile());
        int drawn = 0;
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                drawn += picture.at(x, y) == 0xffc9dbe8 ? 0 : 1;
            }
        }
        assertTrue(drawn > 3000, "walls and houses drawn over the sky: " + drawn + " pixels");
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
