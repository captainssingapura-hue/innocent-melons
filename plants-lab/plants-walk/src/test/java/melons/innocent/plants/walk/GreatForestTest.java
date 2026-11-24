package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.plants.tree.Stage;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The great forest. Its trees are worked out from each cell alone: the same every time, each within its cell, each
 * one of the trees grown. A cell near the eye is trees, and one further off is baked at boughs, then limbs, then its
 * trunks alone, coming back finer only a tenth nearer; from high above the whole is far. And on the card it is drawn
 * from among its trees, from low over them, from high above and from outside its edge, each once everything wanted
 * has been baked, with nothing said by the validation layer: the pictures are written to {@code
 * target/card/great-forest-*.png}, and what is on the card and how long a frame takes are told.
 */
class GreatForestTest {

    private static final long PATIENCE = 240_000;
    private final Forested forest = new Forested(200_000, 5_000, 6, 20261124L);

    @Test
    void itsTreesAreWorkedOutFromEachCellAloneTheSameEveryTime() {
        assertEquals(1_600, forest.cells());
        int trees = 0;
        for (final int cell : new int[] {0, 39, 820, 1_599}) {
            final List<WoodDrawn.Planted> once = forest.planted(cell);
            final List<WoodDrawn.Planted> again = forest.planted(cell);
            assertEquals(49, once.size(), "seven rows of seven in fifty metres");
            for (int tree = 0; tree < once.size(); tree++) {
                final WoodDrawn.Planted one = once.get(tree);
                assertEquals(one.placed().at(0, 3), again.get(tree).placed().at(0, 3), "the same every time");
                assertEquals(one.grown(), again.get(tree).grown());
                final Vec3 from = new Vec3(one.placed().at(0, 3), 0, one.placed().at(2, 3))
                        .minus(forest.corner(cell));
                assertTrue(from.x() > 0 && from.x() < 5_000 && from.z() > 0 && from.z() < 5_000, "within its cell");
                assertTrue(one.grown() >= 0 && one.grown() < 6);
            }
            trees += once.size();
        }
        assertEquals(196, trees);
        assertEquals(new Vec3(-100_000, 0, -100_000), forest.corner(0));
    }

    @Test
    void aCellIsTreesNearAndBakedCoarserByRingsBeyondComingBackFinerOnlyATenthNearer() {
        final Ringed ringed = new Ringed(forest, new Rings(150, 350, 800));
        final int cell = 820;
        final Vec3 middle = forest.middle(cell).plus(new Vec3(0, 750, 0));
        assertEquals(Optional.empty(), ringed.wanted(cell, middle.plus(new Vec3(10_000, 0, 0))), "trees, near");
        assertEquals(Optional.of(Stage.BOUGHS), ringed.wanted(cell, middle.plus(new Vec3(20_000, 0, 0))));
        assertEquals(Optional.of(Stage.BOUGHS), ringed.wanted(cell, middle.plus(new Vec3(14_500, 0, 0))),
                "not trees again just within the ring");
        assertEquals(Optional.empty(), ringed.wanted(cell, middle.plus(new Vec3(13_000, 0, 0))), "a tenth within");
        assertEquals(Optional.of(Stage.LIMBS), ringed.wanted(cell, middle.plus(new Vec3(50_000, 0, 0))));
        assertEquals(Optional.of(Stage.WHOLE), ringed.wanted(cell, middle.plus(new Vec3(90_000, 0, 0))));
        assertEquals(Optional.of(Stage.WHOLE), new Ringed(forest, new Rings(150, 350, 800))
                .wanted(cell, middle.plus(new Vec3(0, 100_000, 0))), "from high above, far");
    }

