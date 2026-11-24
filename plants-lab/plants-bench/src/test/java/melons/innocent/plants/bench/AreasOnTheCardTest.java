package melons.innocent.plants.bench;

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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Areas of forest on the card. Each outline, 150 m across, as one coarse mesh of the whole beside the same trees
 * drawn one by one, from above and half way up: drawn with nothing said by the validation layer, written to {@code
 * target/card/area-<outline>-<angle>.png} for the eye, with how dark each half comes out and how long a frame takes
 * each way told.
 */
class AreasOnTheCardTest {

    private static final int WIDE = 960;
    private static final int HIGH = 540;
    private static final long PATIENCE = 120_000;

    @Test
    void eachOutlineIsDrawnAsOneMeshBesideItsTreesAndEachWayIsTimed() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Daylight day = new Daylight();
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH)) {
            final Laid laid = new Laid().asking(card.rays());
            final OnShow first = shown(Outline.CIRCLE, true, Joined.AS_ONE);
            try (PlantsView view = new PlantsView(card, laid, first)) {
                for (final Outline outline : Outline.values()) {
                    final OnShow beside = shown(outline, true, Joined.AS_ONE);
                    for (final Angle angle : List.of(Angle.ABOVE, Angle.HALF)) {
                        final Camera camera = new Orbit(new Vec3(0, 700, 0), 0, angle.pitch(), beside.apart() * 2.1)
                                .camera(48, 10);
                        made(view, canvas, beside, camera, day);
                        final Picture picture = new Picture(canvas.width(), canvas.pixels());
                        final String name = ("area-" + outline.name() + "-" + angle.name()).toLowerCase(Locale.ROOT);
                        ImageIO.write(picture.image(), "png", folder.resolve(name + ".png").toFile());
                        System.out.println("BENCH: " + name + ": " + new Darkness(picture).said());
                    }
                    for (final Joined joined : Joined.values()) {
                        final OnShow alone = shown(outline, false, joined);
                        final Camera camera = new Orbit(new Vec3(0, 700, 0), 0, 45, alone.apart() * 1.3)
                                .camera(48, 10);
                        made(view, canvas, alone, camera, day);
                        System.out.printf(Locale.ENGLISH, "BENCH: %s: %.1f ms a frame; %s%n", alone.said(),
                                timed(view, canvas, alone, camera, day), view.said(alone));
                    }
                }
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    private OnShow shown(final Outline outline, final boolean beside, final Joined joined) {
        return new OnShow(Subject.AREA, Kinds.MIXED, Optional.empty(), beside, 4, joined, outline, 150);
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
