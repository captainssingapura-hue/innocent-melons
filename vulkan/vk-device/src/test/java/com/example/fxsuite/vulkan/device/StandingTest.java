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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * What stands in a frame, kept for rays and asked by a look, checked by facts, on a card that answers rays. The
 * same wall and smaller face as the picture of depths is checked with, the sun shining down and onward at both:
 * the wall is in the face's shadow where the face stands between it and the sun, and in the sun beside it; the
 * face is in the sun all over; with nothing gathered, or gathered afresh with nothing, or a frame that says no
 * ray reaches, the wall is in the sun all over; a face that is seen through stands in the way of no ray; more
 * things than there was room for are kept all the same; and the validation layer says nothing of any of it. A
 * card opened to answer no rays refuses layouts that ask them, and keeps nothing for them.
 */
class StandingTest {

    private static final int SIZE = 64;
    private static final double REACHES = 1000;

    private final Laid laid = new Laid().asking(true);
    private final Look look = new Look(laid,
            new Surface(new Colour(0.25, 0.5, 0.75), List.of(), new Colour(0, 0, 0), 8));
    private final Mesh wall = new Panel(4, 4, 4).formed();
    private final Mesh face = new Panel(2, 2, 2).formed();
    private final Vec3 faceStands = new Vec3(0, 2.5, -1);
    private final Vec3 nowhere = new Vec3(0, 0, 0);
    private final Camera camera = new Orbit(new Vec3(0, 2, 0), 0, 0, 10).camera(40, 1);
    private final Vec3 sky = new Vec3(0.1, 0.2, 0.3);

    @Test
    void theWallIsInTheFacesShadowWhereTheFaceStandsBetweenItAndTheSunAndInTheSunBesideIt() {
        try (Card card = new Card()) {
            assumeTrue(card.rays(), "the card answers rays");
            try (Canvas canvas = card.canvas(SIZE, SIZE);
                 Standing standing = card.standing();
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent sentWall = card.sent(new Packed(laid.point(), wall));
                 Sent sentFace = card.sent(new Packed(laid.point(), face))) {
                canvas.drawn(sky, frame(REACHES), standing.stood(pass -> {
                    pass.draw(drawing, sentWall, told(nowhere));
                    pass.draw(drawing, sentFace, told(faceStands));
                }), pass -> {
                    pass.draw(drawing, sentWall, told(nowhere));
                    pass.draw(drawing, sentFace, told(faceStands));
                });
                final Picture picture = new Picture(SIZE, canvas.pixels());
                near(picture, 32, 32, 13, 26, 38, "the wall behind the face from the sun: only the light on all");
                near(picture, 18, 41, 64, 128, 191, "the wall beside the shadow: the sun as well");
                near(picture, 32, 45, 64, 128, 191, "the wall below the shadow");
                near(picture, 32, 17, 64, 128, 191, "the face itself, nearest the sun, shades nothing of itself");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void withNothingGatheredOrAFrameThatSaysNoRayReachesTheWallIsInTheSunAllOver() {
        try (Card card = new Card()) {
            assumeTrue(card.rays(), "the card answers rays");
            try (Canvas canvas = card.canvas(SIZE, SIZE);
                 Standing standing = card.standing();
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Sent sentWall = card.sent(new Packed(laid.point(), wall));
                 Sent sentFace = card.sent(new Packed(laid.point(), face))) {
                canvas.drawn(sky, frame(REACHES), pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "nothing kept for rays at all");
                canvas.drawn(sky, frame(REACHES), standing, pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "nothing gathered yet");
                standing.stood(pass -> pass.draw(drawing, sentFace, told(faceStands)));
                canvas.drawn(sky, frame(REACHES), standing, pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 13, 26, 38, "the face gathered, though not seen");
                canvas.drawn(sky, frame(0), standing, pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "a frame that says no ray reaches");
                canvas.drawn(sky, frame(REACHES), standing.stood(pass -> { }),
                        pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "gathered afresh with nothing");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void aFaceSeenThroughStandsInTheWayOfNoRayAndMoreThingsThanThereWasRoomForAreKeptAllTheSame() {
        final Look clear = new Look(laid,
                new Surface(new Colour(1, 0, 0), List.of(), new Colour(0, 0, 0), 8).clear(0.5));
        try (Card card = new Card()) {
            assumeTrue(card.rays(), "the card answers rays");
            try (Canvas canvas = card.canvas(SIZE, SIZE);
                 Standing standing = card.standing();
                 Drawing drawing = card.drawing(laid, look.vertex(), look.fragment());
                 Drawing mixed = card.mixing(laid, clear.vertex(), clear.fragment());
                 Sent sentWall = card.sent(new Packed(laid.point(), wall));
                 Sent sentFace = card.sent(new Packed(laid.point(), face))) {
                canvas.drawn(sky, frame(REACHES), standing.stood(pass -> pass.draw(mixed, sentFace, told(faceStands))),
                        pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 64, 128, 191, "the face being seen through");
                canvas.drawn(sky, frame(REACHES), standing.stood(pass -> {
                    for (int far = 0; far < 200; far++) {
                        pass.draw(drawing, sentFace, told(new Vec3(100 + far, 0, 0)));
                    }
                    pass.draw(drawing, sentFace, told(faceStands));
                }), pass -> pass.draw(drawing, sentWall, told(nowhere)));
                near(new Picture(SIZE, canvas.pixels()), 32, 32, 13, 26, 38, "the last of two hundred and one");
                near(new Picture(SIZE, canvas.pixels()), 18, 41, 64, 128, 191, "and beside its shadow");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        }
    }

    @Test
    void aCardOpenedToAnswerNoRaysRefusesLayoutsThatAskThemAndKeepsNothingForThem() {
        System.setProperty("vulkan.rays", "false");
        try (Card card = new Card()) {
            assertTrue(!card.rays());
            assertThrows(IllegalArgumentException.class, () -> card.drawing(laid, look.vertex(), look.fragment()));
            assertThrows(IllegalStateException.class, card::standing);
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say of it");
        } finally {
            System.clearProperty("vulkan.rays");
        }
    }

    /**
     * What the camera's frame is told: a fifth of white on everything, and a sun, shining down and onward alike,
     * that brings a face square to the camera up to white; and that a ray toward the sun reaches {@code reaches}.
     */
    private Filling frame(final double reaches) {
        final double sun = 0.8 / Math.sqrt(0.5);
        return new Filling(laid.frame()).with("seen", camera.seen(1).columns())
                .with("sunTowards", 0, -1, 1).with("sun", sun, sun, sun).with("ambient", 0.2, 0.2, 0.2)
                .with("sky", 0, 0, 0).with("ground", 0, 0, 0).with("beam", new double[16]).with("eased", 0, 0)
                .with("rays", 0.01, reaches);
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
