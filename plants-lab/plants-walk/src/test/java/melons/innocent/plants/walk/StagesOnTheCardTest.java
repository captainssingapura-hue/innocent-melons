package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.tree.Staging;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Each kind of tree at each of its coarser stages, drawn on the card from just beyond where that stage begins, on a
 * picture 1080 pixels high as the stages are chosen for, beside the same tree drawn as finely as it is grown from
 * the same place: for the eye to judge whether the stage can be told from the tree. The validation layer has nothing
 * to say, and a tree left to choose its own stage there chooses that stage. The pictures are written to
 * {@code target/card/<kind>-stages.png}, a row a stage, the fine tree on the left, each enlarged four times; and
 * {@code <kind>-<stage>-close.png}, the stage seen from close by, to see what it is made of.
 */
class StagesOnTheCardTest {

    private static final int HIGH = 1080;
    private static final int WIDE = 1440;
    /** How many pixels round the tree's middle are kept of each picture, and how many times they are enlarged. */
    private static final int KEPT = 120;
    private static final int TIMES = 4;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();

    @Test
    void eachStageFromWhereItBeginsBesideTheTreeAsGrown() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Staging staging = new Staging(48, HIGH);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, HIGH)) {
            for (final Kind kind : Kind.values()) {
                final String name = kind.name().toLowerCase(Locale.ROOT);
                final List<WoodDrawn.Grown> grown = List.of(new WoodDrawn.Grown(kind, kind.tree(1)));
                final List<WoodDrawn.Planted> planted = List.of(new WoodDrawn.Planted(0, new Mat4(), 1));
                final double middle = kind.tree(1).height() / 2;
                final BufferedImage sheet = new BufferedImage(2 * KEPT * TIMES, 4 * KEPT * TIMES,
                        BufferedImage.TYPE_INT_RGB);
                try (WoodDrawn fine = new WoodDrawn(card, laid, grown, planted, Optional.of(Stage.FINE));
                     WoodDrawn chosen = new WoodDrawn(card, laid, grown, planted, Optional.empty())) {
                    for (int row = 0; row < 4; row++) {
                        final Stage stage = Stage.values()[row + 1];
                        final double from = begins(staging, stage) * 1.05;
                        final Camera camera = new Orbit(new Vec3(0, middle, 0), 30, 4, from).camera(48, 10);
                        placed(sheet, pictured(canvas, fine, camera), 0, row);
                        placed(sheet, pictured(canvas, chosen, camera), 1, row);
                        assertEquals(stage, chosen.stages().getFirst(), kind + " from " + from + " cm");
                        try (WoodDrawn held = new WoodDrawn(card, laid, grown, planted, Optional.of(stage))) {
                            final Camera close = new Orbit(new Vec3(0, middle, 0), 30, 4, middle * 4.2)
                                    .camera(48, 10);
                            ImageIO.write(pictured(canvas, held, close).image(), "png", folder.resolve(
                                    name + "-" + stage.name().toLowerCase(Locale.ROOT) + "-close.png").toFile());
                        }
                    }
                }
                ImageIO.write(sheet, "png", folder.resolve(name + "-stages.png").toFile());
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** How far off {@code stage} begins, for a tree as big as grown: where the thickest it drops is a pixel. */
    private double begins(final Staging staging, final Stage stage) {
        double near = 100;
        double far = 1e6;
        for (int halving = 0; halving < 60; halving++) {
            final double at = (near + far) / 2;
            if (staging.coarsest(at, 1).ordinal() >= stage.ordinal()) {
                far = at;
            } else {
                near = at;
            }
        }
        return far;
    }

    /** The trees as {@code camera} sees them by day, their clumps laid over their wood. */
    private Picture pictured(final Canvas canvas, final WoodDrawn wood, final Camera camera) {
        canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDE / HIGH), pass -> {
            wood.drawn(pass, camera);
            wood.seenThrough(pass, camera);
        });
        return new Picture(canvas.width(), canvas.pixels());
    }

    /** The middle of {@code picture}, enlarged, put on {@code sheet} in column {@code column} of row {@code row}. */
    private void placed(final BufferedImage sheet, final Picture picture, final int column, final int row) {
        for (int y = 0; y < KEPT * TIMES; y++) {
            for (int x = 0; x < KEPT * TIMES; x++) {
                final int argb = picture.at(WIDE / 2 - KEPT / 2 + x / TIMES, HIGH / 2 - KEPT / 2 + y / TIMES);
                sheet.setRGB(column * KEPT * TIMES + x, row * KEPT * TIMES + y, argb & 0xffffff);
            }
        }
    }
}
