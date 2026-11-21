package com.example.fxsuite.texture.bench;

import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.texture.stone.CoursedStone;
import com.example.fxsuite.texture.stone.Footed;
import com.example.fxsuite.texture.stone.RubbleWall;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The walls of an old house on the lab's bench: Chinese grey brick, adobe, rubble and stone in courses, each close to
 * and whole; and a house's wall as it stands, grey brick on courses of stone and adobe on rubble, whole and from far
 * off. Each picture is kept in {@code target/worked} to be looked at.
 */
class WallsTest {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    @Test
    void chineseGreyBrickIsGreyWithFineWhiteJoints() throws IOException {
        final Swatch wall = new Swatch(Brickwork.CHINESE_GREY.wall().surface(), 300, 225);
        final Picture close = wall.seen(70, 1, WIDTH, HEIGHT);
        kept(close, "chinese-grey-brick-close.png");
        kept(wall.seen(420, 1, WIDTH, HEIGHT), "chinese-grey-brick-whole.png");
        assertTrue(spread(close, 1) > 60, "from white lime to dark brick: " + spread(close, 1));
        assertTrue(Math.abs(close.part(WIDTH / 2, HEIGHT / 2, 0) - close.part(WIDTH / 2, HEIGHT / 2, 2)) < 25, "grey");
    }

    @Test
    void adobeIsEarthWithStrawInItAndThickMudJoints() throws IOException {
        final Swatch wall = new Swatch(Brickwork.ADOBE.wall().surface(), 300, 225);
        final Picture close = wall.seen(90, 1, WIDTH, HEIGHT);
        kept(close, "adobe-close.png");
        kept(wall.seen(420, 1, WIDTH, HEIGHT), "adobe-whole.png");
        assertTrue(close.part(WIDTH / 2, HEIGHT / 2, 0) > close.part(WIDTH / 2, HEIGHT / 2, 2) + 30, "earth");
    }

    @Test
    void rubbleIsStonesOfNoTwoShapesAndCoursedStoneLongBlocks() throws IOException {
        final Swatch rubble = new Swatch(new RubbleWall().surface(), 300, 225);
        final Picture close = rubble.seen(150, 1, WIDTH, HEIGHT);
        kept(close, "rubble-close.png");
        kept(rubble.seen(420, 1, WIDTH, HEIGHT), "rubble-whole.png");
        assertTrue(spread(close, 1) > 40, "from pale mortar to dark stone: " + spread(close, 1));
        final Picture coursed = new Swatch(new CoursedStone().wall().surface(), 300, 225).seen(420, 1, WIDTH, HEIGHT);
        kept(coursed, "coursed-stone-whole.png");
        assertTrue(spread(coursed, 1) > 15, "block told from block: " + spread(coursed, 1));
    }

    @Test
    void aHousesWallStandsOnStoneGreyBrickOnCoursesAndAdobeOnRubble() throws IOException {
        // two courses of stone, 61.6 cm, under grey brick; 60 cm of rubble under adobe
        final Surface brickHouse = new Footed(Brickwork.CHINESE_GREY.wall(), new CoursedStone().wall().surface(), 61.6)
                .surface();
        final Surface adobeHouse = new Footed(Brickwork.ADOBE.wall(), new RubbleWall().surface(), 60).surface();
        kept(new Swatch(brickHouse, 300, 225).seen(420, 1, WIDTH, HEIGHT), "grey-brick-on-coursed-stone.png");
        final Swatch adobe = new Swatch(adobeHouse, 300, 225);
        final Picture whole = adobe.seen(420, 1, WIDTH, HEIGHT);
        kept(whole, "adobe-on-rubble.png");
        final Picture far = adobe.seen(420, 60, WIDTH, HEIGHT);
        kept(far, "adobe-on-rubble-as-from-60-times-as-far.png");
        for (final Picture picture : new Picture[] {whole, far}) {
            // near the wall's foot, grey stone; high on it, earth
            final int low = picture.part(WIDTH / 2, HEIGHT / 2 + 95, 0) - picture.part(WIDTH / 2, HEIGHT / 2 + 95, 2);
            final int high = picture.part(WIDTH / 2, HEIGHT / 2 - 60, 0) - picture.part(WIDTH / 2, HEIGHT / 2 - 60, 2);
            assertTrue(high > low + 15, "earth over stone: " + high + " over " + low);
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
