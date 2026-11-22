package com.example.fxsuite.vulkan.bench.card;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.render.Shadows;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
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
 * Shadows on the card, by the bench's day: a grey box a metre each way stands on grey ground. With shadows, the
 * ground behind the box from the sun is much darker than the ground beside it, and the box's top, which nothing
 * stands over, is as bright as with none; with none, the ground is one brightness all over, as it was before there
 * were shadows. Found by rays, on a card that answers them, the shadow lies where the sun's picture has it. The
 * validation layer has nothing to say of any of it. The pictures are kept in {@code target/card}.
 */
class ShadowsOnTheCardTest {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private static final int PIXELS = 1024;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Surface grey = new Surface(new Colour("#b0b0b0"), List.of(), new Colour(0, 0, 0), 1);
    /** From above and to one side, looking down at the ground the box's shadow falls on. */
    private final Camera camera = new Orbit(new Vec3(30, 0, 20), 200, 55, 600).camera(48, 10);
    /** The sun's light on eight metres of ground round the box. */
    private final Beam beam = new Beam(new Vec3(0, 0, 0), day.light().sunTowards(), 800, 800).settled(PIXELS);
    /** On the ground: behind the box from the sun; and clear of its shadow. On the box: the middle of its top. */
    private final Vec3 shaded = new Vec3(70, 0, 40);
    private final Vec3 sunny = new Vec3(120, 0, -60);
    private final Vec3 top = new Vec3(0, 100, 0);

    @Test
    void theGroundBehindTheBoxFromTheSunIsDarkAndWithNoShadowsItIsOneBrightnessAllOver() throws IOException {
        final Picture with;
        final Picture without;
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Shadows shadows = new Shadows(card, laid, PIXELS);
             Formed box = new Formed(card, new Look(laid, grey), new Cuboid(1, 1, 1).formed())) {
            final Framed framed = new Framed(laid, camera, day.light());
            final double aspect = (double) WIDTH / HEIGHT;
            final Consumer<Pass> standing = pass -> box.drawn(pass, new Mat4(new Vec3(0, 50, 0))
                    .times(new Sized(100, 100, 100).matrix()), camera, 1);
            final Consumer<Pass> all = pass -> {
                box.drawn(pass, new Mat4(new Vec3(0, -50, 0)).times(new Sized(4000, 100, 4000).matrix()), camera, 1);
                standing.accept(pass);
            };
            canvas.drawn(day.sky(), framed.filling(aspect, beam, PIXELS), shadows.cast(framed, beam, standing), all);
            with = new Picture(canvas.width(), canvas.pixels());
            canvas.drawn(day.sky(), framed.filling(aspect), all);
            without = new Picture(canvas.width(), canvas.pixels());
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        ImageIO.write(with.image(), "png", folder.resolve("box-shadow.png").toFile());
        assertTrue(bright(with, shaded) < bright(with, sunny) - 150,
                "in the box's shadow: " + bright(with, shaded) + " beside " + bright(with, sunny));
        assertEquals(bright(without, sunny), bright(with, sunny), 3, "clear of the shadow, as with none");
        assertEquals(bright(without, sunny), bright(without, shaded), 3, "with none, one brightness all over");
        assertEquals(bright(without, top), bright(with, top), 3, "the box's top shades nothing of itself");
    }

    @Test
    void foundEitherWayTheCardHasTheShadowLiesInTheSamePlaceAndAWayItHasNotIsAnsweredWithTheSunsPicture()
            throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT)) {
            final Laid asking = new Laid().asking(card.rays());
            try (Shaded shadows = new Shaded(card, asking, PIXELS);
                 Formed box = new Formed(card, new Look(asking, grey), new Cuboid(1, 1, 1).formed())) {
                assertEquals(card.rays() ? List.of(Shadowing.PICTURE, Shadowing.RAYS) : List.of(Shadowing.PICTURE),
                        shadows.offered());
                final Framed framed = new Framed(asking, camera, day.light());
                final Consumer<Pass> standing = pass -> box.drawn(pass, new Mat4(new Vec3(0, 50, 0))
                        .times(new Sized(100, 100, 100).matrix()), camera, 1);
                final Consumer<Pass> all = pass -> {
                    box.drawn(pass, new Mat4(new Vec3(0, -50, 0)).times(new Sized(4000, 100, 4000).matrix()),
                            camera, 1);
                    standing.accept(pass);
                };
                // every way there is, whether or not this card has it
                for (final Shadowing by : Shadowing.values()) {
                    shadows.drawn(canvas, day.sky(), framed, by, beam, standing, all);
                    final Picture found = new Picture(canvas.width(), canvas.pixels());
                    ImageIO.write(found.image(), "png", folder.resolve("box-shadow-by-" + by + ".png").toFile());
                    assertTrue(bright(found, shaded) < bright(found, sunny) - 150,
                            by + ", in the box's shadow: " + bright(found, shaded) + " beside "
                                    + bright(found, sunny));
                    assertTrue(bright(found, top) > bright(found, shaded) + 150, by + ", the box's top in the sun");
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** How bright the picture is where {@code point} of the world falls in it: its red, green and blue together. */
    private int bright(final Picture picture, final Vec3 point) {
        final Vec3 shown = camera.seen((double) WIDTH / HEIGHT).clip(camera.from(point)).shown();
        final int x = (int) ((shown.x() + 1) / 2 * WIDTH);
        final int y = (int) ((shown.y() + 1) / 2 * HEIGHT);
        return picture.part(x, y, 0) + picture.part(x, y, 1) + picture.part(x, y, 2);
    }
}
