package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.shade.Outlined;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A picture of depths drawn first and looked up after, checked by facts. A wall stands before the camera with a
 * smaller face a little in front of it and higher, and the sun shines down and onward at both: the wall is in the
 * smaller face's shadow where that stands between it and the sun, and in the sun beside the shadow; the smaller
 * face, nearest the sun, is in the sun all over; with no picture of depths, or one never drawn on, or one drawn
 * afresh with nothing, the wall is in the sun all over; and the validation layer says nothing of any of it.
 */
class DepthsTest {

    private static final int SIZE = 64;
    private static final int FINE = 256;

    private final Laid laid = new Laid();
    /** A face of one flat colour, with no shine: a quarter, a half and three quarters of what light falls on it. */
    private final Look look = new Look(laid,
            new Surface(new Colour(0.25, 0.5, 0.75), List.of(), new Colour(0, 0, 0), 8));
    private final Mesh wall = new Panel(4, 4, 4).formed();
    private final Mesh face = new Panel(2, 2, 2).formed();
    private final Vec3 faceStands = new Vec3(0, 2.5, -1);
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    /** The sun shines down and onward alike; so the face, one in front of the wall, shades it one lower. */
    private final Beam beam = new Beam(new Vec3(0, 2, 0), new Vec3(0, -1, 1), 12, 12);
    private final Vec3 sky = new Vec3(0.1, 0.2, 0.3);

    @Test
    void theWallIsInTheFacesShadowWhereTheFaceStandsBetweenItAndTheSunAndInTheSunBesideIt() {
        try (Card card = new Card()) {
            final Picture picture = drawn(card, Optional.of(true));
            near(picture, 32, 32, 13, 26, 38, "the wall behind the face from the sun: only the light on everything");
            near(picture, 18, 41, 64, 128, 191, "the wall beside the shadow: the sun as well");
            near(picture, 32, 45, 64, 128, 191, "the wall below the shadow");
            near(picture, 32, 17, 64, 128, 191, "the face itself, nearest the sun, shades nothing of itself");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void withNoPictureOfDepthsOrOneNeverDrawnOnOrOneDrawnAfreshWithNothingTheWallIsInTheSunAllOver() {
        try (Card card = new Card()) {
            near(drawn(card, Optional.empty()), 32, 32, 64, 128, 191, "no picture of depths at all");
            try (Canvas canvas = card.canvas(SIZE, SIZE);
                 Depths depths = card.depths(FINE);
                 Tracing tracing = card.tracing(laid, new Outlined(laid).vertex());
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent sentWall = card.sent(new Packed(laid.point(), wall));
                 Sent sentFace = card.sent(new Packed(laid.point(), face))) {
                canvas.drawn(sky, frame(true), depths, pass -> pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0))));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "one never drawn on");
                depths.drawn(tracing, beamed(), pass -> pass.draw(drawing, sentFace, told(faceStands)));
                canvas.drawn(sky, frame(true), depths, pass -> pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0))));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 13, 26, 38, "the face drawn on it, though not seen");
                depths.drawn(tracing, beamed(), pass -> { });
                canvas.drawn(sky, frame(true), depths, pass -> pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0))));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "one drawn afresh with nothing");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void aFaceThatIsMixedWithWhatIsDrawnLeavesNothingInAPictureOfDepthsSoTheWallBehindItIsInTheSun() {
        final Look clear = new Look(laid,
                new Surface(new Colour(1, 0, 0), List.of(), new Colour(0, 0, 0), 8).clear(0.5));
        try (Card card = new Card();
             Canvas canvas = card.canvas(SIZE, SIZE);
             Depths depths = card.depths(FINE);
             Tracing tracing = card.tracing(laid, new Outlined(laid).vertex());
             Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
             Drawing mixed = card.mixing(laid, clear.vertex(), clear.fragment());
             Sent sentWall = card.sent(new Packed(laid.point(), wall));
             Sent sentFace = card.sent(new Packed(laid.point(), face))) {
            depths.drawn(tracing, beamed(), pass -> {
                pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0)));
                pass.draw(mixed, sentFace, told(faceStands));
            });
            canvas.drawn(sky, frame(true), depths, pass -> pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0))));
            near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191,
                    "the wall behind the face from the sun: in the sun, the face being seen through");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    /**
     * The wall and the face drawn: with the sun's picture of them drawn first and looked up, where {@code shaded}
     * holds true; with a picture of depths that has nothing on it, where it holds false; with none, where it is
     * empty.
     */
    private Picture drawn(final Card card, final Optional<Boolean> shaded) {
        try (Canvas canvas = card.canvas(SIZE, SIZE);
             Depths depths = card.depths(FINE);
             Tracing tracing = card.tracing(laid, new Outlined(laid).vertex());
             Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
             Sent sentWall = card.sent(new Packed(laid.point(), wall));
             Sent sentFace = card.sent(new Packed(laid.point(), face))) {
            if (shaded.orElse(false)) {
                depths.drawn(tracing, beamed(), pass -> {
                    pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0)));
                    pass.draw(drawing, sentFace, told(faceStands));
                });
            }
            if (shaded.isPresent()) {
                canvas.drawn(sky, frame(true), depths, pass -> {
                    pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0)));
                    pass.draw(drawing, sentFace, told(faceStands));
                });
            } else {
                canvas.drawn(sky, frame(true), pass -> {
                    pass.draw(drawing, sentWall, told(new Vec3(0, 0, 0)));
                    pass.draw(drawing, sentFace, told(faceStands));
                });
            }
            return new Picture(canvas.width(), canvas.pixels());
        }
    }

    /** Where the sun's picture has a point said as the camera sees it, the eye at the middle. */
    private Mat4 fromTheEye() {
        return beam.seen().times(new Mat4(camera.eye()));
    }

    /**
     * What the camera's frame is told: a fifth of white on everything, and a sun that brings a face square to the
     * camera up to white; and where the sun's picture has each point, eased by two of its pixels.
     */
    private Filling frame(final boolean lookedUp) {
        final double sun = 0.8 / Math.sqrt(0.5);
        final double pixel = beam.pixel(FINE);
        return new Filling(laid.frame()).with("seen", camera.seen(1).columns())
                .with("sunTowards", 0, -1, 1).with("sun", sun, sun, sun).with("ambient", 0.2, 0.2, 0.2)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0)
                .with("beam", lookedUp ? fromTheEye().columns() : new double[16])
                .with("eased", 2 * pixel, pixel / 48);
    }

    /** What the sun's own frame is told: the same things, seen from the sun. */
    private Filling beamed() {
        return new Filling(laid.frame()).with("seen", fromTheEye().columns())
                .with("sunTowards", 0, -1, 1).with("sun", 0, 0, 0).with("ambient", 0, 0, 0)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0);
    }

    /** What a thing standing at {@code stands} is told: where it stands, as the camera sees it. */
    private Filling told(final Vec3 stands) {
        return new Filling(laid.draw()).with("placed", camera.placed(stands).columns()).with("coarsened", 1)
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
