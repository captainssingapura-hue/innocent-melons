package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A car's parts, each by itself: every one a sound graph in one piece, of steel but for the rubber tyre; the frame
 * the same fore and aft and left and right, so that one set of arms serves every corner; the two arms pivoting on
 * the same line, the lower the longer; the tyre's inside as big as the rim's outside; spread out so that none
 * touches another, the small ones resting on the bench and the frame on the ground; the bench in the avatar's way;
 * and the whole, and each group close up, drawn on the card with nothing said by the validation layer.
 */
class PartsTest {

    private final Spread spread = new Spread();
    private final Parts parts = spread.parts();

    @Test
    void everyPartIsASoundGraphInOnePieceOfSteelButForTheRubberTyre() {
        assertEquals(List.of("frame", "lower-wishbone", "upper-wishbone", "upright", "damper", "rim", "tyre"),
                parts.all().stream().map(Part::name).toList());
        for (final Part part : parts.all()) {
            assertEquals(List.of(), part.shape().problems(), part.name());
            assertEquals(1, part.shape().pieces(), part.name() + " is one piece");
            assertEquals(part.name().equals("tyre") ? new Materials.Rubber() : new Materials.Steel(), part.of());
        }
    }

    @Test
    void theFrameIsTheSameForeAndAftAndLeftAndRight() {
        final Form frame = parts.frame().shape();
        for (final Form.Vertex point : frame.vertices()) {
            assertTrue(has(frame, -point.across(), point.along(), point.up()), "left as right: " + point);
            assertTrue(has(frame, point.across(), -point.along(), point.up()), "fore as aft: " + point);
        }
        assertEquals(330, frame.bounds().depth(), 1e-9, "3.3 m long");
    }

    @Test
    void theTwoArmsPivotOnTheSameLineTheLowerTheLongerAndTheTyreSitsOnTheRim() {
        final Form lower = parts.lowerWishbone().shape();
        final Form upper = parts.upperWishbone().shape();
        for (final Form arm : List.of(lower, upper)) {
            assertTrue(has(arm, 0, 20, 0) && has(arm, 0, -20, 0), arm.name() + " pivots 40 cm apart");
        }
        assertTrue(lower.bounds().width() > upper.bounds().width(), "the lower arm the longer");
        final Part rim = parts.rim();
        final Part tyre = parts.tyre();
        assertEquals(rim.shape().bounds().height() / 2 + rim.thick(),
                tyre.shape().bounds().height() / 2 - tyre.thick(), 1e-9, "the tyre's inside is the rim's outside");
    }

    @Test
    void spreadOutNoneTouchesAnotherTheSmallOnesOnTheBenchAndTheFrameOnTheGround() {
        final List<Part> all = parts.all();
        final Block bench = spread.inTheWay().blocks().getLast();
        for (int one = 0; one < all.size(); one++) {
            final Block around = spread.around(all.get(one));
            for (int other = one + 1; other < all.size(); other++) {
                assertTrue(apart(around, spread.around(all.get(other))),
                        all.get(one).name() + " clear of " + all.get(other).name());
            }
            if (one == 0) {
                assertEquals(0, around.bottom(), 1e-9, "the frame rests on the ground");
                assertTrue(apart(around, bench), "beside the bench");
            } else {
                assertEquals(bench.top(), around.bottom(), 1e-9, all.get(one).name() + " rests on the bench");
                assertTrue(Math.abs(around.x() - bench.x()) + around.halfWidth() <= bench.halfWidth()
                        && Math.abs(around.z() - bench.z()) + around.halfDepth() <= bench.halfDepth(),
                        all.get(one).name() + " within the bench's top");
            }
        }
    }

    @Test
    void theBenchIsInTheAvatarsWay() {
        Avatar now = new Avatar(Body.standing(new Vec3(0, 0, 0), 0), false);
        for (int step = 0; step < 6 * 120; step++) {
            now = now.step(new Controls(1, 0, 0, 0, true), Clock.STEP, spread.inTheWay()).avatar();
        }
        final double z = now.body().position().z();
        assertTrue(z > 400 && z < 555, "stopped at the bench's near side: " + now.body().position());
    }

    @Test
    void theWholeAndEachGroupCloseUpAreDrawnOnTheCardWithNothingSaid() throws IOException {
        final Daylight day = new Daylight();
        final Laid laid = new Laid();
        final Map<String, Orbit> views = new LinkedHashMap<>();
        views.put("parts-spread-out", new Orbit(place(60, 100, 600), 20, 25, 700));
        views.put("parts-arms", new Orbit(place(-190, 85, 600), 15, 40, 240));
        views.put("parts-wheel", new Orbit(place(80, 110, 600), -12, 18, 260));
        views.put("parts-frame", new Orbit(place(430, 45, 600), 40, 28, 470));
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Map<String, Integer> drawn = new LinkedHashMap<>();
        try (Card card = new Card(); Canvas canvas = card.canvas(640, 480);
             Place place = new PartsYard(card, laid)) {
            for (final Map.Entry<String, Orbit> view : views.entrySet()) {
                final Camera camera = view.getValue().camera(48, 10);
                canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(640.0 / 480),
                        pass -> place.drawn(pass, camera));
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                ImageIO.write(picture.image(), "png", folder.resolve(view.getKey() + ".png").toFile());
                drawn.put(view.getKey(), drawn(picture));
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
        drawn.forEach((view, pixels) -> assertTrue(pixels > 3000, view + " drawn over the sky: " + pixels));
    }

    /** How many of {@code picture}'s pixels are not the sky. */
    private int drawn(final Picture picture) {
        int drawn = 0;
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                drawn += picture.at(x, y) == 0xffc9dbe8 ? 0 : 1;
            }
        }
        return drawn;
    }

    private com.example.fxsuite.vulkan.space.Vec3 place(final double across, final double up, final double along) {
        return new com.example.fxsuite.vulkan.space.Vec3(across, up, along);
    }


    /** Whether {@code shape} has a point at the place given. */
    private boolean has(final Form shape, final double across, final double along, final double up) {
        return shape.vertices().stream().anyMatch(point -> Math.abs(point.across() - across) < 1e-9
                && Math.abs(point.along() - along) < 1e-9 && Math.abs(point.up() - up) < 1e-9);
    }

    /** Whether two boxes, both square to the ground's lines, are clear of each other. */
    private boolean apart(final Block one, final Block other) {
        return Math.abs(one.x() - other.x()) >= one.halfWidth() + other.halfWidth()
                || Math.abs(one.z() - other.z()) >= one.halfDepth() + other.halfDepth()
                || one.bottom() >= other.top() || other.bottom() >= one.top();
    }
}
