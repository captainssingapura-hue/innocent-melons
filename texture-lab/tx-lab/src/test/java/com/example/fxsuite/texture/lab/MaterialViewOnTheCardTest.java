package com.example.fxsuite.texture.lab;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bench's view on the card. Every material of the tree drawn on a wall, a ball and a block, by day with
 * shadows, each with nothing said by the validation layer, and the materials told apart. Each shape drawn alone,
 * then all of them together, then none: only the shapes on show are drawn, so more of the picture is covered the
 * more of them there are, and with none there is only the ground and the sky. One material again without shadows
 * and by night, still with nothing said. Paint the colour the thing is said to be, red or blue, and steel the same
 * whichever is said. Pictures of each material, of each shape, of both paints and of glass are kept in
 * {@code target/card}.
 */
class MaterialViewOnTheCardTest {

    private static final int WIDTH = 480;
    private static final int HEIGHT = 300;

    private final Laid laid = new Laid();
    private final Daylight day = new Daylight();
    private final Path folder = Path.of("target", "card");

    @Test
    void everyMaterialOfTheTreeIsDrawnWithNothingSaidAndNoTwoAlike() throws IOException {
        Files.createDirectories(folder);
        final Map<String, Integer> seen = new LinkedHashMap<>();
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             MaterialView view = new MaterialView(card, laid)) {
            final Camera camera = new Orbit(new Stand().middle(), 18, 12, 900).camera(48, 10);
            for (final Leaf<?> leaf : new Materials().tree().leaves()) {
                view.drawn(canvas, new OnShow((Material) leaf, 1), camera, day, true);
                assertEquals(List.of(), card.heard(), leaf.title() + ": the validation layer has nothing to say");
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                ImageIO.write(picture.image(), "png", folder.resolve("material-" + leaf.name() + ".png").toFile());
                // the wall's face, a little left of the picture's middle: the material itself
                seen.put(leaf.title(), picture.at(WIDTH * 3 / 10, HEIGHT / 2));
                assertEquals(0xffc9dbe8, picture.at(4, 4), leaf.title() + ": the sky in a corner");
            }
            view.drawn(canvas, new OnShow(new Materials.Gold(), 4), camera, day.night(), false);
            assertEquals(List.of(), card.heard(), "by night and without shadows: nothing said either");
            assertEquals(0xff0b1026, new Picture(canvas.width(), canvas.pixels()).at(4, 4), "the night's sky");
        }
        assertEquals(40, seen.size());
        assertTrue(seen.values().stream().distinct().count() >= 34, "the materials are told apart: " + seen);
    }

    @Test
    void paintIsTheColourTheThingIsSaidToBeAndNothingElseIsChangedByThatColour() throws IOException {
        Files.createDirectories(folder);
        final Vec3 red = new Called("#a3151c").colour();
        final Vec3 blue = new Called("#1c3f8f").colour();
        final List<Shape> sheets = List.of(Shape.BALL, Shape.CURVED, Shape.PANE);
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             MaterialView view = new MaterialView(card, laid)) {
            final Camera camera = new Orbit(new Stand(sheets).middle(), 18, 12, 700).camera(48, 10);
            final Map<Vec3, Integer> painted = new LinkedHashMap<>();
            final Map<Vec3, Integer> steel = new LinkedHashMap<>();
            for (final Vec3 tint : List.of(red, blue)) {
                view.drawn(canvas, new OnShow(new Materials.GlossPaint(), 1, sheets, tint), camera, day, true);
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                painted.put(tint, picture.at(WIDTH / 2, HEIGHT * 6 / 10));
                ImageIO.write(picture.image(), "png",
                        folder.resolve("paint-" + (tint.equals(red) ? "red" : "blue") + ".png").toFile());
                view.drawn(canvas, new OnShow(new Materials.Steel(), 1, sheets, tint), camera, day, true);
                steel.put(tint, new Picture(canvas.width(), canvas.pixels()).at(WIDTH / 2, HEIGHT * 6 / 10));
            }
            final int reddish = painted.get(red);
            final int bluish = painted.get(blue);
            assertTrue((reddish >> 16 & 0xff) > (reddish & 0xff) + 40,
                    "red paint is red: " + Integer.toHexString(reddish));
            assertTrue((bluish & 0xff) > (bluish >> 16 & 0xff) + 40,
                    "blue paint is blue: " + Integer.toHexString(bluish));
            assertEquals(steel.get(red), steel.get(blue), "steel is steel, whatever colour the thing is said to be");
            view.drawn(canvas, new OnShow(new Materials.Glass(), 1, sheets), camera, day, true);
            ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                    folder.resolve("glass-sheets.png").toFile());
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
        }
    }

    @Test
    void metallicPaintGlittersSeenCloseAndIsOneSoftShineSeenAsFromFurtherOff() throws IOException {
        Files.createDirectories(folder);
        final Material metallic = new Materials.MetallicPaint();
        final List<Shape> ball = List.of(Shape.BALL);
        try (Card card = new Card();
             Canvas canvas = card.canvas(1200, 750);
             MaterialView view = new MaterialView(card, laid)) {
            // close enough to the ball for a pixel to be well within a flake
            final Camera close = new Orbit(new Stand(ball).middle(), 18, 30, 95).camera(48, 1);
            view.drawn(canvas, new OnShow(metallic, 1, ball), close, day, true);
            final Picture near = new Picture(canvas.width(), canvas.pixels());
            ImageIO.write(near.image(), "png", folder.resolve("metallic-close.png").toFile());
            view.drawn(canvas, new OnShow(metallic, 16, ball), close, day, true);
            final Picture far = new Picture(canvas.width(), canvas.pixels());
            ImageIO.write(far.image(), "png", folder.resolve("metallic-close-as-from-far.png").toFile());
            final int glitter = speckled(near);
            assertTrue(glitter > 500, "seen close, flakes stand out from what is beside them: " + glitter);
            assertTrue(speckled(far) < glitter / 20, "as from far, they have run together: " + speckled(far));
            final List<Shape> sheets = List.of(Shape.BALL, Shape.CURVED, Shape.PANE);
            final Camera back = new Orbit(new Stand(sheets).middle(), 24, 14, 550).camera(48, 10);
            view.drawn(canvas, new OnShow(metallic, 1, sheets), back, day, true);
            ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                    folder.resolve("paint-metallic.png").toFile());
            view.drawn(canvas, new OnShow(new Materials.GlossPaint(), 1, sheets), back, day, true);
            ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                    folder.resolve("paint-gloss.png").toFile());
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
        }
    }

    @Test
    void theCarBodyIsOfTheMaterialOnShowAndItsWindowsAreOfGlassWhateverThatIs() throws IOException {
        Files.createDirectories(folder);
        final List<Shape> car = List.of(Shape.CAR);
        try (Card card = new Card();
             Canvas canvas = card.canvas(1200, 750);
             MaterialView view = new MaterialView(card, laid)) {
            // from its near side, a little ahead of it and above
            final Camera camera = new Orbit(new Stand(car).middle(), 28, 14, 620).camera(48, 10);
            final Map<String, Picture> seen = new LinkedHashMap<>();
            seen.put("car-gloss-red", pictured(view, canvas, camera,
                    new OnShow(new Materials.GlossPaint(), 1, car)));
            seen.put("car-metallic-blue", pictured(view, canvas, camera,
                    new OnShow(new Materials.MetallicPaint(), 1, car, new Called("#1c3f8f").colour())));
            seen.put("car-steel", pictured(view, canvas, camera, new OnShow(new Materials.Steel(), 1, car)));
            final Camera behind = new Orbit(new Stand(car).middle(), 150, 18, 620).camera(48, 10);
            seen.put("car-metallic-silver-behind", pictured(view, canvas, behind,
                    new OnShow(new Materials.MetallicPaint(), 1, car, new Called("#b9bec4").colour())));
            for (final Map.Entry<String, Picture> one : seen.entrySet()) {
                ImageIO.write(one.getValue().image(), "png", folder.resolve(one.getKey() + ".png").toFile());
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
            // somewhere the paints differ, the body; and somewhere they are alike though not sky or ground, the glass
            final Picture red = seen.get("car-gloss-red");
            final Picture blue = seen.get("car-metallic-blue");
            final int ground = red.at(20, red.height() - 20);
            int body = 0;
            int glass = 0;
            int beyond = 0;
            for (int down = 0; down < red.height(); down++) {
                for (int across = 0; across < red.width(); across++) {
                    final int here = red.at(across, down);
                    final boolean alike = here == blue.at(across, down);
                    final boolean dark = (here >> 16 & 0xff) + (here >> 8 & 0xff) + (here & 0xff) < 200;
                    body += alike ? 0 : 1;
                    glass += alike && dark ? 1 : 0;
                    beyond += alike && !dark && here != ground && here != 0xffc9dbe8 ? 1 : 0;
                }
            }
            assertTrue(body > 20_000, "the body is of the paint on show: " + body + " pixels differ");
            assertTrue(glass > 5_000, "through its windows its inside is seen, dark under either paint: " + glass);
            assertTrue(beyond > 1_000, "and through two windows the ground beyond it, a little darkened: " + beyond);
        }
    }

    @Test
    void glassIsSeenThroughAndCastsNoShadowAndWhatIsBehindItIsDrawnBeforeIt() throws IOException {
        Files.createDirectories(folder);
        // a wall, and a pane in front of it as the eye sees them, though it comes after it in the row
        final List<Shape> row = List.of(Shape.WALL, Shape.PANE);
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             MaterialView view = new MaterialView(card, laid)) {
            final Stand stand = new Stand(row);
            // from the pane's end of the row, looking along it: the pane nearer, the wall behind it
            final Camera along = new Orbit(stand.stands(Shape.PANE), 80, 6, 420).camera(48, 10);
            final Picture glass = pictured(view, canvas, along, new OnShow(new Materials.Glass(), 1, row));
            ImageIO.write(glass.image(), "png", folder.resolve("glass-in-a-row.png").toFile());
            final Picture steel = pictured(view, canvas, along, new OnShow(new Materials.Steel(), 1, row));
            int through = 0;
            for (int down = 0; down < glass.height(); down++) {
                for (int across = 0; across < glass.width(); across++) {
                    final boolean solid = steel.at(across, down) != 0xffc9dbe8 && steel.at(across, down)
                            != steel.at(4, HEIGHT - 4);
                    through += solid && glass.at(across, down) != steel.at(across, down) ? 1 : 0;
                }
            }
            assertTrue(through > 2_000, "where steel hides what is behind it, glass does not: " + through);
            // a ball alone: in steel it shades the ground beside it, in glass it shades nothing
            final List<Shape> ball = List.of(Shape.BALL);
            final Camera above = new Orbit(new Stand(ball).middle(), 18, 40, 500).camera(48, 10);
            final Picture bare = pictured(view, canvas, above, new OnShow(new Materials.Steel(), 1, List.of()));
            final Picture shading = pictured(view, canvas, above, new OnShow(new Materials.Steel(), 1, ball));
            final Picture clear = pictured(view, canvas, above, new OnShow(new Materials.Glass(), 1, ball));
            ImageIO.write(clear.image(), "png", folder.resolve("glass-ball.png").toFile());
            int shaded = 0;
            int unshaded = 0;
            for (int down = 0; down < bare.height(); down++) {
                for (int across = 0; across < bare.width(); across++) {
                    final int ground = bare.at(across, down);
                    final int there = shading.at(across, down);
                    // darker than the bare ground and still green: the ball's shadow, not the ball
                    final boolean darker = (there & 0xff00) < (ground & 0xff00) - 0x1000
                            && (there >> 8 & 0xff) > (there & 0xff) + 12;
                    shaded += darker ? 1 : 0;
                    unshaded += darker && clear.at(across, down) == ground ? 1 : 0;
                }
            }
            assertTrue(shaded > 300, "the steel ball shades the ground: " + shaded);
            assertTrue(unshaded > shaded / 2, "where it did, the glass ball leaves the ground in the sun: " + unshaded);
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
        }
    }

    @Test
    void eachKindOfGroundIsPicturedOnTheKnollFromAPacesHeightAndFromTheSkyAndNoTwoAreAlike() throws IOException {
        Files.createDirectories(folder);
        final List<Shape> knoll = List.of(Shape.KNOLL);
        final List<Material> grounds = List.of(new Materials.Turf(), new Materials.Topsoil(), new Materials.Sand(),
                new Materials.Gravel(), new Materials.Bedrock(), new Materials.Mud());
        try (Card card = new Card();
             Canvas canvas = card.canvas(1200, 750);
             MaterialView view = new MaterialView(card, laid)) {
            // standing a few paces off, the eye a person's height up; and from well above, the whole knoll
            final Camera afoot = new Orbit(new Vec3(0, 110, 0), 200, 9, 620).camera(48, 10);
            final Camera above = new Orbit(new Vec3(0, 60, 0), 215, 42, 1500).camera(48, 10);
            final Map<String, Integer> seen = new LinkedHashMap<>();
            for (final Material ground : grounds) {
                final Picture near = pictured(view, canvas, afoot, new OnShow(ground, 1, knoll));
                ImageIO.write(near.image(), "png",
                        folder.resolve("ground-" + ((Leaf<?>) ground).name() + "-afoot.png").toFile());
                final Picture far = pictured(view, canvas, above, new OnShow(ground, 1, knoll));
                ImageIO.write(far.image(), "png",
                        folder.resolve("ground-" + ((Leaf<?>) ground).name() + "-from-above.png").toFile());
                seen.put(ground.title(), far.at(600, 420));
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
            assertEquals(6, seen.values().stream().distinct().count(), "no two kinds of ground alike: " + seen);
        }
    }

    @Test
    void eachBodyMaterialIsPicturedOnTheShapesThatSuitItInTheColourItBeginsIn() throws IOException {
        Files.createDirectories(folder);
        try (Card card = new Card();
             Canvas canvas = card.canvas(900, 600);
             MaterialView view = new MaterialView(card, laid)) {
            int pictured = 0;
            for (final Leaf<?> leaf : new Materials().tree().leaves()) {
                if (!leaf.lineage().contains(new Materials.Bodily())) {
                    continue;
                }
                final Suited suited = new Suited((Material) leaf);
                final Camera close = new Orbit(new Stand(suited.shapes()).middle(), 20, 10, 260).camera(48, 10);
                final Picture picture = pictured(view, canvas, close,
                        new OnShow((Material) leaf, 1, suited.shapes(), suited.colour()));
                ImageIO.write(picture.image(), "png", folder.resolve("body-" + leaf.name() + ".png").toFile());
                pictured++;
            }
            assertEquals(13, pictured, "every material of a body");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
        }
    }

    /** {@code shown}, drawn with shadows as {@code camera} sees it: the picture. */
    private Picture pictured(final MaterialView view, final Canvas canvas, final Camera camera, final OnShow shown) {
        view.drawn(canvas, shown, camera, day, true);
        return new Picture(canvas.width(), canvas.pixels());
    }

    /**
     * How many pixels of {@code picture} are a good deal redder or greener than the one to their right: counted
     * over its middle, well within the ball that fills it, so the ball's edge against the ground is not among them.
     */
    private int speckled(final Picture picture) {
        int speckled = 0;
        for (int down = picture.height() / 12; down < picture.height() * 6 / 10; down++) {
            for (int across = picture.width() / 4; across < picture.width() * 3 / 4; across++) {
                final int here = picture.at(across, down);
                final int beside = picture.at(across + 1, down);
                speckled += (here >> 16 & 0xff) + (here >> 8 & 0xff)
                        > (beside >> 16 & 0xff) + (beside >> 8 & 0xff) + 60 ? 1 : 0;
            }
        }
        return speckled;
    }

    @Test
    void onlyTheShapesOnShowAreDrawnEachAloneAllTogetherAndNone() throws IOException {
        Files.createDirectories(folder);
        final Material steel = new Materials.Steel();
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             MaterialView view = new MaterialView(card, laid)) {
            int most = 0;
            for (final Shape shape : Shape.values()) {
                final int covered = covered(view, canvas, new OnShow(steel, 1, List.of(shape)), false);
                assertTrue(covered > 200, shape + " alone is seen: " + covered + " pixels");
                most = Math.max(most, covered);
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                        folder.resolve("shape-" + shape.name().toLowerCase() + ".png").toFile());
            }
            final int all = covered(view, canvas, new OnShow(steel, 1, Arrays.asList(Shape.values())), false);
            assertTrue(all > most, "all five cover more than any one: " + all + " against " + most);
            ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                    folder.resolve("shape-all.png").toFile());
            assertEquals(0, covered(view, canvas, new OnShow(steel, 1, List.of()), false), "none: nothing but ground");
            covered(view, canvas, new OnShow(steel, 1, Arrays.asList(Shape.values())), true);
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of any of it");
        }
    }

    /** How many pixels are neither sky nor bare ground, {@code shown} drawn from in front of the row's middle. */
    private int covered(final MaterialView view, final Canvas canvas, final OnShow shown, final boolean shadowed) {
        final Camera camera = new Orbit(new Stand(shown.shapes()).middle(), 18, 12, 1100).camera(48, 10);
        view.drawn(canvas, shown, camera, day, shadowed);
        final Picture picture = new Picture(canvas.width(), canvas.pixels());
        final int ground = picture.at(WIDTH / 2, HEIGHT - 3);
        int covered = 0;
        for (int down = 0; down < picture.height(); down++) {
            for (int across = 0; across < picture.width(); across++) {
                covered += picture.at(across, down) == 0xffc9dbe8 || picture.at(across, down) == ground ? 0 : 1;
            }
        }
        return covered;
    }
}
