package melons.innocent.doors.walk;

import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Outline;
import melons.innocent.testkit.TestKit;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The doors on the card, worked by the list the walk's window offers: all shut, then each opened in turn, with what
 * is in the way said afresh as the leaves swing. The validation layer has nothing to say, and pictures of the wall
 * from before and behind and of each door close to, shut and open, are written to {@code target/card/}.
 */
class DoorsPlaceOnTheCardTest {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();

    @Test
    void theWindowsListOpensAndShutsEachDoorAndTheLeavesAreInTheWayWhereTheyAre() throws IOException {
        Files.createDirectories(Path.of("target", "card"));
        final Showing showing = new Showing();
        final Doors doors = new Doors(showing.doorways());
        final TestKit kit = new TestKit("tried", new BaseMap(Outline.SQUARE, 100, Grounds.TURF))
                .with((card, laying) -> new DoorsPlace(card, laying, doors, showing));
        final Camera before = new Orbit(new Vec3(0, 130, 500), 0, 7, 1_250).camera(48, 10);
        final Camera behind = new Orbit(new Vec3(0, 130, 500), 180, 7, 1_250).camera(48, 10);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Place place = kit.placing().apply(card, laid)) {
            final Dial working = place.dials().getFirst();
            assertEquals("Open or shut", working.name());
            assertEquals(4, working.steps().size());
            assertTrue(working.asking());
            final int inTheWay = place.inTheWay().blocks().size();
            pictured(canvas, place, before, "doors-shut");
            pictured(canvas, place, behind, "doors-shut-from-behind");
            for (int door = 0; door < 4; door++) {
                final String name = showing.doorways().get(door).name().replace("the ", "").replace(' ', '-');
                final Camera close = new Orbit(new Vec3((door - 1.5) * 300, 115, 500), door % 2 == 0 ? 22 : -22, 5,
                        360).camera(48, 10);
                pictured(canvas, place, close, name + "-shut");
                working.chosen(door);
                place.readied(before);
                for (int tick = 0; tick < 70; tick++) {
                    doors.ticked(new Vec3(0, 0, -900));
                }
                assertTrue(place.rearranged().isPresent(), "a leaf swinging, what is in the way is said afresh");
                pictured(canvas, place, close, name + "-opening");
                for (int tick = 0; tick < 200; tick++) {
                    doors.ticked(new Vec3(0, 0, -900));
                }
                pictured(canvas, place, close, name + "-open");
                assertTrue(place.said().contains(showing.doorways().get(door).name() + ": open"), place.said());
            }
            assertEquals(inTheWay, place.inTheWay().blocks().size(), "as many things in the way, the leaves moved");
            pictured(canvas, place, before, "doors-open");
            pictured(canvas, place, behind, "doors-open-from-behind");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    private void pictured(final Canvas canvas, final Place place, final Camera seen, final String name)
            throws IOException {
        canvas.drawn(day.sky(), new Framed(laid, seen, day.light()).filling((double) WIDTH / HEIGHT), pass -> {
            place.drawn(pass, seen);
            place.seenThrough(pass, seen);
        });
        final BufferedImage image = new com.example.fxsuite.vulkan.worked.Picture(canvas.width(), canvas.pixels())
                .image();
        ImageIO.write(image, "png", Path.of("target", "card", name + ".png").toFile());
    }
}
