package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
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
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bench's view on the card. One oak, and a patch of all three kinds mixed, each at every stage beside the same as
 * grown, from above, half way up and low: each drawn with its shadows with nothing said by the validation layer,
 * both halves of the picture darker than the sky, and written to {@code target/card/<subject>-<stage>-<angle>.png}
 * for the eye, how dark each half comes out told as it goes. The view planted afresh between them sends nothing.
 */
class PlantsViewOnTheCardTest {

    private static final int WIDE = 960;
    private static final int HIGH = 540;

    @Test
    void everySubjectAtEveryStageIsDrawnBesideTheSameAsGrownFromEachAngleWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH)) {
            final Laid laid = new Laid().asking(card.rays());
            final OnShow first = new OnShow(Subject.TREE, Kinds.OAK, Optional.of(Stage.FINE), true);
            try (PlantsView view = new PlantsView(card, laid, first)) {
                for (final Subject subject : Subject.values()) {
                    final Kinds kinds = subject == Subject.TREE ? Kinds.OAK : Kinds.MIXED;
                    for (final Stage stage : Stage.values()) {
                        final OnShow shown = new OnShow(subject, kinds, Optional.of(stage), true);
                        for (final Angle angle : Angle.values()) {
                            final Camera camera = new Orbit(new Vec3(0, 700, 0), 0, angle.pitch(),
                                    subject.apart(4) * 2.2).camera(48, 10);
                            view.drawn(canvas, shown, camera, day, true, view.offered().getLast());
                            final Picture picture = new Picture(canvas.width(), canvas.pixels());
                            final String name = String.format(Locale.ROOT, "%s-%s-%s", subject.name(),
                                    stage.name(), angle.name()).toLowerCase(Locale.ROOT).replace('_', '-');
                            ImageIO.write(picture.image(), "png", folder.resolve(name + ".png").toFile());
                            final Darkness darkness = new Darkness(picture);
                            System.out.println("BENCH: " + name + ": " + darkness.said());
                            assertTrue(darkness.left() > 0.05 && darkness.right() > 0.05,
                                    name + ": " + darkness.said());
                        }
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }
}
