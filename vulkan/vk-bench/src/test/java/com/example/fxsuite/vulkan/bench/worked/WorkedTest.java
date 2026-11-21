package com.example.fxsuite.vulkan.bench.worked;

import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.form.Mesh;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The watermelon's picture worked out by Java, from the terms its shader is written from: the fruit in the middle of
 * the picture, green and lit from above, the sky round it; the same seen as from further and further off, its stripes
 * fading to their average; and, far enough, the coarser look, which is that average and nothing else. Each picture
 * is kept in {@code target/worked} to be looked at.
 */
class WorkedTest {

    private static final int WIDTH = 480;
    private static final int HEIGHT = 360;
    /** The bench's sky and lights, as the kit's own view has them. */
    private final Daylight day = new Daylight();

    private final Watermelon watermelon = new Watermelon();
    private final Mesh formed = watermelon.formed(96);
    private final Vec3 placed = new Vec3(0, watermelon.rests(), 0);
    private final Camera camera = new Orbit(placed, -35, 22, 95).camera(40, 1);
    private final Look look = new Look(new Laid(), watermelon.rind());

    @Test
    void theFruitIsInTheMiddleGreenAndLitFromAboveWithTheSkyRoundIt() throws IOException {
        final Picture picture = worked(look, 1);
        kept(picture, "watermelon-fine.png");
        assertEquals(WIDTH, picture.width());
        assertEquals(HEIGHT, picture.height());
        assertEquals(0xffc9dbe8, picture.at(4, 4), "the sky in a corner");
        final int middle = HEIGHT / 2;
        assertTrue(picture.part(WIDTH / 2, middle, 1) > picture.part(WIDTH / 2, middle, 0)
                && picture.part(WIDTH / 2, middle, 1) > picture.part(WIDTH / 2, middle, 2), "green in the middle");
        assertTrue(brightness(picture, WIDTH / 2, middle - 50) > brightness(picture, WIDTH / 2, middle + 60),
                "lighter above its middle, where it faces the sun, than below");
    }

    @Test
    void seenAsFromFurtherOffItsStripesFadeTowardTheirAverage() throws IOException {
        final Picture fine = worked(look, 1);
        final Picture further = worked(look, 6);
        final Picture furthest = worked(look, 40);
        kept(further, "watermelon-as-from-6-times-as-far.png");
        kept(furthest, "watermelon-as-from-40-times-as-far.png");
        final double fineSpread = spread(fine);
        final double furtherSpread = spread(further);
        final double furthestSpread = spread(furthest);
        assertTrue(fineSpread > furtherSpread && furtherSpread > furthestSpread,
                "less and less between stripe and no stripe: " + fineSpread + ", " + furtherSpread + ", "
                        + furthestSpread);
    }

    @Test
    void farEnoughOffTheCoarserLookIsTheStripesAverageAndNothingElse() throws IOException {
        final Picture far = worked(look.coarser(100, new Averaged(formed)), 1);
        kept(far, "watermelon-far-look.png");
        assertTrue(spread(far) < spread(worked(look, 40)) + 1, "no stripes left in it at all");
        assertTrue(!look.coarser(100, new Averaged(formed)).fragment().code().contains("atan"),
                "and none in its shader");
        assertTrue(look.fragment().code().contains("atan"), "as there are in the fine look's");
    }

    private Picture worked(final Look of, final double coarsened) {
        // worked at twice the size and halved, so edges are smooth; each pixel then covers half as much
        return new Worked(of, formed, placed, camera, day.light(), day.sky(), 2 * coarsened)
                .picture(2 * WIDTH, 2 * HEIGHT)
                .halved();
    }

    /** How far apart the lightest and darkest green are along a line across the fruit, where it is evenly lit. */
    private double spread(final Picture picture) {
        int least = 255;
        int most = 0;
        for (int x = WIDTH / 2 - 60; x <= WIDTH / 2 + 60; x++) {
            least = Math.min(least, picture.part(x, HEIGHT / 2 - 20, 1));
            most = Math.max(most, picture.part(x, HEIGHT / 2 - 20, 1));
        }
        return most - least;
    }

    private int brightness(final Picture picture, final int x, final int y) {
        return picture.part(x, y, 0) + picture.part(x, y, 1) + picture.part(x, y, 2);
    }

    private void kept(final Picture picture, final String name) throws IOException {
        final Path folder = Path.of("target", "worked");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve(name).toFile());
    }
}
