package com.example.fxsuite.vulkan.bench.card;

import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import com.example.fxsuite.vulkan.worked.Worked;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The watermelon drawn on the card, by the shaders Java wrote, against Java's own working of the same picture from
 * the same terms: the two agree at every pixel, the fruit's edge included; so they do with the fruit shown as from six
 * and from forty times as far, its stripes fading; and so do the far look's, where the stripes are their average.
 * The validation layer has nothing to say of any. Each picture the card drew is kept in {@code target/card} to be
 * looked at beside Java's in {@code target/worked}.
 */
class OnTheCardTest {

    private static final int WIDTH = 480;
    private static final int HEIGHT = 360;
    /**
     * How far apart the card's colour and Java's may be at a pixel and still be the same, of 255. On the card this
     * was first drawn on they were never further apart than 2: the card works in singles, Java in doubles.
     */
    private static final int CLOSE = 3;

    private final Daylight day = new Daylight();
    private final Watermelon watermelon = new Watermelon();
    private final Mesh formed = watermelon.formed(96);
    private final Vec3 placed = new Vec3(0, watermelon.rests(), 0);
    private final Camera camera = new Orbit(placed, -35, 22, 95).camera(40, 1);
    private final Look look = new Look(new Laid(), watermelon.rind());

    @Test
    void theCardsWatermelonIsJavasAtEveryPixel() throws IOException {
        final Picture card = onTheCard(look, 1);
        kept(card, "watermelon-fine.png");
        assertEquals(0xffc9dbe8, card.at(4, 4), "the sky in a corner");
        agree(card, worked(look, 1), "the fine look");
    }

    @Test
    void theCardsWatermelonAsFromSixAndFromFortyTimesAsFarIsJavasToo() throws IOException {
        final Picture six = onTheCard(look, 6);
        final Picture forty = onTheCard(look, 40);
        kept(six, "watermelon-as-from-6-times-as-far.png");
        kept(forty, "watermelon-as-from-40-times-as-far.png");
        agree(six, worked(look, 6), "as from six times as far");
        agree(forty, worked(look, 40), "as from forty times as far");
    }

    @Test
    void theFarLooksWatermelonIsJavasToo() throws IOException {
        final Look far = look.coarser(100, new Averaged(formed));
        final Picture card = onTheCard(far, 1);
        kept(card, "watermelon-far-look.png");
        agree(card, worked(far, 1), "the far look");
    }

    /** The picture as the card draws it: at twice the size and halved, as Java's is, so the two are alike. */
    private Picture onTheCard(final Look of, final double coarsened) {
        try (Card card = new Card();
             Canvas canvas = card.canvas(2 * WIDTH, 2 * HEIGHT);
             Formed fruit = new Formed(card, of, formed)) {
            canvas.drawn(day.sky(), new Framed(of.laid(), camera, day.light()).filling((double) WIDTH / HEIGHT),
                    pass -> fruit.drawn(pass, placed, camera, coarsened));
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            return new Picture(canvas.width(), canvas.pixels()).halved();
        }
    }

    /** Java's working of the same: at twice the size too, each pixel covering what the card's covers, and halved. */
    private Picture worked(final Look of, final double coarsened) {
        return new Worked(of, formed, placed, camera, day.light(), day.sky(), coarsened).picture(2 * WIDTH, 2 * HEIGHT)
                .halved();
    }

    /** Fails where any pixel at all, the fruit's edge included, differs by more than a little. */
    private void agree(final Picture card, final Picture java, final String what) {
        int apart = 0;
        int furthest = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int most = 0;
                for (int part = 0; part < 3; part++) {
                    most = Math.max(most, Math.abs(card.part(x, y, part) - java.part(x, y, part)));
                }
                apart += most > CLOSE ? 1 : 0;
                furthest = Math.max(furthest, most);
            }
        }
        assertEquals(0, apart, what + ": " + apart + " pixels apart by more than " + CLOSE
                + ", the furthest by " + furthest);
    }

    private void kept(final Picture picture, final String name) throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve(name).toFile());
    }
}
