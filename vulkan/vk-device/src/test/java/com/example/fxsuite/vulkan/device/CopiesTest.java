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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Copies of a mesh drawn many at once, checked by facts. Two copies of a face, drawn in one draw, stand where each
 * copy takes it, and nothing stands between them; no copies draw nothing; filled again with more than there was room
 * for, all are drawn; a copy laid out otherwise is refused. On a card that answers rays, copies gathered for rays
 * each stand of themselves: the one between the wall and the sun shades it, among two hundred others. And the
 * validation layer says nothing of any of it.
 */
class CopiesTest {

    private static final int SIZE = 64;
    private final Laid laid = new Laid().asking(true);
    private final Laid plain = new Laid();
    private final Mesh wall = new Panel(4, 4, 4).formed();
    private final Mesh face = new Panel(2, 2, 2).formed();
    private final Vec3 faceStands = new Vec3(0, 2.5, -1);
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    private final Vec3 sky = new Vec3(0.1, 0.2, 0.3);

    @Test
    void twoCopiesDrawnInOneDrawStandWhereEachTakesItAndNoCopiesDrawNothing() {
        final Look look = look(plain);
        try (Card card = new Card(); Canvas canvas = card.canvas(SIZE, SIZE);
             Drawing drawing = card.drawing(plain, look.vertex(), look.fragment());
             Sent sentFace = card.sent(new Packed(plain.point(), face));
             Copies copies = card.copies(plain, 1)) {
            copies.filled(List.of(moved(plain, -2, 2.5, -1), moved(plain, 2, 2.5, -1)));
            canvas.drawn(sky, frame(plain), pass -> pass.draw(drawing, sentFace, told(plain), copies));
            final Picture two = new Picture(SIZE, canvas.pixels());
            assertNotEquals(two.at(1, 1), two.at(14, 17), "the copy to the left");
            assertNotEquals(two.at(1, 1), two.at(50, 17), "the copy to the right");
            assertEquals(two.at(1, 1), two.at(32, 17), "nothing between them");
            copies.filled(List.of());
            canvas.drawn(sky, frame(plain), pass -> pass.draw(drawing, sentFace, told(plain), copies));
            final Picture none = new Picture(SIZE, canvas.pixels());
            assertEquals(none.at(1, 1), none.at(14, 17), "no copies, nothing drawn");
            final List<Filling> many = new ArrayList<>();
            for (int far = 0; far < 40; far++) {
                many.add(moved(plain, 100 + far, 0, 0));
            }
            many.add(moved(plain, 2, 2.5, -1));
            copies.filled(many);
            canvas.drawn(sky, frame(plain), pass -> pass.draw(drawing, sentFace, told(plain), copies));
            final Picture more = new Picture(SIZE, canvas.pixels());
            assertNotEquals(more.at(1, 1), more.at(50, 17), "the last of forty-one, more than there was room for");
            assertThrows(IllegalArgumentException.class, () -> copies.filled(List.of(told(plain))));
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void copiesGatheredForRaysEachStandOfThemselves() {
        final Look look = look(laid);
        try (Card card = new Card()) {
            assumeTrue(card.rays(), "the card answers rays");
            try (Canvas canvas = card.canvas(SIZE, SIZE); Standing standing = card.standing();
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent sentWall = card.sent(new Packed(laid.point(), wall));
                 Sent sentFace = card.sent(new Packed(laid.point(), face));
                 Copies copies = card.copies(laid, 4)) {
                final List<Filling> many = new ArrayList<>();
                for (int far = 0; far < 200; far++) {
                    many.add(moved(laid, 100 + far, 0, 0));
                }
                many.add(moved(laid, faceStands.x(), faceStands.y(), faceStands.z()));
                copies.filled(many);
                canvas.drawn(sky, frame(laid), standing.stood(pass -> pass.draw(drawing, sentFace, told(laid),
                        copies)), pass -> pass.draw(drawing, sentWall, told(laid)));
                final Picture shaded = new Picture(SIZE, canvas.pixels());
                assertEquals(13, shaded.part(32, 32, 0), 1, "the wall behind the copy from the sun");
                assertEquals(64, shaded.part(18, 41, 0), 1, "the wall beside its shadow");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    private Look look(final Laid layouts) {
        return new Look(layouts, new Surface(new Colour(0.25, 0.5, 0.75), List.of(), new Colour(0, 0, 0), 8));
    }

    /** A copy moved {@code x}, {@code y}, {@code z} from where the whole stands. */
    private Filling moved(final Laid layouts, final double x, final double y, final double z) {
        return new Filling(layouts.copy()).with("copied", 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, y, z, 1);
    }

    /** The camera's frame: a fifth of white on everything, and a sun down and onward that brings a face to white. */
    private Filling frame(final Laid layouts) {
        final double sun = 0.8 / Math.sqrt(0.5);
        return new Filling(layouts.frame()).with("seen", camera.seen(1).columns())
                .with("sunTowards", 0, -1, 1).with("sun", sun, sun, sun).with("ambient", 0.2, 0.2, 0.2)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0)
                .with("rays", 0.01, 1000);
    }

    /** What the whole is told: standing at the world's middle, as the camera sees it. */
    private Filling told(final Laid layouts) {
        return new Filling(layouts.draw()).with("placed", camera.placed(new Vec3(0, 0, 0)).columns())
                .with("coarsened", 1).with("tint", 1, 1, 1);
    }
}