    @Test
    void itsCellsAreGatheredIntoTilesThatOpenAsTheEyeComesNearEachCellDrawnByOneArea() {
        final Tiered tiered = new Tiered(forest, new Rings(150, 350, 800));
        assertEquals(1_600 + 100 + 25, tiered.areas());
        assertEquals(64, tiered.cells(1_700).size(), "a wide tile of eight cells a side");
        assertEquals(16, tiered.cells(1_600).size(), "a middling one of four");
        assertEquals(List.of(0, 1_600, 1_700), tiered.over(0));
        assertEquals(new Vec3(-80_000, 0, -80_000), tiered.middle(1_700));
        tiered.chosen(new Vec3(0, 200_000, 0));
        for (int cell = 0; cell < forest.cells(); cell++) {
            assertTrue(tiered.owner(cell) >= 1_700 && tiered.want(tiered.owner(cell)) == Stage.WHOLE,
                    "from high above every cell is in a wide tile of trunks");
        }
        tiered.chosen(new Vec3(300, 170, 200));
        int trees = 0;
        for (int cell = 0; cell < forest.cells(); cell++) {
            final int owner = tiered.owner(cell);
            assertTrue(tiered.cells(owner).contains(cell), "drawn by an area it lies in");
            assertTrue(tiered.trees(cell) || tiered.want(owner) != null, "as trees, or by a mesh");
            trees += tiered.trees(cell) ? 1 : 0;
        }
        assertTrue(trees > 10 && trees < 60, "the cells within 150 m are trees: " + trees);
        assertEquals(Stage.WHOLE, tiered.want(1_700), "the far corner still one tile of trunks");
        assertTrue(tiered.trees(820) && tiered.want(tiered.over(820).get(2)) == null, "the middle, opened");
    }

    @Test
    void itIsDrawnFromAmongItsTreesLowOverThemHighAboveAndFromOutsideWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final List<String> named = List.of("among", "low-over", "high-above", "outside");
        final List<Camera> cameras = List.of(
                new Orbit(new Vec3(300, 170, 200), 30, -3, 300).camera(48, 10),
                new Orbit(new Vec3(0, 1_500, 0), 20, 14, 6_000).camera(48, 10),
                new Orbit(new Vec3(0, 0, 0), 20, 40, 150_000).camera(48, 10),
                new Orbit(new Vec3(0, 600, -100_000), 0, 6, 12_000).camera(48, 10));
        try (Card card = new Card(); Canvas canvas = card.canvas(1280, 720)) {
            final Laid laid = new Laid().asking(card.rays());
            try (GreatForestPlace place = new GreatForestPlace(card, laid);
                 Shaded shadows = new Shaded(card, laid, 4096)) {
                for (int view = 0; view < cameras.size(); view++) {
                    final Camera camera = cameras.get(view);
                    final long began = System.currentTimeMillis();
                    do {
                        place.readied(camera);
                        pictured(canvas, place, camera, laid, shadows);
                    } while (!place.settled() && System.currentTimeMillis() - began < PATIENCE);
                    assertTrue(place.settled(), "everything wanted baked: " + place.said());
                    ImageIO.write(pictured(canvas, place, camera, laid, shadows).image(), "png",
                            folder.resolve("great-forest-" + named.get(view) + ".png").toFile());
                    final long settledAt = System.currentTimeMillis();
                    final long timed = System.nanoTime();
                    for (int frame = 0; frame < 20; frame++) {
                        place.readied(camera);
                        pictured(canvas, place, camera, laid, shadows);
                    }
                    System.out.printf("GREAT FOREST: %s: %.1f ms a frame, settled in %.1f s; %s%n", named.get(view),
                            (System.nanoTime() - timed) / 1e6 / 20, (settledAt - began) / 1e3,
                            place.said());
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The place as {@code camera} sees it by day, its shadows found by rays where the card answers them. */
    private Picture pictured(final Canvas canvas, final GreatForestPlace place, final Camera camera, final Laid laid,
                             final Shaded shadows) {
        final Daylight day = new Daylight();
        final Framed framed = new Framed(laid, camera, day.light());
        final Vec3 under = camera.eye().plus(camera.ahead().times(3_000));
        final Beam beam = new Beam(new Vec3(under.x(), 0, under.z()), day.light().sunTowards(), 12_000, 12_000);
        final Consumer<Pass> casting = pass -> place.drawn(pass, camera);
        final Consumer<Pass> all = pass -> {
            place.drawn(pass, camera);
            place.seenThrough(pass, camera);
        };
        final Shadowing by = shadows.offered().getLast();
        shadows.drawn(canvas, day.sky(), framed, by, beam, casting, all);
        return new Picture(canvas.width(), canvas.pixels());
    }
}
