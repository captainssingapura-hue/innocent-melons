package com.example.fxsuite.vulkan.bench.card;

import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Finish;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
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
 * The courier drawn on the card, seen from in front: by day its body is teal against the sky, its feet on the
 * ground; and in no light at all it is black, but for its visor, which is lit from within and shows its own colour
 * exactly. The validation layer has nothing to say of either. The day's picture is kept in {@code target/card}.
 */
class CourierOnTheCardTest {

    private static final int SIZE = 200;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Courier courier = new Courier();
    private final Body body = Body.standing(new Vec3(0, 0, 0), 0);
    private final Pose still = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    /** From four metres in front of it, level with its chest, all but looking straight at it. */
    private final Camera camera = new Orbit(new com.example.fxsuite.vulkan.space.Vec3(0, 150, 0), 180, 2, 400)
            .camera(40, 5);

    @Test
    void byDayItsBodyIsTealAgainstTheSkyAndItsFeetAreOnTheGround() throws IOException {
        final Picture picture = drawn(courier, day.light());
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve("courier.png").toFile());
        assertEquals(0xffc9dbe8, picture.at(4, 4), "the sky in a corner");
        final int middle = SIZE / 2;
        assertTrue(picture.part(middle, middle, 2) > picture.part(middle, middle, 0) + 40, "its chest, teal");
        assertTrue(picture.part(4, SIZE - 4, 1) > picture.part(4, SIZE - 4, 2) + 20, "the ground, green, below");
    }

    @Test
    void inNoLightAtAllOnlyItsVisorShowsAndShowsItsOwnColour() {
        final Picture picture = drawn(courier,
                new Light(day.light().sunTowards(), day.sky().times(0), day.sky().times(0)));
        final int middle = SIZE / 2;
        assertEquals(0xff000000, picture.at(middle, middle), "its chest, black");
        // its visor is 33 cm above its chest, and the picture is some 290 cm tall
        assertEquals(0xff7df9ff, picture.at(middle, middle - 23), "its visor, lit from within");
    }

    @Test
    void byNightItsChestIsDimAndBlueAndItsVisorStandsOutBright() {
        final Picture picture = drawn(courier, day.night().light());
        final int middle = SIZE / 2;
        final int chest = picture.part(middle, middle, 0) + picture.part(middle, middle, 1)
                + picture.part(middle, middle, 2);
        final int visor = picture.part(middle, middle - 23, 0) + picture.part(middle, middle - 23, 1)
                + picture.part(middle, middle - 23, 2);
        assertTrue(chest > 0 && chest < 150, "its chest, dim but seen: " + chest);
        assertTrue(picture.part(middle, middle, 2) > picture.part(middle, middle, 0), "and blue");
        assertTrue(visor > 600, "its visor, lit from within, as bright as by day: " + visor);
    }

    @Test
    void finishedInMetalItsChestIsGoldByDayAndItsLegsGrey() {
        final Picture picture = drawn(new Courier(190, Finish.GILT), day.light());
        final int middle = SIZE / 2;
        final int chest = picture.at(middle, middle + 15);
        assertTrue(picture.part(middle, middle + 15, 0) > 170 && picture.part(middle, middle + 15, 0)
                > picture.part(middle, middle + 15, 1) + 25 && picture.part(middle, middle + 15, 1)
                > picture.part(middle, middle + 15, 2) + 60, "its chest, gold: " + Integer.toHexString(chest));
        // a leg, 10 cm to one side of its middle and 55 cm up
        final int leg = picture.at(middle + 7, middle + 65);
        assertTrue(Math.abs(picture.part(middle + 7, middle + 65, 0) - picture.part(middle + 7, middle + 65, 2)) < 30,
                "a leg, grey: " + Integer.toHexString(leg));
    }

    private Picture drawn(final Courier of, final Light light) {
        try (Card card = new Card();
             Canvas canvas = card.canvas(SIZE, SIZE);
             Cast cast = new Cast(card, laid)) {
            canvas.drawn(day.sky(), new Framed(laid, camera, light).filling(1), pass -> {
                cast.ground(pass, camera);
                cast.figure(pass, of.placed(body, still), camera);
            });
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            return new Picture(canvas.width(), canvas.pixels());
        }
    }
}
