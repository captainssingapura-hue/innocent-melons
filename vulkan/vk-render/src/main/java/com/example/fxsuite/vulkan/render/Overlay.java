package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lettering;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * What is drawn over the picture, as an instrument's display is: lines and letters in light of a colour told with
 * each, laid on a plane a hand's breadth in front of the camera, where nothing of the world comes between, and seen
 * through, so the world shows behind them. Placed in the screen's own units: across from -{@code aspect} at its left
 * to {@code aspect} at its right, up from -1 at its foot to 1 at its top, whatever the view's angle. The letters and
 * a bar are each sent to the card once, and placed by a matrix every time they are drawn. Drawn after all else of a
 * frame, as anything seen through is. Closed, all of it is gone from the card.
 */
public final class Overlay implements AutoCloseable {

    /** How far in front of the eye the plane is, in centimetres; and how much of what is behind a stroke it hides. */
    private static final double AHEAD = 15;
    private static final double HIDES = 0.15;

    private final Lettering lettering = new Lettering();
    private final Looked light;
    private final Shaped bar;
    private final Map<Character, Shaped> letters = new HashMap<>();
    private final List<AutoCloseable> sent = new ArrayList<>();

    /** An overlay on {@code card}, its layouts as {@code laid} says. */
    public Overlay(final Card card, final Laid laid) {
        try {
            this.light = kept(new Looked(card, new Look(laid, new Surface(new Colour("#000000"), List.of(),
                    new Colour("#000000"), 1).glowing(new Tint().colour()).clear(1 - HIDES))));
            final Forming forming = new Forming(new Vec3(0, 0, -1));
            // a unit square, from nothing to one along and half either side across: a bar of no length, which its
            // square ends make a unit long (a bar a unit long would be two, half a unit past either end)
            lettering.barred(forming, new Vec3(0.5, 0, 0), new Vec3(0.5, 0, 0), 1);
            this.bar = kept(new Shaped(card, laid, forming.formed()));
            for (final char letter : "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-+.:/|<>[]%*".toCharArray()) {
                letters.put(letter, kept(new Shaped(card, laid, lettering.form(letter))));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    private <T extends AutoCloseable> T kept(final T thing) {
        sent.add(thing);
        return thing;
    }

    /** The overlay over a picture {@code aspect} times as wide as it is high, seen by {@code camera}. */
    public Over over(final Camera camera, final double aspect) {
        final double half = AHEAD * Math.tan(Math.toRadians(camera.fieldOfView()) / 2);
        final Vec3 r = camera.right().times(half);
        final Vec3 u = camera.up().times(half);
        final Vec3 at = camera.eye().plus(camera.ahead().times(AHEAD));
        // the screen's units to the world: across along the camera's right, up along its up, a unit half the view
        final Mat4 screen = new Mat4(new double[] {r.x(), u.x(), camera.ahead().x(), at.x(),
            r.y(), u.y(), camera.ahead().y(), at.y(), r.z(), u.z(), camera.ahead().z(), at.z(), 0, 0, 0, 1});
        return new Over(screen, camera, aspect);
    }

    /** The overlay over one picture: what draws on it. */
    public final class Over {

        private final Mat4 screen;
        private final Camera camera;
        private final double aspect;

        private Over(final Mat4 screen, final Camera camera, final double aspect) {
            this.screen = screen;
            this.camera = camera;
            this.aspect = aspect;
        }

        /** How wide the picture is, in the screen's units, either side of its middle. */
        public double aspect() {
            return aspect;
        }

        /** A line by {@code pass} from ({@code x0}, {@code y0}) to ({@code x1}, {@code y1}), so thick, in a colour. */
        public void line(final Pass pass, final double x0, final double y0, final double x1, final double y1,
                         final double thick, final Vec3 colour) {
            final double dx = x1 - x0;
            final double dy = y1 - y0;
            final double length = Math.hypot(dx, dy);
            final double c = length > 0 ? dx / length : 1;
            final double s = length > 0 ? dy / length : 0;
            // squared off half its thickness past either end, so a box's corners meet
            final double reach = length + thick;
            final Mat4 placed = screen.times(new Mat4(new double[] {c * reach, -s * thick, 0, x0 - c * thick / 2,
                s * reach, c * thick, 0, y0 - s * thick / 2, 0, 0, 1, 0, 0, 0, 0, 1}));
            light.drawn(pass, bar, placed, camera, 1, colour);
        }

        /** {@code text} by {@code pass}, its first letter's lower left at ({@code x}, {@code y}), {@code high}. */
        public void text(final Pass pass, final String text, final double x, final double y, final double high,
                         final Vec3 colour) {
            for (int at = 0; at < text.length(); at++) {
                final Shaped letter = letters.get(Character.toUpperCase(text.charAt(at)));
                if (letter != null) {
                    final double across = x + at * lettering.set() * high;
                    light.drawn(pass, letter, screen.times(new Mat4(new double[] {high, 0, 0, across,
                        0, high, 0, y, 0, 0, 1, 0, 0, 0, 0, 1})), camera, 1, colour);
                }
            }
        }

        /** How wide {@code text} is when written {@code high}. */
        public double wide(final String text, final double high) {
            return text.length() * lettering.set() * high;
        }

        /** A box's outline, from ({@code x0}, {@code y0}) to ({@code x1}, {@code y1}). */
        public void box(final Pass pass, final double x0, final double y0, final double x1, final double y1,
                        final double thick, final Vec3 colour) {
            line(pass, x0, y0, x1, y0, thick, colour);
            line(pass, x1, y0, x1, y1, thick, colour);
            line(pass, x1, y1, x0, y1, thick, colour);
            line(pass, x0, y1, x0, y0, thick, colour);
        }
    }

    @Override
    public void close() {
        for (final AutoCloseable thing : sent.reversed()) {
            try {
                thing.close();
            } catch (final Exception wrong) {
                throw new IllegalStateException("what was drawn over a picture would not go", wrong);
            }
        }
        sent.clear();
    }
}
