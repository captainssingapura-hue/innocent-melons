package melons.innocent.testkit.loose;

import com.example.fxsuite.avatar.sim.Block;
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
import com.example.fxsuite.vulkan.bench.play.Step;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Ground;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The trial on the card, worked by the list the walk's window offers, which names every thing of it. As it stands,
 * its eight things are drawn and those at rest are in the way, a leaning one as a ramp. The stack's pillar chosen,
 * it goes at the next
 * readying and the stack comes down; the beam's right pillar chosen, the beam tips and leans on the other; that one
 * chosen, the beam lies flat; the lowest crate chosen, the two above it come down onto the ground; and the last
 * entry chosen, everything is back as it was described. Each time what is in the way is said afresh.
 * The validation layer has nothing to say, and a picture of each moment is written to {@code target/card/}.
 */
class LoosePlaceOnTheCardTest {

    private static final int WIDTH = 960;
    private static final int HEIGHT = 540;
    private static final String GROUND = new Ground().name();

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Camera camera = new Orbit(new Vec3(100, 80, 1_200), 0, 12, 2_600).camera(48, 10);
    /** The ramp and the ground before it, from near by. */
    private final Camera beside = new Orbit(new Vec3(1_560, 40, 1_150), 28, 13, 900).camera(48, 10);

