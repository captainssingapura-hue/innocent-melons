package com.example.fxsuite.texture.bench;

import com.example.fxsuite.texture.brick.Bond;
import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A swatch of brick wall on the lab's bench, three metres across: looked at from a hand's breadth, where every brick
 * is its own and specked; whole, from across a room; as the game would show it from further and further off, its
 * joints and then its bricks fading into one colour; and in each bond. Each picture is kept in {@code target/worked}
 * to be looked at.
 */
class SwatchTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final Swatch wall = new Swatch(Brickwork.COMMON_RED.wall(Bond.STRETCHER).surface(), 300, 225);

    @Test
    void closeToEveryBrickIsItsOwnWithMortarBetween() throws IOException {
        final Picture close = wall.seen(60, 1, WIDTH, HEIGHT);
        kept(close, "brick-wall-close.png");
        assertTrue(spread(close, 1) > 60, "from pale mortar to dark brick along a line across it: " + spread(close, 1));
    }

    @Test
    void wholeItStandsInTheMiddleWithTheSkyOverIt() throws IOException {
        final Picture whole = wall.seen(420, 1, WIDTH, HEIGHT);
        kept(whole, "brick-wall-whole.png");
        assertEquals(0xffc9dbe8, whole.at(WIDTH / 2, 4), "the sky over it");
        assertTrue(whole.part(WIDTH / 2, HEIGHT / 2, 0) > whole.part(WIDTH / 2, HEIGHT / 2, 2),
                "red-brown in the middle");
    }

    @Test
    void fromFurtherAndFurtherOffItsJointsAndThenItsBricksFadeIntoOneColour() throws IOException {
        final Picture whole = wall.seen(420, 1, WIDTH, HEIGHT);
        final Picture further = wall.seen(420, 8, WIDTH, HEIGHT);
        final Picture furthest = wall.seen(420, 60, WIDTH, HEIGHT);
        kept(further, "brick-wall-as-from-8-times-as-far.png");
        kept(furthest, "brick-wall-as-from-60-times-as-far.png");
        assertTrue(spread(whole, 0) > spread(further, 0) && spread(further, 0) > spread(furthest, 0),
                "less and less between its lightest and its darkest: " + spread(whole, 0) + ", " + spread(further, 0)
                        + ", " + spread(furthest, 0));
        assertTrue(spread(furthest, 0) <= 2, "and at last none to speak of: " + spread(furthest, 0));
    }

    @Test
    void theCoarserLookForFarOffIsThatOneColourAndNothingElse() throws IOException {
        final Picture far = wall.coarser(60, 420, WIDTH, HEIGHT);
        kept(far, "brick-wall-far-look.png");
        assertEquals(0, spread(far, 0), "one colour all over");
        final Picture furthest = wall.seen(420, 60, WIDTH, HEIGHT);
        for (int part = 0; part < 3; part++) {
            assertEquals(furthest.part(WIDTH / 2, HEIGHT / 2, part), far.part(WIDTH / 2, HEIGHT / 2, part), 3,
                    "the colour the fine look has faded to by then");
        }
    }

    @Test
    void itIsLaidInEachBond() throws IOException {
        for (final Bond bond : Bond.values()) {
            final Picture laid = new Swatch(Brickwork.COMMON_RED.wall(bond).surface(), 300, 225)
                    .seen(200, 1, WIDTH, HEIGHT);
            kept(laid, "brick-wall-" + bond.name().toLowerCase() + ".png");
            assertTrue(spread(laid, 1) > 40, bond + ": brick and mortar both in it");
        }
    }

    /** How far apart the lightest and the darkest are, in red, green or blue, along a line across the wall's middle. */
    private int spread(final Picture picture, final int part) {
        int least = 255;
        int most = 0;
        for (int x = WIDTH / 4; x <= 3 * WIDTH / 4; x++) {
            least = Math.min(least, picture.part(x, HEIGHT / 2 + 7, part));
            most = Math.max(most, picture.part(x, HEIGHT / 2 + 7, part));
        }
        return most - least;
    }

    private void kept(final Picture picture, final String name) throws IOException {
        final Path folder = Path.of("target", "worked");
        Files.createDirectories(folder);
        ImageIO.write(picture.image(), "png", folder.resolve(name).toFile());
    }
}
