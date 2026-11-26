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
 * A patch drawn as one mesh on the card. Made away from the card's thread while frames go on, it is drawn beside
 * the same trees as copies, from above, half way up and low, with nothing said by the validation layer, written to
 * {@code target/card/patch-one-mesh-<angle>.png} for the eye, how dark each half comes out told. Then patches of four
 * and of eight trees a side, as grown, are timed each way, as copies and as one mesh.
 */
class OneMeshOnTheCardTest {

    private static final int WIDE = 960;
    private static final int HIGH = 540;
    /** How long the one mesh may take to make, in thousandths of a second. */
    private static final long PATIENCE = 120_000;

    @Test
    void aPatchAsOneMeshIsDrawnBesideItsCopiesAndEachWayIsTimed() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH)) {
            final Laid laid = new Laid().asking(card.rays());
            final OnShow beside = new OnShow(Subject.PATCH, Kinds.MIXED, Optional.empty(), true, 4, Joined.AS_ONE);
            try (PlantsView view = new PlantsView(card, laid, beside)) {
                for (final Angle angle : Angle.values()) {
                    final Camera camera = new Orbit(new Vec3(0, 700, 0), 0, angle.pitch(),
                            Subject.PATCH.apart(4) * 2.2).camera(48, 10);
                    made(view, canvas, beside, camera, day);
                    final Picture picture = new Picture(canvas.width(), canvas.pixels());
                    final String name = "patch-one-mesh-" + angle.name().toLowerCase(Locale.ROOT);
                    ImageIO.write(picture.image(), "png", folder.resolve(name + ".png").toFile());
                    System.out.println("BENCH: " + name + ": " + new Darkness(picture).said());
                }
                for (final int rows : new int[] {4, 8}) {
                    for (final Joined joined : Joined.values()) {
                        final OnShow alone = new OnShow(Subject.PATCH, Kinds.MIXED, Optional.of(Stage.FINE), false,
                                rows, joined);
                        final Camera camera = new Orbit(new Vec3(0, 700, 0), 0, 30, Subject.PATCH.apart(rows) * 1.6)
                                .camera(48, 10);
                        made(view, canvas, alone, camera, day);
                        System.out.printf(Locale.ENGLISH, "BENCH: %d by %d trees, %s: %.1f ms a frame; %s%n", rows,
                                rows, joined.title(), timed(view, canvas, alone, camera, day), view.said(alone));
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** Frames drawn of {@code shown} until what is on show is made: at once as copies, the one mesh once made. */
    private void made(final PlantsView view, final Canvas canvas, final OnShow shown, final Camera camera,
                      final Daylight day) {
        final long began = System.currentTimeMillis();
        do {
            view.drawn(canvas, shown, camera, day, true, view.offered().getLast());
        } while (shown.joined() == Joined.AS_ONE && !view.said(shown).contains("triangles")
                && !view.said(shown).contains("could not") && System.currentTimeMillis() - began < PATIENCE);
        assertTrue(shown.joined() == Joined.APART || view.said(shown).contains("triangles"), view.said(shown));
    }

    /** How long a frame of {@code shown} takes, in thousandths of a second: twenty, after five to warm up. */
    private double timed(final PlantsView view, final Canvas canvas, final OnShow shown, final Camera camera,
                         final Daylight day) {
        for (int frame = 0; frame < 5; frame++) {
            view.drawn(canvas, shown, camera, day, true, view.offered().getLast());
            canvas.pixels();
        }
        final long began = System.nanoTime();
        for (int frame = 0; frame < 20; frame++) {
            view.drawn(canvas, shown, camera, day, true, view.offered().getLast());
            canvas.pixels();
        }
        return (System.nanoTime() - began) / 1e6 / 20;
    }
}
