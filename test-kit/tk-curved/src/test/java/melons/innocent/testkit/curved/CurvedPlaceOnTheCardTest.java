package melons.innocent.testkit.curved;

import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Limb;
import com.example.fxsuite.vulkan.bench.subject.Trodden;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
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
 * The trial of curved surfaces on the card: each shell drawn as one mesh from its sheet, with nothing said by the
 * validation layer, and the bot standing across the vault's flank and the dome's with each sole on its own ground.
 * Its pictures are written to {@code target/card/curved-*.png}, for the eye.
 */
class CurvedPlaceOnTheCardTest {

    private static final int WIDTH = 1_280;
    private static final int HEIGHT = 720;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Curves curves = new Curves();
    private final Obstacles inTheWay = curves.inTheWay();
    private final Bot bot = new Bot();
    private final Pose still = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    @Test
    void eachShellIsDrawnFromItsSheetAndTheBotStandsOnItWithEachFootOnItsOwnGround() throws IOException {
        Files.createDirectories(Path.of("target", "card"));
        final TestKit kit = new TestKit("tried", new BaseMap(Outline.CIRCLE, 100, Grounds.TURF))
                .with((card, laying) -> new CurvedPlace(card, laying));
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDTH, HEIGHT);
             Place place = kit.placing().apply(card, laid); Cast cast = new Cast(card, laid)) {
            assertEquals(7, place.inTheWay().volumes().size());
            assertTrue(place.said().contains("Underfoot: nothing within reach"), "nobody has taken a step yet");
            pictured(canvas, place, cast, List.of(), new Orbit(new Vec3(300, 200, 1_400), 0, 22, 5_200), "curved-all");
            pictured(canvas, place, cast, List.of(), new Orbit(new Vec3(300, 350, 1_500), 35, 28, 3_000),
                    "curved-ramp");
            pictured(canvas, place, cast, List.of(), new Orbit(new Vec3(300, 60, 700), 180, 8, 900),
                    "curved-ramp-foot");
            pictured(canvas, place, cast, List.of(), new Orbit(new Vec3(2_400, 100, 1_200), 30, 16, 2_400),
                    "curved-vault");
            pictured(canvas, place, cast, List.of(), new Orbit(new Vec3(-2_000, 100, 1_000), -20, 18, 2_200),
                    "curved-dome");
            // astride the vault's flank, facing along it; and on the dome's, facing round it
            final List<Courier.Placed> onVault = stood(2_400 + 350, 1_200, 0);
            for (final double yaw : new double[] {0, 180, 90}) {
                pictured(canvas, place, cast, onVault, new Orbit(new Vec3(2_750, 240, 1_200), yaw, 6, 520),
                        "curved-bot-on-the-vault-" + (int) yaw);
            }
            final List<Courier.Placed> onDome = stood(-2_000 + 480, 1_000, 0);
            pictured(canvas, place, cast, onDome, new Orbit(new Vec3(-1_520, 210, 1_000), 180, 6, 520),
                    "curved-bot-on-the-dome");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /**
     * The bot standing at ({@code x}, {@code z}) facing {@code yaw} degrees round, as high as the engine would stand
     * it there, each foot asked the engine's own ground: held to having each sole on the sheet under it, one well
     * above the other.
     */
    private List<Courier.Placed> stood(final double x, final double z, final double yaw) {
        final double high = inTheWay.footing(x, z, Body.FOOTING, 500).height();
        final Body body = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(x, -high, z), yaw);
        final Trodden trodden = (across, along) -> inTheWay.footing(across, along, 5, high + Body.STEP).height()
                - high;
        final List<Courier.Placed> parts = bot.placed(body, 0, still, false, trodden);
        final double[] left = sole(parts, Limb.LEFT_FOOT);
        final double[] right = sole(parts, Limb.RIGHT_FOOT);
        assertEquals(inTheWay.footing(left[0], left[2], 0, high + Body.STEP).height(), left[1], 2.5, "the left sole");
        assertEquals(inTheWay.footing(right[0], right[2], 0, high + Body.STEP).height(), right[1], 2.5,
                "the right sole");
        assertTrue(Math.abs(left[1] - right[1]) > 5, "one foot well above the other: " + left[1] + ", " + right[1]);
        return parts;
    }

    /** Where the middle of the foot {@code foot} is among a bot's {@code parts}, and how low it comes. */
    private double[] sole(final List<Courier.Placed> parts, final Limb foot) {
        final List<Courier.Piece> pieces = bot.pieces();
        for (int part = 0; part < pieces.size(); part++) {
            if (pieces.get(part).limb() == foot) {
                final Mat4 stands = parts.get(part).stands();
                double lowest = Double.POSITIVE_INFINITY;
                for (int corner = 0; corner < 8; corner++) {
                    lowest = Math.min(lowest, stands.times(new Mat4(new Vec3((corner & 1) - 0.5,
                            (corner >> 1 & 1) - 0.5, (corner >> 2 & 1) - 0.5))).at(1, 3));
                }
                return new double[] {stands.at(0, 3), lowest, stands.at(2, 3)};
            }
        }
        throw new IllegalStateException("a bot has feet");
    }

    private void pictured(final Canvas canvas, final Place place, final Cast cast, final List<Courier.Placed> figure,
                          final Orbit from, final String name) throws IOException {
        final Camera seen = from.camera(48, 10);
        canvas.drawn(day.sky(), new Framed(laid, seen, day.light()).filling((double) WIDTH / HEIGHT), pass -> {
            place.drawn(pass, seen);
            if (!figure.isEmpty()) {
                cast.figure(pass, figure, seen);
            }
        });
        final BufferedImage image = new com.example.fxsuite.vulkan.worked.Picture(canvas.width(), canvas.pixels())
                .image();
        ImageIO.write(image, "png", Path.of("target", "card", name + ".png").toFile());
    }
}
