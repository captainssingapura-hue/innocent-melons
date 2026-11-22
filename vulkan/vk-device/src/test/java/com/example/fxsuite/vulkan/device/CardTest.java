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
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

/**
 * The card opened and drawn on, checked by facts: a triangle drawn into a picture is its own colour where it is and
 * the sky where it is not; the validation layer says nothing of how the kit uses the card, and is heard when the
 * card is misused; what is left open is closed with the card and named; a closed thing and another thread are each
 * refused.
 */
class CardTest {

    private static final int SIZE = 64;

    private final Laid laid = new Laid();
    /** A face of one flat colour, with no shine: lit by an even white light, it is that colour exactly. */
    private final Look look = new Look(laid,
            new Surface(new Colour(0.25, 0.5, 0.75), List.of(), new Colour(0, 0, 0), 8));
    /** Half of a square four centimetres each way: the triangle with the square's lower left corner. */
    private final Mesh square = new Panel(4, 4, 4).formed();
    private final Mesh triangle = new Mesh(square.points(), square.facings(), square.corners().subList(0, 3));
    private final Vec3 placed = new Vec3(0, 0, 0);
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    private final Vec3 sky = new Vec3(0.1, 0.2, 0.3);

    @Test
    void aTriangleDrawnIntoAPictureIsItsOwnColourWhereItIsAndTheSkyWhereItIsNot() {
        try (Card card = new Card()) {
            final Picture picture = drawn(card);
            assertEquals(SIZE, picture.width());
            assertEquals(SIZE, picture.height());
            near(picture, 23, 40, 64, 128, 191, "inside the triangle, its own colour");
            near(picture, 40, 23, 26, 51, 77, "in the square's other half, the sky");
            near(picture, 2, 2, 26, 51, 77, "in a corner, the sky");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void theCardSaysWhichCardItIs() {
        try (Card card = new Card()) {
            assertFalse(card.name().isBlank());
        }
    }

    @Test
    void theValidationLayerIsHeardWhenTheCardIsMisused() {
        final Card card = new Card();
        // bytes on the card with no use said for them: the driver lets it pass, and the layer does not
        final Bytes useless = new Bytes(card.reach(), "bytes of no use", 16, 0);
        useless.close();
        final List<String> heard = card.heard();
        assertFalse(heard.isEmpty(), "the layer spoke");
        assertTrue(heard.getFirst().contains("usage"), heard.getFirst());
        card.close();
    }

    @Test
    void whatTheLayerSaidAndNobodyHeardIsThrownAsTheCardCloses() {
        final Card card = new Card();
        new Bytes(card.reach(), "bytes of no use", 16, 0).close();
        final Refused refused = assertThrows(Refused.class, card::close);
        assertTrue(refused.getMessage().contains("the validation layer"), refused.getMessage());
    }

    @Test
    void whatIsLeftOpenIsClosedWithTheCardAndNamed() {
        final Card card = new Card();
        final Bytes left = new Bytes(card.reach(), "bytes left open", 16, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        final IllegalStateException said = assertThrows(IllegalStateException.class, card::close);
        assertTrue(said.getMessage().contains("bytes left open"), said.getMessage());
        assertTrue(said.getMessage().contains("the memory of bytes left open"), said.getMessage());
        assertThrows(IllegalStateException.class, () -> left.buffer().raw(), "and it is closed");
    }

    @Test
    void aClosedThingAndAClosedCardRefuseUse() {
        final Card card = new Card();
        final Sent mesh = card.sent(new Packed(laid.point(), triangle));
        mesh.close();
        mesh.close();
        assertThrows(IllegalStateException.class, () -> mesh.points().buffer().raw(), "a mesh that is closed");
        card.close();
        card.close();
        assertThrows(IllegalStateException.class, () -> card.canvas(SIZE, SIZE), "a card that is closed");
    }

    @Test
    void aCallFromAnotherThreadIsRefused() throws InterruptedException {
        try (Card card = new Card()) {
            final AtomicReference<Throwable> thrown = new AtomicReference<>();
            final Thread other = new Thread(() -> {
                try {
                    card.canvas(SIZE, SIZE);
                } catch (final IllegalStateException refused) {
                    thrown.set(refused);
                }
            }, "another");
            other.start();
            other.join();
            assertTrue(thrown.get() != null && thrown.get().getMessage().contains("another"),
                    "refused, and the thread named: " + thrown.get());
        }
    }

    @Test
    void aMeshLaidOutOtherwiseThanTheLookTakesItIsRefusedBeforeAnythingIsDrawn() {
        try (Card card = new Card();
             Canvas canvas = card.canvas(SIZE, SIZE);
             Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
             Sent mesh = card.sent(new Packed(laid.point(), triangle))) {
            final Filling wrong = new Filling(laid.frame()).with("seen", camera.seen(1).columns())
                    .with("sunTowards", 0, 1, 0).with("sun", 0, 0, 0).with("ambient", 1, 1, 1)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0);
            assertThrows(IllegalArgumentException.class,
                    () -> canvas.drawn(sky, frame(), pass -> pass.draw(drawing, mesh, wrong)));
            final Pass[] kept = new Pass[1];
            canvas.drawn(sky, frame(), pass -> kept[0] = pass);
            assertThrows(IllegalStateException.class, () -> kept[0].draw(drawing, mesh, told()),
                    "what draws, kept past the end of its drawing");
        }
    }

    @Test
    void aLookThatIsMixedShowsWhatIsBehindItAsFarAsItDoesNotHideItAndComesAfterEveryLookThatWritesOver() {
        // red that hides half of what is behind it, and shows half of itself: lit evenly, half red over half the rest
        final Look clear = new Look(laid,
                new Surface(new Colour(1, 0, 0), List.of(), new Colour(0, 0, 0), 8).clear(0.5));
        final Filling nearer = new Filling(laid.draw()).with("placed", camera.placed(new Vec3(0, 0, -1)).columns())
                .with("coarsened", 1).with("tint", 1, 1, 1);
        final Filling further = new Filling(laid.draw()).with("placed", camera.placed(new Vec3(0, 0, 1)).columns())
                .with("coarsened", 1).with("tint", 1, 1, 1);
        try (Card card = new Card();
             Canvas canvas = card.canvas(SIZE, SIZE);
             Drawing over = card.drawing(laid, look.vertex(), look.fragment());
             Drawing mixed = card.mixing(laid, clear.vertex(), clear.fragment());
             Sent mesh = card.sent(new Packed(laid.point(), triangle))) {
            assertTrue(mixed.mixes() && !over.mixes());
            canvas.drawn(sky, frame(), pass -> {
                pass.draw(over, mesh, told());
                pass.draw(mixed, mesh, nearer);
            });
            final Picture before = new Picture(canvas.width(), canvas.pixels());
            near(before, 23, 40, 159, 64, 96, "in front of the triangle: half red, over half of its colour");
            near(before, 13, 50, 140, 26, 38, "at its own corner, past the triangle's: half red over half the sky");
            near(before, 40, 23, 26, 51, 77, "where neither is, the sky");
            canvas.drawn(sky, frame(), pass -> {
                pass.draw(over, mesh, told());
                pass.draw(mixed, mesh, further);
            });
            near(new Picture(canvas.width(), canvas.pixels()), 23, 40, 64, 128, 191, "behind the triangle: hidden");
            assertThrows(IllegalStateException.class, () -> canvas.drawn(sky, frame(), pass -> {
                pass.draw(mixed, mesh, nearer);
                pass.draw(over, mesh, told());
            }), "written over after one is mixed in");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void noNativeMemoryIsLeftHeldOnceTheCardIsClosed() {
        assertTrue(Configuration.DEBUG_MEMORY_ALLOCATOR.get(false), "LWJGL is counting the memory it is asked for");
        // once before counting: what LWJGL and the driver keep for good is asked for the first time round
        try (Card first = new Card()) {
            drawn(first);
        }
        final long before = held();
        try (Card card = new Card()) {
            drawn(card);
        }
        assertEquals(before, held(), "bytes of native memory held, before and after");
    }

    /** How many bytes of native memory LWJGL has been asked for and not yet given back. */
    private long held() {
        final long[] bytes = new long[1];
        MemoryUtil.memReport((address, memory, thread, name, trace) -> bytes[0] += memory);
        return bytes[0];
    }

    private Picture drawn(final Card card) {
        try (Canvas canvas = card.canvas(SIZE, SIZE);
             Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
             Sent mesh = card.sent(new Packed(laid.point(), triangle))) {
            canvas.drawn(sky, frame(), pass -> pass.draw(drawing, mesh, told()));
            return new Picture(canvas.width(), canvas.pixels());
        }
    }

    /** What the frame is told: the camera, and an even white light with no sun. */
    private Filling frame() {
        return new Filling(laid.frame()).with("seen", camera.seen(1).columns())
                .with("sunTowards", 0, 1, 0).with("sun", 0, 0, 0).with("ambient", 1, 1, 1)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0);
    }

    /** What the triangle is told: where it stands, as the camera sees it. */
    private Filling told() {
        return new Filling(laid.draw()).with("placed", camera.placed(placed).columns()).with("coarsened", 1)
                .with("tint", 1, 1, 1);
    }

    private void near(final Picture picture, final int x, final int y, final int red, final int green,
                      final int blue, final String what) {
        final int[] wanted = {red, green, blue};
        for (int part = 0; part < 3; part++) {
            assertTrue(Math.abs(picture.part(x, y, part) - wanted[part]) <= 1,
                    what + ": " + Integer.toHexString(picture.at(x, y)) + " at " + x + ", " + y);
        }
    }
}