    @Test
    void theWindowsListTakesAnyThingAwayAndBeginsAgain() throws IOException {
        Files.createDirectories(Path.of("target", "card"));
        final Trial trial = new Trial(-1, 12);
        final TestKit kit = new TestKit("tried", new BaseMap(Outline.CIRCLE, 100, Grounds.TURF))
                .with((card, laying) -> new LoosePlace(card, laying, trial));
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Place place = kit.placing().apply(card, laid)) {
            final Dial choice = place.dials().get(1);
            assertEquals("Take away", choice.name());
            assertEquals(23, choice.steps().size(), "each of the twenty-two things, and everything back");
            assertTrue(choice.asking(), "a list that asks: the same thing can be chosen twice running");
            ticked(place, trial, 240);
            assertEquals(0, trial.world().moving(), "as it stands, all is at rest once the crate has fallen");
            final int inTheWay = place.inTheWay().blocks().size();
            pictured(canvas, place, "loose-as-it-stands");
            pictured(canvas, place, beside, "loose-ramp-held");
            choice.chosen(choice.steps().indexOf("the stack's pillar"));
            ticked(place, trial, 2);
            choice.chosen(choice.steps().indexOf("the beam's right pillar"));
            ticked(place, trial, 40);
            assertTrue(trial.world().moving() > 0, "supports taken away, things are coming down");
            pictured(canvas, place, "loose-coming-down");
            ticked(place, trial, 300);
            assertEquals(Set.of("the beam's left pillar", GROUND), trial.world().under("the beam"));
            assertEquals(inTheWay - 2, place.inTheWay().blocks().size(), "two pillars gone");
            final Block ramp = place.inTheWay().blocks().stream()
                    .filter(block -> block.toe() < block.top() - 50 && block.halfWidth() == 20)
                    .findFirst().orElseThrow();
            assertEquals(20, ramp.toe(), 2, "the leaning beam is in the way as a ramp, from its end on the ground");
            assertEquals(130, ramp.top(), 8, "up to its end over the pillar it leans on");
            assertEquals(20, ramp.halfWidth(), 1e-9, "as wide as the beam");
            assertEquals(192, ramp.halfDepth(), 2, "and as long as it reaches over the ground");
            pictured(canvas, place, "loose-come-down");
            choice.chosen(choice.steps().indexOf("the beam's left pillar"));
            ticked(place, trial, 360);
            assertEquals(Set.of(GROUND), trial.world().under("the beam"));
            choice.chosen(choice.steps().indexOf("the lowest crate"));
            ticked(place, trial, 240);
            assertEquals(Set.of(GROUND), trial.world().under("the middle crate"), "the two above it have come down");
            assertEquals(Set.of("the middle crate"), trial.world().under("the highest crate"));
            assertEquals(18, trial.world().things().size(), "three pillars and a crate gone");
            choice.chosen(choice.steps().indexOf("the chock"));
            ticked(place, trial, 150);
            assertTrue(trial.world().moved("the melon").velocity().length() > 80, "the chock gone, the melon rolls");
            pictured(canvas, place, "loose-rolling");
            pictured(canvas, place, beside, "loose-ramp-rolling");
            ticked(place, trial, 1_500);
            assertEquals(0, trial.world().moving(), "and comes to rest out on the ground");
            pictured(canvas, place, beside, "loose-ramp-at-rest");
            pictured(canvas, place, "loose-all-down");
            choice.chosen(choice.steps().size() - 1);
            ticked(place, trial, 1);
            assertEquals(22, trial.world().things().size(), "begun again, every thing is back");
            assertTrue(place.said().contains("Loose bodies"));
            final Dial throwing = place.dials().get(3);
            assertEquals("Throw", throwing.name());
            assertEquals(20, throwing.steps().size(), "every thing but the ramp and the wagon");
            ticked(place, trial, 120);
            throwing.chosen(throwing.steps().indexOf("the ball"));
            ticked(place, trial, 60);
            assertTrue(trial.world().placed("the ball").orElseThrow().at().up() > 150, "thrown, it is in the air");
            assertTrue(place.said().contains("Thrown: the ball at 8.0 m/s, 45 degrees up"), place.said());
            pictured(canvas, place, beside, "loose-thrown");
            throwing.chosen(throwing.steps().indexOf("the ball"));
            ticked(place, trial, 2);
            assertTrue(trial.world().moved("the ball").velocity().up() > 300, "and thrown again from where it is");
            final Dial driving = place.dials().get(7);
            assertEquals("The wagon", driving.name());
            final int stood = place.inTheWay().blocks().size();
            driving.chosen(0);
            ticked(place, trial, 180);
            assertEquals(new Spot(1_000, 0, 0), trial.world().moved("the wagon's crate").velocity(),
                    "set going, the wagon carries its crate at ten metres a second");
            assertTrue(place.inTheWay().blocks().size() < stood, "and going, they are in nobody's way");
            pictured(canvas, place, new Orbit(new Vec3(-600, 100, 600), 0, 10, 2_200).camera(48, 10), "loose-wagon");
            driving.chosen(1);
            ticked(place, trial, 600);
            assertEquals(Set.of("the wagon"), trial.world().under("the wagon's crate"), "stopped, the crate stays");
            assertTrue(trial.world().under("the wagon's melon").contains("the wagon"), "so does its chocked melon");
            assertEquals(Set.of(GROUND), trial.world().under("the wagon's ball"), "and its ball has rolled off");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    @Test
    void whatIsAtHandIsLitWhatIsHeldIsDrawnBeforeTheHandsAndAMelonCutLiesInSlices() throws IOException {
        Files.createDirectories(Path.of("target", "card"));
        final Trial trial = new Trial(-1, 12);
        final Handling handling = new Handling(trial);
        final Camera near = new Orbit(new Vec3(-90, 60, 300), 18, 16, 420).camera(48, 10);
        final Step none = new Step();
        final Step taking = new Step(Set.of(), List.of("T"), 0, 0, false, 0, 0, false);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Place place = new LoosePlace(card, laid, trial, handling)) {
            assertEquals("Do, with what is held or at hand", place.dials().getFirst().name());
            assertEquals(List.of("Carry / Put down", "Cut", "Crush", "Throw", "Eat", "Open or shut"),
                    place.dials().getFirst().steps());
            ticked(place, trial, 60);
            handling.stepped(none, new Spot(-90, 160, 220), 0, 0);
            assertTrue(place.said().contains("At hand: the near melon, a watermelon: Carry, Cut, Crush"), place.said());
            pictured(canvas, place, near, "loose-at-hand");
            handling.stepped(taking, new Spot(-90, 160, 220), 0, 0);
            ticked(place, trial, 2);
            assertTrue(place.said().contains("In hand: the near melon"), place.said());
            pictured(canvas, place, near, "loose-held");
            // readied from the menu, and wound up for a second: held up, with the way it would fly
            for (final String key : List.of("G", "DOWN", "T")) {
                handling.stepped(new Step(Set.of(), List.of(key), 0, 0, false, 0, 0, false),
                        new Spot(-90, 160, 220), 0, 0);
            }
            for (int step = 0; step < 120; step++) {
                handling.stepped(new Step(Set.of("FIRE"), List.of(), 0, 0, false, 0, 0, false),
                        new Spot(-90, 160, 220), 0, 0);
            }
            assertTrue(place.said().contains("throwing, power 67%"), place.said());
            pictured(canvas, place, new Orbit(new Vec3(100, 120, 500), 60, 14, 1_100).camera(48, 10),
                    "loose-winding-up");
            handling.stepped(taking, new Spot(-90, 160, 220), 0, 0);
            handling.stepped(taking, new Spot(-90, 160, 220), 0, 0);
            ticked(place, trial, 240);
            handling.stepped(none, new Spot(-90, 160, 220), 0, 0);
            place.dials().getFirst().chosen(1);
            place.readied(camera);
            handling.stepped(none, new Spot(-90, 160, 220), 0, 0);
            ticked(place, trial, 240);
            assertTrue(trial.world().things().contains("slice 1 of the near melon"), place.said());
            handling.stepped(none, new Spot(-90, 160, 220), 0, 0);
            assertTrue(place.said().contains(", a melon slice: Carry, Eat"), place.said());
            pictured(canvas, place, near, "loose-slices");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The place readied and the trial ticked, a frame to two ticks, and what is in the way taken up. */
    private void ticked(final Place place, final Trial trial, final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            if (tick % 2 == 0) {
                place.readied(camera);
            }
            trial.ticked();
            place.rearranged();
        }
    }

    private void pictured(final Canvas canvas, final Place place, final String name) throws IOException {
        pictured(canvas, place, camera, name);
    }

    private void pictured(final Canvas canvas, final Place place, final Camera seen, final String name)
            throws IOException {
        canvas.drawn(day.sky(), new Framed(laid, seen, day.light()).filling((double) WIDTH / HEIGHT),
                pass -> place.drawn(pass, seen));
        final BufferedImage image = new com.example.fxsuite.vulkan.worked.Picture(canvas.width(), canvas.pixels())
                .image();
        ImageIO.write(image, "png", Path.of("target", "card", name + ".png").toFile());
    }
}
