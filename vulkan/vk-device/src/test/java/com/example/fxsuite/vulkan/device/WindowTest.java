package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A window the card draws to, never shown on the screen here: frame after frame is drawn to it with nothing said by
 * the validation layer and nothing left open; a look built once draws to a window and to a picture alike; and a
 * card opened for pictures only has no windows.
 */
class WindowTest {

    private final Laid laid = new Laid();
    private final Look look = new Look(laid,
            new Surface(new Colour(0.25, 0.5, 0.75), List.of(), new Colour(0, 0, 0), 8));
    private final Mesh square = new Panel(4, 4, 4).formed();
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    private final Vec3 sky = new Vec3(0.1, 0.2, 0.3);

    @Test
    void frameAfterFrameIsDrawnToAWindowWithNothingSaidAndNothingLeftOpen() {
        try (Desk desk = new Desk(); Card card = new Card(desk)) {
            try (Window window = card.window("a window never shown", 320, 240);
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent mesh = card.sent(new Packed(laid.point(), square))) {
                assertTrue(window.width() >= 320 && window.height() >= 240, "at least the pixels asked for");
                assertEquals(320.0 / 240, (double) window.width() / window.height(), 0.01, "and the same shape");
                for (int frame = 0; frame < 5; frame++) {
                    desk.heard();
                    window.drawn(sky, frame(320.0 / 240), pass -> pass.draw(drawing, mesh, told()));
                }
                assertTrue(window.open(), "nobody closed it");
                assertTrue(window.plainKeys(), "its keys come as they are pressed, with no input method between");
                final Moved mouse = window.mouse();
                assertEquals(new Moved(0, 0, false, false, 0), mouse, "and nobody touched it");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void aLookBuiltOnceDrawsToAWindowAndToAPictureAlike() {
        try (Desk desk = new Desk(); Card card = new Card(desk)) {
            try (Window window = card.window("a window never shown", 64, 64);
                 Canvas canvas = card.canvas(64, 64);
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent mesh = card.sent(new Packed(laid.point(), square))) {
                for (final Target target : List.of(window, canvas, window)) {
                    target.drawn(sky, frame(1), pass -> pass.draw(drawing, mesh, told()));
                }
                final Picture picture = new Picture(canvas.width(), canvas.pixels());
                assertEquals(128, picture.part(32, 32, 1), 1, "the square's own colour in the picture's middle");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void aCardOpenedForPicturesOnlyHasNoWindows() {
        try (Card card = new Card()) {
            assertThrows(IllegalStateException.class, () -> card.window("none", 64, 64));
        }
    }

    private Filling frame(final double aspect) {
        return new Filling(laid.frame()).with("seen", camera.seen(aspect).columns())
                .with("sunTowards", 0, 1, 0).with("sun", 0, 0, 0).with("ambient", 1, 1, 1)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0);
    }

    private Filling told() {
        return new Filling(laid.draw()).with("placed", camera.placed(new Vec3(0, 0, 0)).columns())
                .with("coarsened", 1).with("tint", 1, 1, 1);
    }
}
