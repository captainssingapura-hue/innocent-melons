package com.example.fxsuite.places.yard;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
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
 * The house drawn on the card, its front seen from six metres off, to one side of its doorway: grey brick above,
 * paler stone at its foot, the sky over it; drawn in one call with nothing said by the validation layer. The picture
 * is kept in {@code target/card}.
 */
class HomesteadOnTheCardTest {

    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    /** Looking at the front wall two metres right of the doorway's middle, a metre and a half up. */
    private final Camera camera = new Orbit(new Vec3(200, 150, 730), 0, 5, 600).camera(48, 10);

    @Test
    void itsFrontIsGreyBrickOverPalerStoneUnderTheSky() throws IOException {
        final Picture picture;
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Homestead homestead = new Homestead(card, laid)) {
            canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDTH / HEIGHT),
                    pass -> homestead.drawn(pass, camera));
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            picture = new Picture(canvas.width(), canvas.pixels());
        }
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve("house-front.png").toFile());
        assertEquals(0xffc9dbe8, picture.at(4, 4), "the sky in a corner");
        final int brick = brightness(picture, WIDTH / 2, HEIGHT / 2);
        final int stone = brightness(picture, WIDTH / 2, HEIGHT / 2 + 34);
        assertTrue(brick > 150 && brick < 480, "brick, neither sky nor shadow: " + brick);
        assertTrue(Math.abs(picture.part(WIDTH / 2, HEIGHT / 2, 0) - picture.part(WIDTH / 2, HEIGHT / 2, 2)) < 40,
                "and grey");
        assertTrue(stone > brick + 30, "paler stone at its foot: " + stone + " over " + brick);
    }

    private int brightness(final Picture picture, final int x, final int y) {
        return picture.part(x, y, 0) + picture.part(x, y, 1) + picture.part(x, y, 2);
    }
}
